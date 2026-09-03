package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.BrandGoldDark
import com.example.ui.theme.BrandGoldLight
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.BrandPrimaryContainerDark
import com.example.ui.theme.BrandPrimaryContainerLight
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusErrorContainer
import com.example.ui.theme.StatusErrorDark
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessContainer
import com.example.ui.theme.StatusSuccessDark
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.VerificationUiState

@Composable
fun EduTopBar(
    user: UserEntity?,
    onToggleRole: () -> Unit,
    onOpenVerification: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTeacher = user?.role == UserRole.TEACHER
    val isVerified = user?.isEmailVerified == true

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onOpenProfile)
                    .testTag("app_brand_header")
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "EduRehber Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "EduRehber",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isTeacher) "Koç & Yönetim Paneli" else "Öğrenci Portalı",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right Action Controls: Role switch + Verification indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Email Verification Status Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isVerified) StatusSuccessContainer else StatusErrorContainer)
                        .border(
                            width = 1.dp,
                            color = if (isVerified) StatusSuccess.copy(alpha = 0.5f) else StatusError.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onOpenVerification)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("email_verification_status_chip"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isVerified) Icons.Default.MarkEmailRead else Icons.Default.Email,
                            contentDescription = if (isVerified) "Doğrulandı" else "Doğrulanmadı",
                            tint = if (isVerified) StatusSuccessDark else StatusErrorDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isVerified) "Doğrulandı" else "Doğrula",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isVerified) StatusSuccessDark else StatusErrorDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Dynamic Role Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .clickable(onClick = onToggleRole)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("role_toggle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isTeacher) BrandGoldLight else MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = if (isTeacher) "KOÇ" else "ÖĞRENCİ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Rol Değiştir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmailVerificationNotificationBanner(
    user: UserEntity?,
    onOpenVerification: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (user != null && !user.isEmailVerified) {
        Surface(
            color = StatusErrorContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .border(1.dp, StatusError.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenVerification)
                .testTag("unverified_email_banner")
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Uyarı",
                        tint = StatusErrorDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "E-posta Adresiniz Doğrulanmadı",
                            style = MaterialTheme.typography.labelLarge,
                            color = StatusErrorDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${user.email} adresine gelen 6 haneli kodu girin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "Doğrula →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun EmailVerificationDialog(
    state: VerificationUiState,
    onCodeChange: (String) -> Unit,
    onResend: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    fun openMailClient() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"))
            try {
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    if (state.isDialogVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .testTag("email_verification_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Güvenlik",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "E-posta Doğrulama",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "256-Bit SSL Güvenli Doğrulama",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp).testTag("close_verification_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mail Transmission Metadata Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "E-posta",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = state.email,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (state.referenceId.isNotEmpty()) {
                                    Text(
                                        text = state.referenceId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Text(
                                text = "Tek kullanımlık 6 haneli güvenlik kodu e-posta kutunuza iletilmiştir.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Real Timer Bar & Countdown State
                    val minutes = state.validityRemainingSeconds / 60
                    val seconds = state.validityRemainingSeconds % 60
                    val formattedTime = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
                    val progress = (state.validityRemainingSeconds / 180f).coerceIn(0f, 1f)

                    Surface(
                        color = if (state.isExpired) StatusErrorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (state.isExpired) StatusError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("real_timer_card")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (state.isExpired) Icons.Default.HourglassEmpty else Icons.Default.HourglassBottom,
                                        contentDescription = "Zamanlayıcı",
                                        tint = if (state.isExpired) StatusErrorDark else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (state.isExpired) "Kodun Süresi Doldu" else "Kod Geçerlilik Süresi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (state.isExpired) StatusErrorDark else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (state.isExpired) "00:00" else formattedTime,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (state.isExpired) StatusErrorDark else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { if (state.isExpired) 0f else progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (state.validityRemainingSeconds < 30) StatusErrorDark else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 6-Box Segmented OTP Input
                    Text(
                        text = "6 Haneli Doğrulama Kodunu Giriniz",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Hidden background BasicTextField to capture keyboard events
                        BasicTextField(
                            value = state.otpCode,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }.take(6)
                                onCodeChange(digitsOnly)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                if (state.otpCode.length == 6 && !state.isExpired) onSubmit()
                            }),
                            enabled = !state.isExpired && !state.isLocked && !state.isVerifying,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_hidden_input")
                        ) {
                            // Segmented 6 Visual Box Display
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.testTag("otp_boxes_row")
                            ) {
                                for (i in 0 until 6) {
                                    val digit = state.otpCode.getOrNull(i)?.toString() ?: ""
                                    val isFocused = state.otpCode.length == i && !state.isExpired

                                    Box(
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (digit.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                            )
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = when {
                                                    state.isExpired -> StatusError.copy(alpha = 0.4f)
                                                    isFocused -> MaterialTheme.colorScheme.primary
                                                    digit.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = digit,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Attempt Status Counter
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Kalan Deneme",
                            tint = if (state.remainingAttempts <= 2) StatusErrorDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kalan Deneme Hakkı: ${state.remainingAttempts} / 5",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.remainingAttempts <= 2) StatusErrorDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Error Message Alert
                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = StatusErrorContainer,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = StatusErrorDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    // Success Feedback
                    if (state.isVerifiedSuccess) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.testTag("verification_success_badge")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Başarılı",
                                tint = StatusSuccessDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎉 E-posta Başarıyla Doğrulandı!",
                                style = MaterialTheme.typography.labelLarge,
                                color = StatusSuccessDark,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Verification Submission Button
                    Button(
                        onClick = onSubmit,
                        enabled = !state.isVerifying && state.otpCode.length == 6 && !state.isExpired && !state.isLocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_submit_button")
                    ) {
                        if (state.isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (state.isExpired) "Süre Doldu (Yeni Kod İsteyin)" else "Doğrulamayı Tamamla",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Actions Row: Open Mail Client + Resend with Cooldown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Open Mail Client Action
                        TextButton(
                            onClick = { openMailClient() },
                            modifier = Modifier.testTag("open_mail_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Posta Aç",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Posta Kutusunu Aç",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Real Resend Action with 60s cooldown timer
                        if (state.resendCooldownSeconds > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Bekleyin",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Yeni Kod (${state.resendCooldownSeconds}s)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            TextButton(
                                onClick = onResend,
                                enabled = !state.isSendingCode,
                                modifier = Modifier.testTag("resend_otp_button")
                            ) {
                                if (state.isSendingCode) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Tekrar Gönder",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Yeni Kod Gönder",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "💡 Kod gelmediyse spam / gereksiz e-posta klasörünü kontrol ediniz.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSave: (dayOfWeek: Int, subject: String, title: String, duration: Int, questions: Int) -> Unit
) {
    if (isOpen) {
        var dayOfWeek by remember { mutableIntStateOf(1) }
        var subject by remember { mutableStateOf("TYT Matematik") }
        var title by remember { mutableStateOf("") }
        var duration by remember { mutableIntStateOf(60) }
        var questions by remember { mutableIntStateOf(35) }
        var taskType by remember { mutableStateOf("Ödev") }

        val days = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar")
        val taskTypes = listOf("Ödev", "Etüt", "Deneme", "Soru Çözümü")

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("add_task_dialog")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Yeni Ödev / Görev Planla",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Task Type Selector
                    Text("Görev Türü:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        taskTypes.forEach { t ->
                            val isSel = t == taskType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { taskType = t }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Day selector row
                    Text("Hedef Gün", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        days.forEachIndexed { index, d ->
                            val dayNum = index + 1
                            val isSelected = dayNum == dayOfWeek
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { dayOfWeek = dayNum }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.take(3),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Ders / Branş") },
                        placeholder = { Text("Örn: TYT Matematik") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("task_subject_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Ödev Başlığı & Açıklaması") },
                        placeholder = { Text("Örn: Problemler ve Fonksiyonlar 40 Soru") },
                        modifier = Modifier.fillMaxWidth().testTag("task_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = duration.toString(),
                            onValueChange = { duration = it.toIntOrNull() ?: 0 },
                            label = { Text("Süre (Dk)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("task_duration_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        OutlinedTextField(
                            value = questions.toString(),
                            onValueChange = { questions = it.toIntOrNull() ?: 0 },
                            label = { Text("Hedef Soru") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("task_questions_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(dayOfWeek, subject, title, duration, questions)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_task_button")
                    ) {
                        Text("Ödevi / Görevi Kaydet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddExamDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, tr: Double, mat: Double, fen: Double, sos: Double, date: String, notes: String) -> Unit
) {
    if (isOpen) {
        var examTitle by remember { mutableStateOf("") }
        var trNet by remember { mutableDoubleStateOf(32.5) }
        var matNet by remember { mutableDoubleStateOf(28.0) }
        var fenNet by remember { mutableDoubleStateOf(15.0) }
        var sosNet by remember { mutableDoubleStateOf(16.5) }
        var examNotes by remember { mutableStateOf("") }

        val totalNet = String.format(java.util.Locale.US, "%.2f", trNet + matNet + fenNet + sosNet).toDouble()

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("add_exam_dialog")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Deneme Sınavı Kaydet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = { examTitle = it },
                        label = { Text("Deneme Adı / Yayın") },
                        placeholder = { Text("Örn: 3D Türkiye Geneli TYT") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("exam_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Net Inputs Grid
                    Text("Branş Netleri:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = trNet.toString(),
                            onValueChange = { trNet = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Türkçe") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("net_tr_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = matNet.toString(),
                            onValueChange = { matNet = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Mat") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("net_mat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = fenNet.toString(),
                            onValueChange = { fenNet = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Fen") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("net_fen_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        OutlinedTextField(
                            value = sosNet.toString(),
                            onValueChange = { sosNet = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Sos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("net_sos_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Computed Total Net Highlight
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hesaplanan Toplam Net:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalNet Net", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = examNotes,
                        onValueChange = { examNotes = it },
                        label = { Text("Deneme Notu / Hata Analizi") },
                        placeholder = { Text("Geometride süre yetişmedi, fizikte optik tekrarı gerekli...") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("exam_notes_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (examTitle.isNotBlank()) {
                                onSave(examTitle, trNet, matNet, fenNet, sosNet, "Bugün", examNotes)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_exam_button")
                    ) {
                        Text("Denemeyi Kaydet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

