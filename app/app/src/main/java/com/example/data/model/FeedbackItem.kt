package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_items")
data class FeedbackItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rating: Int, // 1 - 5
    val category: String, // "Öneri", "Hata Bildirimi", "Tasarım", "Genel"
    val comment: String,
    val email: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
