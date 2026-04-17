package nad.master.pa.data.local

import nad.master.pa.data.model.RevelationType
import nad.master.pa.data.model.SurahData

/**
 * Complete static Quran data — 114 Surahs with Juzz mapping.
 * This is immutable reference data that never changes.
 */
object QuranData {

    val ALL_SURAHS: List<SurahData> = listOf(
        SurahData(1,  "الفاتحة",         "Al-Fatihah",     "Al-Fatihah",       7,   1, 1,  RevelationType.MECCAN),
        SurahData(2,  "البقرة",          "Al-Baqarah",     "Al-Baqarah",       286, 1, 3,  RevelationType.MEDINAN),
        SurahData(3,  "آل عمران",        "Aal-Imran",      "Aal-Imran",        200, 3, 4,  RevelationType.MEDINAN),
        SurahData(4,  "النساء",          "An-Nisa",        "An-Nisa",          176, 4, 6,  RevelationType.MEDINAN),
        SurahData(5,  "المائدة",         "Al-Ma'idah",     "Al-Ma\'idah",      120, 6, 7,  RevelationType.MEDINAN),
        SurahData(6,  "الأنعام",         "Al-An'am",       "Al-An\'am",        165, 7, 8,  RevelationType.MECCAN),
        SurahData(7,  "الأعراف",         "Al-A'raf",       "Al-A\'raf",        206, 8, 9,  RevelationType.MECCAN),
        SurahData(8,  "الأنفال",         "Al-Anfal",       "Al-Anfal",         75,  9, 10, RevelationType.MEDINAN),
        SurahData(9,  "التوبة",          "At-Tawbah",      "At-Tawbah",        129, 10,11, RevelationType.MEDINAN),
        SurahData(10, "يونس",            "Yunus",          "Yunus",            109, 11, 11, RevelationType.MECCAN),
        SurahData(11, "هود",             "Hud",            "Hud",              123, 11, 12, RevelationType.MECCAN),
        SurahData(12, "يوسف",            "Yusuf",          "Yusuf",            111, 12, 13, RevelationType.MECCAN),
        SurahData(13, "الرعد",           "Ar-Ra'd",        "Ar-Ra\'d",         43,  13, 13, RevelationType.MEDINAN),
        SurahData(14, "إبراهيم",         "Ibrahim",        "Ibrahim",          52,  13, 13, RevelationType.MECCAN),
        SurahData(15, "الحجر",           "Al-Hijr",        "Al-Hijr",          99,  14, 14, RevelationType.MECCAN),
        SurahData(16, "النحل",           "An-Nahl",        "An-Nahl",          128, 14, 14, RevelationType.MECCAN),
        SurahData(17, "الإسراء",         "Al-Isra",        "Al-Isra",          111, 15, 15, RevelationType.MECCAN),
        SurahData(18, "الكهف",           "Al-Kahf",        "Al-Kahf",          110, 15, 16, RevelationType.MECCAN),
        SurahData(19, "مريم",            "Maryam",         "Maryam",           98,  16, 16, RevelationType.MECCAN),
        SurahData(20, "طه",              "Ta-Ha",          "Ta-Ha",            135, 16, 16, RevelationType.MECCAN),
        SurahData(21, "الأنبياء",        "Al-Anbiya",      "Al-Anbiya",        112, 17, 17, RevelationType.MECCAN),
        SurahData(22, "الحج",            "Al-Hajj",        "Al-Hajj",          78,  17, 17, RevelationType.MEDINAN),
        SurahData(23, "المؤمنون",        "Al-Mu'minun",    "Al-Mu\'minun",     118, 18, 18, RevelationType.MECCAN),
        SurahData(24, "النور",           "An-Nur",         "An-Nur",           64,  18, 18, RevelationType.MEDINAN),
        SurahData(25, "الفرقان",         "Al-Furqan",      "Al-Furqan",        77,  18, 19, RevelationType.MECCAN),
        SurahData(26, "الشعراء",         "Ash-Shu'ara",    "Ash-Shu\'ara",     227, 19, 19, RevelationType.MECCAN),
        SurahData(27, "النمل",           "An-Naml",        "An-Naml",          93,  19, 20, RevelationType.MECCAN),
        SurahData(28, "القصص",           "Al-Qasas",       "Al-Qasas",         88,  20, 20, RevelationType.MECCAN),
        SurahData(29, "العنكبوت",        "Al-Ankabut",     "Al-Ankabut",       69,  20, 21, RevelationType.MECCAN),
        SurahData(30, "الروم",           "Ar-Rum",         "Ar-Rum",           60,  21, 21, RevelationType.MECCAN),
        SurahData(31, "لقمان",           "Luqman",         "Luqman",           34,  21, 21, RevelationType.MECCAN),
        SurahData(32, "السجدة",          "As-Sajdah",      "As-Sajdah",        30,  21, 21, RevelationType.MECCAN),
        SurahData(33, "الأحزاب",         "Al-Ahzab",       "Al-Ahzab",         73,  21, 22, RevelationType.MEDINAN),
        SurahData(34, "سبأ",             "Saba",           "Saba",             54,  22, 22, RevelationType.MECCAN),
        SurahData(35, "فاطر",            "Fatir",          "Fatir",            45,  22, 22, RevelationType.MECCAN),
        SurahData(36, "يس",              "Ya-Sin",         "Ya-Sin",           83,  22, 23, RevelationType.MECCAN),
        SurahData(37, "الصافات",         "As-Saffat",      "As-Saffat",        182, 23, 23, RevelationType.MECCAN),
        SurahData(38, "ص",               "Sad",            "Sad",              88,  23, 23, RevelationType.MECCAN),
        SurahData(39, "الزمر",           "Az-Zumar",       "Az-Zumar",         75,  23, 24, RevelationType.MECCAN),
        SurahData(40, "غافر",            "Ghafir",         "Ghafir",           85,  24, 24, RevelationType.MECCAN),
        SurahData(41, "فصلت",            "Fussilat",       "Fussilat",         54,  24, 25, RevelationType.MECCAN),
        SurahData(42, "الشورى",          "Ash-Shura",      "Ash-Shura",        53,  25, 25, RevelationType.MECCAN),
        SurahData(43, "الزخرف",          "Az-Zukhruf",     "Az-Zukhruf",       89,  25, 25, RevelationType.MECCAN),
        SurahData(44, "الدخان",          "Ad-Dukhan",      "Ad-Dukhan",        59,  25, 25, RevelationType.MECCAN),
        SurahData(45, "الجاثية",         "Al-Jathiyah",    "Al-Jathiyah",      37,  25, 25, RevelationType.MECCAN),
        SurahData(46, "الأحقاف",         "Al-Ahqaf",       "Al-Ahqaf",         35,  26, 26, RevelationType.MECCAN),
        SurahData(47, "محمد",            "Muhammad",       "Muhammad",         38,  26, 26, RevelationType.MEDINAN),
        SurahData(48, "الفتح",           "Al-Fath",        "Al-Fath",          29,  26, 26, RevelationType.MEDINAN),
        SurahData(49, "الحجرات",         "Al-Hujurat",     "Al-Hujurat",       18,  26, 26, RevelationType.MEDINAN),
        SurahData(50, "ق",               "Qaf",            "Qaf",              45,  26, 26, RevelationType.MECCAN),
        SurahData(51, "الذاريات",        "Az-Zariyat",     "Az-Zariyat",       60,  26, 27, RevelationType.MECCAN),
        SurahData(52, "الطور",           "At-Tur",         "At-Tur",           49,  27, 27, RevelationType.MECCAN),
        SurahData(53, "النجم",           "An-Najm",        "An-Najm",          62,  27, 27, RevelationType.MECCAN),
        SurahData(54, "القمر",           "Al-Qamar",       "Al-Qamar",         55,  27, 27, RevelationType.MECCAN),
        SurahData(55, "الرحمن",          "Ar-Rahman",      "Ar-Rahman",        78,  27, 27, RevelationType.MEDINAN),
        SurahData(56, "الواقعة",         "Al-Waqi'ah",     "Al-Waqi\'ah",      96,  27, 27, RevelationType.MECCAN),
        SurahData(57, "الحديد",          "Al-Hadid",       "Al-Hadid",         29,  27, 27, RevelationType.MEDINAN),
        SurahData(58, "المجادلة",        "Al-Mujadila",    "Al-Mujadila",      22,  28, 28, RevelationType.MEDINAN),
        SurahData(59, "الحشر",           "Al-Hashr",       "Al-Hashr",         24,  28, 28, RevelationType.MEDINAN),
        SurahData(60, "الممتحنة",        "Al-Mumtahanah",  "Al-Mumtahanah",    13,  28, 28, RevelationType.MEDINAN),
        SurahData(61, "الصف",            "As-Saff",        "As-Saff",          14,  28, 28, RevelationType.MEDINAN),
        SurahData(62, "الجمعة",          "Al-Jumu'ah",     "Al-Jumu\'ah",      11,  28, 28, RevelationType.MEDINAN),
        SurahData(63, "المنافقون",       "Al-Munafiqun",   "Al-Munafiqun",     11,  28, 28, RevelationType.MEDINAN),
        SurahData(64, "التغابن",         "At-Taghabun",    "At-Taghabun",      18,  28, 28, RevelationType.MEDINAN),
        SurahData(65, "الطلاق",          "At-Talaq",       "At-Talaq",         12,  28, 28, RevelationType.MEDINAN),
        SurahData(66, "التحريم",         "At-Tahrim",      "At-Tahrim",        12,  28, 28, RevelationType.MEDINAN),
        SurahData(67, "الملك",           "Al-Mulk",        "Al-Mulk",          30,  29, 29, RevelationType.MECCAN),
        SurahData(68, "القلم",           "Al-Qalam",       "Al-Qalam",         52,  29, 29, RevelationType.MECCAN),
        SurahData(69, "الحاقة",          "Al-Haqqah",      "Al-Haqqah",        52,  29, 29, RevelationType.MECCAN),
        SurahData(70, "المعارج",         "Al-Ma'arij",     "Al-Ma\'arij",      44,  29, 29, RevelationType.MECCAN),
        SurahData(71, "نوح",             "Nuh",            "Nuh",              28,  29, 29, RevelationType.MECCAN),
        SurahData(72, "الجن",            "Al-Jinn",        "Al-Jinn",          28,  29, 29, RevelationType.MECCAN),
        SurahData(73, "المزمل",          "Al-Muzzammil",   "Al-Muzzammil",     20,  29, 29, RevelationType.MECCAN),
        SurahData(74, "المدثر",          "Al-Muddaththir", "Al-Muddaththir",   56,  29, 29, RevelationType.MECCAN),
        SurahData(75, "القيامة",         "Al-Qiyamah",     "Al-Qiyamah",       40,  29, 29, RevelationType.MECCAN),
        SurahData(76, "الإنسان",         "Al-Insan",       "Al-Insan",         31,  29, 29, RevelationType.MEDINAN),
        SurahData(77, "المرسلات",        "Al-Mursalat",    "Al-Mursalat",      50,  29, 29, RevelationType.MECCAN),
        SurahData(78, "النبأ",           "An-Naba",        "An-Naba",          40,  30, 30, RevelationType.MECCAN),
        SurahData(79, "النازعات",        "An-Nazi'at",     "An-Nazi\'at",      46,  30, 30, RevelationType.MECCAN),
        SurahData(80, "عبس",             "Abasa",          "Abasa",            42,  30, 30, RevelationType.MECCAN),
        SurahData(81, "التكوير",         "At-Takwir",      "At-Takwir",        29,  30, 30, RevelationType.MECCAN),
        SurahData(82, "الانفطار",        "Al-Infitar",     "Al-Infitar",       19,  30, 30, RevelationType.MECCAN),
        SurahData(83, "المطففين",        "Al-Mutaffifin",  "Al-Mutaffifin",    36,  30, 30, RevelationType.MECCAN),
        SurahData(84, "الانشقاق",        "Al-Inshiqaq",    "Al-Inshiqaq",      25,  30, 30, RevelationType.MECCAN),
        SurahData(85, "البروج",          "Al-Buruj",       "Al-Buruj",         22,  30, 30, RevelationType.MECCAN),
        SurahData(86, "الطارق",          "At-Tariq",       "At-Tariq",         17,  30, 30, RevelationType.MECCAN),
        SurahData(87, "الأعلى",          "Al-A'la",        "Al-A\'la",         19,  30, 30, RevelationType.MECCAN),
        SurahData(88, "الغاشية",         "Al-Ghashiyah",   "Al-Ghashiyah",     26,  30, 30, RevelationType.MECCAN),
        SurahData(89, "الفجر",           "Al-Fajr",        "Al-Fajr",          30,  30, 30, RevelationType.MECCAN),
        SurahData(90, "البلد",           "Al-Balad",       "Al-Balad",         20,  30, 30, RevelationType.MECCAN),
        SurahData(91, "الشمس",           "Ash-Shams",      "Ash-Shams",        15,  30, 30, RevelationType.MECCAN),
        SurahData(92, "الليل",           "Al-Layl",        "Al-Layl",          21,  30, 30, RevelationType.MECCAN),
        SurahData(93, "الضحى",           "Ad-Duha",        "Ad-Duha",          11,  30, 30, RevelationType.MECCAN),
        SurahData(94, "الشرح",           "Ash-Sharh",      "Ash-Sharh",        8,   30, 30, RevelationType.MECCAN),
        SurahData(95, "التين",           "At-Tin",         "At-Tin",           8,   30, 30, RevelationType.MECCAN),
        SurahData(96, "العلق",           "Al-'Alaq",       "Al-\'Alaq",        19,  30, 30, RevelationType.MECCAN),
        SurahData(97, "القدر",           "Al-Qadr",        "Al-Qadr",          5,   30, 30, RevelationType.MECCAN),
        SurahData(98, "البينة",          "Al-Bayyinah",    "Al-Bayyinah",      8,   30, 30, RevelationType.MEDINAN),
        SurahData(99, "الزلزلة",         "Az-Zalzalah",    "Az-Zalzalah",      8,   30, 30, RevelationType.MEDINAN),
        SurahData(100,"العاديات",        "Al-'Adiyat",     "Al-\'Adiyat",      11,  30, 30, RevelationType.MECCAN),
        SurahData(101,"القارعة",         "Al-Qari'ah",     "Al-Qari\'ah",      11,  30, 30, RevelationType.MECCAN),
        SurahData(102,"التكاثر",         "At-Takathur",    "At-Takathur",      8,   30, 30, RevelationType.MECCAN),
        SurahData(103,"العصر",           "Al-'Asr",        "Al-\'Asr",         3,   30, 30, RevelationType.MECCAN),
        SurahData(104,"الهمزة",          "Al-Humazah",     "Al-Humazah",       9,   30, 30, RevelationType.MECCAN),
        SurahData(105,"الفيل",           "Al-Fil",         "Al-Fil",           5,   30, 30, RevelationType.MECCAN),
        SurahData(106,"قريش",            "Quraysh",        "Quraysh",          4,   30, 30, RevelationType.MECCAN),
        SurahData(107,"الماعون",         "Al-Ma'un",       "Al-Ma\'un",        7,   30, 30, RevelationType.MECCAN),
        SurahData(108,"الكوثر",          "Al-Kawthar",     "Al-Kawthar",       3,   30, 30, RevelationType.MECCAN),
        SurahData(109,"الكافرون",        "Al-Kafirun",     "Al-Kafirun",       6,   30, 30, RevelationType.MECCAN),
        SurahData(110,"النصر",           "An-Nasr",        "An-Nasr",          3,   30, 30, RevelationType.MEDINAN),
        SurahData(111,"المسد",           "Al-Masad",       "Al-Masad",         5,   30, 30, RevelationType.MECCAN),
        SurahData(112,"الإخلاص",         "Al-Ikhlas",      "Al-Ikhlas",        4,   30, 30, RevelationType.MECCAN),
        SurahData(113,"الفلق",           "Al-Falaq",       "Al-Falaq",         5,   30, 30, RevelationType.MECCAN),
        SurahData(114,"الناس",           "An-Nas",         "An-Nas",           6,   30, 30, RevelationType.MECCAN)
    )

    val TOTAL_VERSES = ALL_SURAHS.sumOf { it.verseCount }  // 6236

    /** Group surahs by Juzz number, producing a map 1..30 → List<SurahData> */
    val JUZZ_MAP: Map<Int, List<SurahData>> by lazy {
        (1..30).associateWith { juzzNum ->
            ALL_SURAHS.filter { surah -> surah.juzz <= juzzNum && surah.juzzEnd >= juzzNum }
        }
    }

    fun getSurahByNumber(num: Int): SurahData? = ALL_SURAHS.firstOrNull { it.number == num }

    fun getSurahsInJuzz(juzz: Int): List<SurahData> = JUZZ_MAP[juzz] ?: emptyList()
}
