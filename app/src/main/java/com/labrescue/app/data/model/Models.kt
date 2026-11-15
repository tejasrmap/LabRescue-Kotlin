package com.labrescue.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class MistakeCategory {
    SYNTAX,
    CONFIGURATION,
    LOGIC,
    CONCEPTUAL
}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: String = "user_default",
    val email: String = "student@university.ac.uk",
    val name: String = "Student Learner",
    val programme: String = "BSc (Hons) Computer Science",
    val year: String = "Year 1",
    @ColumnInfo(name = "target_goal")
    val targetGoal: String = "Master Systems & Database Troubleshooting",
    @ColumnInfo(name = "enrolled_subjects")
    val enrolledSubjects: String = "db,net,prog,os",
    @ColumnInfo(name = "total_rescues")
    val totalRescues: Int = 0,
    @ColumnInfo(name = "successful_rescues")
    val successfulRescues: Int = 0,
    @ColumnInfo(name = "streak_days")
    val streakDays: Int = 0
)

@Entity(
    tableName = "user_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserAccount(
    @PrimaryKey
    val id: String,
    val email: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    val salt: String,
    val name: String,
    val programme: String,
    val year: String,
    @ColumnInfo(name = "target_goal")
    val targetGoal: String,
    @ColumnInfo(name = "enrolled_subjects")
    val enrolledSubjects: String = "db,net,prog,os",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

data class Subject(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val iconName: String,
    val totalScenarios: Int = 0
)

data class DiagnosticOption(
    val id: String,
    val label: String,
    val evidenceDetails: String,
    val isCorrectPath: Boolean,
    val whyExplanation: String,
    val penaltyCategory: MistakeCategory? = null,
    val nextNodeId: String? = null
)

data class ScenarioNode(
    val id: String,
    val scenarioId: String,
    val stepIndex: Int,
    val totalSteps: Int,
    val questionPrompt: String,
    val investigationHint: String,
    val options: List<DiagnosticOption>
)

data class Scenario(
    val id: String,
    val subjectId: String,
    val title: String,
    val difficulty: Difficulty,
    val estimatedMinutes: Int,
    val symptom: String,
    val consoleOutput: String,
    val labContext: String,
    val rootCauseSummary: String,
    val fixRecommendation: String,
    val startNodeId: String
)

@Entity(
    tableName = "attempts",
    indices = [Index(value = ["user_id"])]
)
data class Attempt(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String = "user_default",
    @ColumnInfo(name = "scenario_id")
    val scenarioId: String,
    @ColumnInfo(name = "scenario_title")
    val scenarioTitle: String,
    @ColumnInfo(name = "subject_id")
    val subjectId: String,
    val score: Int, // 0 - 100
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long,
    @ColumnInfo(name = "is_success")
    val isSuccess: Boolean,
    @ColumnInfo(name = "mistakes_count")
    val mistakesCount: Int,
    @ColumnInfo(name = "decisions_path_json")
    val decisionsPathJson: String
)

@Entity(
    tableName = "mistakes",
    foreignKeys = [
        ForeignKey(
            entity = Attempt::class,
            parentColumns = ["id"],
            childColumns = ["attempt_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["attempt_id"])]
)
data class Mistake(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "attempt_id")
    val attemptId: String,
    @ColumnInfo(name = "scenario_id")
    val scenarioId: String,
    val category: MistakeCategory,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
