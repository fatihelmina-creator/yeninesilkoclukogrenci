package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CoachEntity
import com.example.data.model.CoachRequestEntity
import com.example.data.model.DailyMotivationEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        TaskEntity::class,
        ExamEntity::class,
        ResourceEntity::class,
        CoachEntity::class,
        CoachRequestEntity::class,
        DailyMotivationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eduDao(): EduDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edurehber_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(getInstance(context).eduDao())
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: EduDao) {
            // Default user (Coach Fatih)
            dao.insertUser(
                UserEntity(
                    email = "fatiherzik72@gmail.com",
                    name = "Fatih Erzik",
                    role = UserRole.TEACHER,
                    isEmailVerified = true,
                    verificationCode = "849201",
                    titleOrTarget = "YKS & LGS Bireysel Gelişim Koordinatörü",
                    bio = "Matematik ve bireysel deneme stratejisi uzmanı. 8 yılı aşkın derece koçluğu tecrübesi."
                )
            )

            // Seed Students
            val seedStudents = listOf(
                StudentEntity(
                    id = "stu_1",
                    name = "Zeynep Kaya",
                    email = "zeynep.kaya@gmail.com",
                    target = "YKS (Sayısal) - Tıp Hedefi",
                    targetNet = 108.0,
                    avgNet = 89.5,
                    isInPool = false,
                    assignedCoachEmail = "fatiherzik72@gmail.com",
                    needs = "TYT Geometri ve Fizik Optik netlerini artırmak, haftalık soru kampları uygulamak istiyor."
                ),
                StudentEntity(
                    id = "stu_2",
                    name = "Can Demir",
                    email = "can.demir@gmail.com",
                    target = "YKS (Eşit Ağırlık) - Hukuk Hedefi",
                    targetNet = 96.0,
                    avgNet = 78.0,
                    isInPool = false,
                    assignedCoachEmail = "fatiherzik72@gmail.com",
                    needs = "Matematik problemleri, paragraf süre yönetimi ve edebiyat konu tekrarları."
                ),
                StudentEntity(
                    id = "stu_3",
                    name = "Elif Şahin",
                    email = "elif.sahin@gmail.com",
                    target = "LGS 2025 - Fen Lisesi",
                    targetNet = 485.0,
                    avgNet = 432.0,
                    isInPool = true,
                    assignedCoachEmail = null,
                    needs = "Haftalık düzenli deneme analizi ve yeni nesil fen/matematik soru çözümü koçu arıyor."
                ),
                StudentEntity(
                    id = "stu_4",
                    name = "Burak Yılmaz",
                    email = "burak.yilmaz@gmail.com",
                    target = "YKS (Sayısal) - İTÜ Bilgisayar",
                    targetNet = 112.0,
                    avgNet = 94.0,
                    isInPool = true,
                    assignedCoachEmail = null,
                    needs = "AYT Matematik İntegral/Türev ve Organik Kimya hızlandırılmış programı arıyor."
                ),
                StudentEntity(
                    id = "stu_5",
                    name = "Ayşe Nur Çelik",
                    email = "aysenur.celik@gmail.com",
                    target = "YKS (Dil) - İngilizce Öğretmenliği",
                    targetNet = 76.0,
                    avgNet = 64.5,
                    isInPool = true,
                    assignedCoachEmail = null,
                    needs = "YDT Vocabulary, Reading analizleri ve haftalık deneme takibi.",
                    isApprovedByTeacher = true,
                    isEmailVerified = true
                ),
                StudentEntity(
                    id = "stu_6",
                    name = "Eren Yıldız",
                    email = "eren.yildiz@ogrenci.com",
                    target = "YKS (Sayısal) - ODTÜ Mühendislik",
                    targetNet = 110.0,
                    avgNet = 91.5,
                    isInPool = false,
                    assignedCoachEmail = "fatiherzik72@gmail.com",
                    needs = "E-posta doğrulandı, koç onayı ve haftalık ödev takvimi atanması bekleniyor.",
                    isApprovedByTeacher = false,
                    isEmailVerified = true
                )
            )
            dao.insertStudents(seedStudents)

            // Seed Tasks for Zeynep Kaya
            val seedTasks = listOf(
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 1,
                    subject = "TYT Matematik",
                    title = "Problemler & Sayı Mantığı Hız Testi (40 Soru)",
                    durationMinutes = 60,
                    targetQuestions = 40,
                    isCompleted = true
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 2,
                    subject = "Fizik",
                    title = "Optik & Dalgalar Konu Özeti ve Soru Çözümü",
                    durationMinutes = 50,
                    targetQuestions = 30,
                    isCompleted = true
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 3,
                    subject = "Türkçe",
                    title = "Paragraf Hız Denemesi (25 Soru - 28 dk)",
                    durationMinutes = 35,
                    targetQuestions = 25,
                    isCompleted = false
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 4,
                    subject = "Geometri",
                    title = "Üçgende Alan & Benzerlik Soru Çözümü",
                    durationMinutes = 60,
                    targetQuestions = 35,
                    isCompleted = false
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 5,
                    subject = "Kimya",
                    title = "Gazlar & Sıvı Çözeltiler AYT Tekrarı",
                    durationMinutes = 45,
                    targetQuestions = 30,
                    isCompleted = false
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 6,
                    subject = "Genel Deneme",
                    title = "135 Dk Süre Tutarak TYT Genel Deneme Sınavı",
                    durationMinutes = 135,
                    targetQuestions = 120,
                    isCompleted = false
                ),
                TaskEntity(
                    studentId = "stu_1",
                    dayOfWeek = 7,
                    subject = "Rehberlik & Analiz",
                    title = "Haftalık Yanlış Defteri Kontrolü & Koç Değerlendirmesi",
                    durationMinutes = 40,
                    targetQuestions = 15,
                    isCompleted = false
                )
            )
            dao.insertTasks(seedTasks)

            // Seed Exams for Zeynep Kaya
            val seedExams = listOf(
                ExamEntity(
                    studentId = "stu_1",
                    title = "Özdebir Türkiye Geneli TYT-1",
                    trNet = 31.5,
                    matNet = 24.5,
                    fenNet = 12.0,
                    sosNet = 14.5,
                    totalNet = 82.5,
                    dateString = "10 Ağu",
                    notes = "Matematikte zaman kontrolü zorladı, Türkçe paragrafta odaklanma iyiydi."
                ),
                ExamEntity(
                    studentId = "stu_1",
                    title = "3D Simülasyon TYT-2",
                    trNet = 33.0,
                    matNet = 27.5,
                    fenNet = 13.5,
                    sosNet = 15.0,
                    totalNet = 89.0,
                    dateString = "18 Ağu",
                    notes = "Geometri netlerinde artış var. Fen fizik dalgalar konusuna çalışılmalı."
                ),
                ExamEntity(
                    studentId = "stu_1",
                    title = "Karekök Kurumsal TYT-3",
                    trNet = 34.5,
                    matNet = 29.0,
                    fenNet = 14.25,
                    sosNet = 16.0,
                    totalNet = 93.75,
                    dateString = "27 Ağu",
                    notes = "Süre yönetimi başarılı. 95 net barajına çok yaklaşıldı."
                )
            )
            dao.insertExams(seedExams)

            // Seed Resources (Official MEB/OGM & Teacher Docs)
            val seedResources = listOf(
                ResourceEntity(
                    id = "res_meb_1",
                    title = "OGM Materyal - Soru Bankası & YKS Deneme Portali",
                    category = "MEB & OGM Materyal",
                    author = "MEB Ortaöğretim Genel Müdürlüğü",
                    downloads = 14250,
                    description = "Milli Eğitim Bakanlığı resmi ders kitapları, konu özetleri, etkileşimli deneyler, 3D modeller ve YKS mini denemeleri.",
                    urlOrUri = "https://ogmmateryal.eba.gov.tr",
                    targetField = "GENEL",
                    fileSizeOrType = "Resmi MEB Portali",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_meb_2",
                    title = "EBA - Eğitim Bilişim Ağı Dijital Ders Kütüphanesi",
                    category = "MEB & OGM Materyal",
                    author = "Milli Eğitim Bakanlığı",
                    downloads = 28400,
                    description = "Tüm sınıf kademeleri için MEB onaylı video ders anlatımları, interaktif alıştırmalar ve canlı ders arşivi.",
                    urlOrUri = "https://www.eba.gov.tr",
                    targetField = "GENEL",
                    fileSizeOrType = "Resmi MEB Portali",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_meb_3",
                    title = "MEB ÖDSGM - Kazanım Kavrama & Değerlendirme Testleri",
                    category = "MEB & OGM Materyal",
                    author = "Ölçme, Değerlendirme ve Sınav Hizmetleri GM",
                    downloads = 19800,
                    description = "9, 10, 11, 12. Sınıf ve Mezun grupları için MEB resmi kazanım testleri PDF arşivi ve beceri temelli sorular.",
                    urlOrUri = "https://odsgm.meb.gov.tr/kurslar",
                    targetField = "SAYISAL / EA / SÖZEL",
                    fileSizeOrType = "Resmi MEB Soru Havuzu",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_meb_4",
                    title = "EBA Akademik Destek - Akıllı YKS Koçluk Sistemi",
                    category = "MEB & OGM Materyal",
                    author = "MEB Akademik Destek Ekibi",
                    downloads = 11300,
                    description = "11 ve 12. sınıflar için hedefe göre yapay zeka ile eksik konu analizi ve kişiye özel YKS deneme motoru.",
                    urlOrUri = "https://akademikdestek.eba.gov.tr",
                    targetField = "YKS (SAY/EA/SÖZ)",
                    fileSizeOrType = "Akıllı YKS Portali",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_meb_5",
                    title = "ÖSYM - Son 10 Yıl YKS (TYT-AYT-YDT) Çıkmış Sorular",
                    category = "MEB & OGM Materyal",
                    author = "ÖSYM Resmi Arşivi",
                    downloads = 32100,
                    description = "ÖSYM resmi portalında yayınlanan tüm geçmiş yıl TYT, AYT ve YDT soru kitapçıkları ve ayrıntılı cevap anahtarları.",
                    urlOrUri = "https://www.osym.gov.tr/TR,15104/yks-cikmis-sorular.html",
                    targetField = "GENEL",
                    fileSizeOrType = "ÖSYM Resmi Arşivi",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_meb_6",
                    title = "MEB Yardımcı Kaynaklar Destek Paketi & Fasiküller",
                    category = "MEB & OGM Materyal",
                    author = "Milli Eğitim Bakanlığı",
                    downloads = 8900,
                    description = "Çalışma fasikülleri, tekrar testleri ve yeni nesil soru bankalarını içeren ücretsiz MEB dijital destek paketi.",
                    urlOrUri = "https://yardimcikaynaklar.meb.gov.tr",
                    targetField = "GENEL / LGS",
                    fileSizeOrType = "MEB Dijital Paket",
                    isOfficialMeb = true
                ),
                ResourceEntity(
                    id = "res_1",
                    title = "TYT Matematik 30 Günlük Problem Kampı PDF",
                    category = "Ders Notu & Kamp",
                    author = "Fatih Erzik",
                    downloads = 1840,
                    description = "Adım adım yeni nesil denklem kurma, grafik okuma ve yüzde-kar-zarar stratejileri.",
                    urlOrUri = "https://ogmmateryal.eba.gov.tr",
                    targetField = "SAYISAL / EA",
                    fileSizeOrType = "PDF (4.2 MB)",
                    contentNotes = "TYT Problem Çözüm Stratejileri:\n1. Verilenleri ve isteneni tabloya dök.\n2. Değişken sayısını minimuma indir.\n3. Oran-orantı mantığıyla zamandan kazan."
                ),
                ResourceEntity(
                    id = "res_2",
                    title = "Haftalık Yanlış Defteri & Hata Analiz Şablonu",
                    category = "Rehberlik & Takip",
                    author = "EduRehber Ekibi",
                    downloads = 3420,
                    description = "Denemelerde yapılan hataları sınıflandırma ve 14 gün sonra tekrar çözme kılavuzu.",
                    urlOrUri = "https://ogmmateryal.eba.gov.tr",
                    targetField = "GENEL",
                    fileSizeOrType = "PDF (1.8 MB)",
                    contentNotes = "Hata Analiz Döngüsü:\n- Dikkat Hatası mı? Bilgi Eksikliği mi? Süre Yetersizliği mi?\n- Her yanlış soru deftere yapıştırılır ve 14 gün sonra tekrar çözülür."
                ),
                ResourceEntity(
                    id = "res_3",
                    title = "Fizik Optik & Dalgalar Formül & Kavram Özeti",
                    category = "Özet Formül",
                    author = "EduRehber Mentorları",
                    downloads = 2150,
                    description = "Tüm optik kuralları, kırılma indisleri ve dalga formüllerini tek sayfada toplayan rehber.",
                    urlOrUri = "https://ogmmateryal.eba.gov.tr",
                    targetField = "SAYISAL",
                    fileSizeOrType = "PDF (2.4 MB)",
                    contentNotes = "Optik & Dalga Özeti:\n- n1 * sin(i) = n2 * sin(r)\n- v = f * lambda\n- Çukur ve tümsek ayna odak özellikleri."
                ),
                ResourceEntity(
                    id = "res_4",
                    title = "YKS Son 10 Yıl Çıkmış Paragraf Soru Dağılımları",
                    category = "Analiz Raporu",
                    author = "Fatih Erzik",
                    downloads = 4120,
                    description = "Ana düşünce, yardımcı düşünce, anlatım teknikleri ve akış bozan cümle istatistikleri.",
                    urlOrUri = "https://www.osym.gov.tr",
                    targetField = "GENEL",
                    fileSizeOrType = "PDF (3.1 MB)",
                    contentNotes = "Paragrafta Hız Taktikleri:\n- Önce soru kökünü ve şıkları tara.\n- Anahtar kelimeleri çizerek oku.\n- Süre: Soru başına ortalama 65 saniyeyi geçme."
                )
            )
            dao.insertResources(seedResources)

            // Seed Coaches with real demand counts and ratings
            val seedCoaches = listOf(
                CoachEntity(
                    id = "coach_1",
                    name = "Fatih Erzik",
                    email = "fatiherzik72@gmail.com",
                    title = "Matematik & YKS Derece Koçu",
                    bio = "Bireysel net analizi, süre yönetimi ve 7 günlük kişisel soru planlama odaklı koçluk programı.",
                    requestsCount = 28,
                    activeStudentsCount = 4,
                    maxCapacity = 6,
                    completedSessionsCount = 142,
                    successRatePct = 96,
                    rating = 4.9,
                    specialty = "Sayısal & YKS"
                ),
                CoachEntity(
                    id = "coach_2",
                    name = "Merve Aydın",
                    email = "merve.aydin@edurehber.com",
                    title = "Eşit Ağırlık & Motivasyon Mentörü",
                    bio = "Sınav kaygısı, odaklanma ve haftalık düzenli çalışma takibi konusunda uzmanlaşmış rehber.",
                    requestsCount = 35,
                    activeStudentsCount = 5,
                    maxCapacity = 5,
                    completedSessionsCount = 198,
                    successRatePct = 98,
                    rating = 5.0,
                    specialty = "Eşit Ağırlık & Sözel"
                ),
                CoachEntity(
                    id = "coach_3",
                    name = "Ahmet Yılmaz",
                    email = "ahmet.yilmaz@edurehber.com",
                    title = "Fen Bilimleri & LGS Danışmanı",
                    bio = "LGS yeni nesil fen soruları ve deneysel mantık muhakeme çalışmaları üzerine mentörlük.",
                    requestsCount = 19,
                    activeStudentsCount = 3,
                    maxCapacity = 5,
                    completedSessionsCount = 87,
                    successRatePct = 94,
                    rating = 4.8,
                    specialty = "LGS & Sayısal"
                ),
                CoachEntity(
                    id = "coach_4",
                    name = "Selin Demirtaş",
                    email = "selin.demirtas@edurehber.com",
                    title = "YDT & İngilizce Dil Koçu",
                    bio = "YDT Vocabulary, Reading analizleri, gramer soru kampları ve haftalık yabancı dil takibi.",
                    requestsCount = 22,
                    activeStudentsCount = 2,
                    maxCapacity = 4,
                    completedSessionsCount = 110,
                    successRatePct = 95,
                    rating = 4.8,
                    specialty = "YDT (Dil)"
                )
            )
            dao.insertCoaches(seedCoaches)

            // Seed Initial Daily Motivation Note
            dao.insertDailyMotivation(
                DailyMotivationEntity(
                    id = 1,
                    dateString = "Bugün • Rehberlik Köşesi",
                    title = "Günün İradesi: Küçük Adımlar, Büyük Zirveler",
                    content = "Sınava hazırlık süreci 100 metrelik bir sprint değil, kararlılıkla koşulan bir maratondur. Bugün çözeceğin fazladan 25 soru veya tamamlayacağın 1 odaklanma etütü, haziran ayındaki hayallerinin teminatıdır. İnan, odaklan ve vazgeçme!",
                    authorName = "Koç Fatih Erzik",
                    authorRole = "YKS & LGS Baş Danışmanı",
                    category = "Disiplin & Odaklanma",
                    isGeneratedByAI = false,
                    likesCount = 48
                )
            )
        }
    }
}
