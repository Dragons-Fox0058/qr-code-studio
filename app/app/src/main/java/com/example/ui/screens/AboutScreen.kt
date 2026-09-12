package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.viewmodel.QrViewModel
import com.example.util.LocalAppStrings

@Composable
fun AboutScreen(
    viewModel: QrViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val scrollState = rememberScrollState()

    val isNotificationEnabled by viewModel.isNotificationEnabled.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()

    var showMitDialog by remember { mutableStateOf(false) }
    var showThirdPartyDialog by remember { mutableStateOf(false) }
    var showChangelogDialog by remember { mutableStateOf(false) }

    val githubUrl = "https://github.com/Dragons-Fox0058/qr-code-studio"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo & Header Box
        Box(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 12.dp)
                .size(130.dp)
                .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = MaterialTheme.colorScheme.primary, spotColor = MaterialTheme.colorScheme.primary)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF7C4DFF),
                            Color(0xFF00E5FF)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_about_logo),
                contentDescription = "QR Code Studio Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .clip(RoundedCornerShape(26.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = strings.appTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        ) {
            Text(
                text = strings.appVersion,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = strings.releaseBadge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Update & Notification Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("card_update_settings"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.updateNotificationsTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isNotificationEnabled) strings.notificationsActive else strings.notificationsOff,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.toggleNotificationSetting(enabled)
                        },
                        modifier = Modifier.testTag("switch_update_notifications")
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = DividerDefaults.color.copy(alpha = 0.5f)
                )

                Button(
                    onClick = { viewModel.checkForUpdates() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_check_updates"),
                    enabled = !isCheckingUpdate,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isCheckingUpdate) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.checkingForUpdates)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.checkUpdates)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Info & Action Items
        AboutActionItem(
            icon = Icons.Default.Code,
            title = strings.repositoryTitle,
            subtitle = strings.repositorySubtitle,
            trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                context.startActivity(intent)
            },
            testTag = "item_github_repo"
        )

        AboutActionItem(
            icon = Icons.Default.VerifiedUser,
            title = strings.projectLicenseTitle,
            subtitle = strings.projectLicenseSubtitle,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
            onClick = { showMitDialog = true },
            testTag = "item_mit_license"
        )

        AboutActionItem(
            icon = Icons.Default.Description,
            title = strings.thirdPartyLicensesTitle,
            subtitle = strings.thirdPartyLicensesSubtitle,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
            onClick = { showThirdPartyDialog = true },
            testTag = "item_third_party_licenses"
        )

        AboutActionItem(
            icon = Icons.Default.History,
            title = strings.changelogTitle,
            subtitle = strings.changelogSubtitle,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
            onClick = { showChangelogDialog = true },
            testTag = "item_changelog"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = strings.designCredits,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }

    // --- DIALOGS ---

    // 1. MIT License Dialog
    if (showMitDialog) {
        AlertDialog(
            onDismissRequest = { showMitDialog = false },
            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(text = "MIT License") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
Copyright (c) 2024-2026 Dragons-Fox0058

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showMitDialog = false }) {
                    Text(strings.close)
                }
            }
        )
    }

    // 2. Third Party Licenses Dialog
    if (showThirdPartyDialog) {
        AlertDialog(
            onDismissRequest = { showThirdPartyDialog = false },
            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(text = strings.thirdPartyLicensesTitle) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ThirdPartyLicenseEntry(
                        name = "ZXing Core (3.5.3)",
                        url = "https://github.com/zxing/zxing",
                        license = "Apache License 2.0"
                    )
                    ThirdPartyLicenseEntry(
                        name = "AndroidX CameraX (1.3.4)",
                        url = "https://developer.android.com/training/camerax",
                        license = "Apache License 2.0"
                    )
                    ThirdPartyLicenseEntry(
                        name = "AndroidX Room (2.6.1)",
                        url = "https://developer.android.com/training/data-storage/room",
                        license = "Apache License 2.0"
                    )
                    ThirdPartyLicenseEntry(
                        name = "Coil Compose (2.7.0)",
                        url = "https://coil-kt.github.io/coil",
                        license = "Apache License 2.0"
                    )
                    ThirdPartyLicenseEntry(
                        name = "Jetpack Compose & Material 3",
                        url = "https://developer.android.com/jetpack/compose",
                        license = "Apache License 2.0"
                    )
                    ThirdPartyLicenseEntry(
                        name = "Kotlin Coroutines & Flow",
                        url = "https://kotlinlang.org",
                        license = "Apache License 2.0"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThirdPartyDialog = false }) {
                    Text(strings.close)
                }
            }
        )
    }

    // 3. Changelog Dialog
    if (showChangelogDialog) {
        AlertDialog(
            onDismissRequest = { showChangelogDialog = false },
            icon = { Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(text = strings.changelogTitle) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ChangelogVersionCard(
                        version = "v1.1.0",
                        date = strings.currentVersionBadge,
                        isCurrent = true,
                        points = strings.changelogV110List
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ChangelogVersionCard(
                        version = "v1.0.0",
                        date = strings.initialVersionBadge,
                        isCurrent = false,
                        points = strings.changelogV100List
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showChangelogDialog = false }) {
                    Text(strings.close)
                }
            }
        )
    }
}

@Composable
fun AboutActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ThirdPartyLicenseEntry(
    name: String,
    url: String,
    license: String
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = license,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ChangelogVersionCard(
    version: String,
    date: String,
    isCurrent: Boolean,
    points: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = version,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            points.forEach { point ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
