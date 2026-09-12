package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object QrCodeGenerator {

    fun generateQrBitmap(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        centerLogo: Bitmap? = null
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = HashMap<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1
            // Use High error correction level if a logo is present to allow code to scan reliably
            hints[EncodeHintType.ERROR_CORRECTION] = if (centerLogo != null) {
                ErrorCorrectionLevel.H
            } else {
                ErrorCorrectionLevel.M
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
                }
            }

            val qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

            if (centerLogo != null) {
                overlayLogo(qrBitmap, centerLogo, backgroundColor)
            } else {
                qrBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun overlayLogo(qrBitmap: Bitmap, logo: Bitmap, bgPaddingColor: Int): Bitmap {
        val combined = Bitmap.createBitmap(qrBitmap.width, qrBitmap.height, qrBitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combined)
        canvas.drawBitmap(qrBitmap, 0f, 0f, null)

        val logoSize = qrBitmap.width / 5
        val logoLeft = (qrBitmap.width - logoSize) / 2
        val logoTop = (qrBitmap.height - logoSize) / 2
        val bgPadding = logoSize / 6

        // Draw a clean white or matching background pill/circle for the logo
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgPaddingColor
            style = Paint.Style.FILL
        }

        val bgRect = RectF(
            (logoLeft - bgPadding).toFloat(),
            (logoTop - bgPadding).toFloat(),
            (logoLeft + logoSize + bgPadding).toFloat(),
            (logoTop + logoSize + bgPadding).toFloat()
        )
        val cornerRadius = bgPadding.toFloat() * 1.5f
        canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint)

        // Draw border around logo badge
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#40000000")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, borderPaint)

        // Scale and draw logo centered
        val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
        canvas.drawBitmap(scaledLogo, logoLeft.toFloat(), logoTop.toFloat(), null)

        return combined
    }

    fun shareQrCode(context: Context, bitmap: Bitmap, title: String, content: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "qr_${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val authority = "${context.packageName}.fileprovider"
            val imageUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "$title\n$content")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "QR Kodunu Paylaş")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to text sharing
            val textIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            context.startActivity(Intent.createChooser(textIntent, "QR İçeriğini Paylaş"))
        }
    }

    fun saveQrToGallery(context: Context, bitmap: Bitmap, title: String): Boolean {
        val fileName = "QR_${System.currentTimeMillis()}.png"
        var outputStream: OutputStream? = null

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRKodlar")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = resolver.openOutputStream(imageUri)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(imagesDir, "QRKodlar").apply { mkdirs() }
                val imageFile = File(appDir, fileName)
                outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            outputStream?.flush()
            outputStream?.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
