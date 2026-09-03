package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyMotivationEntity
import com.example.data.model.ExamMilestone
import com.example.data.model.ExamType
import com.example.data.model.StudentEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.BrandGoldLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusSuccessDark
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningDark
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.MainTab

@Composable
fun DashboardScreen(
    viewModel: EduViewModel,
    onOpenAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isTeacher = currentUser?.role == UserRole.TEACHER
    val coachStudents by viewModel.coachStudents.collectAsStateWithLifecycle()
    val pendingStudents by viewModel.pendingApprovalStudents.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val selectedStuId by viewModel.selectedStudentId.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedScheduleDay.collectAsStateWithLifecycle()
    val tasks by viewModel.currentStudentTasks.collectAsStateWithLifecycle()
    val exams by viewModel.currentStudentExams.collectAsStateWithLifecycle()

    // Active student matching logic
    val currentStudent = if (isTeacher) {
        allStudents.find { it.id == selectedStuId } ?: coachStudents.firstOrNull() ?: allStudents.firstOrNull()
    } else {
        // If student, find student associated with their email or fallback
        allStudents.find { it.email == currentUser?.email } ?: allStudents.find { it.id == selectedStuId } ?: allStudents.firstOrNull()
    }

    val isStudentApproved = currentStudent?.isApprovedByTeacher == true

    // KPI Metrics calculation
    val doneTasksCount = tasks.count { it.isCompleted }
    val totalTasksCount = tasks.size
    val completionPct = if (totalTasksCount > 0) (doneTasksCount * 100) / totalTasksCount else 0
    val totalMinutes = tasks.sumOf { it.durationMinutes }
    val totalHours = String.format(java.util.Locale.US, "%.1f", totalMinutes / 60.0)
    val totalQuestions = tasks.sumOf { it.targetQuestions }
    val lastExam = exams.lastOrNull()

    // Today's tasks
    val todayTasks = tasks.filter { it.dayOfWeek == selectedDay }

    val dailyMotivation by viewModel.dailyMotivation.collectAsStateWithLifecycle()
    val selectedCountdownExam by viewModel.selectedCountdownExam.collectAsStateWithLifecycle()
    val tickerTime by viewModel.currentTickerTime.collectAsStateWithLifecycle()

    var showEditMotivationDialog by remember { mutableStateOf(false) }
    var showMilestoneDialog by remember { mutableStateOf(false) }

    if (showEditMotivationDialog) {
        EditMotivationDialog(
            currentMotivation = dailyMotivation,
            onDismiss = { showEditMotivationDialog = false },
            onSave = { title, content, author, role, category, isAI ->
                viewModel.updateDailyMotivation(title, content, author, role, category, isAI)
                showEditMotivationDialog = false
            },
            onGenerateAI = {
                val (aiTitle, aiContent) = viewModel.generateAIMotivationQuote()
                aiTitle to aiContent
            }
        )
    }

    if (showMilestoneDialog) {
        ExamMilestonesModalDialog(
            examType = selectedCountdownExam,
            onDismiss = { showMilestoneDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Profile & Student Roster Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("dashboard_profile_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isTeacher) "FE" else (currentStudent?.name?.take(2)?.uppercase() ?: "ÖG"),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isTeacher) (currentUser?.name ?: "Fatih Erzik") else (currentStudent?.name ?: "Zeynep Kaya"),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isTeacher) "👨‍🏫 Öğretmen / Koç" else "👨‍🎓 Öğrenci",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = if (isTeacher) {
                                        currentUser?.titleOrTarget ?: "YKS & LGS Bireysel Gelişim Koordinatörü"
                                    } else {
                                        "${currentStudent?.target ?: "YKS Sayısal"} • Hedef: ${currentStudent?.targetNet ?: 105.0} Net"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isTeacher) {
                            Button(
                                onClick = onOpenAddTask,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("dashboard_add_task_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Ödev Ata", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ödev Ata", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Coach Roster Selector (Visible to Teacher)
                    if (isTeacher) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kadro Öğrencileri (İlerleme Takibi):",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${coachStudents.size} Onaylı Öğrenci",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("coach_students_roster_row")
                        ) {
                            items(coachStudents) { student ->
                                val isSelected = student.id == selectedStuId
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .clickable { viewModel.setSelectedStudent(student.id) }
                                        .testTag("roster_chip_${student.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else StatusSuccess)
                                        )
                                        Text(
                                            text = student.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Student View: Coach Approval Status Banner
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = if (isStudentApproved) StatusSuccessContainer else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isStudentApproved) StatusSuccess.copy(alpha = 0.5f) else StatusWarning.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("student_approval_status_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isStudentApproved) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                        contentDescription = "Onay",
                                        tint = if (isStudentApproved) StatusSuccessDark else StatusWarningDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isStudentApproved) "Öğretmen Onayı Aktif ✓" else "Öğretmen / Koç Onayı Bekleniyor",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isStudentApproved) StatusSuccessDark else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isStudentApproved) {
                                                "Atanan Koç: ${currentStudent?.assignedCoachEmail ?: "Fatih Erzik"}"
                                            } else {
                                                "Öğretmeniniz onay verdiğinde tüm ödev ve haftalık plan senkronize olacaktır."
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // DAILY MOTIVATIONAL & GUIDANCE NOTE (Günün Motivasyon Yazısı & Koç İlhamı)
        item {
            DailyMotivationCard(
                motivation = dailyMotivation,
                isTeacher = isTeacher,
                onLike = { viewModel.likeDailyMotivation() },
                onEditClick = { showEditMotivationDialog = true },
                onGenerateAIClick = { viewModel.applyAIMotivationNow() }
            )
        }

        // SEPARATE EXAM COUNTDOWN CALENDARS (TYT / AYT ve LGS Geri Sayım Takvimi)
        item {
            val countdownState = viewModel.calculateCountdown(selectedCountdownExam)
            ExamCountdownHeroWidget(
                countdownState = countdownState,
                selectedExam = selectedCountdownExam,
                onSelectExam = { viewModel.setSelectedCountdownExam(it) },
                onOpenMilestoneCalendar = { showMilestoneDialog = true }
            )
        }

        // TEACHER SPECIAL PANEL: PENDING APPROVAL REQUESTS (Öğretmen için Onay Bekleyen Öğrenciler)
        if (isTeacher && pendingStudents.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("pending_approvals_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HowToReg,
                                        contentDescription = "Onay",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Onay Bekleyen Öğrenci Talepleri",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${pendingStudents.size} yeni öğrenci kaydı öğretmen onayı bekliyor",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        pendingStudents.forEach { student ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("pending_student_${student.id}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = student.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(StatusSuccessContainer)
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "E-posta Doğrulandı ✓",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = StatusSuccessDark,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${student.target} • ${student.needs.ifEmpty { "Lise 12. Sınıf" }}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "E-posta: ${student.email}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { viewModel.rejectStudent(student.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("reject_student_btn_${student.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Reddet",
                                                    tint = StatusErrorDark,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("Reddet", color = StatusErrorDark, style = MaterialTheme.typography.labelSmall)
                                            }

                                            Button(
                                                onClick = { viewModel.approveStudent(student.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("approve_student_btn_${student.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Onayla",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Onayla & Ekle", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // KPI Metric Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiCard(
                    title = "Haftalık Başarı",
                    value = "%$completionPct",
                    subtitle = "$doneTasksCount / $totalTasksCount Görev",
                    accentColor = StatusSuccessDark,
                    modifier = Modifier.weight(1f).testTag("kpi_completion_card")
                )
                KpiCard(
                    title = "Toplam Süre",
                    value = "$totalHours s",
                    subtitle = "Haftalık Plan",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f).testTag("kpi_hours_card")
                )
                KpiCard(
                    title = "Hedef Soru",
                    value = "$totalQuestions",
                    subtitle = "Çözülecek",
                    accentColor = BrandGoldDark,
                    modifier = Modifier.weight(1f).testTag("kpi_questions_card")
                )
                KpiCard(
                    title = "Son Deneme",
                    value = lastExam?.let { "${it.totalNet}" } ?: "-",
                    subtitle = "TYT Neti",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f).testTag("kpi_exam_card")
                )
            }
        }

        // Today's Study & Homework Tasks Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Ödevler",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isTeacher) "Seçili Öğrencinin Günlük Ödevleri" else "Bugünün Ödev ve Görevleri",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Tüm Haftayı Gör →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.setTab(MainTab.SCHEDULE) }
                        .testTag("view_full_schedule_link")
                )
            }
        }

        if (todayTasks.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().testTag("empty_today_tasks_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bugün için planlanmış ödev veya görev bulunmuyor.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTeacher) "+ Yeni Ödev Ata" else "+ Yeni Görev Planla",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onOpenAddTask)
                        )
                    }
                }
            }
        } else {
            items(todayTasks) { task ->
                TaskItemCard(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id, task.isCompleted) }
                )
            }
        }

        // AI Coach Strategic Advisor Teaser Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setTab(MainTab.AICOACH) }
                    .testTag("ai_coach_teaser_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Koç",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Gemini AI Koç Danışmanı",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Kişiselleştirilmiş Strateji & Net Analizi",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${currentStudent?.name ?: "Öğrenci"} için son deneme netleri, soru tamamlama ivmesi ve eksik kazanım analizi hazırlandı.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Stratejik Raporu Gör",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Detay",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        ),
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (task.isCompleted) "Tamamlandı" else "Yapılacak",
                tint = if (task.isCompleted) StatusSuccessDark else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.subject,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${task.durationMinutes} Dk",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${task.targetQuestions} Soru",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (task.assignedBy.isNotEmpty()) {
                        Text(
                            text = "• ${task.taskType}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyMotivationCard(
    motivation: DailyMotivationEntity?,
    isTeacher: Boolean,
    onLike: () -> Unit,
    onEditClick: () -> Unit,
    onGenerateAIClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeMotivation = motivation ?: DailyMotivationEntity()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth().testTag("daily_motivation_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Günün Sözü",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Günün Motivasyon Yazısı",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeMotivation.dateString,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = activeMotivation.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Title
                Text(
                    text = activeMotivation.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )

                // Content
                Text(
                    text = "“${activeMotivation.content}”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                // Author Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (activeMotivation.isGeneratedByAI) Icons.Default.AutoAwesome else Icons.Default.School,
                            contentDescription = "Yazar",
                            tint = if (activeMotivation.isGeneratedByAI) MaterialTheme.colorScheme.primary else BrandGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeMotivation.isGeneratedByAI) {
                                "🤖 ${activeMotivation.authorName} • Yapay Zeka Mentörlüğü"
                            } else {
                                "👨‍🏫 ${activeMotivation.authorName} (${activeMotivation.authorRole})"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Like Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable(onClick = onLike)
                            .testTag("motivation_like_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Beğen",
                                tint = StatusError,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${activeMotivation.likesCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Coach / Student Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isTeacher) {
                        OutlinedButton(
                            onClick = onEditClick,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("edit_motivation_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Düzenle", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yazıyı Düzenle", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onGenerateAIClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_motivation_coach_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Üret", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("✨ Yapay Zekaya Bırak", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onGenerateAIClick,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_motivation_student_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "İlham Al", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("✨ AI'dan Yeni İlham İste", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamCountdownHeroWidget(
    countdownState: com.example.ui.viewmodel.ExamCountdownState,
    selectedExam: ExamType,
    onSelectExam: (ExamType) -> Unit,
    onOpenMilestoneCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth().testTag("exam_countdown_hero_widget")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Exam Selector Row (TYT, AYT, LGS, YDT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Geri Sayım",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sınav Geri Sayım Takvimi",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Takvimi İncele →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onOpenMilestoneCalendar)
                        .testTag("open_milestones_link")
                )
            }

            // Exam Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().testTag("exam_type_chips_row")
            ) {
                items(ExamType.values()) { exam ->
                    val isSelected = exam == selectedExam
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectExam(exam) },
                        label = {
                            Text(
                                text = exam.code,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("chip_exam_${exam.code}")
                    )
                }
            }

            // Exam Info Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedExam.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedExam.examFormattedDate} • ${selectedExam.totalQuestions} Soru • ${selectedExam.durationMinutes} Dk",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Countdown Flip Box Grid: [ Gün ] [ Saat ] [ Dakika ] [ Saniye ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CountdownTimeBox(
                    number = String.format(java.util.Locale.US, "%02d", countdownState.remainingDays),
                    label = "GÜN",
                    highlight = true,
                    modifier = Modifier.weight(1f).testTag("countdown_days_box")
                )
                CountdownTimeBox(
                    number = String.format(java.util.Locale.US, "%02d", countdownState.remainingHours),
                    label = "SAAT",
                    highlight = false,
                    modifier = Modifier.weight(1f).testTag("countdown_hours_box")
                )
                CountdownTimeBox(
                    number = String.format(java.util.Locale.US, "%02d", countdownState.remainingMinutes),
                    label = "DAKİKA",
                    highlight = false,
                    modifier = Modifier.weight(1f).testTag("countdown_mins_box")
                )
                CountdownTimeBox(
                    number = String.format(java.util.Locale.US, "%02d", countdownState.remainingSeconds),
                    label = "SANİYE",
                    highlight = false,
                    modifier = Modifier.weight(1f).testTag("countdown_secs_box")
                )
            }

            // Preparation Progress & Remaining Weekends
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dönem Başından Hazırlık İlerlemesi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "%${(countdownState.progressPct * 100).toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { countdownState.progressPct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Kalan Hafta: ${countdownState.totalWeeksLeft} Hafta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "🎯 Kalan Deneme Hafta Sonu: ${countdownState.totalWeekendsLeft}",
                        style = MaterialTheme.typography.labelSmall,
                        color = StatusSuccessDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CountdownTimeBox(
    number: String,
    label: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge,
                color = if (highlight) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (highlight) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EditMotivationDialog(
    currentMotivation: DailyMotivationEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, author: String, role: String, category: String, isAI: Boolean) -> Unit,
    onGenerateAI: () -> Pair<String, String>
) {
    var title by remember { mutableStateOf(currentMotivation?.title ?: "Günün İradesi") }
    var content by remember { mutableStateOf(currentMotivation?.content ?: "") }
    var authorName by remember { mutableStateOf(currentMotivation?.authorName ?: "Koç Fatih Erzik") }
    var authorRole by remember { mutableStateOf(currentMotivation?.authorRole ?: "YKS & LGS Baş Mentörü") }
    var selectedCategory by remember { mutableStateOf(currentMotivation?.category ?: "Disiplin & Odaklanma") }
    var isAI by remember { mutableStateOf(currentMotivation?.isGeneratedByAI ?: false) }

    val categories = listOf("Disiplin & Odaklanma", "Sınav Stratejisi", "Pes Etmeme", "Hız & Süre Yönetimi", "LGS & YKS Başarı")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Günün Motivasyon Yazısını Belirle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Öğrencilerin ana ekranında her gün gösterilecek güdüleyici rehberlik notunu yazabilir veya yapay zekadan öneri alabilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick AI Generator Button
                OutlinedButton(
                    onClick = {
                        val (aiTitle, aiContent) = onGenerateAI()
                        title = aiTitle
                        content = aiContent
                        authorName = "EduRehber AI Danışmanı"
                        authorRole = "Yapay Zeka Destekli Mentör"
                        isAI = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_generate_ai_quote_btn")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✨ Yapay Zekadan Otomatik İlham Yazısı Al", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık / Slogan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("motivation_title_input")
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Motivasyon & Tavsiye Metni") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("motivation_content_input")
                )

                OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Koç / Yazar İsmi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("motivation_author_input")
                )

                // Category selector chips
                Text("Kategori:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isCatSelected = cat == selectedCategory
                        FilterChip(
                            selected = isCatSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, content, authorName, authorRole, selectedCategory, isAI)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_motivation_btn")
            ) {
                Text("Yayınla & Kaydet")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun ExamMilestonesModalDialog(
    examType: ExamType,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Timeline, contentDescription = "Yol Haritası", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("${examType.code} Stratejik Sınav Takvimi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(examType.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Sınav Tarihi: ${examType.examFormattedDate}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Format: ${examType.totalQuestions} Soru • ${examType.durationMinutes} Dakika • ${examType.targetAudience}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "Dönemsel Başarı Dönüm Noktaları:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                examType.milestones.forEachIndexed { index, milestone ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth().testTag("milestone_item_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = milestone.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = milestone.dateRange,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = milestone.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Tamam")
            }
        }
    )
}
