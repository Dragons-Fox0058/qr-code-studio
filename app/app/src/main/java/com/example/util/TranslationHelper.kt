package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TranslationHelper {

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    suspend fun translate(
        context: Context,
        text: String,
        targetLangCode: String
    ): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return@withContext Pair("", false)

        val target = if (targetLangCode == "system" || targetLangCode.isEmpty()) "en" else targetLangCode

        val online = isOnline(context)
        if (online) {
            try {
                val googleResult = fetchGoogleTranslate(trimmed, target)
                if (googleResult.isNotBlank()) {
                    return@withContext Pair(googleResult, true) // true = Google Translate (Online)
                }
            } catch (_: Exception) {
                // Fallback to offline translation
            }
        }

        // Offline / Local fallback translation
        val offlineResult = translateOffline(trimmed, target)
        return@withContext Pair(offlineResult, false) // false = Local/Offline
    }

    private fun fetchGoogleTranslate(text: String, targetLang: String): String {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=$targetLang&dt=t&q=$encodedText"
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")

        return try {
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonArray = JSONArray(response.toString())
                val sentencesArray = jsonArray.getJSONArray(0)
                val resultBuilder = StringBuilder()
                for (i in 0 until sentencesArray.length()) {
                    val sentence = sentencesArray.getJSONArray(i)
                    resultBuilder.append(sentence.getString(0))
                }
                resultBuilder.toString().trim()
            } else {
                ""
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun translateOffline(text: String, targetLang: String): String {
        // Offline dictionary lookup for frequent phrases, greetings, keywords and QR protocols
        val dictionary = mapOf(
            "hello" to mapOf("tr" to "Merhaba", "es" to "Hola", "de" to "Hallo", "fr" to "Bonjour", "ru" to "Привет", "ar" to "مرحبا", "zh" to "你好", "ja" to "こんにちは"),
            "welcome" to mapOf("tr" to "Hoş geldiniz", "es" to "Bienvenido", "de" to "Willkommen", "fr" to "Bienvenue", "ru" to "Добро пожаловать"),
            "website" to mapOf("tr" to "Web Sitesi", "es" to "Sitio web", "de" to "Webseite", "fr" to "Site web", "ru" to "Веб-сайт"),
            "wifi network" to mapOf("tr" to "Wi-Fi Ağı", "es" to "Red Wi-Fi", "de" to "WLAN-Netzwerk", "fr" to "Réseau Wi-Fi", "ru" to "Сеть Wi-Fi"),
            "password" to mapOf("tr" to "Şifre", "es" to "Contraseña", "de" to "Passwort", "fr" to "Mot de passe", "ru" to "Пароль"),
            "phone" to mapOf("tr" to "Telefon", "es" to "Teléfono", "de" to "Telefon", "fr" to "Téléphone", "ru" to "Телефон"),
            "email" to mapOf("tr" to "E-posta", "es" to "Correo electrónico", "de" to "E-Mail", "fr" to "E-mail", "ru" to "Электронная почта"),
            "message" to mapOf("tr" to "Mesaj", "es" to "Mensaje", "de" to "Nachricht", "fr" to "Message", "ru" to "Сообщение"),
            "scan" to mapOf("tr" to "Tara", "es" to "Escanear", "de" to "Scannen", "fr" to "Scanner", "ru" to "Сканировать"),
            "success" to mapOf("tr" to "Başarılı", "es" to "Éxito", "de" to "Erfolg", "fr" to "Succès", "ru" to "Успех"),
            "error" to mapOf("tr" to "Hata", "es" to "Error", "de" to "Fehler", "fr" to "Erreur", "ru" to "Ошибка")
        )

        val lower = text.lowercase()
        for ((key, translations) in dictionary) {
            if (lower == key || lower.contains(key)) {
                val translated = translations[targetLang] ?: translations["tr"] ?: translations["es"]
                if (translated != null) {
                    return "$translated (Yerel / Offline)"
                }
            }
        }

        // Standardized offline explanation if unknown phrase
        return when (targetLang) {
            "tr" -> "[$text] (Çevrimdışı Sözlük: İnternet bağlandığında tam Google Çevirisi sunulacaktır)"
            "es" -> "[$text] (Traducción local fuera de línea)"
            "de" -> "[$text] (Lokale Offline-Übersetzung)"
            "fr" -> "[$text] (Traduction locale hors ligne)"
            "ru" -> "[$text] (Автономный локальный перевод)"
            else -> "[$text] (Offline Local Translation)"
        }
    }
}
