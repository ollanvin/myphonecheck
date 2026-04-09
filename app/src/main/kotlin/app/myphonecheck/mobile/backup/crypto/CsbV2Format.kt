package app.myphonecheck.mobile.backup.crypto

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 올란빈 앱팩토리 백업 파일 **v2** 바이너리 레이아웃 (`.csb` 내부 페이로드).
 *
 * v1(레거시)은 매직 없이 `IV(12) + ciphertext` 로 시작한다. v2는 선두 매직 `CSB2` 로 식별한다.
 *
 * 레이아웃 (big-endian 정수):
 * ```
 * [0..3]   magic "CSB2"
 * [4]      fileFormatVersion (2)
 * [5]      kdfId (1 = PBKDF2-HMAC-SHA256)
 * [6..9]   pbkdf2Iterations (Int)
 * [10]     derivedKeyLengthBytes (1..255, AES-256 = 32)
 * [11..12] saltLength (UInt16)
 * [..]     salt
 * [1]      ivLength (GCM 표준 12)
 * [..]     iv
 * [..4]    ciphertextLength (Int)
 * [..]     ciphertext (AES-GCM: 암호문 + auth tag 포함)
 * ```
 *
 * 암호화/KDF는 이 클래스 범위 밖에서 수행한다. 여기서는 직렬화·검증만 담당한다.
 */
object CsbV2Format {

    const val MAGIC = "CSB2"
    private val magicBytes = MAGIC.toByteArray(Charsets.US_ASCII)

    /** 이 바이너리 블록의 스키마 버전 (파일 포맷 v2). */
    const val FILE_FORMAT_VERSION: Int = 2

    /** PBKDF2-HMAC-SHA256 (초기 2.1 기본 KDF). */
    const val KDF_PBKDF2_HMAC_SHA256: Int = 1

    /** AES-256용 파생 키 길이 (바이트). */
    const val DERIVED_KEY_LENGTH_BYTES: Int = 32

    /** GCM 권장 IV 길이. */
    const val GCM_IV_LENGTH_BYTES: Int = 12

    /** 초기 기본 반복 횟수 (encode 시 권장 기본값; 디코드는 파일 값을 신뢰). */
    const val DEFAULT_PBKDF2_ITERATIONS: Int = 310_000

    /** [validateForEncode] 및 권장 PBKDF2 반복 횟수 하한. */
    const val MIN_PBKDF2_ITERATIONS: Int = 100_000

    /** [validateForEncode] 및 권장 PBKDF2 반복 횟수 상한. */
    const val MAX_PBKDF2_ITERATIONS: Int = 2_000_000

    const val MIN_SALT_LENGTH: Int = 16
    const val MAX_SALT_LENGTH: Int = 64

    /** 매직 다음: version(1)+kdf(1)+iter(4)+dkLen(1)+saltLen(2). */
    private const val HEADER_AFTER_MAGIC_LEN = 9

    /** v1과 구분: 최소 길이 미만이면 v2가 아님. */
    val MIN_V2_FILE_LENGTH: Int
        get() {
            val fixedHeader = magicBytes.size + HEADER_AFTER_MAGIC_LEN
            val minAfterSalt =
                MIN_SALT_LENGTH + // salt (최소)
                    1 + // ivLength
                    GCM_IV_LENGTH_BYTES + // iv
                    4 + // ciphertextLength
                    1 // ciphertext (최소 1바이트)
            return fixedHeader + minAfterSalt
        }

    fun isProbablyV2(bytes: ByteArray): Boolean {
        if (bytes.size < magicBytes.size + 1) return false
        return magicBytes.indices.all { i -> bytes[i] == magicBytes[i] } &&
            bytes[magicBytes.size].toInt() and 0xFF == FILE_FORMAT_VERSION
    }

    /**
     * [envelope]를 v2 `.csb` 바이너리 블록으로 직렬화한다.
     * @throws IllegalArgumentException 규칙 위반 시
     */
    fun encode(envelope: CsbV2Envelope): ByteArray {
        envelope.validateForEncode()
        val salt = envelope.salt
        val iv = envelope.iv
        val ct = envelope.ciphertext

        val outSize = magicBytes.size + 1 + 1 + 4 + 1 + 2 + salt.size + 1 + iv.size + 4 + ct.size
        val buf = ByteBuffer.allocate(outSize)

        buf.put(magicBytes)
        buf.put(FILE_FORMAT_VERSION.toByte())
        buf.put(KDF_PBKDF2_HMAC_SHA256.toByte())
        buf.putInt(envelope.pbkdf2Iterations)
        buf.put(DERIVED_KEY_LENGTH_BYTES.toByte())
        buf.putShort(salt.size.toShort())
        buf.put(salt)
        buf.put(iv.size.toByte())
        buf.put(iv)
        buf.putInt(ct.size)
        buf.put(ct)

        return buf.array()
    }

    /**
     * v2 바이너리 블록을 파싱한다.
     * @throws IllegalArgumentException 매직/버전/KDF/길이 불일치
     */
    fun decode(bytes: ByteArray): CsbV2Envelope {
        require(bytes.size >= MIN_V2_FILE_LENGTH) { "파일이 너무 짧습니다" }

        var pos = 0
        for (i in magicBytes.indices) {
            require(bytes[pos++] == magicBytes[i]) { "v2 매직(CSB2)이 아닙니다" }
        }

        val fileVer = bytes[pos++].toInt() and 0xFF
        require(fileVer == FILE_FORMAT_VERSION) { "지원하지 않는 v2 fileFormatVersion: $fileVer" }

        val kdfId = bytes[pos++].toInt() and 0xFF
        when (kdfId) {
            KDF_PBKDF2_HMAC_SHA256 -> Unit
            else -> throw IllegalArgumentException("지원하지 않는 KDF id: $kdfId")
        }

        require(pos + 4 + 1 + 2 <= bytes.size) { "헤더가 잘렸습니다" }
        val bb = ByteBuffer.wrap(bytes, pos, bytes.size - pos).order(ByteOrder.BIG_ENDIAN)
        val iterations = bb.int
        require(iterations in MIN_PBKDF2_ITERATIONS..MAX_PBKDF2_ITERATIONS) {
            "파일의 pbkdf2Iterations가 허용 범위 밖입니다: $iterations"
        }
        val dkLen = bb.get().toInt() and 0xFF
        require(dkLen == DERIVED_KEY_LENGTH_BYTES) {
            "지원하지 않는 derivedKeyLengthBytes: $dkLen (기대 $DERIVED_KEY_LENGTH_BYTES)"
        }
        val saltLen = bb.short.toInt() and 0xFFFF
        require(saltLen in MIN_SALT_LENGTH..MAX_SALT_LENGTH) {
            "salt 길이가 허용 범위가 아닙니다: $saltLen"
        }
        require(bb.remaining() >= saltLen) { "salt가 잘렸습니다" }
        val salt = ByteArray(saltLen)
        bb.get(salt)
        require(bb.hasRemaining()) { "IV 길이 필드가 없습니다" }
        val ivLen = bb.get().toInt() and 0xFF
        require(ivLen == GCM_IV_LENGTH_BYTES) { "GCM IV 길이는 $GCM_IV_LENGTH_BYTES 바이트여야 합니다: $ivLen" }
        require(bb.remaining() >= ivLen + 4) { "IV/길이 필드가 잘렸습니다" }
        val iv = ByteArray(ivLen)
        bb.get(iv)
        val ctLen = bb.int
        require(ctLen >= 0) { "ciphertextLength가 음수입니다" }
        require(bb.remaining() == ctLen) {
            "ciphertext 길이 불일치: 선언 $ctLen, 남은 ${bb.remaining()} (전체 ${bytes.size})"
        }
        val ciphertext = ByteArray(ctLen)
        bb.get(ciphertext)
        require(!bb.hasRemaining()) {
            "ciphertext 이후 예기치 않은 바이트 ${bb.remaining()}개"
        }

        return CsbV2Envelope(
            pbkdf2Iterations = iterations,
            salt = salt,
            iv = iv,
            ciphertext = ciphertext,
        )
    }
}

/**
 * v2 블록에 담기는 KDF·암호문 필드 (직렬화 전/후 공통).
 */
data class CsbV2Envelope(
    val pbkdf2Iterations: Int,
    val salt: ByteArray,
    val iv: ByteArray,
    val ciphertext: ByteArray,
) {
    fun validateForEncode() {
        require(pbkdf2Iterations in CsbV2Format.MIN_PBKDF2_ITERATIONS..CsbV2Format.MAX_PBKDF2_ITERATIONS) {
            "pbkdf2Iterations는 ${CsbV2Format.MIN_PBKDF2_ITERATIONS}..${CsbV2Format.MAX_PBKDF2_ITERATIONS} 범위여야 합니다"
        }
        require(salt.size in CsbV2Format.MIN_SALT_LENGTH..CsbV2Format.MAX_SALT_LENGTH) {
            "salt 길이는 ${CsbV2Format.MIN_SALT_LENGTH}..${CsbV2Format.MAX_SALT_LENGTH} 바이트여야 합니다"
        }
        require(iv.size == CsbV2Format.GCM_IV_LENGTH_BYTES) {
            "IV는 ${CsbV2Format.GCM_IV_LENGTH_BYTES} 바이트여야 합니다"
        }
        require(ciphertext.isNotEmpty()) { "ciphertext가 비어 있습니다" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CsbV2Envelope
        return pbkdf2Iterations == other.pbkdf2Iterations &&
            salt.contentEquals(other.salt) &&
            iv.contentEquals(other.iv) &&
            ciphertext.contentEquals(other.ciphertext)
    }

    override fun hashCode(): Int {
        var result = pbkdf2Iterations
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }
}
