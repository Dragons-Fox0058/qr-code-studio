package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ColorOption
import com.example.ui.viewmodel.PresetLogo
import com.example.ui.viewmodel.QrType
import com.example.ui.viewmodel.QrViewModel
import com.example.util.LocalAppStrings
import com.example.util.QrCodeGenerator

@Composable
fun GeneratorScreen(
    viewModel: QrViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val scrollState = rememberScrollState()

    val selectedType by viewModel.selectedQrType.collectAsState()
    val generatedBitmap by viewModel.generatedBitmap.collectAsState()

    // Inputs
    val url by viewModel.urlInput.collectAsState()
    val text by viewModel.textInput.collectAsState()
    val wifiSsid by viewModel.wifiSsid.collectAsState()
    val wifiPass by viewModel.wifiPassword.collectAsState()
    val phone by viewModel.phoneInput.collectAsState()
    val emailTo by viewModel.emailTo.collectAsState()
    val emailSub by viewModel.emailSubject.collectAsState()
    val emailBody by viewModel.emailBody.collectAsState()
    val smsPhone by viewModel.smsPhone.collectAsState()
    val smsMsg by viewModel.smsMessage.collectAsState()

    // Customization
    val selectedFg by viewModel.selectedFgColor.collectAsState()
    val selectedBg by viewModel.selectedBgColor.collectAsState()
    val selectedLogo by viewModel.selectedLogo.collectAsState()
    val customLogoUri by viewModel.customLogoUri.collectAsState()

    // Photo picker for custom logo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setCustomLogo(uri)
            Toast.makeText(context, strings.customLogoSelected, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.createQrTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Type selection chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QrType.entries.forEach { type ->
                val typeLabel = when (type) {
                    QrType.URL -> strings.qrTypeUrl
                    QrType.TEXT -> strings.qrTypeText
                    QrType.WIFI -> strings.qrTypeWifi
                    QrType.PHONE -> strings.qrTypePhone
                    QrType.EMAIL -> strings.qrTypeEmail
                    QrType.SMS -> strings.qrTypeSms
                }
                FilterChip(
                    selected = selectedType == type,
                    onClick = { viewModel.setQrType(type) },
                    label = { Text("${type.iconText} $typeLabel") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("qr_type_chip_${type.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Form Fields based on chosen type
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedType) {
                    QrType.URL -> {
                        OutlinedTextField(
                            value = url,
                            onValueChange = {
                                viewModel.urlInput.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.websiteUrlLabel) },
                            placeholder = { Text("https://example.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_url")
                        )
                    }
                    QrType.TEXT -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                viewModel.textInput.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.textContentLabel) },
                            placeholder = { Text("...") },
                            minLines = 3,
                            maxLines = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_text")
                        )
                    }
                    QrType.WIFI -> {
                        OutlinedTextField(
                            value = wifiSsid,
                            onValueChange = {
                                viewModel.wifiSsid.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.wifiSsidLabel) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_wifi_ssid")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = wifiPass,
                            onValueChange = {
                                viewModel.wifiPassword.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.wifiPasswordLabel) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_wifi_password")
                        )
                    }
                    QrType.PHONE -> {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                viewModel.phoneInput.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.phoneNumberLabel) },
                            placeholder = { Text("+1 555 123 4567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_phone")
                        )
                    }
                    QrType.EMAIL -> {
                        OutlinedTextField(
                            value = emailTo,
                            onValueChange = {
                                viewModel.emailTo.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.emailToLabel) },
                            placeholder = { Text("example@mail.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth().testTag("input_email")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = emailSub,
                            onValueChange = {
                                viewModel.emailSubject.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.emailSubjectLabel) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = emailBody,
                            onValueChange = {
                                viewModel.emailBody.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.emailBodyLabel) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    QrType.SMS -> {
                        OutlinedTextField(
                            value = smsPhone,
                            onValueChange = {
                                viewModel.smsPhone.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.smsPhoneLabel) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("input_sms_phone")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = smsMsg,
                            onValueChange = {
                                viewModel.smsMessage.value = it
                                viewModel.generateQrCode()
                            },
                            label = { Text(strings.smsMessageLabel) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth().testTag("input_sms_message")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customization: Logo & Colors
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.appearanceAndLogo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Foreground Colors
                Text(
                    text = strings.foregroundColor,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    viewModel.foregroundColors.forEach { colorOpt ->
                        val color = Color(android.graphics.Color.parseColor(colorOpt.hex))
                        val isSelected = selectedFg.hex == colorOpt.hex
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    viewModel.selectedFgColor.value = colorOpt
                                    viewModel.generateQrCode()
                                }
                                .testTag("color_fg_${colorOpt.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (colorOpt.hex == "#FFFFFF") Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Logo Selection
                Text(
                    text = strings.centerLogo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Gallery Photo Button
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("button_pick_logo_gallery")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = strings.selectFromGallery,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.selectFromGallery, fontSize = 12.sp)
                    }

                    viewModel.presetLogos.forEach { logo ->
                        val isSelected = selectedLogo.id == logo.id && customLogoUri == null
                        val logoLabel = when (logo.id) {
                            "none" -> strings.logoNone
                            "web" -> strings.logoWeb
                            "wifi" -> strings.logoWifi
                            "phone" -> strings.logoPhone
                            "mail" -> strings.logoMail
                            "star" -> strings.logoStar
                            "heart" -> strings.logoHeart
                            "shop" -> strings.logoShop
                            else -> logo.label
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectPresetLogo(logo) },
                            label = { Text(if (logo.emoji.isEmpty()) logoLabel else "${logo.emoji} $logoLabel") },
                            modifier = Modifier.testTag("logo_chip_${logo.id}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live QR Code Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (generatedBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(android.graphics.Color.parseColor(selectedBg.hex)))
                            .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = strings.createQrTitle,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("generated_qr_image")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons: Share, Save to Gallery, Save to History
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                QrCodeGenerator.shareQrCode(
                                    context = context,
                                    bitmap = bmp,
                                    title = viewModel.buildTitle(),
                                    content = viewModel.buildContent()
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_share_qr"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.shareQr)
                    }

                    OutlinedButton(
                        onClick = {
                            generatedBitmap?.let { bmp ->
                                val success = QrCodeGenerator.saveQrToGallery(
                                    context = context,
                                    bitmap = bmp,
                                    title = viewModel.buildTitle()
                                )
                                if (success) {
                                    Toast.makeText(context, strings.qrSavedSuccess, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_save_gallery")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.downloadQr)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.saveCreatedQrToHistory()
                        Toast.makeText(context, strings.savedToHistory, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_save_history")
                ) {
                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.saveToHistory)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
