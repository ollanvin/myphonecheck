package app.myphonecheck.mobile.data.localcache.db

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * WO-STASH-RESTORE-V19-SCHEMA — Room export JSON 정합 (instrumented 없이 JVM에서 검증).
 */
class HubMessageSchemaJsonTest {

    @Test
    fun hub_message_columns_match_export_v19() {
        val root = File(System.getProperty("user.dir") ?: ".")
        val jsonFile = File(
            root,
            "schemas/app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase/19.json",
        )
        assertTrue(
            "Schema file missing: ${jsonFile.absolutePath}",
            jsonFile.exists(),
        )
        val text = jsonFile.readText(Charsets.UTF_8)
        assertTrue(text.contains("\"tableName\": \"hub_message\""))
        val required = listOf(
            "sender_identifier",
            "sender_label",
            "content",
            "has_link",
            "extracted_urls",
            "extracted_numbers",
            "category",
            "received_at",
            "source",
        )
        required.forEach { col ->
            assertTrue("Missing column $col", text.contains("\"columnName\": \"$col\""))
        }
        assertTrue(text.contains("index_hub_message_received_at"))
        assertTrue(text.contains("index_hub_message_sender_identifier_received_at"))
    }
}
