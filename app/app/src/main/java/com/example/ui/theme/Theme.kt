package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val id: String, val displayName: String, val iconEmoji: String) {
    DYNAMIC("dynamic", "Material You (Sistem/Duvar Kağıdı)", "🎨"),
    AMOLED_DARK("amoled", "Saf AMOLED Siyahı", "🖤"),
    OCEAN_BLUE("ocean", "Okyanus Mavisi", "🌊"),
    EMERALD_GREEN("emerald", "Zümrüt Yeşili", "🌿"),
    CYBERPUNK_PURPLE("cyberpunk", "Siberpunk Mor", "💜"),
    SUNSET_AMBER("sunset", "Gün Batımı Amber", "🌅"),
    ROSE_PASTEL("rose", "Pastel Gül", "🌸")
}

private val DefaultDarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SecondaryCyanDark,
    onSecondary = Color(0xFF082F49),
    tertiary = AccentTeal,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = SecondaryCyan,
    onSecondary = Color.White,
    tertiary = AccentTeal,
    background = BackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569)
)

private val AmoledDarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = SecondaryCyanDark,
    onSecondary = Color.Black,
    tertiary = AccentTeal,
    background = AmoledBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = AmoledSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val OceanDarkColorScheme = darkColorScheme(
    primary = OceanPrimaryDark,
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = OceanSecondary,
    onSecondary = Color(0xFF082F49),
    background = Color(0xFF031926),
    onBackground = Color(0xFFF0F9FF),
    surface = Color(0xFF07273C),
    onSurface = Color(0xFFF0F9FF),
    surfaceVariant = Color(0xFF0C3854),
    onSurfaceVariant = Color(0xFFBAE6FD)
)

private val OceanLightColorScheme = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = OceanSecondary,
    onSecondary = Color.White,
    background = Color(0xFFF0F9FF),
    onBackground = Color(0xFF082F49),
    surface = Color.White,
    onSurface = Color(0xFF082F49),
    surfaceVariant = Color(0xFFE0F2FE),
    onSurfaceVariant = Color(0xFF0369A1)
)

private val EmeraldDarkColorScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = EmeraldSecondary,
    onSecondary = Color(0xFF022C22),
    background = Color(0xFF021C16),
    onBackground = Color(0xFFECFDF5),
    surface = Color(0xFF063328),
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF0B4638),
    onSurfaceVariant = Color(0xFFA7F3D0)
)

private val EmeraldLightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA7F3D0),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    background = Color(0xFFF0FDF4),
    onBackground = Color(0xFF022C22),
    surface = Color.White,
    onSurface = Color(0xFF022C22),
    surfaceVariant = Color(0xFFD1FAE5),
    onSurfaceVariant = Color(0xFF065F46)
)

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = CyberpunkPrimaryDark,
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF6B21A8),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = CyberpunkSecondary,
    onSecondary = Color.White,
    background = Color(0xFF140224),
    onBackground = Color(0xFFFAF5FF),
    surface = Color(0xFF220938),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF350E55),
    onSurfaceVariant = Color(0xFFE9D5FF)
)

private val CyberpunkLightColorScheme = lightColorScheme(
    primary = CyberpunkPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF6B21A8),
    secondary = CyberpunkSecondary,
    onSecondary = Color.White,
    background = Color(0xFFFAF5FF),
    onBackground = Color(0xFF3B0764),
    surface = Color.White,
    onSurface = Color(0xFF3B0764),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF581C87)
)

private val SunsetDarkColorScheme = darkColorScheme(
    primary = SunsetPrimaryDark,
    onPrimary = Color(0xFF451A03),
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = SunsetSecondary,
    onSecondary = Color.White,
    background = Color(0xFF1E0C04),
    onBackground = Color(0xFFFFFBEB),
    surface = Color(0xFF2C150A),
    onSurface = Color(0xFFFFFBEB),
    surfaceVariant = Color(0xFF451E0E),
    onSurfaceVariant = Color(0xFFFDE68A)
)

private val SunsetLightColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDE68A),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = SunsetSecondary,
    onSecondary = Color.White,
    background = Color(0xFFFFFBEB),
    onBackground = Color(0xFF451A03),
    surface = Color.White,
    onSurface = Color(0xFF451A03),
    surfaceVariant = Color(0xFFFEF3C7),
    onSurfaceVariant = Color(0xFF92400E)
)

private val RoseDarkColorScheme = darkColorScheme(
    primary = RosePrimaryDark,
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF881337),
    onPrimaryContainer = Color(0xFFFFE4E6),
    secondary = RoseSecondary,
    onSecondary = Color.White,
    background = Color(0xFF20030C),
    onBackground = Color(0xFFFFF1F2),
    surface = Color(0xFF300715),
    onSurface = Color(0xFFFFF1F2),
    surfaceVariant = Color(0xFF4A0D23),
    onSurfaceVariant = Color(0xFFFECDD3)
)

private val RoseLightColorScheme = lightColorScheme(
    primary = RosePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFECDD3),
    onPrimaryContainer = Color(0xFF881337),
    secondary = RoseSecondary,
    onSecondary = Color.White,
    background = Color(0xFFFFF1F2),
    onBackground = Color(0xFF4C0519),
    surface = Color.White,
    onSurface = Color(0xFF4C0519),
    surfaceVariant = Color(0xFFFFE4E6),
    onSurfaceVariant = Color(0xFF9F1239)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    themeMode: AppThemeMode = AppThemeMode.DYNAMIC,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme: ColorScheme = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        when (themeMode) {
            AppThemeMode.DYNAMIC -> {
                if (darkTheme) DefaultDarkColorScheme else DefaultLightColorScheme
            }
            AppThemeMode.AMOLED_DARK -> if (darkTheme) AmoledDarkColorScheme else DefaultLightColorScheme
            AppThemeMode.OCEAN_BLUE -> if (darkTheme) OceanDarkColorScheme else OceanLightColorScheme
            AppThemeMode.EMERALD_GREEN -> if (darkTheme) EmeraldDarkColorScheme else EmeraldLightColorScheme
            AppThemeMode.CYBERPUNK_PURPLE -> if (darkTheme) CyberpunkDarkColorScheme else CyberpunkLightColorScheme
            AppThemeMode.SUNSET_AMBER -> if (darkTheme) SunsetDarkColorScheme else SunsetLightColorScheme
            AppThemeMode.ROSE_PASTEL -> if (darkTheme) RoseDarkColorScheme else RoseLightColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
