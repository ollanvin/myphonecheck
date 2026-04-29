package app.myphonecheck.mobile.data.localcache.db

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * WO-STASH-RESTORE-V19-SCHEMA — v18(stub hub_message) → v19 full schema 마이그레이션 회귀.
 */
@RunWith(AndroidJUnit4::class)
class Migration18To19Test {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MyPhoneCheckDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate18To19_validatesAgainstExportedSchema() {
        helper.createDatabase(TEST_DB, 18).close()
        helper.runMigrationsAndValidate(TEST_DB, 19, true, Migration18To19)
    }

    companion object {
        private const val TEST_DB = "migration-test-hub-message.db"
    }
}
