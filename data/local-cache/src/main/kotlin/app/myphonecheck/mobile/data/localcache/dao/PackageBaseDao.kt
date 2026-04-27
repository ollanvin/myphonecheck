package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity

@Dao
interface PackageBaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<PackageBaseEntity>)

    @Query("SELECT * FROM package_base_entry ORDER BY appLabel ASC")
    suspend fun getAll(): List<PackageBaseEntity>

    @Query("SELECT * FROM package_base_entry WHERE sensitivePermissionsCsv LIKE '%' || :permission || '%'")
    suspend fun findWithPermission(permission: String): List<PackageBaseEntity>

    @Query("SELECT COUNT(*) FROM package_base_entry")
    suspend fun count(): Int

    @Query("DELETE FROM package_base_entry")
    suspend fun clear()
}
