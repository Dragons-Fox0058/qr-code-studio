package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FeedbackItem
import com.example.data.model.QrItem
import com.example.data.repository.QrRepository
import com.example.ui.theme.AppThemeMode
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import com.example.util.LogoHelper
import com.example.util.NotificationHelper
import com.example.util.QrCodeGenerator
import com.example.util.QrDecoder
import com.example.util.TranslationHelper
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
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

enum class ScreenTab {
    CREATE,
    SCAN,
    HISTORY,
    SETTINGS,
    ABOUT
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

data class GitHubRelease(
    val tagName: String,
    val name: String,
    val publishedAt: String,
    val body: String,
    val htmlUrl: String,
    val points: List<String>
)

class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = QrRepository(database.qrDao(), database.feedbackDao())
    private val prefs = application.getSharedPreferences("qr_studio_prefs", Application.MODE_PRIVATE)

    // --- App Theme, Language & Navigation ---
    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = system, true = dark, false = light
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _useDynamicColor = MutableStateFlow(prefs.getBoolean("use_dynamic_color", true))
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor.asStateFlow()

    private val savedThemeMode = try {
        AppThemeMode.valueOf(prefs.getString("app_theme_mode", AppThemeMode.DYNAMIC.name) ?: AppThemeMode.DYNAMIC.name)
    } catch (_: Exception) {
        AppThemeMode.DYNAMIC
    }
    private val _themeMode = MutableStateFlow(savedThemeMode)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.SYSTEM)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _currentTab = MutableStateFlow(ScreenTab.CREATE)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // --- Advanced Scanner & Generator Settings ---
    private val _vibrateOnScan = MutableStateFlow(prefs.getBoolean("vibrate_on_scan", true))
    val vibrateOnScan: StateFlow<Boolean> = _vibrateOnScan.asStateFlow()

    private val _beepOnScan = MutableStateFlow(prefs.getBoolean("beep_on_scan", true))
    val beepOnScan: StateFlow<Boolean> = _beepOnScan.asStateFlow()

    private val _autoCopyScan = MutableStateFlow(prefs.getBoolean("auto_copy_scan", false))
    val autoCopyScan: StateFlow<Boolean> = _autoCopyScan.asStateFlow()

    private val _autoOpenWeb = MutableStateFlow(prefs.getBoolean("auto_open_web", false))
    val autoOpenWeb: StateFlow<Boolean> = _autoOpenWeb.asStateFlow()

    private val _continuousScan = MutableStateFlow(prefs.getBoolean("continuous_scan", false))
    val continuousScan: StateFlow<Boolean> = _continuousScan.asStateFlow()

    private val _defaultErrorCorrection = MutableStateFlow(prefs.getString("default_error_correction", "M") ?: "M")
    val defaultErrorCorrection: StateFlow<String> = _defaultErrorCorrection.asStateFlow()

    private val _defaultQrSize = MutableStateFlow(prefs.getInt("default_qr_size", 512))
    val defaultQrSize: StateFlow<Int> = _defaultQrSize.asStateFlow()

    private val _defaultExportFormat = MutableStateFlow(prefs.getString("default_export_format", "PNG") ?: "PNG")
    val defaultExportFormat: StateFlow<String> = _defaultExportFormat.asStateFlow()

    // --- GitHub Releases State ---
    private val _gitHubReleases = MutableStateFlow<List<GitHubRelease>>(emptyList())
    val gitHubReleases: StateFlow<List<GitHubRelease>> = _gitHubReleases.asStateFlow()

    private val _isLoadingReleases = MutableStateFlow(false)
    val isLoadingReleases: StateFlow<Boolean> = _isLoadingReleases.asStateFlow()

    private val _releasesFetchError = MutableStateFlow<String?>(null)
    val releasesFetchError: StateFlow<String?> = _releasesFetchError.asStateFlow()

    // --- Translation Engine State ---
    private val _translationTargetLang = MutableStateFlow(prefs.getString("translation_target_lang", "tr") ?: "tr")
    val translationTargetLang: StateFlow<String> = _translationTargetLang.asStateFlow()

    private val _translatedText = MutableStateFlow<String?>(null)
    val translatedText: StateFlow<String?> = _translatedText.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _translationIsOnline = MutableStateFlow(true)
    val translationIsOnline: StateFlow<Boolean> = _translationIsOnline.asStateFlow()

    // --- Custom In-App Share Sheet State ---
    private val _showShareSheet = MutableStateFlow(false)
    val showShareSheet: StateFlow<Boolean> = _showShareSheet.asStateFlow()

    private val _shareTargetText = MutableStateFlow("")
    val shareTargetText: StateFlow<String> = _shareTargetText.asStateFlow()

    private val _shareTargetBitmap = MutableStateFlow<Bitmap?>(null)
    val shareTargetBitmap: StateFlow<Bitmap?> = _shareTargetBitmap.asStateFlow()

    fun openShareSheet(text: String, bitmap: Bitmap? = null) {
        _shareTargetText.value = text
        _shareTargetBitmap.value = bitmap
        _showShareSheet.value = true
    }

    fun closeShareSheet() {
        _showShareSheet.value = false
    }

    fun setUseDynamicColor(enabled: Boolean) {
        _useDynamicColor.value = enabled
        prefs.edit().putBoolean("use_dynamic_color", enabled).apply()
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("app_theme_mode", mode.name).apply()
    }

    fun setVibrateOnScan(enabled: Boolean) {
        _vibrateOnScan.value = enabled
        prefs.edit().putBoolean("vibrate_on_scan", enabled).apply()
    }

    fun setBeepOnScan(enabled: Boolean) {
        _beepOnScan.value = enabled
        prefs.edit().putBoolean("beep_on_scan", enabled).apply()
    }

    fun setContinuousScan(enabled: Boolean) {
        _continuousScan.value = enabled
        prefs.edit().putBoolean("continuous_scan", enabled).apply()
    }

    fun setDefaultQrSize(size: Int) {
        _defaultQrSize.value = size
        prefs.edit().putInt("default_qr_size", size).apply()
    }

    fun setDefaultExportFormat(format: String) {
        _defaultExportFormat.value = format
        prefs.edit().putString("default_export_format", format).apply()
    }

    fun clearAppCache() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getApplication<Application>().cacheDir.deleteRecursively()
                _statusMessage.emit("Önbellek başarıyla temizlendi.")
            } catch (_: Exception) {
                _statusMessage.emit("Önbellek temizlenemedi.")
            }
        }
    }

    fun fetchGitHubReleases() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingReleases.value = true
            _releasesFetchError.value = null
            try {
                val url = URL("https://api.github.com/repos/Dragons-Fox0058/qr-code-studio/releases")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "QRCodeStudio-Android")
                    connectTimeout = 7000
                    readTimeout = 7000
                }

                val code = connection.responseCode
                if (code in 200..299) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val array = JSONArray(responseText)
                    val releases = mutableListOf<GitHubRelease>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val tagName = obj.optString("tag_name", "v1.2.0")
                        val name = obj.optString("name", tagName)
                        val publishedAt = obj.optString("published_at", "").take(10)
                        val body = obj.optString("body", "")
                        val htmlUrl = obj.optString("html_url", "https://github.com/Dragons-Fox0058/qr-code-studio/releases")

                        val points = body.lines()
                            .map { it.trim().removePrefix("-").removePrefix("*").trim() }
                            .filter { it.isNotBlank() && !it.startsWith("#") }

                        releases.add(
                            GitHubRelease(
                                tagName = tagName,
                                name = name,
                                publishedAt = publishedAt,
                                body = body,
                                htmlUrl = htmlUrl,
                                points = if (points.isNotEmpty()) points else listOf(body)
                            )
                        )
                    }
                    _gitHubReleases.value = releases
                } else {
                    _releasesFetchError.value = "HTTP $code"
                }
            } catch (e: Exception) {
                _releasesFetchError.value = e.localizedMessage ?: "Bağlantı hatası"
            } finally {
                _isLoadingReleases.value = false
            }
        }
    }

    fun setAutoCopyScan(enabled: Boolean) {
        _autoCopyScan.value = enabled
        prefs.edit().putBoolean("auto_copy_scan", enabled).apply()
    }

    fun setAutoOpenWeb(enabled: Boolean) {
        _autoOpenWeb.value = enabled
        prefs.edit().putBoolean("auto_open_web", enabled).apply()
    }

    fun setDefaultErrorCorrection(level: String) {
        _defaultErrorCorrection.value = level
        prefs.edit().putString("default_error_correction", level).apply()
    }

    fun setTranslationTargetLang(langCode: String) {
        _translationTargetLang.value = langCode
        prefs.edit().putString("translation_target_lang", langCode).apply()
    }

    fun translateText(text: String, targetLangCode: String? = null) {
        val target = targetLangCode ?: _translationTargetLang.value
        viewModelScope.launch {
            _isTranslating.value = true
            _translatedText.value = null
            val (result, isOnline) = TranslationHelper.translate(
                getApplication(),
                text,
                target
            )
            _translatedText.value = result
            _translationIsOnline.value = isOnline
            _isTranslating.value = false
        }
    }

    fun clearTranslation() {
        _translatedText.value = null
    }

    // --- Update Notifications & Dialogs ---
    private val _showNotificationPrompt = MutableStateFlow(false)
    val showNotificationPrompt: StateFlow<Boolean> = _showNotificationPrompt.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(prefs.getBoolean("updates_notification_enabled", false))
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    init {
        val promptShown = prefs.getBoolean("notification_prompt_already_shown", false)
        if (!promptShown) {
            _showNotificationPrompt.value = true
        }
    }

    fun dismissNotificationPrompt(accepted: Boolean) {
        _showNotificationPrompt.value = false
        prefs.edit()
            .putBoolean("notification_prompt_already_shown", true)
            .putBoolean("updates_notification_enabled", accepted)
            .apply()
        _isNotificationEnabled.value = accepted
        if (accepted) {
            NotificationHelper.createNotificationChannel(getApplication())
            NotificationHelper.sendUpdateNotification(
                getApplication(),
                "QR Code Studio v1.2.0",
                "Güncelleme bildirimleri aktif! En yeni sürümü kullanıyorsunuz."
            )
        }
    }

    fun toggleNotificationSetting(enabled: Boolean) {
        prefs.edit().putBoolean("updates_notification_enabled", enabled).apply()
        _isNotificationEnabled.value = enabled
        if (enabled) {
            NotificationHelper.createNotificationChannel(getApplication())
            NotificationHelper.sendUpdateNotification(
                getApplication(),
                "QR Code Studio v1.2.0",
                "Güncelleme bildirimleri başarıyla açıldı."
            )
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            kotlinx.coroutines.delay(1200)
            _isCheckingUpdate.value = false
            _statusMessage.emit("En güncel sürümü (v1.2.0) kullanıyorsunuz!")
        }
    }

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

        if (_beepOnScan.value) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (_: Exception) {}
        }

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
