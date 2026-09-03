package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CoachEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.StatusSuccessDark
import com.example.ui.viewmodel.EduViewModel

@Composable
fun PoolScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val subTabIndex by viewModel.poolSubTabIndex.collectAsStateWithLifecycle()
    val searchQuery by viewModel.poolSearchQuery.collectAsStateWithLifecycle()
    val poolStudents by viewModel.poolStudents.collectAsStateWithLifecycle()
    val resources by viewModel.allResources.collectAsStateWithLifecycle()
    val coaches by viewModel.coachesListFlow.collectAsStateWithLifecycle()

    var selectedResourceFilter by remember { mutableStateOf("Tümü") }
    var showUploadResourceDialog by remember { mutableStateOf(false) }
    var selectedCoachForRequest by remember { mutableStateOf<CoachEntity?>(null) }
    var viewingResourceDetails by remember { mutableStateOf<ResourceEntity?>(null) }

    val filteredStudents = poolStudents.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.target.contains(searchQuery, ignoreCase = true) ||
        it.needs.contains(searchQuery, ignoreCase = true)
    }

    val filteredResources = resources.filter { res ->
        val matchesQuery = res.title.contains(searchQuery, ignoreCase = true) ||
                res.category.contains(searchQuery, ignoreCase = true) ||
                res.targetField.contains(searchQuery, ignoreCase = true) ||
                res.author.contains(searchQuery, ignoreCase = true)
        
        val matchesCategory = when (selectedResourceFilter) {
            "MEB & OGM Materyal" -> res.isOfficialMeb
            "Sayısal" -> res.targetField.contains("SAY", ignoreCase = true) || res.category.contains("Sayısal", ignoreCase = true)
            "Eşit Ağırlık" -> res.targetField.contains("EA", ignoreCase = true) || res.category.contains("Eşit", ignoreCase = true)
            "Sözel" -> res.targetField.contains("SÖZ", ignoreCase = true) || res.category.contains("Sözel", ignoreCase = true)
            "Öğretmen Dokümanı" -> !res.isOfficialMeb
            else -> true
        }
        matchesQuery && matchesCategory
    }

    val filteredCoaches = coaches.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.specialty.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Eğitim & Koçluk Havuzu",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "MEB/OGM kaynakları, koçluk talepleri ve öğrenci havuzu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (subTabIndex == 2 && currentUser?.role == UserRole.TEACHER) {
                Button(
                    onClick = { showUploadResourceDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("upload_resource_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Yükle", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Doküman Yükle", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subtab Switcher
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth().testTag("pool_subtabs_container")
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf(
                    "Öğrenci Havuzu (${poolStudents.size})",
                    "Koç Havuzu (${coaches.size})",
                    "Kaynaklar & MEB (${resources.size})"
                ).forEachIndexed { index, label ->
                    val isSelected = index == subTabIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { viewModel.setPoolSubTab(index) }
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

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setPoolSearchQuery(it) },
            placeholder = { Text("MEB portali, konu özeti, öğretmen veya koç ara...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Ara",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("pool_search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Resources Category Filter Chips (when on Resources tab)
        if (subTabIndex == 2) {
            Spacer(modifier = Modifier.height(8.dp))
            val resourceCategories = listOf("Tümü", "MEB & OGM Materyal", "Sayısal", "Eşit Ağırlık", "Sözel", "Öğretmen Dokümanı")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().testTag("resource_filter_chips_row")
            ) {
                items(resourceCategories) { cat ->
                    val isSelected = selectedResourceFilter == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.clickable { selectedResourceFilter = cat }
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cards List
        when (subTabIndex) {
            0 -> {
                // Students Pool
                if (filteredStudents.isEmpty()) {
                    EmptyPoolCard(text = "Havuzda açıkta öğrenci talebi bulunamadı.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize().testTag("pool_students_list")
                    ) {
                        items(filteredStudents, key = { it.id }) { student ->
                            StudentPoolCard(
                                student = student,
                                onClaim = {
                                    viewModel.claimStudent(student.id)
                                    Toast.makeText(context, "🎉 ${student.name} kadronuza eklendi!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
            1 -> {
                // Coaches Pool with Dynamic Performance Stars & Real Demands
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().testTag("pool_coaches_list")
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Performans Yıldızları, koçların gerçek öğrenci talepleri, seans tamamlama ve net artış oranlarına göre dinamik olarak hesaplanır.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    items(filteredCoaches, key = { it.id }) { coach ->
                        CoachPoolCard(
                            coach = coach,
                            onRequest = {
                                selectedCoachForRequest = coach
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
            2 -> {
                // Resources & MEB/OGM Materyal
                if (filteredResources.isEmpty()) {
                    EmptyPoolCard(text = "Seçilen filtreye uygun eğitim kaynağı bulunamadı.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize().testTag("pool_resources_list")
                    ) {
                        items(filteredResources, key = { it.id }) { resource ->
                            ResourceCard(
                                resource = resource,
                                onOpenResource = {
                                    handleResourceAction(context, resource, viewModel) {
                                        viewingResourceDetails = resource
                                    }
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }

    // Modal: Upload Document Dialog for Coaches
    if (showUploadResourceDialog) {
        UploadResourceModal(
            onDismiss = { showUploadResourceDialog = false },
            onUpload = { title, category, targetField, description, url, type, notes ->
                viewModel.uploadCustomResource(
                    title = title,
                    category = category,
                    targetField = targetField,
                    description = description,
                    urlOrUri = url,
                    fileSizeOrType = type,
                    contentNotes = notes
                )
                showUploadResourceDialog = false
                Toast.makeText(context, "✅ Doküman başarıyla havuza yüklendi!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal: Coach Request Dialog
    selectedCoachForRequest?.let { coach ->
        CoachRequestModal(
            coach = coach,
            onDismiss = { selectedCoachForRequest = null },
            onSubmit = { message ->
                viewModel.sendCoachRequest(coach.id, coach.name, message)
                selectedCoachForRequest = null
                Toast.makeText(context, "🚀 ${coach.name} için koçluk talebiniz iletildi! Talep sayısı ve performans yıldızı güncellendi.", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal: Document Content Reader Dialog
    viewingResourceDetails?.let { res ->
        ResourceDetailsModal(
            resource = res,
            onDismiss = { viewingResourceDetails = null },
            onOpenUrl = {
                handleSafeUrlIntent(context, res.urlOrUri)
                viewModel.incrementResourceDownload(res.id)
            },
            onShare = {
                handleShareResource(context, res)
                viewModel.incrementResourceDownload(res.id)
            }
        )
    }
}

private fun handleResourceAction(
    context: Context,
    resource: ResourceEntity,
    viewModel: EduViewModel,
    onShowReader: () -> Unit
) {
    viewModel.incrementResourceDownload(resource.id)
    val url = resource.urlOrUri.trim()
    if (url.startsWith("http://") || url.startsWith("https://")) {
        handleSafeUrlIntent(context, url)
        Toast.makeText(context, "🌐 '${resource.title}' resmi portali açılıyor...", Toast.LENGTH_SHORT).show()
    } else if (resource.contentNotes.isNotEmpty()) {
        onShowReader()
    } else {
        Toast.makeText(context, "📥 '${resource.title}' dokümanı hazırlandı.", Toast.LENGTH_SHORT).show()
    }
}

private fun handleSafeUrlIntent(context: Context, url: String) {
    try {
        val safeUri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, safeUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Bağlantı açılamadı: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun handleShareResource(context: Context, resource: ResourceEntity) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "📚 ${resource.title}\nKategori: ${resource.category} (${resource.targetField})\nHazırlayan: ${resource.author}\n\nİçerik & Notlar:\n${resource.contentNotes}\n\nBağlantı: ${resource.urlOrUri}"
            )
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Dokümanı Paylaş / Dışa Aktar"))
    } catch (e: Exception) {
        Toast.makeText(context, "Paylaşım hatası: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun StudentPoolCard(
    student: StudentEntity,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth().testTag("student_pool_card_${student.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = student.target,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = student.needs,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text("Mevcut Net:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${student.avgNet}", style = MaterialTheme.typography.labelLarge, color = StatusSuccessDark, fontWeight = FontWeight.Black)
                    }
                    Column {
                        Text("Hedef Net:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${student.targetNet}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                    }
                }

                Button(
                    onClick = onClaim,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("claim_student_btn_${student.id}")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Ekle", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Koçluğuna Al", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CoachPoolCard(
    coach: CoachEntity,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth().testTag("coach_card_${coach.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = coach.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        if (coach.verifiedBadge) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.Verified, contentDescription = "Onaylı", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        text = "${coach.title} • ${coach.specialty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Dynamic Performance Stars Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandGoldDark.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGoldDark.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Puan", tint = BrandGoldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", coach.rating),
                            style = MaterialTheme.typography.titleMedium,
                            color = BrandGoldDark,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = coach.bio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Demand Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Gelen Talep", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    Text("📊 ${coach.requestsCount} Talep", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text("Aktif Kadro", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    Text("👥 ${coach.activeStudentsCount} / ${coach.maxCapacity}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text("Başarı Oranı", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    Text("🎯 %${coach.successRatePct} Net Artış", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = StatusSuccessDark)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Seans: ${coach.completedSessionsCount} Tamamlandı",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("request_coach_btn_${coach.id}")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Koçluk Talep Et", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResourceCard(
    resource: ResourceEntity,
    onOpenResource: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (resource.isOfficialMeb) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth().testTag("resource_card_${resource.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (resource.isOfficialMeb) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 2.dp)
                        ) {
                            Text(
                                text = "🏛️ RESMİ MEB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = resource.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "📥 ${resource.downloads} İndirme",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kaynak: ${resource.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Format: ${resource.fileSizeOrType} • ${resource.targetField}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onOpenResource,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resource.isOfficialMeb) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (resource.isOfficialMeb) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("resource_action_btn_${resource.id}")
                ) {
                    Icon(
                        imageVector = if (resource.isOfficialMeb) Icons.Default.OpenInBrowser else Icons.Default.Download,
                        contentDescription = "Aç",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (resource.isOfficialMeb) "Resmi Portali Aç" else "Görüntüle / İndir",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyPoolCard(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
    ) {
        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadResourceModal(
    onDismiss: () -> Unit,
    onUpload: (title: String, category: String, targetField: String, description: String, url: String, type: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Ders Notu & Kamp") }
    var targetField by remember { mutableStateOf("SAYISAL") }
    var description by remember { mutableStateOf("") }
    var urlOrUri by remember { mutableStateOf("https://ogmmateryal.eba.gov.tr") }
    var fileSizeOrType by remember { mutableStateOf("PDF (3.2 MB)") }
    var contentNotes by remember { mutableStateOf("") }

    val categories = listOf("Ders Notu & Kamp", "Soru Bankası & Test", "Özet Formül Tablosu", "Rehberlik & Takip", "Deneme Sınavı")
    val targetFields = listOf("SAYISAL", "EŞİT AĞIRLIK", "SÖZEL", "DİL (YDT)", "LGS", "GENEL")

    var categoryExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
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
                        text = "Yeni Eğitim Dokümanı Yükle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Doküman Başlığı") },
                    placeholder = { Text("Örn: AYT Fizik Optik Özet Notları") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Target Field Dropdown
                    ExposedDropdownMenuBox(
                        expanded = targetExpanded,
                        onExpandedChange = { targetExpanded = !targetExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = targetField,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Alan") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = targetExpanded,
                            onDismissRequest = { targetExpanded = false }
                        ) {
                            targetFields.forEach { field ->
                                DropdownMenuItem(
                                    text = { Text(field) },
                                    onClick = {
                                        targetField = field
                                        targetExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = urlOrUri,
                    onValueChange = { urlOrUri = it },
                    label = { Text("Doküman Web Linki / OGM / EBA / PDF") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Kısa Açıklama") },
                    placeholder = { Text("Bu dokümanın içeriği ve öğrenciye faydası...") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contentNotes,
                    onValueChange = { contentNotes = it },
                    label = { Text("Ders Notları & Çözüm İpuçları (İsteğe Bağlı)") },
                    placeholder = { Text("Öğrencilerin uygulama içinden okuyabileceği özet notlar...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            onUpload(title, category, targetField, description, urlOrUri, fileSizeOrType, contentNotes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Dokümanı Yayınla & Öğrencilerle Paylaş", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CoachRequestModal(
    coach: CoachEntity,
    onDismiss: () -> Unit,
    onSubmit: (message: String) -> Unit
) {
    var message by remember { mutableStateOf("Haftalık net analizi ve soru takibi için koçluk talep ediyorum.") }

    Dialog(onDismissRequest = onDismiss) {
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
                        text = "Koçluk Başvuru Formu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(text = coach.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${coach.title} • ⭐ ${coach.rating} Performans Puanı", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Mevcut Talep Sayısı: ${coach.requestsCount} Başvuru", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Koça Notunuz & Hedefiniz") },
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSubmit(message) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_coach_request_btn")
                ) {
                    Text("Talebi Gönder (Havuza İşle)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResourceDetailsModal(
    resource: ResourceEntity,
    onDismiss: () -> Unit,
    onOpenUrl: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                        text = "Doküman Detayı & Notlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Hazırlayan: ${resource.author} • Format: ${resource.fileSizeOrType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Özet İçerik & Notlar:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = resource.contentNotes.ifEmpty { resource.description },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Paylaş", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onOpenUrl,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Portali Aç", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
