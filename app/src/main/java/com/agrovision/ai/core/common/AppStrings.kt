package com.agrovision.ai.core.common

object AppStrings {
    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "फसलरक्षक AI"
        AppLanguage.MARATHI -> "पीकरक्षक AI"
        AppLanguage.ENGLISH -> "FasalRakshak AI"
    }

    fun appSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "फसल सुरक्षा एवं रोग निदान"
        AppLanguage.MARATHI -> "पीक संरक्षण आणि रोग निदान"
        AppLanguage.ENGLISH -> "Crop Disease & Pest Defense"
    }

    // Nav items
    fun navHome(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "होम"
        AppLanguage.MARATHI -> "मुख्य"
        AppLanguage.ENGLISH -> "Home"
    }

    fun navHistory(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "इतिहास"
        AppLanguage.MARATHI -> "इतिहास"
        AppLanguage.ENGLISH -> "History"
    }

    fun navScan(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "स्कैन"
        AppLanguage.MARATHI -> "स्कॅन"
        AppLanguage.ENGLISH -> "Scan"
    }

    fun navGuide(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "मार्गदर्शिका"
        AppLanguage.MARATHI -> "मार्गदर्शक"
        AppLanguage.ENGLISH -> "Guide"
    }

    fun navMore(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "अन्य"
        AppLanguage.MARATHI -> "अधिक"
        AppLanguage.ENGLISH -> "More"
    }

    // History & Status
    fun allScans(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "सभी जांच"
        AppLanguage.MARATHI -> "सर्व तपासण्या"
        AppLanguage.ENGLISH -> "All scans"
    }

    fun needsAttention(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "उपचार चाहिए"
        AppLanguage.MARATHI -> "उपचार आवश्यक"
        AppLanguage.ENGLISH -> "Needs attention"
    }

    fun healthy(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "स्वस्थ फसल"
        AppLanguage.MARATHI -> "निरोगी पीक"
        AppLanguage.ENGLISH -> "Healthy"
    }

    fun noPestSeen(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "कोई कीट नहीं दिखा"
        AppLanguage.MARATHI -> "कोणताही कीड आढळली नाही"
        AppLanguage.ENGLISH -> "No pest seen"
    }

    fun scanFirstLeaf(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "पहली पत्ती स्कैन करें"
        AppLanguage.MARATHI -> "पहिले पान स्कॅन करा"
        AppLanguage.ENGLISH -> "Scan your first leaf"
    }

    fun scanRecordDeleted(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "जांच रिकॉर्ड हटाया गया"
        AppLanguage.MARATHI -> "तपासणी नोंद हटवली"
        AppLanguage.ENGLISH -> "Scan record deleted"
    }

    fun undo(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "पूर्ववत करें"
        AppLanguage.MARATHI -> "पूर्ववत करा"
        AppLanguage.ENGLISH -> "Undo"
    }

    // Settings
    fun languageSetting(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "भाषा"
        AppLanguage.MARATHI -> "भाषा"
        AppLanguage.ENGLISH -> "Language"
    }

    fun selectLanguageDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "अपनी पसंदीदा भाषा चुनें"
        AppLanguage.MARATHI -> "आपली पसंतीची भाषा निवडा"
        AppLanguage.ENGLISH -> "Choose your preferred language"
    }

    fun brightSunMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "तेज धूप मोड"
        AppLanguage.MARATHI -> "प्रखर सूर्यप्रकाश मोड"
        AppLanguage.ENGLISH -> "Bright-sun mode"
    }

    fun brightSunDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "खेत में तेज धूप के लिए उच्च कंट्रास्ट"
        AppLanguage.MARATHI -> "शेतात उन्हात स्पष्ट दिसण्यासाठी हाय कॉन्ट्रास्ट"
        AppLanguage.ENGLISH -> "High contrast for midday field sunlight"
    }

    fun privacyTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "डेटा गोपनीयता"
        AppLanguage.MARATHI -> "डेटा गोपनीयता"
        AppLanguage.ENGLISH -> "Privacy commitment"
    }

    fun privacyDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "सभी फोटो आपके फोन में सुरक्षित रहती हैं। इंटरनेट के बिना काम करता है।"
        AppLanguage.MARATHI -> "सर्व फोटो आपल्या फोनवर सुरक्षित राहतात. इंटरनेटशिवाय कार्य करते."
        AppLanguage.ENGLISH -> "All leaf photos stay strictly on this phone. 100% offline."
    }

    // Crops
    fun anyCrop(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "सभी फसलें"
        AppLanguage.MARATHI -> "सर्व पिके"
        AppLanguage.ENGLISH -> "All crops"
    }

    fun cropRice(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "धान (चावल)"
        AppLanguage.MARATHI -> "भात (धान)"
        AppLanguage.ENGLISH -> "Rice"
    }

    fun cropWheat(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "गेहूं"
        AppLanguage.MARATHI -> "गहू"
        AppLanguage.ENGLISH -> "Wheat"
    }

    fun cropTomato(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "टमाटर"
        AppLanguage.MARATHI -> "टोमॅटो"
        AppLanguage.ENGLISH -> "Tomato"
    }

    fun cropPotato(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "आलू"
        AppLanguage.MARATHI -> "बटाटा"
        AppLanguage.ENGLISH -> "Potato"
    }

    fun cropCotton(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "कपास"
        AppLanguage.MARATHI -> "कापूस"
        AppLanguage.ENGLISH -> "Cotton"
    }

    fun cropMaize(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "मक्का"
        AppLanguage.MARATHI -> "मका"
        AppLanguage.ENGLISH -> "Maize"
    }

    fun cropSoybean(lang: AppLanguage): String = when (lang) {
        AppLanguage.HINDI -> "सोयाबीन"
        AppLanguage.MARATHI -> "सोयाबीन"
        AppLanguage.ENGLISH -> "Soybean"
    }
}
