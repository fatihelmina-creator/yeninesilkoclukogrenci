package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReadyCurriculum
import com.example.data.model.TaskEntity
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccessDark
import com.example.ui.viewmodel.EduViewModel

@Composable
fun ScheduleScreen(
    viewModel: EduViewModel,
    onOpenAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDay by viewModel.selectedScheduleDay.collectAsStateWithLifecycle()
    val tasks by viewModel.currentStudentTasks.collectAsStateWithLifecycle()
    val selectedCurriculumIndex by viewModel.selectedCurriculumIndex.collectAsStateWithLifecycle()
    val readyCurricula = viewModel.readyCurricula

    var activeMainTab by remember { mutableStateOf(0) } // 0: Aktif Haftalık Planım, 1: Hazır Branş Programları (SAY/EA/SÖZ/DİL/LGS)
    var showApplyConfirmDialog by remember { mutableStateOf<ReadyCurriculum?>(null) }

    val daysList = listOf(
        1 to "Pazartesi",
        2 to "Salı",
        3 to "Çarşamba",
        4 to "Perşembe",
        5 to "Cuma",
        6 to "Cumartesi",
        7 to "Pazar"
    )

    val currentDayTasks = tasks.filter { it.dayOfWeek == selectedDay }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Main Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ders Çalışma Programı",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Kişisel haftalık çizelge ve hazır Sayısal/EA/Sözel şablonları",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (activeMainTab == 0) {
                Button(
                    onClick = onOpenAddTask,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("schedule_add_task_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ekle", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Görev Ekle", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Top Switcher: Aktif Planım vs Hazır Programlar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().testTag("schedule_mode_switcher")
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("📅 Haftalık Çalışma Planım", "⭐ Hazır Alan Programları (SAY / EA / SÖZ)").forEachIndexed { index, label ->
                    val isSelected = index == activeMainTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { activeMainTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeMainTab == 0) {
            // === TAB 0: PERSONAL WEEKLY PLAN ===

            // 7-day selector pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("schedule_day_selector_row")
            ) {
                items(daysList) { (dayNum, dayName) ->
                    val isSelected = dayNum == selectedDay
                    val dayTaskCount = tasks.count { it.dayOfWeek == dayNum }
                    val completedCount = tasks.count { it.dayOfWeek == dayNum && it.isCompleted }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier
                            .clickable { viewModel.setSelectedScheduleDay(dayNum) }
                            .testTag("day_pill_$dayNum")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (dayTaskCount > 0) "$completedCount/$dayTaskCount Bitti" else "Görev Yok",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day's Tasks List
            if (currentDayTasks.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bu gün için henüz bir çalışma görevi atanmadı.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onOpenAddTask,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ Görev Ekle", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { activeMainTab = 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hazır Program Seç", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().testTag("schedule_tasks_list")
                ) {
                    items(currentDayTasks, key = { it.id }) { task ->
                        DetailedTaskItemCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task.id, task.isCompleted) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        } else {
            // === TAB 1: READY-MADE CURRICULA (SAY / EA / SOZ / DIL / LGS) ===
            val currentCurriculum = readyCurricula.getOrElse(selectedCurriculumIndex) { readyCurricula.first() }

            // Field Selector Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("ready_curricula_field_row")
            ) {
                itemsIndexed(readyCurricula) { index, cur ->
                    val isSelected = index == selectedCurriculumIndex
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .clickable { viewModel.setSelectedCurriculumIndex(index) }
                            .testTag("curriculum_chip_${cur.field}")
                    ) {
                        Text(
                            text = cur.field,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().testTag("ready_curriculum_details_list")
            ) {
                // Curriculum Header Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = currentCurriculum.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Text(
                                    text = currentCurriculum.recommendedNets,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusSuccessDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentCurriculum.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentCurriculum.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentCurriculum.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showApplyConfirmDialog = currentCurriculum },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("apply_curriculum_btn")
                            ) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Bu Programı Çalışma Planıma Uygula",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 7 Days Breakdown Header
                item {
                    Text(
                        text = "Haftalık Günlük Ders Dağılımı (${currentCurriculum.tasks.size} Görev)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 7 Days Tasks Preview
                val groupedTasks = currentCurriculum.tasks.groupBy { it.dayOfWeek }
                items(daysList) { (dayNum, dayName) ->
                    val dayTasks = groupedTasks[dayNum] ?: emptyList()
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 $dayName",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${dayTasks.sumOf { it.durationMinutes }} Dk • ${dayTasks.sumOf { it.targetQuestions }} Soru",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            dayTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = task.subject,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${task.durationMinutes} dk / ${task.targetQuestions} soru",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal: Confirm Apply Curriculum
    showApplyConfirmDialog?.let { cur ->
        Dialog(onDismissRequest = { showApplyConfirmDialog = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hazır Programı Uygula",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        IconButton(onClick = { showApplyConfirmDialog = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "📌 '${cur.title}' programı 7 gün boyunca tüm ders saatleri ve soru kotalarıyla çalışma planınıza aktarılacaktır.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mevcut haftalık listeniz bu hazır şablonla güncellenecektir. Onaylıyor musunuz?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showApplyConfirmDialog = null },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Vazgeç")
                        }

                        Button(
                            onClick = {
                                viewModel.applyCurriculum(cur.id)
                                showApplyConfirmDialog = null
                                activeMainTab = 0
                                Toast.makeText(context, "🎉 '${cur.title}' başarıyla çalışma planınıza uygulandı!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("confirm_apply_curriculum_btn")
                        ) {
                            Text("Uygula", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedTaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
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
        modifier = modifier.fillMaxWidth().testTag("detailed_task_${task.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(28.dp).testTag("task_check_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (task.isCompleted) "Tamamlandı" else "Yapılacak",
                    tint = if (task.isCompleted) StatusSuccessDark else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onToggle)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.subject,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${task.taskType} • ${task.assignedBy}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp).testTag("delete_task_btn_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Sil",
                    tint = StatusError.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
