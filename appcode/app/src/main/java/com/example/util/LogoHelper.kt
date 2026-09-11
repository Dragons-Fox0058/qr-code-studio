package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object LogoHelper {

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createPresetLogoBitmap(context: Context, symbol: String, colorHex: String = "#1E88E5"): Bitmap {
        val size = 128
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(colorHex)
            textSize = 64f
            textAlign = Paint.Align.CENTER
        }

        // Center text emoji / symbol
        val xPos = size / 2f
        val yPos = (size / 2f - (paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(symbol, xPos, yPos, paint)

        return bitmap
    }
}
