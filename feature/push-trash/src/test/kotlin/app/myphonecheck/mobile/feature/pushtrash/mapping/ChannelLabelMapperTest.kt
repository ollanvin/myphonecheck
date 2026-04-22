package app.myphonecheck.mobile.feature.pushtrash.mapping

import android.content.Context
import app.myphonecheck.mobile.feature.pushtrash.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class ChannelLabelMapperTest {

    @Test
    fun label_unknownChannel_returnsRawId() {
        val context = mockk<Context>(relaxed = true)
        assertEquals("raw-channel-id", ChannelLabelMapper.label(context, "any.pkg", "raw-channel-id"))
    }

    @Test
    fun displayAppLabel_knownPackage_usesStringResource() {
        val context = mockk<Context>()
        every { context.getString(R.string.push_trash_app_label_coupang) } returns "Coupang"
        assertEquals("Coupang", ChannelLabelMapper.displayAppLabel(context, "com.coupang.mobile"))
    }

    @Test
    fun displayAppLabel_unknownPackage_returnsPackageName() {
        val context = mockk<Context>(relaxed = true)
        assertEquals("com.unknown.app", ChannelLabelMapper.displayAppLabel(context, "com.unknown.app"))
    }
}
