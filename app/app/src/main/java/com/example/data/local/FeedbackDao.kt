package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FeedbackItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedback_items ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(item: FeedbackItem): Long

    @Query("DELETE FROM feedback_items WHERE id = :id")
    suspend fun deleteFeedbackById(id: Long)
}
