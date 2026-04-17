package nad.master.pa.data.local

import nad.master.pa.data.model.Hadith

/**
 * Curated daily Hadiths — rotated by day of year.
 */
object HadithData {

    val HADITHS: List<Hadith> = listOf(
        Hadith(id = "h01",
            textEnglish = "The best of deeds are those done regularly, even if they are few.",
            narrator    = "Aisha (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Consistency"
        ),
        Hadith(id = "h02",
            textEnglish = "The strong person is not the one who can overpower others. The strong person is the one who controls himself when he is angry.",
            narrator    = "Abu Hurairah (RA)",
            source      = "Sahih al-Bukhari & Muslim",
            category    = "Self-Discipline"
        ),
        Hadith(id = "h03",
            textEnglish = "Take advantage of five before five: your youth before your old age, your health before your illness, your wealth before your poverty, your free time before your preoccupation, and your life before your death.",
            narrator    = "Ibn Abbas (RA)",
            source      = "Shu'ab al-Iman, Bayhaqi",
            category    = "Time Management"
        ),
        Hadith(id = "h04",
            textEnglish = "Tie your camel first, then put your trust in Allah.",
            narrator    = "Anas (RA)",
            source      = "Tirmidhi",
            category    = "Planning & Tawakkul"
        ),
        Hadith(id = "h05",
            textEnglish = "No one has ever eaten a better meal than that which he earned by the work of his own hands.",
            narrator    = "Al-Miqdam (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Work Ethic"
        ),
        Hadith(id = "h06",
            textEnglish = "Allah does not look at your shapes or wealth, but He looks at your hearts and deeds.",
            narrator    = "Abu Hurairah (RA)",
            source      = "Sahih Muslim",
            category    = "Sincerity"
        ),
        Hadith(id = "h07",
            textEnglish = "Seeking knowledge is an obligation upon every Muslim.",
            narrator    = "Anas ibn Malik (RA)",
            source      = "Ibn Majah",
            category    = "Knowledge"
        ),
        Hadith(id = "h08",
            textEnglish = "Every act of kindness is charity.",
            narrator    = "Jabir (RA)",
            source      = "Sahih Muslim",
            category    = "Character"
        ),
        Hadith(id = "h09",
            textEnglish = "Whoever believes in Allah and the Last Day should speak good or keep silent.",
            narrator    = "Abu Hurairah (RA)",
            source      = "Sahih al-Bukhari & Muslim",
            category    = "Speech"
        ),
        Hadith(id = "h10",
            textEnglish = "Make things easy, do not make them difficult; cheer people up, do not put them off.",
            narrator    = "Anas (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Character"
        ),
        Hadith(id = "h11",
            textEnglish = "None of you truly believes until he loves for his brother what he loves for himself.",
            narrator    = "Anas (RA)",
            source      = "Sahih al-Bukhari & Muslim",
            category    = "Brotherhood"
        ),
        Hadith(id = "h12",
            textEnglish = "The most beloved deeds to Allah are those which are done consistently, even if they are small.",
            narrator    = "Aisha (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Consistency"
        ),
        Hadith(id = "h13",
            textEnglish = "Verily, with every hardship comes ease.",
            narrator    = "Ibn Abbas (RA)",
            source      = "Quran 94:5 — Tafsir",
            category    = "Patience"
        ),
        Hadith(id = "h14",
            textEnglish = "If you have no shame, do as you wish.",
            narrator    = "Abu Mas'ud (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Morals"
        ),
        Hadith(id = "h15",
            textEnglish = "Allah is beautiful and loves beauty.",
            narrator    = "Ibn Masoud (RA)",
            source      = "Sahih Muslim",
            category    = "Aesthetics"
        ),
        Hadith(id = "h16",
            textEnglish = "The best among you is the one who learns the Quran and teaches it.",
            narrator    = "Uthman (RA)",
            source      = "Sahih al-Bukhari",
            category    = "Quran"
        ),
        Hadith(id = "h17",
            textEnglish = "Whoever recites a letter from the Book of Allah, he will get a reward, and this reward will be multiplied by ten.",
            narrator    = "Ibn Masoud (RA)",
            source      = "Tirmidhi",
            category    = "Quran"
        ),
        Hadith(id = "h18",
            textEnglish = "Guard the five daily prayers. Whoever does that, Allah will make things easy for him in this world and the Hereafter.",
            narrator    = "Abdullah ibn Amr (RA)",
            source      = "Ibn Hibban",
            category    = "Prayer"
        ),
        Hadith(id = "h19",
            textEnglish = "A believer is never stung from the same hole twice.",
            narrator    = "Abu Hurairah (RA)",
            source      = "Sahih al-Bukhari & Muslim",
            category    = "Wisdom"
        ),
        Hadith(id = "h20",
            textEnglish = "The world is a prison for the believer and a paradise for the disbeliever.",
            narrator    = "Abu Hurairah (RA)",
            source      = "Sahih Muslim",
            category    = "Perspective"
        )
    )

    fun getDailyHadith(): Hadith {
        val dayOfYear = java.time.LocalDate.now().dayOfYear
        return HADITHS[dayOfYear % HADITHS.size]
    }
}

/**
 * Motivational quotes based on performance discipline scores.
 */
object DisciplineQuotes {

    data class DisciplineQuote(
        val text: String,
        val author: String,
        val minScore: Float = 0f,   // Show when score >= minScore
        val maxScore: Float = 100f  // Show when score < maxScore
    )

    val QUOTES: List<DisciplineQuote> = listOf(
        // Excellent (80-100)
        DisciplineQuote("MashaAllah! Your discipline is your greatest weapon. Keep it sharp.", "NAD MASTER", 80f),
        DisciplineQuote("The heights of great men reached and kept were not attained in sudden flight, but they while their companions slept, were toiling upward in the night.", "Longfellow", 80f),
        DisciplineQuote("Subhanallah — you are proving that commitment and faith are one and the same.", "NAD MASTER", 85f),
        DisciplineQuote("Alhamdulillah for the strength you've shown. Every completed session is a seed of your future success.", "NAD MASTER", 90f),

        // Good (60-79)
        DisciplineQuote("You are making progress. Stay consistent. Allah rewards the consistent.", "NAD MASTER", 60f, 80f),
        DisciplineQuote("Good effort, Nad. The Prophet ﷺ said: The deed most loved by Allah is one done consistently, even if it is small.", "NAD MASTER", 60f, 80f),
        DisciplineQuote("Progress, not perfection. You're building momentum — don't stop now.", "NAD MASTER", 65f, 80f),

        // Average (40-59)
        DisciplineQuote("Your potential is clear, but your follow-through needs work. Tomorrow is a new day — start fresh.", "NAD MASTER", 40f, 60f),
        DisciplineQuote("Discipline is choosing between what you want now and what you want most. What do you want most?", "Abraham Lincoln", 40f, 60f),
        DisciplineQuote("Every missed session is a debt to your future self. Pay it back — don't let it compound.", "NAD MASTER", 40f, 60f),

        // Below Average (0-39)
        DisciplineQuote("Nad, it's time to be honest with yourself. The schedule exists for a reason. Rise above comfort.", "NAD MASTER", 0f, 40f),
        DisciplineQuote("The people who succeed are consistent in their worst moments, not just their best. Get back on track.", "NAD MASTER", 0f, 40f),
        DisciplineQuote("Remember why you started. Remember your goals. Remember who you want to become. Act accordingly.", "NAD MASTER", 0f, 40f),
        DisciplineQuote("وَأَن لَّيْسَ لِلْإِنسَانِ إِلَّا مَا سَعَىٰ — Man will have only what he strives for. (Quran 53:39)", "Quran", 0f, 40f)
    )

    fun getQuoteForScore(score: Float): DisciplineQuote {
        val applicableQuotes = QUOTES.filter { score >= it.minScore && score < it.maxScore }
        return if (applicableQuotes.isNotEmpty()) {
            applicableQuotes.random()
        } else {
            QUOTES.last()
        }
    }
}
