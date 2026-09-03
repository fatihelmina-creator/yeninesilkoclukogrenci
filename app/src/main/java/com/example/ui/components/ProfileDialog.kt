package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorContainer
import com.example.ui.theme.StatusErrorDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusSuccessDark

@Composable
fun ProfileDialog(
    isOpen: Boolean,
    user: UserEntity?,
    onDismiss: () -> Unit,
    onRegisterOrSave: (
        name: String,
        email: String,
        role: UserRole,
        target: String,
        institution: String,
        assignedTeacherEmail: String?
    ) -> Unit,
    onSwitchAccount: (String) -> Unit,
    onOpenVerification: (String) -> Unit
) {
    if (!isOpen) return

    var activeTab by remember { mutableIntStateOf(0) } // 0: Yeni Kayıt Ol, 1: Hızlı Giriş / Hesap Değiştir, 2: Profilim

    // Registration Form State
    var regRole by remember { mutableStateOf(UserRole.STUDENT) }
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regTitleOrTarget by remember { mutableStateOf("") }
    var regInstitution by remember { mutableStateOf("") }
    var regTeacherEmail by remember { mutableStateOf("fatiherzik72@gmail.com") }

    // Edit Profile State
    var editName by remember(user) { mutableStateOf(user?.name ?: "") }
    var editEmail by remember(user) { mutableStateOf(user?.email ?: "") }
    var editRole by remember(user) { mutableStateOf(user?.role ?: UserRole.TEACHER) }
    var editTitleOrTarget by remember(user) { mutableStateOf(user?.titleOrTarget ?: "") }
    var editInstitution by remember(user) { mutableStateOf(user?.schoolOrInstitution ?: "") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .testTag("profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
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
                                imageVector = Icons.Default.School,
                                contentDescription = "Hesap",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Kullanıcı & Kayıt Yönetimi",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Öğretmen ve öğrenci doğrulama sistemi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_profile_dialog_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Top Mode Switcher (Kayıt Ol vs Hesap Değiştir vs Profilim)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        listOf("Kayıt Ol", "Hesap Seç", "Profilim").forEachIndexed { idx, title ->
                            val isSel = activeTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.surface else Color.Transparent)
                                    .clickable { activeTab = idx }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSel) FontWeight.Black else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TAB 0: SEPARATE USER REGISTRATION (Öğretmen ve Öğrenci için Ayrı Kayıt)
                if (activeTab == 0) {
                    Text(
                        text = "Kayıt Türünü Seçin:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Role Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Teacher Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (regRole == UserRole.TEACHER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (regRole == UserRole.TEACHER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { regRole = UserRole.TEACHER }
                                .testTag("register_role_teacher")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "👨‍🏫 Öğretmen / Koç",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (regRole == UserRole.TEACHER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ödev ve Takip Yetkisi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (regRole == UserRole.TEACHER) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Student Option
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (regRole == UserRole.STUDENT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (regRole == UserRole.STUDENT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { regRole = UserRole.STUDENT }
                                .testTag("register_role_student")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "👨‍🎓 Öğrenci",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (regRole == UserRole.STUDENT) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Kişisel Çalışma Alanı",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (regRole == UserRole.STUDENT) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form Fields
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Ad Soyad") },
                        placeholder = { Text(if (regRole == UserRole.TEACHER) "Örn: Mehmet Hoca" else "Örn: Burcu Aydın") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("E-posta Adresi") },
                        placeholder = { Text(if (regRole == UserRole.TEACHER) "mehmet.ogretmen@edu.com" else "burcu.ogrenci@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regTitleOrTarget,
                        onValueChange = { regTitleOrTarget = it },
                        label = { Text(if (regRole == UserRole.TEACHER) "Branş / Uzmanlık Alanı" else "Hedef Sınav & Bölüm") },
                        placeholder = { Text(if (regRole == UserRole.TEACHER) "Örn: Matematik & Geometri" else "Örn: YKS Sayısal - Tıp Hedefi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_target_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regInstitution,
                        onValueChange = { regInstitution = it },
                        label = { Text(if (regRole == UserRole.TEACHER) "Okul / Kurum Adı" else "Mevcut Okul / Sınıf") },
                        placeholder = { Text(if (regRole == UserRole.TEACHER) "EduRehber Akademi" else "Fen Lisesi 12. Sınıf") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_institution_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    if (regRole == UserRole.STUDENT) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = regTeacherEmail,
                            onValueChange = { regTeacherEmail = it },
                            label = { Text("Eğitmen / Koç E-postası (Onay için)") },
                            placeholder = { Text("fatiherzik72@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_coach_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Policy Info Card
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Bilgi",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (regRole == UserRole.TEACHER) {
                                    "📧 Kayıt sonrası 6 haneli e-posta doğrulama kodu gönderilecektir."
                                } else {
                                    "📧 E-posta doğrulamasının ardından koçunuzun onayı ile ödev atamaları etkinleşecektir."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Register Button
                    Button(
                        onClick = {
                            if (regEmail.isNotBlank() && regName.isNotBlank()) {
                                onRegisterOrSave(
                                    regName,
                                    regEmail,
                                    regRole,
                                    regTitleOrTarget,
                                    regInstitution,
                                    if (regRole == UserRole.STUDENT) regTeacherEmail else null
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_registration_btn")
                    ) {
                        Icon(imageVector = Icons.Default.HowToReg, contentDescription = "Kayıt", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (regRole == UserRole.TEACHER) "Öğretmen Hesabı Oluştur & Doğrula" else "Öğrenci Kaydı Yap & Doğrula",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // TAB 1: QUICK ROLE SWITCH & ACCOUNT PICKER
                if (activeTab == 1) {
                    Text(
                        text = "Demo Hesap Seçici (RBAC Testi):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Switch Options
                    val accounts = listOf(
                        Triple("fatiherzik72@gmail.com", "Fatih Erzik", "👨‍🏫 Öğretmen / Koç (Tam Yetki)"),
                        Triple("zeynep.kaya@gmail.com", "Zeynep Kaya", "👨‍🎓 Öğrenci (Onaylı & Ödev Atanmış)"),
                        Triple("eren.yildiz@ogrenci.com", "Eren Yıldız", "👨‍🎓 Öğrenci (Öğretmen Onayı Bekliyor)")
                    )

                    accounts.forEach { (email, name, roleDesc) ->
                        val isCurrent = user?.email == email
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSwitchAccount(email)
                                    onDismiss()
                                }
                                .testTag("account_item_$email")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(text = roleDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text(text = email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Aktif", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Geçiş Yap", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                // TAB 2: EDIT PROFILE
                if (activeTab == 2) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Ad Soyad") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("E-posta Adresi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editTitleOrTarget,
                        onValueChange = { editTitleOrTarget = it },
                        label = { Text(if (editRole == UserRole.TEACHER) "Branş / Uzmanlık" else "Hedef Sınav & Bölüm") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editInstitution,
                        onValueChange = { editInstitution = it },
                        label = { Text("Kurum / Okul") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_institution_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email Verification Banner
                    Surface(
                        color = if (user?.isEmailVerified == true) StatusSuccessContainer else StatusErrorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (user?.isEmailVerified == true) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = "Durum",
                                    tint = if (user?.isEmailVerified == true) StatusSuccessDark else StatusErrorDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (user?.isEmailVerified == true) "E-posta Doğrulandı ✓" else "E-posta Doğrulanmadı",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (user?.isEmailVerified == true) StatusSuccessDark else StatusErrorDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (user?.isEmailVerified == true) "Hesabınız güvenli ve onaylı." else "Doğrulama kodu talep edin.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (user?.isEmailVerified != true) {
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onOpenVerification(editEmail)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusErrorDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Kod Al", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Profile Button
                    Button(
                        onClick = {
                            if (editEmail.isNotBlank()) {
                                onRegisterOrSave(
                                    editName,
                                    editEmail,
                                    editRole,
                                    editTitleOrTarget,
                                    editInstitution,
                                    user?.assignedTeacherEmail
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_profile_button")
                    ) {
                        Text("Profili Güncelle", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
