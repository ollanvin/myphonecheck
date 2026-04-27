package app.myphonecheck.mobile.feature.initialscan.service

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity

/**
 * Initial Scan 결과 묶음 (Architecture v2.0.0 §28).
 *
 * 디바이스 베이스데이터 — 6 Surface 활성화 입력.
 */
data class ScanResult(
    val simContext: SimContext,
    val callCount: Int,
    val smsCount: Int,
    val packageCount: Int,
    val scannedAtMillis: Long,
)

/**
 * 스캐너 출력 묶음 — 영구 저장 전 메모리 단계.
 */
data class ScannedBases(
    val simContext: SimContext,
    val callBase: List<CallBaseEntity>,
    val smsBase: List<SmsBaseEntity>,
    val packageBase: List<PackageBaseEntity>,
)
