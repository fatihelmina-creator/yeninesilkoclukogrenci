package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val displayName: String, val badgeText: String) {
    TEACHER("Öğretmen / Koç", "ÖĞRETMEN & KOÇ"),
    STUDENT("Öğrenci", "ÖĞRENCİ PORTALI")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val role: UserRole,
    val isEmailVerified: Boolean = false,
    val verificationCode: String = "849201",
    val titleOrTarget: String = "YKS Koçu",
    val bio: String = "Öğrenci gelişim ve stratejik rehberlik uzmanı.",
    val schoolOrInstitution: String = "EduRehber Akademi",
    val isTeacherApproved: Boolean = true, // For students: indicates teacher approval
    val assignedTeacherEmail: String? = "fatiherzik72@gmail.com",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val target: String,
    val targetNet: Double,
    val avgNet: Double,
    val isInPool: Boolean,
    val assignedCoachEmail: String?,
    val needs: String,
    val isApprovedByTeacher: Boolean = true,
    val isEmailVerified: Boolean = true
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val dayOfWeek: Int, // 1=Pazartesi .. 7=Pazar
    val subject: String,
    val title: String,
    val durationMinutes: Int,
    val targetQuestions: Int,
    val isCompleted: Boolean = false,
    val assignedBy: String = "Öğretmen / Koç",
    val taskType: String = "Ödev" // Ödev, Etüt, Deneme, Soru Çözümü
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val title: String,
    val trNet: Double,
    val matNet: Double,
    val fenNet: Double,
    val sosNet: Double,
    val totalNet: Double,
    val dateString: String,
    val notes: String = ""
)

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "MEB Resmi Portal", "OGM Materyal", "Ders Notu & Kamp", "Rehberlik & Takip", "Özet Formül"
    val author: String,
    val downloads: Int,
    val description: String,
    val urlOrUri: String = "", // Real URL or URI
    val targetField: String = "GENEL", // SAYISAL, EŞİT AĞIRLIK, SÖZEL, GENEL, LGS
    val fileSizeOrType: String = "PDF", // "Resmi Web Portali", "PDF (3.8 MB)", "Ders Notu"
    val isOfficialMeb: Boolean = false,
    val authorEmail: String? = null,
    val contentNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coaches")
data class CoachEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val title: String,
    val bio: String,
    val requestsCount: Int = 18, // Talep sayısı
    val activeStudentsCount: Int = 4, // Aktif Kadro
    val maxCapacity: Int = 6, // Kontenjan
    val completedSessionsCount: Int = 142, // Seans sayısı
    val successRatePct: Int = 96, // Başarı skoru %
    val rating: Double = 4.9, // Dinamik hesaplanan performans yıldızı
    val specialty: String = "Sayısal & YKS",
    val verifiedBadge: Boolean = true
)

@Entity(tableName = "daily_motivations")
data class DailyMotivationEntity(
    @PrimaryKey val id: Long = 1,
    val dateString: String = "Bugün",
    val title: String = "Günün İradesi: Küçük Adımlar, Büyük Zirveler",
    val content: String = "Yol yokuş yukarı olduğunda durma isteği normaldir. Başarıyı getiren şey, tam da o an fazladan 20 soru çözebilme disiplinidir. Gelecekteki sen, bugünkü emeğine teşekkür edecek.",
    val authorName: String = "Koç Fatih Erzik",
    val authorRole: String = "YKS & LGS Baş Danışmanı",
    val category: String = "Disiplin & Odaklanma", // "Disiplin & Odaklanma", "Sınav Stratejisi", "Pes Etmeme", "Hız & Zaman Yönetimi"
    val isGeneratedByAI: Boolean = false,
    val likesCount: Int = 42,
    val updatedAt: Long = System.currentTimeMillis()
)

data class ExamMilestone(
    val title: String,
    val dateRange: String,
    val description: String,
    val isPassed: Boolean = false,
    val tag: String = "Strateji"
)

enum class ExamType(
    val code: String,
    val displayName: String,
    val subtitle: String,
    val examDateMillis: Long, // timestamp
    val examFormattedDate: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val targetAudience: String,
    val badgeColorHex: Long,
    val milestones: List<ExamMilestone>
) {
    TYT(
        code = "TYT",
        displayName = "TYT (Temel Yeterlilik Testi)",
        subtitle = "YKS 1. Oturum - Tüm Adaylar İçin Temel Baraj & Hız Sınavı",
        examDateMillis = 1781856900000L, // 20 Haziran 2026 10:15
        examFormattedDate = "20 Haziran 2026 • 10:15",
        totalQuestions = 120,
        durationMinutes = 165,
        targetAudience = "12. Sınıf & Mezun Adaylar",
        badgeColorHex = 0xFF2563EB,
        milestones = listOf(
            ExamMilestone("TYT Temel Konu Taraması", "Eylül - Kasım", "Türkçe paragraf ve temel matematik kampının bitirilmesi", true, "Konu"),
            ExamMilestone("Branş Denemeleri Maratonu", "Aralık - Şubat", "Haftalık 2 Türkçe ve 2 Matematik branş denemesi", false, "Hız"),
            ExamMilestone("165 Dk Zaman Yönetimi & Kondisyon", "Mart - Nisan", "Gerçek sınav saati provası (10:15 - 13:00)", false, "Kondisyon"),
            ExamMilestone("ÖSYM Çıkmış Sorular & Son Rötuş", "Mayıs - Haziran", "Son 7 yılın tüm çıkmış sorularının analizi", false, "Zirve")
        )
    ),
    AYT(
        code = "AYT",
        displayName = "AYT (Alan Yeterlilik Testi)",
        subtitle = "YKS 2. Oturum - Sayısal, Eşit Ağırlık ve Sözel Branş Sınavı",
        examDateMillis = 1781943300000L, // 21 Haziran 2026 10:15
        examFormattedDate = "21 Haziran 2026 • 10:15",
        totalQuestions = 160,
        durationMinutes = 180,
        targetAudience = "Lisans Programı Hedefleyen Tüm Adaylar",
        badgeColorHex = 0xFF7C3AED,
        milestones = listOf(
            ExamMilestone("11. & 12. Sınıf Derin Konu Hakimiyeti", "Ekim - Ocak", "Limit, Türev, İntegral, Organik Kimya, Edebiyat derinliği", false, "Kazanım"),
            ExamMilestone("Alan Odaklı Soru Bankası Taraması", "Şubat - Mart", "Konu başı en az 300 ileri düzey yeni nesil soru", false, "Pekiştirme"),
            ExamMilestone("AYT Seri Genel Deneme Kampı", "Nisan - Mayıs", "Haftada 3 tam kapsamlı AYT denemesi", false, "Deneme"),
            ExamMilestone("Formül ve Bilgi Kartı Tekrarları", "1 - 19 Haziran", "Unutulan ayrıntı formüller ve hafıza teknikleri", false, "Hafıza")
        )
    ),
    LGS(
        code = "LGS",
        displayName = "LGS (Liselere Geçiş Sistemi)",
        subtitle = "8. Sınıf - Nitelikli Fen, Anadolu ve Sosyal Bilimler Liseleri",
        examDateMillis = 1780727400000L, // 7 Haziran 2026 09:30
        examFormattedDate = "7 Haziran 2026 • 09:30 & 11:30",
        totalQuestions = 90,
        durationMinutes = 155,
        targetAudience = "8. Sınıf Öğrencileri",
        badgeColorHex = 0xFF059669,
        milestones = listOf(
            ExamMilestone("1. Dönem MEB Kazanımları Tamamlama", "Ekim - Aralık", "Tüm derslerin birinci dönem ünitelerinin pekiştirilmesi", false, "Konu"),
            ExamMilestone("MEB Aylık Örnek Sorular Çözümü", "Her Ay Düzenli", "Yayınlanan tüm ÖDSGM örnek sorularının titiz çözümü", false, "MEB Örnek"),
            ExamMilestone("Sayısal Mantık & Yeni Nesil Soru Kampı", "Şubat - Nisan", "Görsel yorumlama ve grafik okuma becerileri", false, "Mantık"),
            ExamMilestone("Süre & Hata Analizi Maratonu", "Mayıs - Haziran", "Gerçek iki oturumlu (Sözel 75 dk + Sayısal 80 dk) denemeler", false, "Sınav Provasi")
        )
    ),
    YDT(
        code = "YDT",
        displayName = "YDT (Yabancı Dil Testi)",
        subtitle = "YKS 3. Oturum - İngilizce, Almanca, Fransızca, Rusça, Arapça",
        examDateMillis = 1781963100000L, // 21 Haziran 2026 15:45
        examFormattedDate = "21 Haziran 2026 • 15:45",
        totalQuestions = 80,
        durationMinutes = 120,
        targetAudience = "Dil Puanıyla Tercih Yapacak Adaylar",
        badgeColorHex = 0xFFEA580C,
        milestones = listOf(
            ExamMilestone("İleri Seviye Kelime & Phrasal Verb", "Eylül - Ocak", "Günde 30 akademik kelime ve okuma parçaları", false, "Kelime"),
            ExamMilestone("Gramer ve Çeviri Teknikleri", "Şubat - Mart", "Cümle tamamlama ve paragraf tamamlama pratikleri", false, "Gramer"),
            ExamMilestone("80 Soruluk Hız Denemeleri", "Nisan - Mayıs", "120 dakikalık tam sınav provaları", false, "Deneme"),
            ExamMilestone("Son Yılların YDT Soruları", "Haziran", "ÖSYM dil testleri soru kalıpları analizi", false, "Final")
        )
    )
}

@Entity(tableName = "coach_requests")
data class CoachRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coachId: String,
    val coachName: String,
    val studentName: String,
    val studentEmail: String,
    val studentTarget: String,
    val currentNet: Double = 0.0,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "BEKLEMEDE" // BEKLEMEDE, KABUL_EDILDI
)

data class ReadyTaskItem(
    val dayOfWeek: Int, // 1=Pzt .. 7=Paz
    val subject: String,
    val title: String,
    val durationMinutes: Int,
    val targetQuestions: Int,
    val taskType: String = "Etüt"
)

data class ReadyCurriculum(
    val id: String,
    val title: String,
    val field: String, // "SAYISAL", "EŞİT AĞIRLIK", "SÖZEL", "DİL (YDT)", "LGS"
    val subtitle: String,
    val badge: String,
    val description: String,
    val weeklyHours: Double,
    val weeklyQuestions: Int,
    val recommendedNets: String,
    val primaryColorHex: Long = 0xFF4F46E5,
    val tasks: List<ReadyTaskItem>
)

data class CoachProfile(
    val id: String,
    val name: String,
    val title: String,
    val rating: Double,
    val capacity: String,
    val bio: String,
    val verifiedBadge: Boolean = true,
    val requestsCount: Int = 0
)

