package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemeMode
import com.example.ui.viewmodel.QrViewModel
import com.example.util.AppLanguage
import com.example.util.LocalAppStrings

@Composable
fun SettingsScreen(
    viewModel: QrViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val useDynamicColor by viewModel.useDynamicColor.collectAsState()
    val vibrateOnScan by viewModel.vibrateOnScan.collectAsState()
    val beepOnScan by viewModel.beepOnScan.collectAsState()
    val autoCopyScan by viewModel.autoCopyScan.collectAsState()
    val autoOpenWeb by viewModel.autoOpenWeb.collectAsState()
    val continuousScan by viewModel.continuousScan.collectAsState()
    val defaultErrorCorrection by viewModel.defaultErrorCorrection.collectAsState()
    val defaultQrSize by viewModel.defaultQrSize.collectAsState()
    val defaultExportFormat by viewModel.defaultExportFormat.collectAsState()
    val translationTargetLang by viewModel.translationTargetLang.collectAsState()
    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 0: About App & Release Notes (Moved to Settings)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                SettingsClickableRow(
                    icon = Icons.Default.Info,
                    title = strings.aboutAppTitle,
                    subtitle = strings.aboutAppSubtitle,
                    onClick = { viewModel.selectTab(com.example.ui.viewmodel.ScreenTab.ABOUT) },
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Section 1: Appearance & Theme
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.ColorLens,
                title = strings.appearanceTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Dynamic Color (Material You) Toggle
                    SettingsSwitchRow(
                        icon = Icons.Default.Palette,
                        title = strings.dynamicColorTitle,
                        subtitle = strings.dynamicColorDesc,
                        checked = useDynamicColor,
                        onCheckedChange = { viewModel.setUseDynamicColor(it) }
                    )

                    // Theme Palette Selector
                    SettingsClickableRow(
                        icon = Icons.Default.ColorLens,
                        title = strings.selectThemeTitle,
                        subtitle = "${themeMode.iconEmoji} ${themeMode.displayName}",
                        onClick = { showThemePicker = true }
                    )
                }
            }
        }

        // Section 2: Language (50 Languages)
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.Language,
                title = strings.changeLanguage
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                SettingsClickableRow(
                    icon = Icons.Default.Language,
                    title = "${selectedLanguage.flag} ${selectedLanguage.displayName}",
                    subtitle = "${selectedLanguage.nativeName} (${selectedLanguage.code.uppercase()})",
                    onClick = { showLanguagePicker = true },
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        // Section 3: Scanner Preferences
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.QrCode,
                title = strings.scannerSettingsTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Vibrate on scan
                    SettingsSwitchRow(
                        icon = Icons.Default.Vibration,
                        title = strings.vibrateOnScan,
                        subtitle = strings.vibrateOnScanDesc,
                        checked = vibrateOnScan,
                        onCheckedChange = { viewModel.setVibrateOnScan(it) }
                    )

                    // Beep sound on scan
                    SettingsSwitchRow(
                        icon = Icons.Default.VolumeUp,
                        title = strings.beepOnScan,
                        subtitle = strings.beepOnScanDesc,
                        checked = beepOnScan,
                        onCheckedChange = { viewModel.setBeepOnScan(it) }
                    )

                    // Auto copy scan
                    SettingsSwitchRow(
                        icon = Icons.Default.ContentCopy,
                        title = strings.autoCopyScan,
                        subtitle = strings.autoCopyScanDesc,
                        checked = autoCopyScan,
                        onCheckedChange = { viewModel.setAutoCopyScan(it) }
                    )

                    // Auto open web links
                    SettingsSwitchRow(
                        icon = Icons.Default.OpenInBrowser,
                        title = strings.autoOpenWeb,
                        subtitle = strings.autoOpenWebDesc,
                        checked = autoOpenWeb,
                        onCheckedChange = { viewModel.setAutoOpenWeb(it) }
                    )

                    // Continuous scan mode
                    SettingsSwitchRow(
                        icon = Icons.Default.PhotoCamera,
                        title = strings.continuousScan,
                        subtitle = strings.continuousScanDesc,
                        checked = continuousScan,
                        onCheckedChange = { viewModel.setContinuousScan(it) }
                    )
                }
            }
        }

        // Section 4: Generator Defaults
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.Security,
                title = strings.qrGeneratorSettingsTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Error correction
                    Column {
                        Text(
                            text = strings.errorCorrectionLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.errorCorrectionDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("L" to "%7", "M" to "%15", "Q" to "%25", "H" to "%30").forEach { (lvl, pct) ->
                                FilterChip(
                                    selected = defaultErrorCorrection == lvl,
                                    onClick = { viewModel.setDefaultErrorCorrection(lvl) },
                                    label = { Text("$lvl ($pct)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // QR Resolution
                    Column {
                        Text(
                            text = strings.qrResolutionLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(512 to "512 px", 1024 to "1024 px", 2048 to "2048 HD").forEach { (size, label) ->
                                FilterChip(
                                    selected = defaultQrSize == size,
                                    onClick = { viewModel.setDefaultQrSize(size) },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Export Format
                    Column {
                        Text(
                            text = strings.qrExportFormatLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("PNG", "JPEG").forEach { format ->
                                FilterChip(
                                    selected = defaultExportFormat == format,
                                    onClick = { viewModel.setDefaultExportFormat(format) },
                                    label = { Text(format, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Translation Engine
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.Translate,
                title = strings.translationSettingsTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingsClickableRow(
                        icon = Icons.Default.Translate,
                        title = strings.translateTargetLangLabel,
                        subtitle = "${translationTargetLang.uppercase()} • ${strings.targetLangDesc}",
                        onClick = { showLanguagePicker = true }
                    )
                }
            }
        }

        // Section 6: Notifications & Updates
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.Notifications,
                title = strings.updateNotificationsTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.Default.Notifications,
                        title = strings.updateNotificationsTitle,
                        subtitle = if (isNotificationEnabled) strings.notificationsActive else strings.notificationsOff,
                        checked = isNotificationEnabled,
                        onCheckedChange = { viewModel.toggleNotificationSetting(it) }
                    )

                    Button(
                        onClick = { viewModel.checkForUpdates() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.checkUpdates)
                    }
                }
            }
        }

        // Section 7: Data & Storage
        item {
            SettingsCategoryHeader(
                icon = Icons.Default.Storage,
                title = strings.storageSettingsTitle
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Clear Cache
                    SettingsClickableRow(
                        icon = Icons.Default.CleaningServices,
                        title = strings.clearCacheTitle,
                        subtitle = strings.clearCacheDesc,
                        onClick = {
                            viewModel.clearAppCache()
                            Toast.makeText(context, strings.cacheCleared, Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Clear History
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearConfirm = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.clearHistory,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = strings.clearHistoryDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Language Search & Selection Dialog (50 Languages)
    if (showLanguagePicker) {
        SearchableLanguageDialog(
            currentLanguage = selectedLanguage,
            onSelect = {
                viewModel.setLanguage(it)
                viewModel.setTranslationTargetLang(if (it == AppLanguage.SYSTEM) "tr" else it.code)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false }
        )
    }

    // Theme Selection Dialog (7 Themes)
    if (showThemePicker) {
        ThemeSelectionDialog(
            currentTheme = themeMode,
            onSelect = {
                viewModel.setThemeMode(it)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }

    // Clear Confirm Dialog
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(strings.clearHistoryConfirmTitle, fontWeight = FontWeight.Bold) },
            text = { Text(strings.clearHistoryConfirmDesc) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory(false)
                        viewModel.clearHistory(true)
                        showClearConfirm = false
                        Toast.makeText(context, strings.historyCleared, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.yesClear)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun SettingsCategoryHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableLanguageDialog(
    currentLanguage: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val strings = LocalAppStrings.current

    val allLanguages = remember { AppLanguage.values().toList() }
    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) allLanguages
        else allLanguages.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.nativeName.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "${strings.selectLanguageTitle} (50+)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(strings.searchLanguageHint, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLanguages, key = { it.code }) { lang ->
                    val isSelected = lang == currentLanguage
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(lang) },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lang.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "${lang.nativeName} • ${lang.code.uppercase()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppThemeMode,
    onSelect: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val themes = remember { AppThemeMode.values().toList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.selectThemeTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(themes, key = { it.id }) { mode ->
                    val isSelected = mode == currentTheme
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelect(mode) },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = mode.iconEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = mode.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}
