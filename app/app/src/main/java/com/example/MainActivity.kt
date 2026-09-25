package com.example

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomShareSheet
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QrType
import com.example.ui.viewmodel.QrViewModel
import com.example.ui.viewmodel.ScreenTab
import com.example.util.LanguageManager
import com.example.util.LocalAppStrings
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: QrViewModel by viewModels()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingShareIntent(intent)
    }

    private fun handleIncomingShareIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == Intent.ACTION_SEND) {
            val type = intent.type ?: ""
            if (type.startsWith("text/")) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    viewModel.setQrType(QrType.TEXT)
                    viewModel.textInput.value = sharedText
                    viewModel.generateQrCode()
                    viewModel.selectTab(ScreenTab.CREATE)
                    Toast.makeText(this, "Paylaşılan metin QR oluşturucuya aktarıldı!", Toast.LENGTH_SHORT).show()
                }
            } else if (type.startsWith("image/")) {
                @Suppress("DEPRECATION")
                val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                if (imageUri != null) {
                    viewModel.scanFromGallery(imageUri)
                    viewModel.selectTab(ScreenTab.SCAN)
                    Toast.makeText(this, "Paylaşılan görsel taranıyor...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle initial intent if app was opened via system share sheet
        handleIncomingShareIntent(intent)

        setContent {
            val isDarkModeConfig by viewModel.isDarkMode.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()
            val useDynamicColor by viewModel.useDynamicColor.collectAsState()
            val systemInDark = isSystemInDarkTheme()
            val effectiveDark = isDarkModeConfig ?: systemInDark

            val currentTab by viewModel.currentTab.collectAsState()
            val selectedLanguage by viewModel.selectedLanguage.collectAsState()
            val strings = remember(selectedLanguage) {
                LanguageManager.getStrings(selectedLanguage.code)
            }

            val context = LocalContext.current

            val showShareSheet by viewModel.showShareSheet.collectAsState()
            val shareTargetText by viewModel.shareTargetText.collectAsState()
            val shareTargetBitmap by viewModel.shareTargetBitmap.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.statusMessage.collectLatest { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            // Notification Prompt Launcher for Android 13+
            val showNotificationPrompt by viewModel.showNotificationPrompt.collectAsState()
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                viewModel.dismissNotificationPrompt(isGranted)
            }

            if (showNotificationPrompt) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissNotificationPrompt(false) },
                    icon = {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    title = { Text(strings.updateNotificationsTitle) },
                    text = { Text(strings.updateNotificationsDesc) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.dismissNotificationPrompt(true)
                                }
                            },
                            modifier = Modifier.testTag("dialog_allow_notifications_btn")
                        ) {
                            Text(strings.allowNotifications)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.dismissNotificationPrompt(false) },
                            modifier = Modifier.testTag("dialog_dismiss_notifications_btn")
                        ) {
                            Text(strings.notNow)
                        }
                    }
                )
            }

            CompositionLocalProvider(LocalAppStrings provides strings) {
                MyApplicationTheme(
                    themeMode = themeMode,
                    useDynamicColor = useDynamicColor,
                    darkTheme = effectiveDark
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWideScreen = maxWidth >= 600.dp

                        if (isWideScreen) {
                            // Foldable / Tablet Adaptive Layout with NavigationRail
                            Row(modifier = Modifier.fillMaxSize()) {
                                NavigationRail(
                                    modifier = Modifier.fillMaxHeight(),
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    header = {
                                        Text(
                                            text = "QR",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 16.dp)
                                        )
                                    }
                                ) {
                                    NavigationRailItem(
                                        selected = currentTab == ScreenTab.CREATE,
                                        onClick = { viewModel.selectTab(ScreenTab.CREATE) },
                                        icon = { Icon(Icons.Default.QrCode, contentDescription = strings.tabCreate) },
                                        label = { Text(strings.tabCreate) }
                                    )
                                    NavigationRailItem(
                                        selected = currentTab == ScreenTab.SCAN,
                                        onClick = { viewModel.selectTab(ScreenTab.SCAN) },
                                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = strings.tabScan) },
                                        label = { Text(strings.tabScan) }
                                    )
                                    NavigationRailItem(
                                        selected = currentTab == ScreenTab.HISTORY,
                                        onClick = { viewModel.selectTab(ScreenTab.HISTORY) },
                                        icon = { Icon(Icons.Default.History, contentDescription = strings.tabHistory) },
                                        label = { Text(strings.tabHistory) }
                                    )
                                    NavigationRailItem(
                                        selected = currentTab == ScreenTab.SETTINGS || currentTab == ScreenTab.ABOUT,
                                        onClick = { viewModel.selectTab(ScreenTab.SETTINGS) },
                                        icon = { Icon(Icons.Default.Settings, contentDescription = strings.tabSettings) },
                                        label = { Text(strings.tabSettings) }
                                    )
                                }

                                Scaffold(
                                    modifier = Modifier.weight(1f),
                                    topBar = {
                                        MainTopBar(
                                            strings = strings,
                                            currentTab = currentTab,
                                            onBackToSettings = { viewModel.selectTab(ScreenTab.SETTINGS) }
                                        )
                                    }
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        ScreenContent(
                                            currentTab = currentTab,
                                            viewModel = viewModel
                                        )
                                    }
                                }
                            }
                        } else {
                            // Standard Phone Layout with Bottom Navigation Bar
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    MainTopBar(
                                        strings = strings,
                                        currentTab = currentTab,
                                        onBackToSettings = { viewModel.selectTab(ScreenTab.SETTINGS) }
                                    )
                                },
                                bottomBar = {
                                    NavigationBar(
                                        modifier = Modifier.testTag("bottom_nav_bar"),
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ) {
                                        NavigationBarItem(
                                            selected = currentTab == ScreenTab.CREATE,
                                            onClick = { viewModel.selectTab(ScreenTab.CREATE) },
                                            icon = { Icon(Icons.Default.QrCode, contentDescription = strings.tabCreate) },
                                            label = { Text(strings.tabCreate, maxLines = 1, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("nav_item_create")
                                        )
                                        NavigationBarItem(
                                            selected = currentTab == ScreenTab.SCAN,
                                            onClick = { viewModel.selectTab(ScreenTab.SCAN) },
                                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = strings.tabScan) },
                                            label = { Text(strings.tabScan, maxLines = 1, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("nav_item_scan")
                                        )
                                        NavigationBarItem(
                                            selected = currentTab == ScreenTab.HISTORY,
                                            onClick = { viewModel.selectTab(ScreenTab.HISTORY) },
                                            icon = { Icon(Icons.Default.History, contentDescription = strings.tabHistory) },
                                            label = { Text(strings.tabHistory, maxLines = 1, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("nav_item_history")
                                        )
                                        NavigationBarItem(
                                            selected = currentTab == ScreenTab.SETTINGS || currentTab == ScreenTab.ABOUT,
                                            onClick = { viewModel.selectTab(ScreenTab.SETTINGS) },
                                            icon = { Icon(Icons.Default.Settings, contentDescription = strings.tabSettings) },
                                            label = { Text(strings.tabSettings, maxLines = 1, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("nav_item_settings")
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                    ) {
                                    ScreenContent(
                                        currentTab = currentTab,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }

                        // Custom Share Sheet Modal
                        if (showShareSheet) {
                            CustomShareSheet(
                                textToShare = shareTargetText,
                                bitmapToShare = shareTargetBitmap,
                                onDismiss = { viewModel.closeShareSheet() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    strings: com.example.util.TranslationStrings,
    currentTab: ScreenTab,
    onBackToSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (currentTab == ScreenTab.ABOUT) strings.aboutTitle else strings.appTitle,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (currentTab == ScreenTab.ABOUT) {
                IconButton(onClick = onBackToSettings) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.backToSettings
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ScreenContent(
    currentTab: ScreenTab,
    viewModel: QrViewModel
) {
    when (currentTab) {
        ScreenTab.CREATE -> {
            GeneratorScreen(viewModel = viewModel)
        }
        ScreenTab.SCAN -> {
            ScannerScreen(
                viewModel = viewModel,
                onNavigateToCreateWithText = { text ->
                    viewModel.setQrType(QrType.TEXT)
                    viewModel.textInput.value = text
                    viewModel.generateQrCode()
                    viewModel.selectTab(ScreenTab.CREATE)
                }
            )
        }
        ScreenTab.HISTORY -> {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateToCreateWithText = { text ->
                    viewModel.setQrType(QrType.TEXT)
                    viewModel.textInput.value = text
                    viewModel.generateQrCode()
                    viewModel.selectTab(ScreenTab.CREATE)
                }
            )
        }
        ScreenTab.SETTINGS -> {
            SettingsScreen(viewModel = viewModel)
        }
        ScreenTab.ABOUT -> {
            AboutScreen(
                viewModel = viewModel,
                onBack = { viewModel.selectTab(ScreenTab.SETTINGS) }
            )
        }
    }
}
