package com.example.data.model

data class ActiveSet(
    val setNumber: Int,
    var weightKg: Float,
    var reps: Int,
    var isCompleted: Boolean = false,
    var isPR: Boolean = false
)

data class ActiveExercise(
    val exerciseId: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val defaultRestSeconds: Int,
    val alternativeIds: List<String>,
    val sets: MutableList<ActiveSet> = mutableListOf(),
    val instructions: String = "",
    val tips: String = "",
    val commonMistakes: String = ""
)

data class ActiveWorkoutSession(
    val planId: String,
    val workoutName: String,
    val startTimeMillis: Long,
    val exercises: MutableList<ActiveExercise>,
    var currentExerciseIndex: Int = 0,
    var isPaused: Boolean = false,
    var pausedSeconds: Long = 0,
    var elapsedSeconds: Int = 0
)
