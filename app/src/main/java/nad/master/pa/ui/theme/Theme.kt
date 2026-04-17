package nad.master.pa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NadMasterColorScheme = darkColorScheme(
    primary          = WarmCream,
    onPrimary        = DarkBrown,
    primaryContainer = MediumBrown,
    onPrimaryContainer = LightCream,

    secondary        = LightCream,
    onSecondary      = DarkBrown,
    secondaryContainer = MediumBrown,
    onSecondaryContainer = WarmCream,

    tertiary         = IslamicGreen,
    onTertiary       = DarkBrown,
    tertiaryContainer = IslamicGreenDark,
    onTertiaryContainer = LightCream,

    error            = CriticalRed,
    onError          = Color.White,
    errorContainer   = Color(0xFF4D1A1F),
    onErrorContainer = Color(0xFFFFC1C7),

    background       = DarkBrown,
    onBackground     = LightCream,

    surface          = MediumBrown,
    onSurface        = LightCream,
    surfaceVariant   = SurfaceCard,
    onSurfaceVariant = WarmCream,

    outline          = WarmCream.copy(alpha = 0.3f),
    outlineVariant   = WarmCream.copy(alpha = 0.15f),

    inverseSurface   = LightCream,
    inverseOnSurface = DarkBrown,
    inversePrimary   = DarkBrown,

    scrim            = Overlay,
)

@Composable
fun NadMasterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NadMasterColorScheme,
        typography  = NadMasterTypography,
        content     = content
    )
}
