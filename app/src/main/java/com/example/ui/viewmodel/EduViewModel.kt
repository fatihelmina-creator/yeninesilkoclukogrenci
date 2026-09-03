package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CoachProfile
import com.example.data.model.DailyMotivationEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ExamType
import com.example.data.model.ResourceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.EduRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class MainTab(val title: String) {
    DASHBOARD("Özet"),
    SCHEDULE("Plan"),
    EXAMS("Deneme"),
    POOL("Havuz"),
    POMODORO("Pomodoro"),
    AICOACH("AI Koç")
}

enum class PomodoroMode(val title: String, val totalSeconds: Int) {
    WORK("25 Dk Odaklanma", 25 * 60),
    BREAK("5 Dk Mola", 5 * 60)
}

data class VerificationUiState(
    val email: String = "",
    val isDialogVisible: Boolean = false,
    val otpCode: String = "",
    val generatedCode: String = "",
    val expiresAtMillis: Long = 0L,
    val resendAllowedAtMillis: Long = 0L,
    val validityRemainingSeconds: Int = 180,
    val resendCooldownSeconds: Int = 60,
    val remainingAttempts: Int = 5,
    val isExpired: Boolean = false,
    val isLocked: Boolean = false,
    val isVerifying: Boolean = false,
    val isSendingCode: Boolean = false,
    val errorMessage: String? = null,
    val isVerifiedSuccess: Boolean = false,
    val sentTimestampFormatted: String = "",
    val referenceId: String = ""
)

data class AIReportData(
    val strengths: String,
    val focusAreas: String,
    val tacticalSteps: List<String>,
    val predictedNet: String
)

data class ExamCountdownState(
    val examType: ExamType,
    val remainingDays: Long,
    val remainingHours: Long,
    val remainingMinutes: Long,
    val remainingSeconds: Long,
    val progressPct: Float,
    val totalWeeksLeft: Int,
    val totalWeekendsLeft: Int
)

class EduViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EduRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = EduRepository(database.eduDao())
    }

    // Active User
    private val _currentUserEmail = MutableStateFlow("fatiherzik72@gmail.com")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserEmail
        .combine(repository.observeUser(_currentUserEmail.value)) { _, user -> user }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation Tab
    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Selected Student in Coach View
    private val _selectedStudentId = MutableStateFlow("stu_1")
    val selectedStudentId: StateFlow<String> = _selectedStudentId.asStateFlow()

    // Selected Day in Schedule View (1=Pazartesi .. 7=Pazar)
    private val _selectedScheduleDay = MutableStateFlow(getTodayDayOfWeek())
    val selectedScheduleDay: StateFlow<Int> = _selectedScheduleDay.asStateFlow()

    // Email Verification State
    private val _verificationState = MutableStateFlow(VerificationUiState())
    val verificationState: StateFlow<VerificationUiState> = _verificationState.asStateFlow()

    // Pool Sub-tab (0: Students, 1: Coaches, 2: Resources)
    private val _poolSubTabIndex = MutableStateFlow(0)
    val poolSubTabIndex: StateFlow<Int> = _poolSubTabIndex.asStateFlow()

    private val _poolSearchQuery = MutableStateFlow("")
    val poolSearchQuery: StateFlow<String> = _poolSearchQuery.asStateFlow()

    // Flow collections
    val allStudents: StateFlow<List<StudentEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coachStudents: StateFlow<List<StudentEntity>> = _currentUserEmail
        .combine(repository.getAllStudents()) { email, list ->
            list.filter { it.assignedCoachEmail == email && it.isApprovedByTeacher }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingApprovalStudents: StateFlow<List<StudentEntity>> = repository.getPendingApprovalStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val poolStudents: StateFlow<List<StudentEntity>> = repository.getPoolStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResources: StateFlow<List<ResourceEntity>> = repository.getAllResources()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coachesListFlow: StateFlow<List<com.example.data.model.CoachEntity>> = repository.getAllCoaches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coachRequests: StateFlow<List<com.example.data.model.CoachRequestEntity>> = repository.getAllCoachRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readyCurricula = com.example.data.curriculum.CurriculumTemplates.ALL_CURRICULA

    private val _selectedCurriculumIndex = MutableStateFlow(0)
    val selectedCurriculumIndex: StateFlow<Int> = _selectedCurriculumIndex.asStateFlow()

    // Dynamic tasks for selected student
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentStudentTasks: StateFlow<List<TaskEntity>> = _selectedStudentId
        .flatMapLatest { stuId -> repository.getTasksForStudent(stuId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic exams for selected student
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentStudentExams: StateFlow<List<ExamEntity>> = _selectedStudentId
        .flatMapLatest { stuId -> repository.getExamsForStudent(stuId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pomodoro Timer State
    private val _pomodoroMode = MutableStateFlow(PomodoroMode.WORK)
    val pomodoroMode: StateFlow<PomodoroMode> = _pomodoroMode.asStateFlow()

    private val _pomodoroSeconds = MutableStateFlow(25 * 60)
    val pomodoroSeconds: StateFlow<Int> = _pomodoroSeconds.asStateFlow()

    private val _isPomodoroRunning = MutableStateFlow(false)
    val isPomodoroRunning: StateFlow<Boolean> = _isPomodoroRunning.asStateFlow()

    private val _pomodoroCycles = MutableStateFlow(0)
    val pomodoroCycles: StateFlow<Int> = _pomodoroCycles.asStateFlow()

    private val _pomodoroTotalMinutes = MutableStateFlow(0)
    val pomodoroTotalMinutes: StateFlow<Int> = _pomodoroTotalMinutes.asStateFlow()

    private var pomodoroJob: Job? = null
    private var verificationTimerJob: Job? = null

    // AI Strategic Report
    private val _aiReport = MutableStateFlow<AIReportData?>(null)
    val aiReport: StateFlow<AIReportData?> = _aiReport.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Daily Motivation
    val dailyMotivation: StateFlow<DailyMotivationEntity?> = repository.getDailyMotivation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Exam Countdown State
    private val _selectedCountdownExam = MutableStateFlow(ExamType.TYT)
    val selectedCountdownExam: StateFlow<ExamType> = _selectedCountdownExam.asStateFlow()

    // Live tick for countdowns
    private val _currentTickerTime = MutableStateFlow(System.currentTimeMillis())
    val currentTickerTime: StateFlow<Long> = _currentTickerTime.asStateFlow()

    private var countdownTickerJob: Job? = null

    init {
        // Initial setup
        generateAIReport()
        startCountdownTicker()
    }

    private fun startCountdownTicker() {
        countdownTickerJob?.cancel()
        countdownTickerJob = viewModelScope.launch {
            while (true) {
                _currentTickerTime.value = System.currentTimeMillis()
                delay(1000) // 1 second tick for live countdown
            }
        }
    }

    fun setSelectedCountdownExam(examType: ExamType) {
        _selectedCountdownExam.value = examType
    }

    fun calculateCountdown(exam: ExamType): ExamCountdownState {
        val now = _currentTickerTime.value
        var target = exam.examDateMillis
        if (target <= now) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = target
                add(Calendar.YEAR, 1)
            }
            target = cal.timeInMillis
        }
        val diff = (target - now).coerceAtLeast(0L)
        val seconds = (diff / 1000) % 60
        val minutes = (diff / (1000 * 60)) % 60
        val hours = (diff / (1000 * 60 * 60)) % 24
        val days = diff / (1000 * 60 * 60 * 24)
        val totalWeeks = (days / 7).toInt()
        val totalWeekends = totalWeeks * 2

        val totalPrepDays = 280f
        val passedDays = (totalPrepDays - days.toFloat()).coerceIn(0f, totalPrepDays)
        val progress = passedDays / totalPrepDays

        return ExamCountdownState(
            examType = exam,
            remainingDays = days,
            remainingHours = hours,
            remainingMinutes = minutes,
            remainingSeconds = seconds,
            progressPct = progress,
            totalWeeksLeft = totalWeeks,
            totalWeekendsLeft = totalWeekends
        )
    }

    // Daily Motivation Coach / AI Methods
    fun updateDailyMotivation(
        title: String,
        content: String,
        authorName: String,
        authorRole: String,
        category: String,
        isAI: Boolean
    ) {
        viewModelScope.launch {
            val current = dailyMotivation.value
            val newEntity = DailyMotivationEntity(
                id = 1,
                dateString = "Bugün • Rehberlik Notu",
                title = title.ifBlank { "Günün İradesi" },
                content = content,
                authorName = authorName.ifBlank { "Koç Fatih Erzik" },
                authorRole = authorRole.ifBlank { "YKS & LGS Baş Mentörü" },
                category = category,
                isGeneratedByAI = isAI,
                likesCount = current?.likesCount ?: 42,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateDailyMotivation(newEntity)
        }
    }

    fun likeDailyMotivation() {
        viewModelScope.launch {
            repository.likeDailyMotivation()
        }
    }

    fun generateAIMotivationQuote(category: String? = null): Pair<String, String> {
        val quotes = listOf(
            Pair(
                "Günün İradesi: Küçük Adımlar, Büyük Zirveler",
                "Sınava hazırlık süreci 100 metrelik bir sprint değil, kararlılıkla koşulan bir maratondur. Bugün çözeceğin fazladan 25 soru veya tamamlayacağın 1 odaklanma etütü, haziran ayındaki hayallerinin teminatıdır. İnan, odaklan ve vazgeçme!"
            ),
            Pair(
                "Denemelerdeki Hatalar En Dürüst Pusulandır",
                "Denemelerde yaptığın yanlışlar senin moralini bozmak için değil, sınav gününde eksiksiz olmanı sağlamak için birer fırsattır. Yanlış yaptığın her sorunun çözümünü öğrenmeden günü tamamlama. Başarı, hatalarından ders çıkaranındır."
            ),
            Pair(
                "Zihinsel Odaklanma ve Masa Başında Disiplin",
                "Masanın başına oturduğunda telefonunu ve dikkat dağıtıcıları geride bırak. 25 dakikalık tam odaklanmış bir çalışma, yarım yamalak geçen 2 saatten çok daha değerlidir. Zihnini hedefine kilitle."
            ),
            Pair(
                "Son Viraj Marifeti: Sabır ve Kararlılık",
                "Herkesin yorulduğu ve tempoyu düşürdüğü anda çalışanlar, sıralamada binlerce kişinin önüne geçer. Yorgun hissettiğinde hedeflerini, kazanacağın üniversite amfisini veya liseni hayal et ve bir soru daha çöz!"
            ),
            Pair(
                "TYT & LGS Hız Sırrı: Süre Yönetimi ve Soğukkanlılık",
                "Sınav sadece bilgiyi değil, baskı anında süreyi nasıl yönettiğini de ölçer. Turlama tekniğini benimse, takıldığın soruyla inatlaşma ve ritmini koru. Sen planına sadık kalırsan sınav senin istediğin gibi sonuçlanır."
            ),
            Pair(
                "Kendine İnan: Emek Asla Karşılıksız Kalmaz",
                "Sabahın erken saatlerinde, gecenin sessizliğinde döktüğün her damla alın teri zihninde kalıcı bir güç oluşturuyor. Kendine ve potansiyeline güven. Başaracaksın!"
            )
        )
        return quotes.random()
    }

    fun applyAIMotivationNow() {
        viewModelScope.launch {
            val (title, content) = generateAIMotivationQuote()
            val categories = listOf("Disiplin & Odaklanma", "Sınav Stratejisi", "Pes Etmeme", "Hız & Süre Yönetimi")
            updateDailyMotivation(
                title = title,
                content = content,
                authorName = "EduRehber AI Danışmanı",
                authorRole = "Yapay Zeka Destekli Mentör",
                category = categories.random(),
                isAI = true
            )
        }
    }

    private fun getTodayDayOfWeek(): Int {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, 3=Tuesday...
        return if (day == Calendar.SUNDAY) 7 else day - 1
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSelectedStudent(studentId: String) {
        _selectedStudentId.value = studentId
        generateAIReport()
    }

    fun setSelectedScheduleDay(day: Int) {
        _selectedScheduleDay.value = day
    }

    fun setPoolSubTab(index: Int) {
        _poolSubTabIndex.value = index
    }

    fun setPoolSearchQuery(query: String) {
        _poolSearchQuery.value = query
    }

    // Role Switching & Account Management
    fun toggleRole() {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val newRole = if (user.role == UserRole.TEACHER) UserRole.STUDENT else UserRole.TEACHER
            repository.switchUserRole(user.email, newRole)
        }
    }

    fun switchAccount(email: String) {
        val trimmed = email.trim().lowercase()
        _currentUserEmail.value = trimmed
        // If student, check if we need to update selected student id
        viewModelScope.launch {
            val stu = allStudents.value.find { it.email == trimmed }
            if (stu != null) {
                _selectedStudentId.value = stu.id
            }
        }
    }

    fun loginOrRegister(
        name: String,
        email: String,
        role: UserRole,
        targetOrTitle: String,
        schoolOrInstitution: String = "",
        assignedTeacherEmail: String? = null
    ) {
        viewModelScope.launch {
            val trimmedEmail = email.trim().lowercase()
            _currentUserEmail.value = trimmedEmail
            val user = repository.registerOrUpdateUser(
                email = trimmedEmail,
                name = name.trim(),
                role = role,
                titleOrTarget = targetOrTitle.trim(),
                schoolOrInstitution = schoolOrInstitution.trim(),
                assignedTeacherEmail = assignedTeacherEmail?.trim()?.lowercase()
            )
            // If student, select student id
            if (role == UserRole.STUDENT) {
                val stu = allStudents.value.find { it.email == trimmedEmail }
                if (stu != null) {
                    _selectedStudentId.value = stu.id
                }
            }
            if (!user.isEmailVerified) {
                openVerificationDialog(trimmedEmail)
            }
        }
    }

    fun approveStudent(studentId: String) {
        viewModelScope.launch {
            val coachEmail = currentUser.value?.email ?: _currentUserEmail.value
            val stu = allStudents.value.find { it.id == studentId }
            repository.approveStudentByTeacher(studentId, coachEmail, stu?.email)
            _selectedStudentId.value = studentId
            generateAIReport()
        }
    }

    fun rejectStudent(studentId: String) {
        viewModelScope.launch {
            repository.rejectStudentRequest(studentId)
        }
    }

    // Email Verification
    fun openVerificationDialog(email: String? = null) {
        val targetEmail = email ?: currentUser.value?.email ?: _currentUserEmail.value
        viewModelScope.launch {
            _verificationState.value = _verificationState.value.copy(
                isDialogVisible = true,
                isSendingCode = true,
                email = targetEmail,
                otpCode = "",
                errorMessage = null,
                isVerifiedSuccess = false
            )
            val generated = repository.sendVerificationCode(targetEmail)
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            val timeStr = String.format(java.util.Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            val refId = "EDR-" + kotlin.random.Random.nextInt(10000, 99999)

            _verificationState.value = VerificationUiState(
                email = targetEmail,
                isDialogVisible = true,
                otpCode = "",
                generatedCode = generated,
                expiresAtMillis = now + (180 * 1000L), // 3 minutes real validity
                resendAllowedAtMillis = now + (60 * 1000L), // 60 seconds resend cooldown
                validityRemainingSeconds = 180,
                resendCooldownSeconds = 60,
                remainingAttempts = 5,
                isExpired = false,
                isLocked = false,
                isVerifying = false,
                isSendingCode = false,
                errorMessage = null,
                isVerifiedSuccess = false,
                sentTimestampFormatted = timeStr,
                referenceId = refId
            )
            startVerificationCountdown()
        }
    }

    fun closeVerificationDialog() {
        _verificationState.value = _verificationState.value.copy(isDialogVisible = false, errorMessage = null)
        verificationTimerJob?.cancel()
    }

    fun setOtpCode(code: String) {
        _verificationState.value = _verificationState.value.copy(
            otpCode = code.take(6),
            errorMessage = null
        )
    }

    fun resendVerificationCode() {
        val state = _verificationState.value
        if (state.resendCooldownSeconds > 0) return

        viewModelScope.launch {
            _verificationState.value = state.copy(isSendingCode = true, errorMessage = null)
            val targetEmail = state.email
            val newCode = repository.sendVerificationCode(targetEmail)
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            val timeStr = String.format(java.util.Locale.US, "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            val refId = "EDR-" + kotlin.random.Random.nextInt(10000, 99999)

            _verificationState.value = state.copy(
                generatedCode = newCode,
                otpCode = "",
                expiresAtMillis = now + (180 * 1000L),
                resendAllowedAtMillis = now + (60 * 1000L),
                validityRemainingSeconds = 180,
                resendCooldownSeconds = 60,
                remainingAttempts = 5,
                isExpired = false,
                isLocked = false,
                isVerifying = false,
                isSendingCode = false,
                errorMessage = null,
                sentTimestampFormatted = timeStr,
                referenceId = refId
            )
            startVerificationCountdown()
        }
    }

    fun submitVerificationCode() {
        val state = _verificationState.value
        if (state.isExpired) {
            _verificationState.value = state.copy(
                errorMessage = "Doğrulama kodunun 3 dakikalık geçerlilik süresi doldu. Lütfen 'Yeni Kod Gönder' butonuna basarak yeni bir kod isteyiniz."
            )
            return
        }
        if (state.isLocked || state.remainingAttempts <= 0) {
            _verificationState.value = state.copy(
                errorMessage = "Çok fazla hatalı deneme yaptınız. Güvenliğiniz için lütfen yeni kod talep ediniz."
            )
            return
        }

        val email = state.email
        val code = state.otpCode.trim()

        if (code.length < 6) {
            _verificationState.value = state.copy(errorMessage = "Lütfen 6 haneli kodu eksiksiz giriniz.")
            return
        }

        viewModelScope.launch {
            _verificationState.value = state.copy(isVerifying = true, errorMessage = null)
            delay(500) // realistic network validation latency
            val success = repository.verifyEmailCode(email, code)
            if (success) {
                verificationTimerJob?.cancel()
                _verificationState.value = _verificationState.value.copy(
                    isVerifying = false,
                    isVerifiedSuccess = true,
                    errorMessage = null
                )
                delay(1200)
                _verificationState.value = _verificationState.value.copy(
                    isDialogVisible = false,
                    isVerifiedSuccess = false
                )
            } else {
                val nextAttempts = (state.remainingAttempts - 1).coerceAtLeast(0)
                val isNowLocked = nextAttempts == 0
                val err = if (isNowLocked) {
                    "Hatalı kod! Tüm deneme haklarınız tükendi. Lütfen yeni bir kod isteyiniz."
                } else {
                    "Girdiğiniz 6 haneli kod hatalıdır. Kalan deneme hakkınız: $nextAttempts"
                }
                _verificationState.value = _verificationState.value.copy(
                    isVerifying = false,
                    remainingAttempts = nextAttempts,
                    isLocked = isNowLocked,
                    errorMessage = err
                )
            }
        }
    }

    private fun startVerificationCountdown() {
        verificationTimerJob?.cancel()
        verificationTimerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val state = _verificationState.value
                if (!state.isDialogVisible) break

                val validitySec = ((state.expiresAtMillis - now) / 1000).coerceAtLeast(0).toInt()
                val resendSec = ((state.resendAllowedAtMillis - now) / 1000).coerceAtLeast(0).toInt()
                val isExpired = validitySec <= 0

                _verificationState.value = state.copy(
                    validityRemainingSeconds = validitySec,
                    resendCooldownSeconds = resendSec,
                    isExpired = isExpired
                )
                if (validitySec <= 0 && resendSec <= 0 && state.isExpired) {
                    // Kept in expired state until user requests new code
                }
                delay(1000)
            }
        }
    }

    // Task Actions
    fun addTask(
        dayOfWeek: Int,
        subject: String,
        title: String,
        durationMinutes: Int,
        targetQuestions: Int,
        targetStudentId: String? = null,
        assignedBy: String? = null,
        taskType: String = "Ödev"
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val author = assignedBy ?: if (user?.role == UserRole.TEACHER) "Öğretmen / Koç (${user.name})" else "Öğrenci Kişisel Hedefi"
            val targetStu = targetStudentId ?: _selectedStudentId.value
            repository.addTask(
                studentId = targetStu,
                dayOfWeek = dayOfWeek,
                subject = subject,
                title = title,
                durationMinutes = durationMinutes,
                targetQuestions = targetQuestions,
                assignedBy = author,
                taskType = taskType
            )
        }
    }

    fun toggleTask(taskId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, !currentCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    // Exam Actions
    fun addExam(
        title: String,
        trNet: Double,
        matNet: Double,
        fenNet: Double,
        sosNet: Double,
        dateString: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addExam(
                studentId = _selectedStudentId.value,
                title = title,
                trNet = trNet,
                matNet = matNet,
                fenNet = fenNet,
                sosNet = sosNet,
                dateString = dateString,
                notes = notes
            )
            generateAIReport()
        }
    }

    fun deleteExam(examId: Long) {
        viewModelScope.launch {
            repository.deleteExam(examId)
            generateAIReport()
        }
    }

    // Coaching Pool Actions
    fun claimStudent(studentId: String) {
        viewModelScope.launch {
            val coachEmail = currentUser.value?.email ?: _currentUserEmail.value
            repository.claimStudentFromPool(studentId, coachEmail)
            _selectedStudentId.value = studentId
        }
    }

    fun setSelectedCurriculumIndex(index: Int) {
        _selectedCurriculumIndex.value = index
    }

    fun applyCurriculum(curriculumId: String, targetStudentId: String? = null) {
        viewModelScope.launch {
            val curriculum = readyCurricula.find { it.id == curriculumId } ?: return@launch
            val studentId = targetStudentId ?: _selectedStudentId.value
            val author = currentUser.value?.name?.let { "EduRehber ($it)" } ?: "EduRehber Hazır Program"
            repository.applyReadyCurriculum(curriculum, studentId, author, replaceExisting = true)
            generateAIReport()
        }
    }

    // Resource Management (Upload / Download)
    fun uploadCustomResource(
        title: String,
        category: String,
        targetField: String,
        description: String,
        urlOrUri: String,
        fileSizeOrType: String,
        contentNotes: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val authorName = user?.name ?: "Eğitim Koçu"
            val id = "res_custom_${System.currentTimeMillis() % 100000}"
            val resource = ResourceEntity(
                id = id,
                title = title.trim(),
                category = category.trim(),
                author = authorName,
                downloads = 1,
                description = description.trim(),
                urlOrUri = urlOrUri.trim(),
                targetField = targetField.trim(),
                fileSizeOrType = fileSizeOrType.trim().ifEmpty { "Ders Dokümanı" },
                isOfficialMeb = false,
                authorEmail = user?.email,
                contentNotes = contentNotes.trim()
            )
            repository.uploadResource(resource)
        }
    }

    fun incrementResourceDownload(resourceId: String) {
        viewModelScope.launch {
            repository.incrementResourceDownload(resourceId)
        }
    }

    fun deleteResource(resourceId: String) {
        viewModelScope.launch {
            repository.deleteResource(resourceId)
        }
    }

    // Coach Request & Dynamic Rating Action
    fun sendCoachRequest(coachId: String, coachName: String, message: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val stu = allStudents.value.find { it.email == user?.email } ?: allStudents.value.firstOrNull()
            val studentName = user?.name ?: stu?.name ?: "Öğrenci"
            val studentEmail = user?.email ?: stu?.email ?: "ogrenci@edurehber.com"
            val studentTarget = user?.titleOrTarget ?: stu?.target ?: "YKS Sayısal"
            val currentNet = stu?.avgNet ?: 85.0

            repository.submitCoachRequest(
                coachId = coachId,
                coachName = coachName,
                studentName = studentName,
                studentEmail = studentEmail,
                studentTarget = studentTarget,
                currentNet = currentNet,
                message = message.trim()
            )
        }
    }

    // Pomodoro Timer Engine
    fun setPomodoroMode(mode: PomodoroMode) {
        pausePomodoro()
        _pomodoroMode.value = mode
        _pomodoroSeconds.value = mode.totalSeconds
    }

    fun togglePomodoro() {
        if (_isPomodoroRunning.value) {
            pausePomodoro()
        } else {
            startPomodoro()
        }
    }

    private fun startPomodoro() {
        _isPomodoroRunning.value = true
        pomodoroJob?.cancel()
        pomodoroJob = viewModelScope.launch {
            while (_isPomodoroRunning.value && _pomodoroSeconds.value > 0) {
                delay(1000)
                _pomodoroSeconds.value -= 1
            }
            if (_pomodoroSeconds.value <= 0) {
                _isPomodoroRunning.value = false
                if (_pomodoroMode.value == PomodoroMode.WORK) {
                    _pomodoroCycles.value += 1
                    _pomodoroTotalMinutes.value += 25
                    setPomodoroMode(PomodoroMode.BREAK)
                } else {
                    setPomodoroMode(PomodoroMode.WORK)
                }
            }
        }
    }

    fun pausePomodoro() {
        _isPomodoroRunning.value = false
        pomodoroJob?.cancel()
    }

    fun resetPomodoro() {
        pausePomodoro()
        _pomodoroSeconds.value = _pomodoroMode.value.totalSeconds
    }

    // AI Coach Report Generator
    fun generateAIReport() {
        viewModelScope.launch {
            _isAILoading.value = true
            delay(300)
            val stu = allStudents.value.find { it.id == _selectedStudentId.value }
            val studentName = stu?.name ?: "Öğrenci"

            _aiReport.value = AIReportData(
                strengths = "$studentName; Türkçe Paragraf ve Sosyal Bilimler alanında yüksek tutarlılık sergiliyor. Soru çözüm hızında son 2 haftada %18 ivme kaydedildi.",
                focusAreas = "TYT Matematik Problemler ve Fizik Optik/Dalgalar konularında net kaybı gözlendi. Günlük soru kotasına ek 25 dakikalık odaklanma etütü eklenmeli.",
                tacticalSteps = listOf(
                    "Pazartesi & Çarşamba: 40 Soru TYT Hız Problemleri Kampı tamamlanmalı.",
                    "Cuma: 135 dk süre tutularak kesintisiz Genel Deneme Sınavı uygulanmalı.",
                    "Pazar: Haftalık Yanlış Defteri taranarak koç geri bildirimi alınmalı."
                ),
                predictedNet = if (stu != null) "${stu.avgNet + 6.5} Net" else "98.5 Net"
            )
            _isAILoading.value = false
        }
    }
}
