package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.StatusSuccessDark
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.PomodoroMode

@Composable
fun PomodoroScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val mode by viewModel.pomodoroMode.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.pomodoroSeconds.collectAsStateWithLifecycle()
    val isRunning by viewModel.isPomodoroRunning.collectAsStateWithLifecycle()
    val completedCycles by viewModel.pomodoroCycles.collectAsStateWithLifecycle()
    val totalMinutes by viewModel.pomodoroTotalMinutes.collectAsStateWithLifecycle()

    val totalModeSeconds = mode.totalSeconds
    val progress = (secondsRemaining.toFloat() / totalModeSeconds.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "pomo_progress"
    )

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Title Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Odaklanma & Pomodoro Etütü",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "25 dk odaklanma, 5 dk ara ile yüksek dikkat seansı",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Timer Card
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth().testTag("pomodoro_card")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Switcher Pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (mode == PomodoroMode.WORK) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { viewModel.setPomodoroMode(PomodoroMode.WORK) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("pomo_work_mode_btn")
                    ) {
                        Text(
                            text = "25 Dk Odaklanma",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (mode == PomodoroMode.WORK) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (mode == PomodoroMode.BREAK) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { viewModel.setPomodoroMode(PomodoroMode.BREAK) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("pomo_break_mode_btn")
                    ) {
                        Text(
                            text = "5 Dk Mola",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (mode == PomodoroMode.BREAK) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Circular Timer Display
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        strokeWidth = 10.dp,
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (mode == PomodoroMode.WORK) MaterialTheme.colorScheme.primary else BrandGoldDark,
                        strokeWidth = 10.dp,
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isRunning) (if (mode == PomodoroMode.WORK) "Odaklanılıyor..." else "Mola Zamanı") else "Hazır",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isRunning) StatusSuccessDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Play / Pause / Reset Control Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.togglePomodoro() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .width(160.dp)
                            .testTag("pomodoro_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Duraklat" else "Başlat",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "Duraklat" else "Başlat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.size(52.dp).testTag("pomodoro_reset_btn")
                    ) {
                        IconButton(onClick = { viewModel.resetPomodoro() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sıfırla",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tamamlanan:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$completedCycles Seans", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Toplam Süre:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalMinutes Dk", style = MaterialTheme.typography.titleMedium, color = StatusSuccessDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

