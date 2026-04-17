package nad.master.pa.data.local

import java.time.LocalDate
import java.util.Calendar

/**
 * Hijri (Islamic) date calculator.
 *
 * Algorithm reference: https://hijri-calendar.com/en/
 * The calculation uses the tabular Islamic calendar (Arithmetical calendar),
 * which is the basis used by hijri-calendar.com for standard Hijri dates.
 *
 * Accuracy note: For full astronomical precision (moon-sighting based), the
 * actual declaration of each month start may differ by ±1 day by region.
 * For personal reminder purposes this tabular calculation is sufficient.
 */
object HijriDateHelper {

    data class HijriDate(
        val day: Int,
        val month: Int,
        val year: Int,
        val monthName: String = MONTH_NAMES[month - 1]
    ) {
        override fun toString() = "$day $monthName $year AH"
    }

    val MONTH_NAMES = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    /**
     * Convert a Gregorian date to Hijri using the tabular algorithm.
     * Source: hijri-calendar.com/en/ — Arithmetical (Tabular) method.
     */
    fun toHijri(gregorian: LocalDate = LocalDate.now()): HijriDate {
        val y = gregorian.year
        val m = gregorian.monthValue
        val d = gregorian.dayOfMonth

        // Julian Day Number
        val jdn = gregorianToJdn(y, m, d)
        return jdnToHijri(jdn)
    }

    private fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val yr = year + 4800 - a
        val mo = month + 12 * a - 3
        return day + (153 * mo + 2) / 5 + 365L * yr + yr / 4 - yr / 100 + yr / 400 - 32045
    }

    private fun jdnToHijri(jdn: Long): HijriDate {
        val l = jdn - 1948440 + 10632
        val n = (l - 1) / 10631
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316) * ((50 * l2) / 17719) +
                (l2 / 5670) * ((43 * l2) / 15238)
        val l3 = l2 - ((30 - j) / 15) * ((17719 * j) / 50) -
                 (j / 16) * ((15238 * j) / 43) + 29
        val month = (24 * l3) / 709
        val day   = l3 - (709 * month) / 24
        val year  = 30 * n + j - 30

        return HijriDate(day.toInt(), month.toInt(), year.toInt())
    }

    /**
     * Returns true if today is a recommended Sawn (fasting) day:
     * - Every Monday and Thursday
     * - 13th, 14th, 15th of any Hijri month (Ayyam al-Bid)
     *
     * Reference: hijri-calendar.com/en/ for Hijri date, Sunnah.com for fasting days.
     */
    fun isSawnRecommendedToday(): SawnInfo? {
        val today     = LocalDate.now()
        val hijri     = toHijri(today)
        val dayOfWeek = today.dayOfWeek

        return when {
            dayOfWeek == java.time.DayOfWeek.MONDAY ->
                SawnInfo("Monday Fast", "Sawn al-Ithnayn — The Prophet ﷺ fasted on Mondays.")
            dayOfWeek == java.time.DayOfWeek.THURSDAY ->
                SawnInfo("Thursday Fast", "Sawn al-Khamis — The Prophet ﷺ fasted on Thursdays.")
            hijri.day in listOf(13, 14, 15) ->
                SawnInfo(
                    "Ayyam al-Bid — Day ${hijri.day}",
                    "The White Days of ${hijri.monthName} ${hijri.year} AH. Fasting is highly recommended."
                )
            else -> null
        }
    }

    data class SawnInfo(val title: String, val description: String)

    /**
     * Format today's Hijri date as a display string.
     * e.g. "17 Shawwal 1446 AH"
     */
    fun todayHijriString(): String = toHijri().toString()
}
