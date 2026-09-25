package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QrItem
import com.example.ui.viewmodel.QrViewModel
import com.example.util.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: QrViewModel,
    onNavigateToCreateWithText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val currentTab by viewModel.historyTab.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()

    val scannedList by viewModel.scannedItems.collectAsState()
    val createdList by viewModel.createdItems.collectAsState()

    val displayList = if (currentTab == 0) scannedList else createdList

    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.historyTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (displayList.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("button_clear_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = strings.clearHistory,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Row: Tarananlar vs Oluşturulanlar
        TabRow(
            selectedTabIndex = currentTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Tab(
                selected = currentTab == 0,
                onClick = { viewModel.historyTab.value = 0 },
                text = { Text("${strings.historyScannedTab} (${scannedList.size})") },
                modifier = Modifier.testTag("tab_history_scanned")
            )
            Tab(
                selected = currentTab == 1,
                onClick = { viewModel.historyTab.value = 1 },
                text = { Text("${strings.historyCreatedTab} (${createdList.size})") },
                modifier = Modifier.testTag("tab_history_created")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.historySearchQuery.value = it },
            placeholder = { Text(strings.searchInHistory) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.historySearchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_history_search")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // History Items List
        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentTab == 0) strings.emptyScannedHistory else strings.emptyCreatedHistory,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("QR", item.content))
                            Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            viewModel.openShareSheet(item.content)
                        },
                        onDelete = { viewModel.deleteItem(item) },
                        onOpen = {
                            if (item.type == "URL") {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.content))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, item.content, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                onNavigateToCreateWithText(item.content)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(strings.clearHistoryConfirmTitle) },
            text = { Text(strings.clearHistoryConfirmDesc) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory(currentTab == 1)
                        showClearDialog = false
                    },
                    modifier = Modifier.testTag("dialog_confirm_clear_history")
                ) {
                    Text(strings.yesClear, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    item: QrItem,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    val dateStr = remember(item.timestamp) {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    val icon = getIconForType(item.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("history_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row {
                val appStrings = LocalAppStrings.current
                IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = appStrings.copyText,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = appStrings.shareQr,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = appStrings.clearHistory,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun getIconForType(type: String): ImageVector {
    return when (type) {
        "URL" -> Icons.Default.Language
        "WIFI" -> Icons.Default.Wifi
        "PHONE" -> Icons.Default.Call
        "EMAIL" -> Icons.Default.Email
        "SMS" -> Icons.Default.Message
        else -> Icons.Default.TextFields
    }
}
