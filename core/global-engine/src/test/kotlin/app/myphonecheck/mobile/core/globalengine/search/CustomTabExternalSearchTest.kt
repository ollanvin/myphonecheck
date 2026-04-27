package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class CustomTabExternalSearchTest {

    private val external = CustomTabExternalSearch()

    private fun query(key: String, type: QueryType = QueryType.PHONE_NUMBER): SearchQuery {
        val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))
        return SearchQuery(key, type, SearchContext(sim))
    }

    @Test
    fun `simple key encoded`() {
        val intent = external.buildIntent(query("+821012345678"))
        assertEquals("https://www.google.com/search?q=%2B821012345678", intent.url)
    }

    @Test
    fun `key with spaces encoded as plus`() {
        val intent = external.buildIntent(query("Bank Alert"))
        assertTrue(intent.url.endsWith("Bank+Alert"))
    }

    @Test
    fun `query type does not change URL structure`() {
        val phone = external.buildIntent(query("abc", QueryType.PHONE_NUMBER)).url
        val pkg = external.buildIntent(query("abc", QueryType.APP_PACKAGE)).url
        assertEquals(phone, pkg)
    }

    @Test
    fun `URL prefix uses Google search base`() {
        val intent = external.buildIntent(query("x"))
        assertTrue(intent.url.startsWith("https://www.google.com/search?q="))
    }
}
