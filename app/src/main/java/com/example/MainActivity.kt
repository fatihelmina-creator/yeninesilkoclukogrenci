package com.example
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddExamDialog
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.EduTopBar
import com.example.ui.components.EmailVerificationDialog
import com.example.ui.components.EmailVerificationNotificationBanner
import com.example.ui.components.ProfileDialog
import com.example.ui.screens.AICoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExamTrackerScreen
import com.example.ui.screens.PoolScreen
import com.example.ui.screens.PomodoroScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.BrandPrimaryContainerDark
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EduRehberTheme
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduRehberTheme(darkTheme = false) {
                EduRehberApp()
            }
        }
    }
}

@Composable
fun EduRehberApp(
    viewModel: EduViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()

    var isAddTaskDialogOpen by remember { mutableStateOf(false) }
    var isAddExamDialogOpen by remember { mutableStateOf(false) }
    var isProfileDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets(top = 28.dp))
            ) {
                EduTopBar(
                    user = currentUser,
                    onToggleRole = { viewModel.toggleRole() },
                    onOpenVerification = { viewModel.openVerificationDialog() },
                    onOpenProfile = { isProfileDialogOpen = true }
                )
                EmailVerificationNotificationBanner(
                    user = currentUser,
                    onOpenVerification = { viewModel.openVerificationDialog() }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav_bar")
            ) {
                val navItems = listOf(
                    Triple(MainTab.DASHBOARD, Icons.Default.Dashboard, "Özet"),
                    Triple(MainTab.SCHEDULE, Icons.Default.CalendarMonth, "Plan"),
                    Triple(MainTab.EXAMS, Icons.Default.Assessment, "Deneme"),
                    Triple(MainTab.POOL, Icons.Default.Group, "Havuz"),
                    Triple(MainTab.POMODORO, Icons.Default.Timer, "Pomodoro"),
                    Triple(MainTab.AICOACH, Icons.Default.AutoAwesome, "AI Koç")
                )

                navItems.forEach { (tab, icon, label) ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp),
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF64748B)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = if (tab == MainTab.AICOACH) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color(0xFF64748B),
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 250),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    MainTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onOpenAddTask = { isAddTaskDialogOpen = true }
                    )
                    MainTab.SCHEDULE -> ScheduleScreen(
                        viewModel = viewModel,
                        onOpenAddTask = { isAddTaskDialogOpen = true }
                    )
                    MainTab.EXAMS -> ExamTrackerScreen(
                        viewModel = viewModel,
                        onOpenAddExam = { isAddExamDialogOpen = true }
                    )
                    MainTab.POOL -> PoolScreen(
                        viewModel = viewModel
                    )
                    MainTab.POMODORO -> PomodoroScreen(
                        viewModel = viewModel
                    )
                    MainTab.AICOACH -> AICoachScreen(
                        viewModel = viewModel
                    )
                }
            }
        }

        // Dialogs & Sheets
        EmailVerificationDialog(
            state = verificationState,
            onCodeChange = { viewModel.setOtpCode(it) },
            onResend = { viewModel.resendVerificationCode() },
            onSubmit = { viewModel.submitVerificationCode() },
            onDismiss = { viewModel.closeVerificationDialog() }
        )

        AddTaskDialog(
            isOpen = isAddTaskDialogOpen,
            onDismiss = { isAddTaskDialogOpen = false },
            onSave = { dayOfWeek, subject, title, duration, questions ->
                viewModel.addTask(dayOfWeek, subject, title, duration, questions)
            }
        )

        AddExamDialog(
            isOpen = isAddExamDialogOpen,
            onDismiss = { isAddExamDialogOpen = false },
            onSave = { title, tr, mat, fen, sos, date, notes ->
                viewModel.addExam(title, tr, mat, fen, sos, date, notes)
            }
        )

        ProfileDialog(
            isOpen = isProfileDialogOpen,
            user = currentUser,
            onDismiss = { isProfileDialogOpen = false },
            onRegisterOrSave = { name, email, role, target, institution, assignedTeacherEmail ->
                viewModel.loginOrRegister(name, email, role, target, institution, assignedTeacherEmail)
            },
            onSwitchAccount = { email ->
                viewModel.switchAccount(email)
            },
            onOpenVerification = { email ->
                viewModel.openVerificationDialog(email)
            }
        )
    }
}
