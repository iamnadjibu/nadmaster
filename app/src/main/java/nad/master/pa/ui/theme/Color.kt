package nad.master.pa.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core NAD MASTER Palette ──────────────────────────────────────────────────
val DarkBrown    = Color(0xFF1A120B)
val MediumBrown  = Color(0xFF3C2A21)
val WarmCream    = Color(0xFFD5CEA3)
val LightCream   = Color(0xFFE5E5CB)

// ── Semantic / Session Colors ────────────────────────────────────────────────
val IslamicGreen  = Color(0xFF4CAF50)
val IslamicGreenDark = Color(0xFF2E7D32)
val CriticalRed   = Color(0xFFE05263)
val WarningAmber  = Color(0xFFFFC107)
val InfoBlue      = Color(0xFF64B5F6)
val CompletedTeal = Color(0xFF4DB6AC)

// ── Surface Variants ─────────────────────────────────────────────────────────
val SurfaceCard   = Color(0xFF2A1E17)   // slightly lighter than DarkBrown for inside-card tones
val Divider       = Color(0x33D5CEA3)   // WarmCream at 20% alpha
val DisabledText  = Color(0x66D5CEA3)   // WarmCream at 40% alpha
val Overlay       = Color(0xB31A120B)   // DarkBrown at 70% alpha

// ── Chart Colors ─────────────────────────────────────────────────────────────
val ChartSuccess  = IslamicGreen
val ChartFail     = CriticalRed
val ChartNeutral  = WarmCream

// ── Status indicator colors ───────────────────────────────────────────────────
val StatusCompleted = IslamicGreen
val StatusCurrent   = WarningAmber
val StatusUpcoming  = Color(0xFF9E9E9E)
val StatusMissed    = CriticalRed
