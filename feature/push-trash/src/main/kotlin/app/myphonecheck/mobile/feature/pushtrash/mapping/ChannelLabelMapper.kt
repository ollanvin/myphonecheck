package app.myphonecheck.mobile.feature.pushtrash.mapping

import android.content.Context
import app.myphonecheck.mobile.feature.pushtrash.R

/**
 * Maps `(packageName, channelId)` pairs to human-readable labels.
 *
 * **Do not add guessed channel IDs.** Only add entries after capturing real `channelId`
 * values on a device (see manual test doc). Until then the table stays empty and
 * [label] falls back to the raw `channelId` string.
 */
object ChannelLabelMapper {

    private val verifiedChannelLabels: Map<Pair<String, String>, Int> = emptyMap()

    fun label(context: Context, packageName: String, channelId: String): String {
        val res = verifiedChannelLabels[packageName to channelId] ?: return channelId
        return context.getString(res)
    }

    /**
     * Friendly name for known large-app packages (not channel-specific).
     */
    fun displayAppLabel(context: Context, packageName: String): String {
        val res = PACKAGE_LABELS[packageName] ?: return packageName
        return context.getString(res)
    }

    private val PACKAGE_LABELS: Map<String, Int> = mapOf(
        "com.coupang.mobile" to R.string.push_trash_app_label_coupang,
        "com.woowahan.baemin" to R.string.push_trash_app_label_baemin,
        "com.fineapp.yogiyo" to R.string.push_trash_app_label_yogiyo,
        "viva.republica.toss" to R.string.push_trash_app_label_toss,
        "com.elevenst" to R.string.push_trash_app_label_11st,
    )
}
