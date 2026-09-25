package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.viewmodel.QrViewModel
import com.example.util.LocalAppStrings
import com.example.util.QrImageAnalyzer
import java.util.concurrent.Executors

@Composable
fun ScannerScreen(
    viewModel: QrViewModel,
    onNavigateToCreateWithText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannedResult by viewModel.scannedResult.collectAsState()
    val flashEnabled by viewModel.flashEnabled.collectAsState()
    val vibrateOnScan by viewModel.vibrateOnScan.collectAsState()
    val autoCopyScan by viewModel.autoCopyScan.collectAsState()
    val autoOpenWeb by viewModel.autoOpenWeb.collectAsState()

    var cameraRef by remember { mutableStateOf<Camera?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, strings.cameraPermissionDesc, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Gallery photo scan launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.scanFromGallery(uri)
        }
    }

    // Trigger haptic vibration, auto-copy, auto-open on new scan
    LaunchedEffect(scannedResult) {
        val result = scannedResult
        if (result != null) {
            if (vibrateOnScan) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                        vibratorManager?.defaultVibrator?.vibrate(
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        vibrator?.vibrate(100)
                    }
                } catch (_: Exception) {}
            }

            if (autoCopyScan) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("QR Kod", result)
                clipboard.setPrimaryClip(clip)
            }

            if (autoOpenWeb && (result.startsWith("http://", ignoreCase = true) || result.startsWith("https://", ignoreCase = true))) {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                    context.startActivity(browserIntent)
                } catch (_: Exception) {}
            }
        }
    }

    // Camera executor and lifecycle cleanup
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (_: Exception) {}
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analyzer = QrImageAnalyzer { result ->
                                viewModel.onQrScanned(result)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor, analyzer)
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                            cameraRef = cam
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Sync flash toggle with camera
            LaunchedEffect(flashEnabled, cameraRef) {
                cameraRef?.cameraControl?.enableTorch(flashEnabled)
            }

            // Animated Laser Scanner Overlay
            ScannerOverlay()
        } else {
            // Permission Denied / Prompt UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = strings.cameraPermissionTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.cameraPermissionDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.testTag("button_request_permission")
                ) {
                    Text(strings.grantPermission)
                }
            }
        }

        // Top Header & Action Controls (Flash & Gallery)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Text(
                    text = strings.scanQrTitle,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Flash Toggle Button
                FilledIconButton(
                    onClick = { viewModel.toggleFlash() },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (flashEnabled) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("button_toggle_flash")
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (flashEnabled) strings.flashOff else strings.flashOn,
                        tint = Color.White
                    )
                }

                // Gallery Scan Button
                FilledIconButton(
                    onClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("button_scan_gallery")
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = strings.scanFromGallery,
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom instruction banner
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Text(
                    text = strings.cameraAimHint,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        // Result Dialog when code is detected
        if (scannedResult != null) {
            ScannedResultDialog(
                result = scannedResult!!,
                viewModel = viewModel,
                onDismiss = {
                    viewModel.clearTranslation()
                    viewModel.clearScannedResult()
                },
                onNavigateToCreate = { text ->
                    viewModel.clearTranslation()
                    viewModel.clearScannedResult()
                    onNavigateToCreateWithText(text)
                }
            )
        }
    }
}

@Composable
fun ScannerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val frameSize = size.minDimension * 0.70f
        val left = (size.width - frameSize) / 2f
        val top = (size.height - frameSize) / 2f

        // Dark dim around frame
        drawRect(
            color = Color(0x77000000),
            topLeft = Offset.Zero,
            size = size
        )

        // Clear square frame
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(20f, 20f),
            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
        )

        // Corner borders
        val cornerLength = 40f
        val strokeWidth = 8f
        val primaryColor = Color(0xFF38BDF8)

        // Top Left
        drawLine(primaryColor, Offset(left, top), Offset(left + cornerLength, top), strokeWidth)
        drawLine(primaryColor, Offset(left, top), Offset(left, top + cornerLength), strokeWidth)

        // Top Right
        drawLine(primaryColor, Offset(left + frameSize, top), Offset(left + frameSize - cornerLength, top), strokeWidth)
        drawLine(primaryColor, Offset(left + frameSize, top), Offset(left + frameSize, top + cornerLength), strokeWidth)

        // Bottom Left
        drawLine(primaryColor, Offset(left, top + frameSize), Offset(left + cornerLength, top + frameSize), strokeWidth)
        drawLine(primaryColor, Offset(left, top + frameSize), Offset(left, top + frameSize - cornerLength), strokeWidth)

        // Bottom Right
        drawLine(primaryColor, Offset(left + frameSize, top + frameSize), Offset(left + frameSize - cornerLength, top + frameSize), strokeWidth)
        drawLine(primaryColor, Offset(left + frameSize, top + frameSize), Offset(left + frameSize, top + frameSize - cornerLength), strokeWidth)

        // Laser scan line
        val laserY = top + (frameSize * laserPosition)
        drawLine(
            color = Color(0xFF00E5FF),
            start = Offset(left + 10f, laserY),
            end = Offset(left + frameSize - 10f, laserY),
            strokeWidth = 4f
        )
    }
}

@Composable
fun ScannedResultDialog(
    result: String,
    viewModel: QrViewModel,
    onDismiss: () -> Unit,
    onNavigateToCreate: (String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val isUrl = result.startsWith("http://", ignoreCase = true) || result.startsWith("https://", ignoreCase = true)

    val translatedText by viewModel.translatedText.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val isOnline by viewModel.translationIsOnline.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.scannedResultTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Scanned Content Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions row: Copy, Custom Share, Translate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("QR Kod", result)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_button_copy"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.copyText, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.openShareSheet(result)
                        },
                        modifier = Modifier.weight(1f).testTag("dialog_button_share"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.shareQr, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.translateText(result)
                        },
                        enabled = !isTranslating,
                        modifier = Modifier.weight(1f).testTag("dialog_button_translate"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.translateAction, fontSize = 11.sp)
                    }
                }

                // Translation Result Card
                if (isTranslating) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.translating, style = MaterialTheme.typography.bodySmall)
                    }
                } else if (translatedText != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOnline) strings.translatedOnlineGoogle else strings.translatedOfflineLocal,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Çeviri", translatedText ?: "")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, strings.copiedToClipboard, Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.copyText, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = translatedText ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isUrl) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_button_open_browser")
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.openInBrowser)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onNavigateToCreate(result) },
                modifier = Modifier.testTag("dialog_button_recreate")
            ) {
                Text(strings.createSimilar)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_button_close")
            ) {
                Text(strings.close)
            }
        }
    )
}
