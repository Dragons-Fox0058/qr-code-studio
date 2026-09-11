package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FeedbackItem
import com.example.data.model.QrItem
import com.example.data.repository.QrRepository
import com.example.util.LogoHelper
import com.example.util.QrCodeGenerator
import com.example.util.QrDecoder
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ScreenTab {
    CREATE,
    SCAN,
    HISTORY
}

enum class QrType(val label: String, val iconText: String) {
    URL("Web Sitesi", "🌐"),
    TEXT("Metin", "📝"),
    WIFI("Wi-Fi", "📶"),
    PHONE("Telefon", "📞"),
    EMAIL("E-posta", "✉️"),
    SMS("SMS", "💬")
}

data class PresetLogo(
    val id: String,
    val label: String,
    val emoji: String
)

data class ColorOption(
    val name: String,
    val hex: String
)

class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = QrRepository(database.qrDao(), database.feedbackDao())

    // --- App Theme, Language & Navigation ---
    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = system, true = dark, false = light
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _currentTab = MutableStateFlow(ScreenTab.CREATE)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    fun setDarkMode(dark: Boolean?) {
        _isDarkMode.value = dark
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun selectTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    // --- Generator State ---
    val presetLogos = listOf(
        PresetLogo("none", "Yok", ""),
        PresetLogo("web", "Web", "🌐"),
        PresetLogo("wifi", "Wi-Fi", "📶"),
        PresetLogo("phone", "Telefon", "📞"),
        PresetLogo("mail", "E-posta", "✉️"),
        PresetLogo("star", "Yıldız", "⭐"),
        PresetLogo("heart", "Kalp", "❤️"),
        PresetLogo("shop", "Alışveriş", "🛍️")
    )

    val foregroundColors = listOf(
        ColorOption("Siyah", "#000000"),
        ColorOption("Gece Mavisi", "#0D47A1"),
        ColorOption("Zümrüt", "#1B5E20"),
        ColorOption("Mürdüm", "#4A148C"),
        ColorOption("Kızıl", "#B71C1C"),
        ColorOption("Koyu Camgöbeği", "#006064")
    )

    val backgroundColors = listOf(
        ColorOption("Beyaz", "#FFFFFF"),
        ColorOption("Krem", "#FFF8E1"),
        ColorOption("Açık Gri", "#F5F5F5"),
        ColorOption("Buz Mavisi", "#E1F5FE")
    )

    private val _selectedQrType = MutableStateFlow(QrType.URL)
    val selectedQrType: StateFlow<QrType> = _selectedQrType.asStateFlow()

    // Inputs
    val urlInput = MutableStateFlow("https://")
    val textInput = MutableStateFlow("")
    val wifiSsid = MutableStateFlow("")
    val wifiPassword = MutableStateFlow("")
    val wifiSecurity = MutableStateFlow("WPA") // WPA, WEP, nopass
    val phoneInput = MutableStateFlow("")
    val emailTo = MutableStateFlow("")
    val emailSubject = MutableStateFlow("")
    val emailBody = MutableStateFlow("")
    val smsPhone = MutableStateFlow("")
    val smsMessage = MutableStateFlow("")

    // Style & Logo
    val selectedFgColor = MutableStateFlow(foregroundColors[0])
    val selectedBgColor = MutableStateFlow(backgroundColors[0])
    val selectedLogo = MutableStateFlow(presetLogos[0])
    val customLogoUri = MutableStateFlow<Uri?>(null)

    private val _generatedBitmap = MutableStateFlow<Bitmap?>(null)
    val generatedBitmap: StateFlow<Bitmap?> = _generatedBitmap.asStateFlow()

    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage: SharedFlow<String> = _statusMessage.asSharedFlow()

    init {
        generateQrCode()
    }

    fun setQrType(type: QrType) {
        _selectedQrType.value = type
        generateQrCode()
    }

    fun setCustomLogo(uri: Uri?) {
        customLogoUri.value = uri
        if (uri != null) {
            selectedLogo.value = PresetLogo("custom", "Özel Logo", "🖼️")
        }
        generateQrCode()
    }

    fun selectPresetLogo(logo: PresetLogo) {
        selectedLogo.value = logo
        customLogoUri.value = null
        generateQrCode()
    }

    fun generateQrCode() {
        viewModelScope.launch(Dispatchers.Default) {
            val content = buildContent()
            if (content.isBlank()) {
                _generatedBitmap.value = null
                return@launch
            }

            var logoBitmap: Bitmap? = null
            val logo = selectedLogo.value
            val customUri = customLogoUri.value

            if (customUri != null) {
                logoBitmap = LogoHelper.loadBitmapFromUri(getApplication(), customUri)
            } else if (logo.emoji.isNotBlank()) {
                logoBitmap = LogoHelper.createPresetLogoBitmap(
                    getApplication(),
                    logo.emoji,
                    selectedFgColor.value.hex
                )
            }

            val fg = Color.parseColor(selectedFgColor.value.hex)
            val bg = Color.parseColor(selectedBgColor.value.hex)

            val bitmap = QrCodeGenerator.generateQrBitmap(
                content = content,
                size = 512,
                foregroundColor = fg,
                backgroundColor = bg,
                centerLogo = logoBitmap
            )
            _generatedBitmap.value = bitmap
        }
    }

    fun buildContent(): String {
        return when (_selectedQrType.value) {
            QrType.URL -> urlInput.value.trim()
            QrType.TEXT -> textInput.value.trim()
            QrType.WIFI -> {
                val ssid = wifiSsid.value.trim()
                val pass = wifiPassword.value
                val sec = wifiSecurity.value
                if (ssid.isEmpty()) "" else "WIFI:T:$sec;S:$ssid;P:$pass;;"
            }
            QrType.PHONE -> {
                val phone = phoneInput.value.trim()
                if (phone.isEmpty()) "" else "tel:$phone"
            }
            QrType.EMAIL -> {
                val to = emailTo.value.trim()
                val sub = emailSubject.value.trim()
                val body = emailBody.value.trim()
                if (to.isEmpty()) "" else "mailto:$to?subject=$sub&body=$body"
            }
            QrType.SMS -> {
                val phone = smsPhone.value.trim()
                val msg = smsMessage.value.trim()
                if (phone.isEmpty()) "" else "smsto:$phone:$msg"
            }
        }
    }

    fun buildTitle(): String {
        return when (_selectedQrType.value) {
            QrType.URL -> urlInput.value.ifBlank { "Web Bağlantısı" }
            QrType.TEXT -> textInput.value.take(24).ifBlank { "Metin QR Kodu" }
            QrType.WIFI -> "Wi-Fi: ${wifiSsid.value}"
            QrType.PHONE -> "Telefon: ${phoneInput.value}"
            QrType.EMAIL -> "E-posta: ${emailTo.value}"
            QrType.SMS -> "SMS: ${smsPhone.value}"
        }
    }

    fun saveCreatedQrToHistory() {
        val content = buildContent()
        val bmp = _generatedBitmap.value
        if (content.isBlank() || bmp == null) return

        viewModelScope.launch {
            val item = QrItem(
                content = content,
                title = buildTitle(),
                type = _selectedQrType.value.name,
                isCreated = true,
                foregroundHex = selectedFgColor.value.hex,
                backgroundHex = selectedBgColor.value.hex,
                logoType = selectedLogo.value.id
            )
            repository.saveQrItem(item)
            _statusMessage.emit("QR Kod geçmişe kaydedildi!")
        }
    }

    // --- Scanner State ---
    private val _scannedResult = MutableStateFlow<String?>(null)
    val scannedResult: StateFlow<String?> = _scannedResult.asStateFlow()

    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled.asStateFlow()

    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
    }

    fun onQrScanned(rawResult: String) {
        if (_scannedResult.value == rawResult) return
        _scannedResult.value = rawResult

        // Save to DB
        viewModelScope.launch {
            val type = detectQrType(rawResult)
            val title = formatScannedTitle(rawResult, type)
            val item = QrItem(
                content = rawResult,
                title = title,
                type = type,
                isCreated = false
            )
            repository.saveQrItem(item)
        }
    }

    fun clearScannedResult() {
        _scannedResult.value = null
    }

    fun scanFromGallery(uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            val bitmap = LogoHelper.loadBitmapFromUri(getApplication(), uri)
            if (bitmap != null) {
                val decoded = QrDecoder.decodeBitmap(bitmap)
                if (decoded != null) {
                    withContext(Dispatchers.Main) {
                        onQrScanned(decoded)
                    }
                } else {
                    _statusMessage.emit("Görselde okunabilir QR kod bulunamadı.")
                }
            } else {
                _statusMessage.emit("Görsel yüklenemedi.")
            }
        }
    }

    private fun detectQrType(text: String): String {
        return when {
            text.startsWith("http://", ignoreCase = true) || text.startsWith("https://", ignoreCase = true) -> "URL"
            text.startsWith("WIFI:", ignoreCase = true) -> "WIFI"
            text.startsWith("tel:", ignoreCase = true) -> "PHONE"
            text.startsWith("mailto:", ignoreCase = true) -> "EMAIL"
            text.startsWith("smsto:", ignoreCase = true) || text.startsWith("sms:", ignoreCase = true) -> "SMS"
            text.contains("BEGIN:VCARD", ignoreCase = true) -> "VCARD"
            else -> "TEXT"
        }
    }

    private fun formatScannedTitle(text: String, type: String): String {
        return when (type) {
            "URL" -> text
            "WIFI" -> {
                val match = Regex("S:([^;]+)").find(text)
                "Wi-Fi: ${match?.groupValues?.get(1) ?: "Ağ"}"
            }
            "PHONE" -> "Arama: ${text.removePrefix("tel:")}"
            "EMAIL" -> "E-posta: ${text.removePrefix("mailto:")}"
            "SMS" -> "SMS: ${text.removePrefix("smsto:").removePrefix("sms:")}"
            "VCARD" -> "Kişi Kartı (vCard)"
            else -> text.take(30)
        }
    }

    // --- History State ---
    val historyTab = MutableStateFlow(0) // 0: Scanned, 1: Created
    val historySearchQuery = MutableStateFlow("")

    val scannedItems: StateFlow<List<QrItem>> = combine(
        repository.allScannedItems,
        historySearchQuery
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter { it.content.contains(query, ignoreCase = true) || it.title.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val createdItems: StateFlow<List<QrItem>> = combine(
        repository.allCreatedItems,
        historySearchQuery
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter { it.content.contains(query, ignoreCase = true) || it.title.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteItem(item: QrItem) {
        viewModelScope.launch {
            repository.deleteQrItem(item)
            _statusMessage.emit("Kayıt silindi.")
        }
    }

    fun clearHistory(isCreated: Boolean) {
        viewModelScope.launch {
            repository.clearHistory(isCreated)
            _statusMessage.emit("Geçmiş temizlendi.")
        }
    }
}
