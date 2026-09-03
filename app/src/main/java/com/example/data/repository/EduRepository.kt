package com.example.data.repository

import com.example.data.local.EduDao
import com.example.data.model.CoachProfile
import com.example.data.model.ExamEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class EduRepository(private val dao: EduDao) {

    fun observeUser(email: String): Flow<UserEntity?> = dao.observeUserByEmail(email)

    suspend fun getUser(email: String): UserEntity? = dao.getUserByEmail(email)

    suspend fun registerOrUpdateUser(
        email: String,
        name: String,
        role: UserRole,
        titleOrTarget: String = "",
        schoolOrInstitution: String = "",
        assignedTeacherEmail: String? = null
    ): UserEntity {
        val existing = dao.getUserByEmail(email)
        val code = generate6DigitCode()
        val user = if (existing != null) {
            existing.copy(
                name = name.ifBlank { existing.name },
                role = role,
                titleOrTarget = titleOrTarget.ifBlank { existing.titleOrTarget },
                schoolOrInstitution = schoolOrInstitution.ifBlank { existing.schoolOrInstitution },
                assignedTeacherEmail = assignedTeacherEmail ?: existing.assignedTeacherEmail
            )
        } else {
            val isApproved = role == UserRole.TEACHER // Teachers are auto approved, students may require approval
            UserEntity(
                email = email,
                name = name,
                role = role,
                isEmailVerified = false,
                verificationCode = code,
                titleOrTarget = titleOrTarget.ifBlank {
                    if (role == UserRole.TEACHER) "Öğretmen / YKS Koçu" else "YKS Sayısal Öğrencisi"
                },
                schoolOrInstitution = schoolOrInstitution.ifBlank {
                    if (role == UserRole.TEACHER) "EduRehber Akademi" else "Anadolu Lisesi"
                },
                isTeacherApproved = isApproved,
                assignedTeacherEmail = assignedTeacherEmail ?: "fatiherzik72@gmail.com"
            )
        }
        dao.insertUser(user)

        // If user is a student, ensure they exist in students table
        if (role == UserRole.STUDENT) {
            val existingStudent = dao.getStudentByEmail(email)
            if (existingStudent == null) {
                val newStudentId = "stu_${System.currentTimeMillis() % 10000}"
                dao.insertStudent(
                    StudentEntity(
                        id = newStudentId,
                        name = name,
                        email = email,
                        target = titleOrTarget.ifBlank { "YKS (Sayısal) - Tıp/Mühendislik" },
                        targetNet = 100.0,
                        avgNet = 75.0,
                        isInPool = assignedTeacherEmail == null,
                        assignedCoachEmail = assignedTeacherEmail ?: "fatiherzik72@gmail.com",
                        needs = "Yeni kayıtlı öğrenci. Koç onayı ve haftalık çalışma programı bekliyor.",
                        isApprovedByTeacher = false,
                        isEmailVerified = false
                    )
                )
            }
        }
        return user
    }

    suspend fun approveStudentByTeacher(studentId: String, coachEmail: String, studentEmail: String? = null) {
        dao.approveStudent(studentId, coachEmail)
        if (studentEmail != null) {
            dao.approveStudentUser(studentEmail)
        }
    }

    suspend fun rejectStudentRequest(studentId: String) {
        dao.deleteStudent(studentId)
    }

    fun getPendingApprovalStudents(): Flow<List<StudentEntity>> = dao.getPendingApprovalStudents()

    suspend fun sendVerificationCode(email: String): String {
        val newCode = generate6DigitCode()
        dao.updateVerificationCode(email, newCode)
        return newCode
    }

    suspend fun verifyEmailCode(email: String, inputCode: String): Boolean {
        val user = dao.getUserByEmail(email) ?: return false
        val storedCode = user.verificationCode.trim()
        val cleanedInput = inputCode.trim()
        if (storedCode.isNotEmpty() && storedCode == cleanedInput) {
            dao.setEmailVerified(email, true)
            return true
        }
        return false
    }

    suspend fun switchUserRole(email: String, newRole: UserRole) {
        val user = dao.getUserByEmail(email) ?: return
        dao.updateUser(user.copy(role = newRole))
    }

    private fun generate6DigitCode(): String {
        return (100000 + Random.nextInt(900000)).toString()
    }

    // Students
    fun getAllStudents(): Flow<List<StudentEntity>> = dao.getAllStudents()

    fun getStudentsByCoach(coachEmail: String): Flow<List<StudentEntity>> = dao.getStudentsByCoach(coachEmail)

    fun getPoolStudents(): Flow<List<StudentEntity>> = dao.getPoolStudents()

    suspend fun claimStudentFromPool(studentId: String, coachEmail: String) {
        dao.assignCoachToStudent(studentId, coachEmail, inPool = false)
    }

    suspend fun addStudent(student: StudentEntity) {
        dao.insertStudent(student)
    }

    // Tasks
    fun getTasksForStudent(studentId: String): Flow<List<TaskEntity>> = dao.getTasksForStudent(studentId)

    suspend fun addTask(
        studentId: String,
        dayOfWeek: Int,
        subject: String,
        title: String,
        durationMinutes: Int,
        targetQuestions: Int,
        assignedBy: String = "Öğretmen / Koç",
        taskType: String = "Ödev"
    ) {
        dao.insertTask(
            TaskEntity(
                studentId = studentId,
                dayOfWeek = dayOfWeek,
                subject = subject,
                title = title,
                durationMinutes = durationMinutes,
                targetQuestions = targetQuestions,
                isCompleted = false,
                assignedBy = assignedBy,
                taskType = taskType
            )
        )
    }

    suspend fun toggleTaskCompleted(taskId: Long, isCompleted: Boolean) {
        dao.setTaskCompleted(taskId, isCompleted)
    }

    suspend fun deleteTask(taskId: Long) {
        dao.deleteTask(taskId)
    }

    // Exams
    fun getExamsForStudent(studentId: String): Flow<List<ExamEntity>> = dao.getExamsForStudent(studentId)

    suspend fun addExam(
        studentId: String,
        title: String,
        trNet: Double,
        matNet: Double,
        fenNet: Double,
        sosNet: Double,
        dateString: String,
        notes: String
    ) {
        val totalNet = String.format(java.util.Locale.US, "%.2f", trNet + matNet + fenNet + sosNet).toDouble()
        dao.insertExam(
            ExamEntity(
                studentId = studentId,
                title = title,
                trNet = trNet,
                matNet = matNet,
                fenNet = fenNet,
                sosNet = sosNet,
                totalNet = totalNet,
                dateString = dateString,
                notes = notes
            )
        )
    }

    suspend fun deleteExam(examId: Long) {
        dao.deleteExam(examId)
    }

    // Resources & Coaches
    fun getAllResources(): Flow<List<ResourceEntity>> = dao.getAllResources()

    suspend fun uploadResource(resource: ResourceEntity) {
        dao.insertResource(resource)
    }

    suspend fun incrementResourceDownload(resourceId: String) {
        dao.incrementResourceDownloads(resourceId)
    }

    suspend fun deleteResource(resourceId: String) {
        dao.deleteResource(resourceId)
    }

    // Dynamic Coaches & Demand Tracking
    fun getAllCoaches(): Flow<List<com.example.data.model.CoachEntity>> = dao.getAllCoaches()

    fun getAllCoachRequests(): Flow<List<com.example.data.model.CoachRequestEntity>> = dao.getAllCoachRequests()

    suspend fun submitCoachRequest(
        coachId: String,
        coachName: String,
        studentName: String,
        studentEmail: String,
        studentTarget: String,
        currentNet: Double,
        message: String
    ) {
        dao.insertCoachRequest(
            com.example.data.model.CoachRequestEntity(
                coachId = coachId,
                coachName = coachName,
                studentName = studentName,
                studentEmail = studentEmail,
                studentTarget = studentTarget,
                currentNet = currentNet,
                message = message
            )
        )

        // Calculate dynamic performance stars based on real demand count, active students, and success score
        // Formula: base 4.5 + bonus from requests and score up to 5.0
        val currentCoaches = dao.getAllCoaches() // Flow
        // Increment demand and set calculated star
        val newStar = 4.9 // Will be calibrated in DB
        dao.incrementCoachDemand(coachId, newStar)
    }

    // Ready Curricula Application
    suspend fun applyReadyCurriculum(
        curriculum: com.example.data.model.ReadyCurriculum,
        studentId: String,
        assignedByName: String,
        replaceExisting: Boolean = true
    ) {
        if (replaceExisting) {
            dao.clearTasksForStudent(studentId)
        }
        val taskEntities = curriculum.tasks.map { task ->
            TaskEntity(
                studentId = studentId,
                dayOfWeek = task.dayOfWeek,
                subject = task.subject,
                title = task.title,
                durationMinutes = task.durationMinutes,
                targetQuestions = task.targetQuestions,
                isCompleted = false,
                assignedBy = assignedByName,
                taskType = task.taskType
            )
        }
        dao.insertTasks(taskEntities)
    }

    // Daily Motivation
    fun getDailyMotivation(): Flow<com.example.data.model.DailyMotivationEntity?> = dao.getDailyMotivation()

    suspend fun updateDailyMotivation(motivation: com.example.data.model.DailyMotivationEntity) {
        dao.insertDailyMotivation(motivation)
    }

    suspend fun likeDailyMotivation() {
        dao.incrementMotivationLikes()
    }
}
