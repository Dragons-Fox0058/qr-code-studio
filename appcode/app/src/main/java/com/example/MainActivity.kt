package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.QrType
import com.example.ui.viewmodel.QrViewModel
import com.example.ui.viewmodel.ScreenTab
import com.example.util.AppLanguage
import com.example.util.LanguageManager
import com.example.util.LocalAppStrings
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: QrViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkModeConfig by viewModel.isDarkMode.collectAsState()
            val systemInDark = isSystemInDarkTheme()
            val effectiveDark = isDarkModeConfig ?: systemInDark

            val currentTab by viewModel.currentTab.collectAsState()
            val selectedLanguage by viewModel.selectedLanguage.collectAsState()
            val strings = remember(selectedLanguage) {
                LanguageManager.getStrings(selectedLanguage.code)
            }

            var languageMenuExpanded by remember { mutableStateOf(false) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.statusMessage.collectLatest { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            CompositionLocalProvider(LocalAppStrings provides strings) {
                MyApplicationTheme(darkTheme = effectiveDark) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = strings.appTitle,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                actions = {
                                    // Language selection button with dropdown menu
                                    Box {
                                        IconButton(
                                            onClick = { languageMenuExpanded = true },
                                            modifier = Modifier.testTag("button_select_language")
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Language,
                                                    contentDescription = strings.changeLanguage
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = languageMenuExpanded,
                                            onDismissRequest = { languageMenuExpanded = false }
                                        ) {
                                            AppLanguage.entries.forEach { lang ->
                                                val isSelected = selectedLanguage == lang
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "${lang.flag} ${lang.nativeName}",
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                                fontSize = 14.sp
                                                            )
                                                            if (isSelected) {
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.primary
                                                                )
                                                            }
                                                        }
                                                    },
                                                    onClick = {
                                                        viewModel.setLanguage(lang)
                                                        languageMenuExpanded = false
                                                    },
                                                    modifier = Modifier.testTag("lang_item_${lang.code}")
                                                )
                                            }
                                        }
                                    }

                                    // Dark/Light theme toggle button next to language button
                                    IconButton(
                                        onClick = {
                                            viewModel.setDarkMode(!effectiveDark)
                                        },
                                        modifier = Modifier.testTag("button_toggle_theme")
                                    ) {
                                        Icon(
                                            imageVector = if (effectiveDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = strings.changeTheme
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors()
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.CREATE,
                                    onClick = { viewModel.selectTab(ScreenTab.CREATE) },
                                    icon = { Icon(Icons.Default.QrCode, contentDescription = strings.tabCreate) },
                                    label = { Text(strings.tabCreate) },
                                    modifier = Modifier.testTag("nav_item_create")
                                )
                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.SCAN,
                                    onClick = { viewModel.selectTab(ScreenTab.SCAN) },
                                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = strings.tabScan) },
                                    label = { Text(strings.tabScan) },
                                    modifier = Modifier.testTag("nav_item_scan")
                                )
                                NavigationBarItem(
                                    selected = currentTab == ScreenTab.HISTORY,
                                    onClick = { viewModel.selectTab(ScreenTab.HISTORY) },
                                    icon = { Icon(Icons.Default.History, contentDescription = strings.tabHistory) },
                                    label = { Text(strings.tabHistory) },
                                    modifier = Modifier.testTag("nav_item_history")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
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
                            }
                        }
                    }
                }
            }
        }
    }
}
