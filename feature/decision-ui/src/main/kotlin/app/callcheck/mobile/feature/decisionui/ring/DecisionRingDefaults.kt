package app.callcheck.mobile.feature.decisionui.ring

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Decision Ring 크기 규격.
 *
 * 디자인 시스템 V1 기준:
 * - DECISION_CARD: 전화 수신 시 전체 판단 화면 (280dp)
 * - HOME_DASHBOARD: 홈 화면 중앙 대시보드 (200dp)
 * - HISTORY_ITEM: 히스토리 목록 아이템 내 미니 링 (24dp)
 * - WIDGET: 홈 화면 위젯 (48dp)
 * - NOTIFICATION: 알림 아이콘 (24dp)
 */
object DecisionRingDefaults {

    /** 전화 수신 시 Decision Card 내 링 크기 */
    val DECISION_CARD_SIZE: Dp = 280.dp

    /** 홈 대시보드 Mini Ring 크기 */
    val HOME_DASHBOARD_SIZE: Dp = 200.dp

    /** 위젯 Mini Ring 크기 */
    val WIDGET_SIZE: Dp = 48.dp

    /** 히스토리 아이템 인라인 링 크기 */
    val HISTORY_ITEM_SIZE: Dp = 24.dp

    /** 알림 아이콘 크기 */
    val NOTIFICATION_SIZE: Dp = 24.dp
}
