package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.QrItem
import kotlinx.coroutines.flow.Flow

@Dao
interface QrDao {
    @Query("SELECT * FROM qr_items ORDER BY timestamp DESC")
    fun getAllQrItems(): Flow<List<QrItem>>

    @Query("SELECT * FROM qr_items WHERE isCreated = 0 ORDER BY timestamp DESC")
    fun getScannedQrItems(): Flow<List<QrItem>>

    @Query("SELECT * FROM qr_items WHERE isCreated = 1 ORDER BY timestamp DESC")
    fun getCreatedQrItems(): Flow<List<QrItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQrItem(item: QrItem): Long

    @Delete
    suspend fun deleteQrItem(item: QrItem)

    @Query("DELETE FROM qr_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM qr_items WHERE isCreated = :isCreated")
    suspend fun deleteAllByType(isCreated: Boolean)
}
