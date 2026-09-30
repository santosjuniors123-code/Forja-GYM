package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val email: String = "",
    val age: Int = 25,
    val gender: String = "Masculino",
    val heightCm: Float = 175f,
    val initialWeightKg: Float = 80f,
    val currentWeightKg: Float = 80f,
    val targetWeightKg: Float = 75f,
    val mainGoal: String = "Hipertrofia",
    val trainingLevel: String = "Intermediário (1-3 anos)",
    val availableDays: Int = 5,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastWorkoutDate: String = "",
    val isRegistered: Boolean = false,
    val isOnboarded: Boolean = false,
    val isDemoUser: Boolean = false,
    val dailyWaterGoalMl: Int = 3000
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val level: String,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultRestSeconds: Int = 60,
    val preparation: String,
    val execution: String,
    val commonMistakes: String,
    val tips: String,
    val alternativeIds: String, // Comma separated IDs
    val isFavorite: Boolean = false,
    val mediaType: String = "placeholder",
    val mediaPlaceholderLabel: String = "Demonstração 3D disponível"
)

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey val id: String,
    val code: String, // "A", "B", "C", "D", "E"
    val name: String,
    val description: String,
    val targetMuscles: String,
    val estimatedMinutes: Int,
    val exerciseIds: String // Comma separated IDs
)

@Entity(tableName = "active_workout_state")
data class ActiveWorkoutStateEntity(
    @PrimaryKey val id: Int = 1,
    val planId: String,
    val workoutName: String,
    val startTimeMillis: Long,
    val currentExerciseIndex: Int = 0,
    val completedSetsJson: String = "", // serialized list of sets
    val isPaused: Boolean = false,
    val accumulatedPausedSeconds: Long = 0,
    val restTimerEndMillis: Long = 0
)

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: String,
    val workoutName: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Int,
    val totalVolumeKg: Float,
    val totalSets: Int,
    val totalReps: Int,
    val dateString: String // YYYY-MM-DD
)

@Entity(tableName = "completed_sets")
data class CompletedSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutSessionId: Long,
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val weightKg: Float,
    val reps: Int,
    val isPR: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val exerciseId: String,
    val exerciseName: String,
    val maxWeightKg: Float,
    val repsAtMaxWeight: Int,
    val dateString: String,
    val timestamp: Long
)

@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val dateString: String,
    val timeString: String,
    val note: String = "",
    val timestamp: Long
)

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val timestamp: Long,
    val rightArm: Float = 0f,
    val leftArm: Float = 0f,
    val chest: Float = 0f,
    val waist: Float = 0f,
    val abdomen: Float = 0f,
    val hips: Float = 0f,
    val rightThigh: Float = 0f,
    val leftThigh: Float = 0f,
    val calves: Float = 0f
)

@Entity(tableName = "evolution_photos")
data class EvolutionPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val timestamp: Long,
    val angle: String, // "Frontal", "Lateral", "Costas"
    val imageUri: String,
    val note: String = ""
)

@Entity(tableName = "hydration_records")
data class HydrationRecordEntity(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val consumedMl: Int = 0,
    val targetMl: Int = 3000
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "PESO", "CARGA", "FREQUENCIA", "HIDRATACAO", "CARDIO", "TREINOS"
    val targetValue: Float,
    val currentValue: Float,
    val unit: String,
    val deadline: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

@Entity(tableName = "cardio_records")
data class CardioRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String,
    val durationMinutes: Int,
    val distanceKm: Float,
    val estimatedCalories: Int,
    val dateString: String,
    val timestamp: Long,
    val notes: String = ""
)
