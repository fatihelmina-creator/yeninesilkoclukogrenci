package com.example.data.curriculum

import com.example.data.model.ReadyCurriculum
import com.example.data.model.ReadyTaskItem

object CurriculumTemplates {

    val SAYISAL_PROGRAM = ReadyCurriculum(
        id = "sayisal_yks",
        title = "YKS Sayısal (SAY) Derece Programı",
        field = "SAYISAL",
        subtitle = "Tıp & Mühendislik Hedefli Haftalık Yoğun Kamp",
        badge = "Haftalık 33 Saat • 980 Soru",
        description = "AYT Matematik ve Fen (Fizik, Kimya, Biyoloji) ağırlıklı, günlük paragraf ve geometri rutinleri ile desteklenmiş, haftasonu tam deneme simülasyonlu profesyonel program.",
        weeklyHours = 33.5,
        weeklyQuestions = 980,
        recommendedNets = "Hedef: 100 - 115+ TYT / 70 - 78+ AYT",
        primaryColorHex = 0xFF2563EB,
        tasks = listOf(
            // Pazartesi
            ReadyTaskItem(1, "AYT Matematik", "Fonksiyonlar & Polinomlar Karma Soru Çözümü", 90, 45, "Konu & Soru"),
            ReadyTaskItem(1, "Fizik", "Vektörler & Kuvvet-Hareket Dinamik Testi", 60, 30, "Soru Çözümü"),
            ReadyTaskItem(1, "Türkçe", "TYT Paragraf Rutini & Hız Analizi", 35, 25, "Rutin"),
            // Salı
            ReadyTaskItem(2, "Kimya", "Modern Atom Teorisi & Periyodik Özellikler", 60, 35, "Konu & Soru"),
            ReadyTaskItem(2, "Biyoloji", "Hücre Bölünmeleri & Kalıtım Genetik Kampı", 60, 30, "Konu & Soru"),
            ReadyTaskItem(2, "Geometri", "Üçgende Açılar & Özel Üçgenler", 60, 30, "Rutin Geometri"),
            // Çarşamba
            ReadyTaskItem(3, "AYT Matematik", "Türev & İntegral Uygulamaları & Grafik Yorumlama", 90, 45, "Konu & Soru"),
            ReadyTaskItem(3, "Fizik", "Elektrik ve Manyetizma Manyetik Alan Testi", 75, 35, "Soru Çözümü"),
            ReadyTaskItem(3, "Türkçe", "Dil Bilgisi: Sözcük Türleri ve Cümle Ögeleri", 45, 30, "Ödev"),
            // Perşembe
            ReadyTaskItem(4, "Kimya", "Gazlar & Sıvı Çözeltilerde Derişim", 60, 35, "Konu & Soru"),
            ReadyTaskItem(4, "Biyoloji", "İnsan Fizyolojisi: Sinir & Endokrin Sistem", 60, 30, "Konu & Soru"),
            ReadyTaskItem(4, "Geometri", "Çemberde Açı & Dairede Alan", 60, 30, "Rutin Geometri"),
            // Cuma
            ReadyTaskItem(5, "TYT Matematik", "Problemler & Sayı Mantığı Hız Testi", 75, 40, "Problem Kampı"),
            ReadyTaskItem(5, "Fizik", "Optik & Dalga Mekaniği Kırılma Yasaları", 60, 30, "Ödev"),
            ReadyTaskItem(5, "Rehberlik & Analiz", "Haftalık Yanlış Defteri Kontrolü & Tekrar", 45, 20, "Hata Analizi"),
            // Cumartesi
            ReadyTaskItem(6, "Genel Deneme", "165 Dk Süreli TYT Tam Deneme Sınavı", 165, 120, "Deneme"),
            ReadyTaskItem(6, "Deneme Analizi", "TYT Soru Taraması, Yanlış ve Boş Analizi", 60, 0, "Analiz"),
            // Pazar
            ReadyTaskItem(7, "AYT Deneme", "AYT Matematik-Fen Alan Denemesi (80 Soru)", 180, 80, "Deneme"),
            ReadyTaskItem(7, "Koçluk & Tekrar", "Haftalık Eksik Telafisi & Koç Değerlendirmesi", 60, 25, "Etüt")
        )
    )

    val ESIT_AGIRLIK_PROGRAM = ReadyCurriculum(
        id = "esitagirlik_yks",
        title = "YKS Eşit Ağırlık (EA) Başarı Programı",
        field = "EŞİT AĞIRLIK",
        subtitle = "Hukuk, İktisat & Yönetim Hedefli Denge Programı",
        badge = "Haftalık 31 Saat • 920 Soru",
        description = "Matematik ve Türk Dili-Edebiyatı omurgası üzerinde, Tarih-1 ve Coğrafya-1 destekli, süre ve hafıza teknikleri odaklı haftalık çalışma planı.",
        weeklyHours = 31.0,
        weeklyQuestions = 920,
        recommendedNets = "Hedef: 90 - 105+ TYT / 65 - 75+ AYT",
        primaryColorHex = 0xFF7C3AED,
        tasks = listOf(
            // Pazartesi
            ReadyTaskItem(1, "TYT Matematik", "Problemler & Denklem Kurma Hız Kampı", 90, 45, "Problem Kampı"),
            ReadyTaskItem(1, "Edebiyat", "İslamiyet Öncesi & Divan Edebiyatı Nazım Şekilleri", 75, 40, "Konu & Hafıza"),
            ReadyTaskItem(1, "Türkçe", "Paragraf Hız Rutini (25 Soru - 28 Dk)", 35, 25, "Rutin"),
            // Salı
            ReadyTaskItem(2, "Tarih-1", "İlk ve Orta Çağlarda Türk Dünyası & İslam Tarihi", 60, 35, "Konu & Soru"),
            ReadyTaskItem(2, "Coğrafya-1", "İklim Elemanları, Yer Şekilleri ve Harita Bilgisi", 60, 30, "Soru Çözümü"),
            ReadyTaskItem(2, "Geometri", "Üçgenler & Çokgenler Temel Çözümler", 60, 30, "Rutin Geometri"),
            // Çarşamba
            ReadyTaskItem(3, "AYT Matematik", "Logaritma, Diziler & Trigonometri Temelleri", 90, 45, "Konu & Soru"),
            ReadyTaskItem(3, "Edebiyat", "Tanzimat, Servet-i Fünun & Fecr-i Ati Dönemi", 75, 40, "Konu & Soru"),
            ReadyTaskItem(3, "Felsefe & Din", "TYT Felsefe ve Din Kültürü Kavram Testleri", 45, 25, "Ödev"),
            // Perşembe
            ReadyTaskItem(4, "Tarih-1", "Osmanlı Devleti Siyaseti & Islahat Hareketleri", 60, 35, "Konu & Soru"),
            ReadyTaskItem(4, "Coğrafya-1", "Nüfus, Göç & Türkiye'nin Ekonomik Coğrafyası", 60, 30, "Soru Çözümü"),
            ReadyTaskItem(4, "TYT Matematik", "Oran-Orantı, Kümeler ve Mantık", 60, 30, "Soru Çözümü"),
            // Cuma
            ReadyTaskItem(5, "AYT Matematik", "Limit, Süreklilik ve Türev Başlangıcı", 90, 45, "Konu & Soru"),
            ReadyTaskItem(5, "Edebiyat", "Milli Edebiyat & Cumhuriyet Dönemi Roman/Şiir", 75, 40, "Konu & Hafıza"),
            ReadyTaskItem(5, "Hata Defteri", "Haftalık Yanlış Soru Tekrarı & Formül Kontrolü", 45, 20, "Hata Analizi"),
            // Cumartesi
            ReadyTaskItem(6, "Genel Deneme", "165 Dk Süreli TYT Genel Deneme Sınavı", 165, 120, "Deneme"),
            ReadyTaskItem(6, "Deneme Analizi", "TYT Yanlış/Boş Taraması & Koç Değerlendirmesi", 60, 0, "Analiz"),
            // Pazar
            ReadyTaskItem(7, "AYT Deneme", "AYT Edebiyat-Sosyal-1 ve Matematik Branş Denemesi", 180, 80, "Deneme"),
            ReadyTaskItem(7, "Telafi & Plan", "Haftalık Eksik Tamamlama ve Yeni Hafta Hazırlığı", 60, 20, "Etüt")
        )
    )

    val SOZEL_PROGRAM = ReadyCurriculum(
        id = "sozel_yks",
        title = "YKS Sözel (SÖZ) Kapsamlı Programı",
        field = "SÖZEL",
        subtitle = "İletişim, Tarih, Coğrafya & Özel Yetenek Hedefli",
        badge = "Haftalık 29 Saat • 880 Soru",
        description = "Edebiyat, Tarih-1/2, Coğrafya-1/2, Felsefe Grubu (Psikoloji, Sosyoloji, Mantık) ve Din Kültürü'nü kapsayan derinlikli sözel başarı planı.",
        weeklyHours = 29.5,
        weeklyQuestions = 880,
        recommendedNets = "Hedef: 85 - 100+ TYT / 68 - 76+ Sözel AYT",
        primaryColorHex = 0xFFD97706,
        tasks = listOf(
            // Pazartesi
            ReadyTaskItem(1, "Edebiyat", "Halk & Divan Edebiyatı Şairleri ve Eserleri", 90, 45, "Hafıza & Soru"),
            ReadyTaskItem(1, "Tarih-1", "Osmanlı Kültür & Medeniyeti Detaylı Analiz", 75, 40, "Konu & Soru"),
            ReadyTaskItem(1, "Türkçe", "TYT Paragraf & Anlam Bilgisi Hız Testi", 40, 30, "Rutin"),
            // Salı
            ReadyTaskItem(2, "Coğrafya-1/2", "Ekosistem, Madde Döngüleri & Küresel İklim", 75, 40, "Konu & Soru"),
            ReadyTaskItem(2, "Felsefe Grubu", "Mantık & Psikolojiye Giriş Temel Kavramlar", 60, 30, "Ödev"),
            ReadyTaskItem(2, "Temel Matematik", "TYT Temel Kavramlar & Rasyonel Sayılar", 60, 25, "Temel Matematik"),
            // Çarşamba
            ReadyTaskItem(3, "Edebiyat", "Cumhuriyet Dönemi Türk Edebiyatı Akımları & Roman", 90, 50, "Konu & Soru"),
            ReadyTaskItem(3, "Tarih-2", "20. Yüzyıl Başlarında Dünya & Milli Mücadele", 75, 40, "Konu & Soru"),
            ReadyTaskItem(3, "Din Kültürü", "AYT Din Kültürü ve Mezhepler Soru Çözümü", 45, 25, "Soru Çözümü"),
            // Perşembe
            ReadyTaskItem(4, "Coğrafya-2", "Türkiye'de Sanayi, Tarım, Madenler & Ulaşım", 60, 35, "Konu & Soru"),
            ReadyTaskItem(4, "Felsefe Grubu", "Sosyoloji Toplumsal Yapı & Bilgi Felsefesi", 60, 30, "Konu & Soru"),
            ReadyTaskItem(4, "Türkçe", "Sözcükte Yapı, Ekler & Cümle Türleri", 60, 35, "Soru Çözümü"),
            // Cuma
            ReadyTaskItem(5, "Edebiyat", "Eser-Yazar Hafıza Kartları & Karma Deneme Testi", 75, 45, "Hafıza Kampı"),
            ReadyTaskItem(5, "Tarih & Coğrafya", "Tarih ve Coğrafya Karma Alan Taraması", 75, 45, "Karma Soru"),
            ReadyTaskItem(5, "Temel Matematik", "Problemler: Yaş, Sayı, Kesir Çözümleri", 50, 25, "Problem"),
            // Cumartesi
            ReadyTaskItem(6, "Genel Deneme", "165 Dk TYT Genel Deneme Sınavı", 165, 120, "Deneme"),
            ReadyTaskItem(6, "Deneme Analizi", "Sözel & Sosyal Net Kayıpları Analiz Defteri", 60, 0, "Analiz"),
            // Pazar
            ReadyTaskItem(7, "AYT Sözel Deneme", "AYT Sözel-2 Alan Denemesi (80 Soru)", 180, 80, "Deneme"),
            ReadyTaskItem(7, "Koçluk & Değerlendirme", "Haftalık Sözel Ezber Kontrolü & Koç Notu", 60, 20, "Etüt")
        )
    )

    val DIL_PROGRAM = ReadyCurriculum(
        id = "dil_ydt",
        title = "YKS YDT (Dil) Şampiyon Programı",
        field = "DİL (YDT)",
        subtitle = "İngilizce Öğretmenliği & Mütercim Tercümanlık Hedefli",
        badge = "Haftalık 28 Saat • 850 Soru",
        description = "YDT Vocabulary, Cloze Test, Reading pasaj analizleri ve gramer soru kampları ile TYT desteğini birleştiren yabancı dil programı.",
        weeklyHours = 28.0,
        weeklyQuestions = 850,
        recommendedNets = "Hedef: 80 - 95+ TYT / 74 - 79+ YDT",
        primaryColorHex = 0xFF059669,
        tasks = listOf(
            ReadyTaskItem(1, "YDT Vocabulary", "Phrasal Verbs & Academic Word List (50 Kelime)", 60, 40, "Kelime"),
            ReadyTaskItem(1, "YDT Reading", "5 Uzun Pasaj Analizi & Çeviri Çalışması", 75, 30, "Okuma"),
            ReadyTaskItem(1, "TYT Türkçe", "Paragraf Hız Testi", 35, 25, "Rutin"),
            ReadyTaskItem(2, "YDT Grammar", "Tenses, Modals & Passive Voice Soru Çözümü", 75, 45, "Gramer"),
            ReadyTaskItem(2, "TYT Matematik", "Temel Matematik Problemler Kampı", 60, 30, "Temel"),
            ReadyTaskItem(3, "YDT Reading & Skills", "Sentence Completion & Dialogue Questions", 75, 40, "Soru Çözümü"),
            ReadyTaskItem(3, "TYT Sosyal", "Tarih ve Coğrafya Hızlı Konu Tekrarı", 60, 30, "Ödev"),
            ReadyTaskItem(4, "YDT Cloze Test", "Cloze Test & Restatement Özel Taktik Testi", 60, 35, "Taktik"),
            ReadyTaskItem(4, "TYT Türkçe", "Dil Bilgisi ve Noktalama İşaretleri", 45, 30, "Ödev"),
            ReadyTaskItem(5, "YDT Translation", "İngilizce-Türkçe / Türkçe-İngilizce Çeviri Testi", 60, 40, "Soru Çözümü"),
            ReadyTaskItem(5, "Hata Defteri", "Yanlış Yapılan Kelime ve Gramer Kartları", 45, 20, "Analiz"),
            ReadyTaskItem(6, "Genel Deneme", "165 Dk TYT Genel Deneme Sınavı", 165, 120, "Deneme"),
            ReadyTaskItem(7, "YDT Alan Denemesi", "80 Soruluk Tam YDT Alan Denemesi (120 Dk)", 120, 80, "Deneme"),
            ReadyTaskItem(7, "Deneme Analizi", "YDT Pasaj ve Kelime Hata Defteri Taraması", 60, 0, "Analiz")
        )
    )

    val LGS_PROGRAM = ReadyCurriculum(
        id = "lgs_hazirlik",
        title = "LGS 8. Sınıf Fen Lisesi Hazırlık Programı",
        field = "LGS",
        subtitle = "Yeni Nesil Mantık Muhakeme & Beceri Temelli Sorular",
        badge = "Haftalık 24 Saat • 750 Soru",
        description = "LGS Matematik yeni nesil problem çözme, Fen Bilimleri deneysel kurgu ve Türkçe paragraf muhakeme tekniklerini kapsayan 8. sınıf rehberi.",
        weeklyHours = 24.0,
        weeklyQuestions = 750,
        recommendedNets = "Hedef: 470 - 495+ LGS Puanı",
        primaryColorHex = 0xFFDC2626,
        tasks = listOf(
            ReadyTaskItem(1, "LGS Matematik", "Çarpanlar ve Katlar & Üslü İfadeler Yeni Nesil", 75, 35, "Yeni Nesil"),
            ReadyTaskItem(1, "LGS Türkçe", "Paragrafta Muhakeme ve Sözel Mantık", 45, 25, "Rutin"),
            ReadyTaskItem(2, "LGS Fen Bilimleri", "Mevsimler ve İklim & DNA ve Genetik Kod", 60, 30, "Deneysel Soru"),
            ReadyTaskItem(2, "T.C. İnkılap Tarihi", "Bir Kahraman Doğuyor & Milli Uyanış", 45, 25, "Konu & Soru"),
            ReadyTaskItem(3, "LGS Matematik", "Kareköklü İfadeler & Veri Analizi", 75, 35, "Yeni Nesil"),
            ReadyTaskItem(3, "LGS İngilizce & Din", "Friendship Ünitesi & Kader İnancı", 45, 30, "Ödev"),
            ReadyTaskItem(4, "LGS Fen Bilimleri", "Basınç (Katı, Sıvı, Gaz) Deney Soruları", 60, 30, "Soru Çözümü"),
            ReadyTaskItem(4, "LGS Türkçe", "Metin Türleri & Yazım Kuralları", 45, 25, "Ödev"),
            ReadyTaskItem(5, "LGS Matematik", "Olasılık & Cebirsel İfadeler Beceri Temelli", 75, 35, "Yeni Nesil"),
            ReadyTaskItem(5, "Hata Defteri", "Haftalık Çözülemeyen Yeni Nesil Soru Tekrarı", 45, 15, "Hata Analizi"),
            ReadyTaskItem(6, "LGS Tam Deneme", "Sözel Bölüm (75 Dk) + Sayısal Bölüm (80 Dk) Tam Deneme", 155, 90, "Deneme"),
            ReadyTaskItem(7, "Deneme Analizi", "LGS Soru Taraması & Koç Analizi", 60, 0, "Analiz")
        )
    )

    val ALL_CURRICULA = listOf(
        SAYISAL_PROGRAM,
        ESIT_AGIRLIK_PROGRAM,
        SOZEL_PROGRAM,
        DIL_PROGRAM,
        LGS_PROGRAM
    )
}
