package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CoachEntity
import com.example.data.model.CoachRequestEntity
import com.example.data.model.DailyMotivationEntity
import com.example.data.model.ExamEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EduDao {
    // Users
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun observeUserByEmail(email: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isEmailVerified = :isVerified WHERE email = :email")
    suspend fun setEmailVerified(email: String, isVerified: Boolean)

    @Query("UPDATE users SET verificationCode = :code WHERE email = :email")
    suspend fun updateVerificationCode(email: String, code: String)

    // Students
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE assignedCoachEmail = :coachEmail AND isApprovedByTeacher = 1")
    fun getStudentsByCoach(coachEmail: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE isInPool = 1 AND isApprovedByTeacher = 1")
    fun getPoolStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE isApprovedByTeacher = 0")
    fun getPendingApprovalStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Query("UPDATE students SET isInPool = :inPool, assignedCoachEmail = :coachEmail, isApprovedByTeacher = 1 WHERE id = :studentId")
    suspend fun assignCoachToStudent(studentId: String, coachEmail: String, inPool: Boolean = false)

    @Query("UPDATE students SET isApprovedByTeacher = 1, assignedCoachEmail = :coachEmail, isInPool = 0 WHERE id = :studentId")
    suspend fun approveStudent(studentId: String, coachEmail: String)

    @Query("UPDATE users SET isTeacherApproved = 1 WHERE email = :studentEmail")
    suspend fun approveStudentUser(studentEmail: String)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudent(studentId: String)

    // Tasks
    @Query("SELECT * FROM tasks WHERE studentId = :studentId ORDER BY dayOfWeek ASC, id ASC")
    fun getTasksForStudent(studentId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE studentId = :studentId")
    suspend fun clearTasksForStudent(studentId: String)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Long, completed: Boolean)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    // Exams
    @Query("SELECT * FROM exams WHERE studentId = :studentId ORDER BY id ASC")
    fun getExamsForStudent(studentId: String): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExam(examId: Long)

    // Resources
    @Query("SELECT * FROM resources ORDER BY isOfficialMeb DESC, downloads DESC, createdAt DESC")
    fun getAllResources(): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<ResourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: ResourceEntity)

    @Query("UPDATE resources SET downloads = downloads + 1 WHERE id = :resourceId")
    suspend fun incrementResourceDownloads(resourceId: String)

    @Query("DELETE FROM resources WHERE id = :resourceId")
    suspend fun deleteResource(resourceId: String)

    // Coaches
    @Query("SELECT * FROM coaches ORDER BY rating DESC, requestsCount DESC")
    fun getAllCoaches(): Flow<List<CoachEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoaches(coaches: List<CoachEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoach(coach: CoachEntity)

    @Query("UPDATE coaches SET requestsCount = requestsCount + 1, rating = :newRating WHERE id = :coachId")
    suspend fun incrementCoachDemand(coachId: String, newRating: Double)

    // Coach Requests
    @Query("SELECT * FROM coach_requests ORDER BY timestamp DESC")
    fun getAllCoachRequests(): Flow<List<CoachRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoachRequest(request: CoachRequestEntity)

    // Daily Motivation Note (Coach written or AI generated)
    @Query("SELECT * FROM daily_motivations WHERE id = 1 LIMIT 1")
    fun getDailyMotivation(): Flow<DailyMotivationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyMotivation(motivation: DailyMotivationEntity)

    @Query("UPDATE daily_motivations SET likesCount = likesCount + 1 WHERE id = 1")
    suspend fun incrementMotivationLikes()
}
