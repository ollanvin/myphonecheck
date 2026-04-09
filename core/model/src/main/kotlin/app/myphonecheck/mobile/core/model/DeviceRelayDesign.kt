package app.myphonecheck.mobile.core.model

/**
 * ═══════════════════════════════════════════════════════════════════
 * Device Relay Protocol — 설계 문서 (v1.0)
 * ═══════════════════════════════════════════════════════════════════
 *
 * 목적: MyPhoneCheck 기기 간 판정 결과를 공유하되, 중앙 서버 없이
 *       프라이버시를 완전히 보장하는 federated relay 프로토콜.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 절대 원칙                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. 중앙 서버 없음 — 서버가 존재하지 않는다                    │
 * │ 2. 번호 원문 전송 없음 — hash만 전송                          │
 * │ 3. 개인 통화 내역 전송 없음 — signal summary만 전송          │
 * │ 4. TTL 기반 자동 만료 — 영구 보관 데이터 없음                │
 * │ 5. 비용 제로 — 서버/인프라 비용 발생하지 않음                 │
 * │ 6. 사용자 opt-in only — 기본값은 relay 비활성                │
 * └──────────────────────────────────────────────────────────────┘
 *
 * ═══════════════════════════════════════════════════════════════
 * 1. RELAY PACKET 구조
 * ═══════════════════════════════════════════════════════════════
 *
 * 기기가 공유하는 데이터 단위:
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ RelayPacket                                                   │
 * ├──────────────────────────────────────────────────────────────┤
 * │ phoneHash    : String   — SHA-256(canonicalNumber + salt)    │
 * │ signalType   : String   — SCAM | SPAM | BUSINESS | SAFE ... │
 * │ riskLevel    : String   — HIGH | MEDIUM | LOW | UNKNOWN     │
 * │ confidence   : Float    — 0.0 ~ 1.0                         │
 * │ reportCount  : Int      — 이 기기에서의 수신 횟수            │
 * │ ttlMs        : Long     — 패킷 유효 기간 (밀리초)           │
 * │ createdAtMs  : Long     — 패킷 생성 시각 (epoch)            │
 * │ deviceId     : String   — 익명 기기 ID (랜덤, 재설치 시 변경)│
 * │ protocolVer  : Int      — 프로토콜 버전 (현재 1)             │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 핵심: phoneHash로는 원번호 역추적 불가.
 *       signalType/riskLevel은 카테고리 수준 — 통화 내용 유추 불가.
 *       deviceId는 익명 — 사용자 신원 추적 불가.
 *
 * ═══════════════════════════════════════════════════════════════
 * 2. RELAY 메커니즘 (Federated P2P)
 * ═══════════════════════════════════════════════════════════════
 *
 * 2-1. 기본 flow:
 *   ┌────────┐       ┌──────────────┐       ┌────────┐
 *   │ Device A│──→──│ Nearby Devices│──→──│ Device B│
 *   └────────┘       └──────────────┘       └────────┘
 *
 *   Device A: 미저장 번호 수신 → 판정 완료 → RelayPacket 생성
 *   Nearby Devices: Bluetooth LE / Wi-Fi Direct 범위 내 기기
 *   Device B: RelayPacket 수신 → hash 매칭 시 판정 보조 데이터로 활용
 *
 * 2-2. 전송 채널 (우선순위):
 *   ① Google Nearby Messages API (최적)
 *     - 별도 서버 불필요 (Google 인프라 활용)
 *     - BLE + Wi-Fi + Ultrasonic 자동 선택
 *     - 패킷 크기 제한: ~32KB (RelayPacket에 충분)
 *   ② Android Nearby Connections API (fallback)
 *     - 직접 P2P 연결
 *     - 동일 Wi-Fi 필요
 *   ③ Firebase Cloud Messaging (향후 확장, v2)
 *     - 비용 발생할 수 있어 v1에서는 제외
 *     - hash 기반 topic 구독으로 구현 가능
 *
 * 2-3. 비용 분석:
 *   ┌──────────────────────────┬──────────────────────┐
 *   │ 채널                     │ 서버 비용             │
 *   ├──────────────────────────┼──────────────────────┤
 *   │ Nearby Messages API     │ $0 (Google 인프라)    │
 *   │ Nearby Connections API  │ $0 (P2P 직접 연결)   │
 *   │ BLE Advertising         │ $0 (하드웨어 기능)    │
 *   └──────────────────────────┴──────────────────────┘
 *   결론: 인프라 비용 $0/월.
 *
 * ═══════════════════════════════════════════════════════════════
 * 3. HASH 설계
 * ═══════════════════════════════════════════════════════════════
 *
 * phoneHash 생성:
 *   input  = canonicalNumber (E.164 형식, +포함)
 *   salt   = 고정 앱 솔트 + 일별 회전 솔트 (일단위 변경)
 *   output = SHA-256(input + salt).hex().take(16)
 *
 * 16자리 hex = 64bit → 충돌 확률 극히 낮음 (10^19 조합).
 * 일별 솔트 회전 → 동일 번호라도 다른 날 다른 hash → 장기 추적 불가.
 *
 * ═══════════════════════════════════════════════════════════════
 * 4. TTL 정책
 * ═══════════════════════════════════════════════════════════════
 *
 * ┌────────────────────────┬──────────────────────────┐
 * │ 패킷 유형              │ TTL                       │
 * ├────────────────────────┼──────────────────────────┤
 * │ SCAM / HIGH risk      │ 24시간                    │
 * │ SPAM / MEDIUM risk    │ 12시간                    │
 * │ BUSINESS / LOW risk   │ 6시간                     │
 * │ UNKNOWN               │ 1시간                     │
 * └────────────────────────┴──────────────────────────┘
 *
 * 위험도가 높을수록 TTL이 길다 → 위험 번호 정보가 더 오래 공유됨.
 * 모든 패킷은 TTL 만료 시 수신 기기에서 자동 삭제.
 *
 * ═══════════════════════════════════════════════════════════════
 * 5. 수신측 처리
 * ═══════════════════════════════════════════════════════════════
 *
 * 수신 기기가 RelayPacket을 받으면:
 * 1. TTL 검증 → 만료된 패킷 즉시 폐기
 * 2. hash 저장 → 메모리 LRU 캐시 (최대 100개)
 * 3. 전화 수신 시 canonicalNumber의 hash와 매칭
 * 4. 매칭 성공 시:
 *    - 자체 판정 결과에 relay 데이터를 보조 근거로 추가
 *    - relay 데이터만으로 자동 차단/거절 절대 불가
 *    - "다른 사용자 {n}명이 이 번호를 {signalType}으로 보고" 형태 표시
 *
 * ═══════════════════════════════════════════════════════════════
 * 6. 프라이버시 보장
 * ═══════════════════════════════════════════════════════════════
 *
 * ┌──────────────────────────────┬──────────────────────────┐
 * │ 공격 벡터                    │ 방어 메커니즘             │
 * ├──────────────────────────────┼──────────────────────────┤
 * │ 원번호 역추적                │ SHA-256 + 일별 솔트 회전 │
 * │ 통화 내용 유추               │ signal type만 공유       │
 * │ 사용자 신원 추적             │ 익명 deviceId           │
 * │ 장기 패턴 분석               │ TTL 자동 만료           │
 * │ 패킷 가로채기                │ BLE/Wi-Fi 범위 제한     │
 * │ 중간자 공격                  │ 패킷 서명 (HMAC)       │
 * └──────────────────────────────┴──────────────────────────┘
 *
 * ═══════════════════════════════════════════════════════════════
 * 7. 사용자 설정
 * ═══════════════════════════════════════════════════════════════
 *
 * 설정 > 프라이버시 > Device Relay:
 * - OFF (기본값): relay 비활성. 패킷 송수신 없음.
 * - RECEIVE ONLY: 다른 기기 패킷 수신만. 자기 데이터 공유 안 함.
 * - FULL: 패킷 송수신 모두 활성.
 *
 * ═══════════════════════════════════════════════════════════════
 * 8. 구현 로드맵
 * ═══════════════════════════════════════════════════════════════
 *
 * v1.0: 설계 문서 확정 (현재)
 * v1.1: RelayPacket 모델 + hash 유틸리티 구현
 * v1.2: Nearby Messages API 연동 + 송신 로직
 * v1.3: 수신 + LRU 캐시 + 매칭 로직
 * v1.4: UI 연동 ("다른 사용자 N명 보고" 표시)
 * v2.0: Firebase topic 기반 글로벌 확장 (검토 후)
 *
 * ═══════════════════════════════════════════════════════════════
 */

/**
 * Device Relay 패킷 데이터 모델.
 * 기기 간 전송되는 최소 단위.
 */
data class RelayPacket(
    /** SHA-256(canonicalNumber + dailySalt).hex().take(16) */
    val phoneHash: String,
    /** SCAM, SPAM, BUSINESS, INSTITUTION, DELIVERY, SAFE, UNKNOWN */
    val signalType: String,
    /** HIGH, MEDIUM, LOW, UNKNOWN */
    val riskLevel: String,
    /** 0.0 ~ 1.0 */
    val confidence: Float,
    /** 이 기기에서의 수신 횟수 */
    val reportCount: Int,
    /** 패킷 유효 기간 (밀리초) */
    val ttlMs: Long,
    /** 패킷 생성 시각 (epoch millis) */
    val createdAtMs: Long,
    /** 익명 기기 ID */
    val deviceId: String,
    /** 프로토콜 버전 */
    val protocolVersion: Int = 1,
) {
    /** TTL 유효성 확인 */
    fun isValid(nowMs: Long = System.currentTimeMillis()): Boolean {
        return (nowMs - createdAtMs) < ttlMs
    }

    companion object {
        // TTL 상수 (밀리초)
        const val TTL_HIGH_RISK: Long = 24 * 60 * 60 * 1000L      // 24시간
        const val TTL_MEDIUM_RISK: Long = 12 * 60 * 60 * 1000L    // 12시간
        const val TTL_LOW_RISK: Long = 6 * 60 * 60 * 1000L        // 6시간
        const val TTL_UNKNOWN: Long = 1 * 60 * 60 * 1000L         // 1시간

        fun ttlForRiskLevel(riskLevel: RiskLevel): Long = when (riskLevel) {
            RiskLevel.HIGH -> TTL_HIGH_RISK
            RiskLevel.MEDIUM -> TTL_MEDIUM_RISK
            RiskLevel.LOW -> TTL_LOW_RISK
            RiskLevel.UNKNOWN -> TTL_UNKNOWN
        }
    }
}

/**
 * Device Relay 사용자 설정 모드.
 */
enum class RelayMode {
    /** 비활성 (기본값) — 패킷 송수신 없음 */
    OFF,
    /** 수신 전용 — 다른 기기 패킷 수신만, 자기 데이터 공유 안 함 */
    RECEIVE_ONLY,
    /** 전체 활성 — 패킷 송수신 모두 */
    FULL,
}
