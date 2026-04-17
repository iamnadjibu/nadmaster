package nad.master.pa.data.local

import nad.master.pa.data.model.DhikrCategory
import nad.master.pa.data.model.DhikrItem

/**
 * Authentic Islamic Dhikr and Adhkar — curated static data.
 */
object DhikrData {

    val ALL_DHIKR: List<DhikrItem> = listOf(

        // ── MORNING ADHKAR ───────────────────────────────────────────────────
        DhikrItem(
            id = "morning_01", category = DhikrCategory.MORNING,
            titleArabic = "أذكار الصباح", titleEnglish = "Morning Adhkar - Ayat al-Kursi",
            textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm...",
            translation = "Allah — there is no deity except Him, the Ever-Living, the Sustainer of existence...",
            count = 1, source = "Quran 2:255",
            benefits = "Whoever recites Ayat al-Kursi in the morning will be protected until the evening."
        ),
        DhikrItem(
            id = "morning_02", category = DhikrCategory.MORNING,
            titleArabic = "أصبحنا وأصبح الملك لله", titleEnglish = "Morning Remembrance of Allah's Kingdom",
            textArabic = "أَصْبَحْنَا وَأَصْبَحَ المُلْكُ للهِ، وَالحَمْدُ للهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            transliteration = "Asbahna wa asbahal mulku lillah, wal hamdu lillah, la ilaha illallahu wahdahu la sharika lah",
            translation = "We have reached the morning and at this very time unto Allah belongs all sovereignty, and all praise is for Allah. None has the right to be worshipped except Allah.",
            count = 1, source = "Abu Dawud",
            benefits = "Protection and blessing throughout the day"
        ),
        DhikrItem(
            id = "morning_03", category = DhikrCategory.MORNING,
            titleArabic = "اللهم بك أصبحنا", titleEnglish = "Morning Dhikr — Seeking Allah's Bounty",
            textArabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ",
            transliteration = "Allahumma bika asbahna, wa bika amsayna, wa bika nahya, wa bika namutu wa ilaikan-nushur",
            translation = "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
            count = 1, source = "Abu Dawud, Tirmidhi"
        ),
        DhikrItem(
            id = "morning_04", category = DhikrCategory.MORNING,
            titleArabic = "اللهم أنت ربي لا إله إلا أنت", titleEnglish = "Sayyid al-Istighfar",
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
            transliteration = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana abduka, wa ana ala ahdika wa wa'dika mastata'tu",
            translation = "O Allah, You are my Lord, none has the right to be worshipped except You; You created me and I am Your slave...",
            count = 1, source = "Sahih al-Bukhari 6306",
            benefits = "If said with firm conviction in the morning and the person dies that day, they will enter paradise."
        ),
        DhikrItem(
            id = "morning_05", category = DhikrCategory.MORNING,
            titleArabic = "سبحان الله وبحمده", titleEnglish = "Tasbih — Morning",
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            transliteration = "SubhanAllahi wa bihamdih",
            translation = "Glory be to Allah and praise Him.",
            count = 100, source = "Sahih al-Bukhari & Muslim",
            benefits = "Whoever says this 100 times in the morning and evening will have their sins forgiven even if they are as numerous as the foam of the sea."
        ),

        // ── EVENING ADHKAR ───────────────────────────────────────────────────
        DhikrItem(
            id = "evening_01", category = DhikrCategory.EVENING,
            titleArabic = "أمسينا وأمسى الملك لله", titleEnglish = "Evening Remembrance",
            textArabic = "أَمْسَيْنَا وَأَمْسَى المُلْكُ للهِ، وَالحَمْدُ للهِ، لَا إِلَهَ إِلَّا اللهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            transliteration = "Amsayna wa amsal mulku lillah, wal hamdu lillah, la ilaha illallahu wahdahu la sharika lah",
            translation = "We have reached the evening and at this very time unto Allah belongs all sovereignty, and all praise is for Allah.",
            count = 1, source = "Abu Dawud"
        ),
        DhikrItem(
            id = "evening_02", category = DhikrCategory.EVENING,
            titleArabic = "اللهم أنت ربي لا إله إلا أنت — مساءً", titleEnglish = "Sayyid al-Istighfar — Evening",
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
            transliteration = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana abduka, wa ana ala ahdika wa wa'dika mastata'tu",
            translation = "O Allah, You are my Lord, none has the right to be worshipped except You; You created me and I am Your slave...",
            count = 1, source = "Sahih al-Bukhari 6306"
        ),
        DhikrItem(
            id = "evening_03", category = DhikrCategory.EVENING,
            titleArabic = "أعوذ بكلمات الله التامات", titleEnglish = "Protection from Evil — Evening",
            textArabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            transliteration = "A'udhu bikalimatillahit-tammati min sharri ma khalaq",
            translation = "I seek refuge in the perfect words of Allah from the evil of that which He has created.",
            count = 3, source = "Muslim 2709",
            benefits = "Whoever says this in the evening three times, nothing will harm him that night."
        ),

        // ── BEFORE SLEEP ─────────────────────────────────────────────────────
        DhikrItem(
            id = "sleep_01", category = DhikrCategory.BEFORE_SLEEP,
            titleArabic = "باسمك اللهم أموت وأحيا", titleEnglish = "Sleeping Supplication",
            textArabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            transliteration = "Bismika Allahumma amutu wa ahya",
            translation = "In Your name, O Allah, I die and I live.",
            count = 1, source = "Sahih al-Bukhari"
        ),
        DhikrItem(
            id = "sleep_02", category = DhikrCategory.BEFORE_SLEEP,
            titleArabic = "اللهم قني عذابك", titleEnglish = "Before Sleep — Protection from Punishment",
            textArabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
            transliteration = "Allahumma qini adhabaka yawma tab'athu ibadak",
            translation = "O Allah, protect me from Your punishment on the Day you resurrect Your servants.",
            count = 3, source = "Abu Dawud, Tirmidhi"
        ),
        DhikrItem(
            id = "sleep_03", category = DhikrCategory.BEFORE_SLEEP,
            titleArabic = "آية الكرسي قبل النوم", titleEnglish = "Ayat al-Kursi before Sleep",
            textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            transliteration = "Allahu la ilaha illa Huwal Hayyul Qayyum...",
            translation = "Allah — there is no deity except Him, the Ever-Living, the Self-Sustaining...",
            count = 1, source = "Sahih al-Bukhari",
            benefits = "Whoever recites this before sleeping, Allah will appoint a guardian over him and no Shaytan will come near him until morning."
        ),

        // ── TAHAJJUD DUAS ─────────────────────────────────────────────────────
        DhikrItem(
            id = "tahajjud_01", category = DhikrCategory.TAHAJJUD,
            titleArabic = "دعاء قيام الليل", titleEnglish = "Tahajjud Opening Supplication",
            textArabic = "اللَّهُمَّ لَكَ الحَمْدُ أَنْتَ نُورُ السَّمَوَاتِ وَالأَرْضِ وَمَنْ فِيهِنَّ",
            transliteration = "Allahumma lakal hamdu anta nurus samawati wal ardi wa man fihinn",
            translation = "O Allah, all praise is for You. You are the Light of the heavens and earth and all that is within them.",
            count = 1, source = "Sahih al-Bukhari 1120"
        ),
        DhikrItem(
            id = "tahajjud_02", category = DhikrCategory.TAHAJJUD,
            titleArabic = "اللهم اجعل في قلبي نوراً", titleEnglish = "Supplication for Light (Tahajjud)",
            textArabic = "اللَّهُمَّ اجْعَلْ فِي قَلْبِي نُوراً، وَفِي بَصَرِي نُوراً، وَفِي سَمْعِي نُوراً",
            transliteration = "Allahumma ij'al fi qalbi nuran, wa fi basari nuran, wa fi sam'i nuran",
            translation = "O Allah, place light in my heart, light in my sight, and light in my hearing.",
            count = 1, source = "Sahih al-Bukhari 6316"
        ),

        // ── AFTER SALAH ──────────────────────────────────────────────────────
        DhikrItem(
            id = "after_salah_01", category = DhikrCategory.AFTER_SALAH,
            titleArabic = "الاستغفار بعد الصلاة", titleEnglish = "Istighfar after Salah",
            textArabic = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullah",
            translation = "I seek forgiveness from Allah.",
            count = 3, source = "Muslim 591"
        ),
        DhikrItem(
            id = "after_salah_02", category = DhikrCategory.AFTER_SALAH,
            titleArabic = "التسبيح والتحميد بعد الصلاة", titleEnglish = "Tasbih, Tahmid, Takbir after Salah",
            textArabic = "سُبْحَانَ اللَّهِ (٣٣) الحَمْدُ لِلَّهِ (٣٣) اللَّهُ أَكْبَرُ (٣٣)",
            transliteration = "SubhanAllah (33) Alhamdulillah (33) Allahu Akbar (33)",
            translation = "Glory be to Allah (33×), All praise to Allah (33×), Allah is the Greatest (33×).",
            count = 33, source = "Muslim 597"
        ),
        DhikrItem(
            id = "after_salah_03", category = DhikrCategory.AFTER_SALAH,
            titleArabic = "لا إله إلا الله وحده", titleEnglish = "Tahlil after Salah",
            textArabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la sharika lah, lahul mulku wa lahul hamdu wa huwa ala kulli shay'in qadir",
            translation = "None has the right to be worshipped except Allah, alone, without partner. To Him belongs all sovereignty and praise, and He is over all things omnipotent.",
            count = 1, source = "Muslim 597"
        ),

        // ── ISTIGHFAR ─────────────────────────────────────────────────────────
        DhikrItem(
            id = "istighfar_01", category = DhikrCategory.ISTIGHFAR,
            titleArabic = "سيد الاستغفار", titleEnglish = "Master Supplication of Forgiveness",
            textArabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ",
            transliteration = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana abduka...",
            translation = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your slave...",
            count = 1, source = "Sahih al-Bukhari 6306",
            benefits = "The master supplication of forgiveness, grants entry to paradise."
        ),
        DhikrItem(
            id = "istighfar_02", category = DhikrCategory.ISTIGHFAR,
            titleArabic = "رب اغفر لي", titleEnglish = "Simple Istighfar",
            textArabic = "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ، إِنَّكَ أَنْتَ التَّوَّابُ الغَفُورُ",
            transliteration = "Rabbi ighfir li wa tub alayya, innaka anta tawwabul ghafur",
            translation = "My Lord, forgive me and accept my repentance, You are indeed the Ever-Returning, the Oft-Forgiving.",
            count = 100, source = "Abu Dawud, Tirmidhi",
            benefits = "The Prophet (SAW) used to say this 100 times a day."
        ),

        // ── ALL-TIMES DHIKR ──────────────────────────────────────────────────
        DhikrItem(
            id = "all_times_01", category = DhikrCategory.ALL_TIMES,
            titleArabic = "سبحان الله وبحمده سبحان الله العظيم", titleEnglish = "Two Light but Heavy Phrases",
            textArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahi wa bihamdih, SubhanAllahil Azim",
            translation = "Glory be to Allah and His is the praise; Glory be to Allah the Almighty.",
            count = 1, source = "Sahih al-Bukhari & Muslim",
            benefits = "Two words that are light on the tongue, heavy in the scale, and beloved to the Most Merciful."
        ),
        DhikrItem(
            id = "all_times_02", category = DhikrCategory.ALL_TIMES,
            titleArabic = "لا حول ولا قوة إلا بالله", titleEnglish = "Hawqala",
            textArabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            translation = "There is no power nor strength except with Allah.",
            count = 1, source = "Sahih al-Bukhari & Muslim",
            benefits = "A treasure from the treasures of paradise."
        ),

        // ── QURANIC DUAS ─────────────────────────────────────────────────────
        DhikrItem(
            id = "quran_dua_01", category = DhikrCategory.QURANIC_DUAS,
            titleArabic = "ربنا آتنا في الدنيا حسنة", titleEnglish = "Dua for Goodness in Both Worlds",
            textArabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil akhirati hasanatan wa qina adhaaban-nar",
            translation = "Our Lord, give us good in this world and good in the next world, and save us from the punishment of the Fire.",
            count = 1, source = "Quran 2:201"
        ),
        DhikrItem(
            id = "quran_dua_02", category = DhikrCategory.QURANIC_DUAS,
            titleArabic = "ربنا لا تزغ قلوبنا", titleEnglish = "Dua for Steadfast Heart",
            textArabic = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِنْ لَدُنْكَ رَحْمَةً",
            transliteration = "Rabbana la tuzigh qulubana ba'da idh hadaytana wa hab lana min ladunka rahmah",
            translation = "Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy.",
            count = 1, source = "Quran 3:8"
        ),
        DhikrItem(
            id = "quran_dua_03", category = DhikrCategory.QURANIC_DUAS,
            titleArabic = "رب اشرح لي صدري", titleEnglish = "Dua of Musa — Ease and Understanding",
            textArabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي",
            transliteration = "Rabbi ishrah li sadri wa yassir li amri",
            translation = "My Lord, expand for me my chest and ease for me my task.",
            count = 1, source = "Quran 20:25-26"
        ),

        // ── RUQIYAH ──────────────────────────────────────────────────────────
        DhikrItem(
            id = "ruqiyah_01", category = DhikrCategory.RUQIYAH,
            titleArabic = "الفاتحة", titleEnglish = "Al-Fatihah — Opening Chapter",
            textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝ الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ۝ الرَّحْمَٰنِ الرَّحِيمِ ۝ مَالِكِ يَوْمِ الدِّينِ",
            transliteration = "Bismillahir rahmanir rahim, Alhamdu lillahir rabbil alamin, Ar-rahmanir rahim, Maliki yawmid din...",
            translation = "In the name of Allah, the Most Gracious, the Most Merciful. All praise is for Allah, Lord of all worlds...",
            count = 3, source = "Sahih al-Bukhari 2276",
            benefits = "Al-Fatihah is a cure for every disease."
        ),
        DhikrItem(
            id = "ruqiyah_02", category = DhikrCategory.RUQIYAH,
            titleArabic = "المعوذتان", titleEnglish = "Al-Mu'awwidhatan — The Two Protectors",
            textArabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِنْ شَرِّ مَا خَلَقَ... | قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ",
            transliteration = "Qul a'udhu bi rabbil falaq... | Qul a'udhu bi rabbil nas...",
            translation = "Say: I seek refuge with the Lord of the daybreak... | Say: I seek refuge with the Lord of mankind...",
            count = 3, source = "Abu Dawud 5082",
            benefits = "Recite three times morning and evening — sufficient for everything."
        ),

        // ── SUNNAH DUAS ──────────────────────────────────────────────────────
        DhikrItem(
            id = "sunnah_01", category = DhikrCategory.SUNNAH_DUAS,
            titleArabic = "دعاء دخول المسجد", titleEnglish = "Entering the Masjid",
            textArabic = "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ",
            transliteration = "Allahumma iftah li abwaba rahmatik",
            translation = "O Allah, open the gates of Your mercy for me.",
            count = 1, source = "Muslim 713"
        ),
        DhikrItem(
            id = "sunnah_02", category = DhikrCategory.SUNNAH_DUAS,
            titleArabic = "دعاء الاستفتاح", titleEnglish = "Opening Supplication in Prayer",
            textArabic = "اللَّهُمَّ بَاعِدْ بَيْنِي وَبَيْنَ خَطَايَايَ كَمَا بَاعَدْتَ بَيْنَ الْمَشْرِقِ وَالْمَغْرِبِ",
            transliteration = "Allahumma ba'id bayni wa bayna khatayaya kama ba'adta baynal mashriqi wal maghrib",
            translation = "O Allah, separate me from my sins as the East and West are separated.",
            count = 1, source = "Sahih al-Bukhari & Muslim"
        )
    )

    fun getByCategory(category: DhikrCategory): List<DhikrItem> =
        ALL_DHIKR.filter { it.category == category }

    val CATEGORIES_ORDER = listOf(
        DhikrCategory.MORNING,
        DhikrCategory.EVENING,
        DhikrCategory.BEFORE_SLEEP,
        DhikrCategory.TAHAJJUD,
        DhikrCategory.BEFORE_SALAH,
        DhikrCategory.AFTER_SALAH,
        DhikrCategory.QURANIC_DUAS,
        DhikrCategory.SUNNAH_DUAS,
        DhikrCategory.ISTIGHFAR,
        DhikrCategory.ALL_TIMES,
        DhikrCategory.RUQIYAH
    )

    fun categoryDisplayName(category: DhikrCategory): String = when (category) {
        DhikrCategory.MORNING      -> "Morning Adhkar"
        DhikrCategory.EVENING      -> "Evening Adhkar"
        DhikrCategory.BEFORE_SLEEP -> "Before Sleep"
        DhikrCategory.TAHAJJUD     -> "Tahajjud Duas"
        DhikrCategory.BEFORE_SALAH -> "Before Salah"
        DhikrCategory.AFTER_SALAH  -> "After Salah"
        DhikrCategory.QURANIC_DUAS -> "Quranic Duas"
        DhikrCategory.SUNNAH_DUAS  -> "Sunnah Duas"
        DhikrCategory.ISTIGHFAR    -> "Istighfar"
        DhikrCategory.ALL_TIMES    -> "Dhikr for All Times"
        DhikrCategory.RUQIYAH      -> "Ruqiyah Recitation"
    }
}
