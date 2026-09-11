package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_items")
data class QrItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val title: String,
    val type: String, // "URL", "TEXT", "WIFI", "PHONE", "EMAIL", "SMS", "VCARD"
    val isCreated: Boolean, // true = oluşturuldu, false = tarandı
    val timestamp: Long = System.currentTimeMillis(),
    val foregroundHex: String = "#000000",
    val backgroundHex: String = "#FFFFFF",
    val logoType: String = "NONE" // "NONE", "URL", "WIFI", "CALL", "EMAIL", "STAR", "HEART", "GALLERY"
)
