package com.example.data.model

data class ActiveSet(
    val setNumber: Int,
    var weightKg: Float,
    var reps: Int,
    var durationSeconds: Int = 0,
    var distanceKm: Float = 0f,
    var isCompleted: Boolean = false,
    var isPR: Boolean = false,
    var notes: String = ""
)

data class ActiveExercise(
    val exerciseId: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val exerciseType: String = "Musculação",
    val defaultRestSeconds: Int = 60,
    val alternativeIds: List<String> = emptyList(),
    val sets: MutableList<ActiveSet> = mutableListOf(),
    val instructions: String = "",
    val tips: String = "",
    val commonMistakes: String = "",
    var notes: String = "",
    val previousPerformance: String? = null // e.g. "Último treino: 10x 30kg, 10x 30kg, 8x 32kg"
)

data class ActiveWorkoutSession(
    val planId: String,
    val workoutName: String,
    val startTimeMillis: Long,
    val exercises: MutableList<ActiveExercise>,
    var currentExerciseIndex: Int = 0,
    var isPaused: Boolean = false,
    var pausedSeconds: Long = 0,
    var elapsedSeconds: Int = 0,
    var notes: String = ""
)
