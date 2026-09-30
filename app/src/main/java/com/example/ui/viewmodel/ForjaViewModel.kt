package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ForjaDatabase
import com.example.data.local.entities.AchievementEntity
import com.example.data.local.entities.BodyMeasurementEntity
import com.example.data.local.entities.CardioRecordEntity
import com.example.data.local.entities.CompletedSetEntity
import com.example.data.local.entities.CompletedWorkoutEntity
import com.example.data.local.entities.EvolutionPhotoEntity
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.GoalEntity
import com.example.data.local.entities.HydrationRecordEntity
import com.example.data.local.entities.PersonalRecordEntity
import com.example.data.local.entities.UserProfileEntity
import com.example.data.local.entities.WeightRecordEntity
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.data.model.ActiveExercise
import com.example.data.model.ActiveSet
import com.example.data.model.ActiveWorkoutSession
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthState
import com.example.data.repository.ForjaRepository
import com.example.data.utils.FormatUtils
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ForjaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ForjaDatabase.getDatabase(application, viewModelScope)
    val repository = ForjaRepository(database)
    val authRepository = AuthRepository(repository)

    // Navigation state
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    // Selected Exercise for detail view
    private val _selectedExercise = MutableStateFlow<ExerciseEntity?>(null)
    val selectedExercise: StateFlow<ExerciseEntity?> = _selectedExercise.asStateFlow()

    // Exercise search & filters
    val searchQuery = MutableStateFlow("")
    val selectedMuscleFilter = MutableStateFlow("Todos")
    val selectedEquipmentFilter = MutableStateFlow("Todos")
    val selectedLevelFilter = MutableStateFlow("Todos")

    // Active Workout Execution State
    private val _activeWorkout = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeWorkout: StateFlow<ActiveWorkoutSession?> = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    val restSecondsRemaining = MutableStateFlow(0)
    val isRestTimerRunning = MutableStateFlow(false)
    val defaultRestTime = MutableStateFlow(60)

    // PR banner notification
    val prAlert = MutableStateFlow<String?>(null)

    // Settings
    val isDarkMode = MutableStateFlow(true)
    val usePounds = MutableStateFlow(false)
    val notificationsEnabled = MutableStateFlow(true)

    // Shared StateFlows from Room
    val userProfile: StateFlow<UserProfileEntity?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allExercises: StateFlow<List<ExerciseEntity>> = repository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutPlans: StateFlow<List<WorkoutPlanEntity>> = repository.getAllPlans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedWorkouts: StateFlow<List<CompletedWorkoutEntity>> = repository.getAllCompletedWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCompletedCount: StateFlow<Int> = repository.getTotalCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val personalRecords: StateFlow<List<PersonalRecordEntity>> = repository.getAllPersonalRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightRecords: StateFlow<List<WeightRecordEntity>> = repository.getAllWeightRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyMeasurements: StateFlow<List<BodyMeasurementEntity>> = repository.getAllMeasurements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evolutionPhotos: StateFlow<List<EvolutionPhotoEntity>> = repository.getAllPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayHydration: StateFlow<HydrationRecordEntity?> = repository.getTodayHydration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentHydration: StateFlow<List<HydrationRecordEntity>> = repository.getRecentHydration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = repository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cardioRecords: StateFlow<List<CardioRecordEntity>> = repository.getAllCardio()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authState: StateFlow<AuthState> = authRepository.authState

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userProfile.collect { profile ->
                // Profile observed reactively
            }
        }
    }

    // Navigation helpers
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (screenStack.isNotEmpty()) {
            val previous = screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = previous
            true
        } else {
            if (_currentScreen.value != Screen.DASHBOARD) {
                _currentScreen.value = Screen.DASHBOARD
                true
            } else {
                false
            }
        }
    }

    fun selectExercise(exercise: ExerciseEntity) {
        _selectedExercise.value = exercise
        navigateTo(Screen.EXERCISE_DETAIL)
    }

    fun toggleFavorite(exerciseId: String, currentVal: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(exerciseId, !currentVal)
        }
    }

    // Onboarding Submission
    fun completeOnboarding(
        name: String,
        age: Int,
        gender: String,
        heightCm: Float,
        currentWeightKg: Float,
        targetWeightKg: Float,
        mainGoal: String,
        trainingLevel: String,
        availableDays: Int
    ) {
        viewModelScope.launch {
            repository.updateOnboarding(
                name = name,
                age = age,
                gender = gender,
                heightCm = heightCm,
                currentWeightKg = currentWeightKg,
                targetWeightKg = targetWeightKg,
                mainGoal = mainGoal,
                trainingLevel = trainingLevel,
                availableDays = availableDays
            )
            screenStack.clear()
            _currentScreen.value = Screen.DASHBOARD
        }
    }

    // --- WORKOUT EXECUTION SYSTEM ---

    fun startWorkout(planId: String) {
        viewModelScope.launch {
            val plan = repository.getPlanById(planId) ?: return@launch
            val exerciseIds = plan.exerciseIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val exerciseEntities = repository.getExercisesByIds(exerciseIds)
            val exerciseMap = exerciseEntities.associateBy { it.id }

            val activeExercises = exerciseIds.mapNotNull { id ->
                val entity = exerciseMap[id] ?: return@mapNotNull null
                val recentSets = repository.getRecentSetsForExercise(id)
                val lastWeight = recentSets.firstOrNull()?.weightKg ?: 20f

                val sets = (1..entity.defaultSets).map { setIndex ->
                    ActiveSet(
                        setNumber = setIndex,
                        weightKg = lastWeight,
                        reps = entity.defaultReps,
                        isCompleted = false
                    )
                }.toMutableList()

                ActiveExercise(
                    exerciseId = entity.id,
                    name = entity.name,
                    muscleGroup = entity.muscleGroup,
                    equipment = entity.equipment,
                    defaultRestSeconds = entity.defaultRestSeconds,
                    alternativeIds = entity.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    sets = sets,
                    instructions = entity.execution,
                    tips = entity.tips,
                    commonMistakes = entity.commonMistakes
                )
            }.toMutableList()

            val session = ActiveWorkoutSession(
                planId = plan.id,
                workoutName = plan.name,
                startTimeMillis = System.currentTimeMillis(),
                exercises = activeExercises,
                currentExerciseIndex = 0
            )

            _activeWorkout.value = session
            startWorkoutTimer()
            navigateTo(Screen.ACTIVE_WORKOUT)
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeWorkout.value?.let { session ->
                    if (!session.isPaused) {
                        session.elapsedSeconds++
                        // Trigger state update
                        _activeWorkout.value = session.copy(elapsedSeconds = session.elapsedSeconds)
                    }
                }
            }
        }
    }

    fun toggleWorkoutPause() {
        _activeWorkout.value?.let { session ->
            session.isPaused = !session.isPaused
            _activeWorkout.value = session.copy(isPaused = session.isPaused)
        }
    }

    fun completeSet(exerciseIndex: Int, setIndex: Int, weightKg: Float, reps: Int) {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            val exercise = session.exercises[exerciseIndex]
            if (setIndex in exercise.sets.indices) {
                val currentSet = exercise.sets[setIndex]
                val updatedSet = currentSet.copy(
                    weightKg = weightKg,
                    reps = reps,
                    isCompleted = true
                )
                exercise.sets[setIndex] = updatedSet

                // Check for Personal Record automatically
                viewModelScope.launch {
                    val isPR = repository.checkAndUpdatePR(
                        exerciseId = exercise.exerciseId,
                        exerciseName = exercise.name,
                        weightKg = weightKg,
                        reps = reps
                    )
                    if (isPR) {
                        exercise.sets[setIndex] = updatedSet.copy(isPR = true)
                        prAlert.value = "🔥 NOVO RECORDE: ${exercise.name} com ${weightKg}kg!"
                    }
                }

                _activeWorkout.value = session.copy()

                // Trigger rest timer
                startRestTimer(exercise.defaultRestSeconds)
            }
        }
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            val exercise = session.exercises[exerciseIndex]
            val lastSet = exercise.sets.lastOrNull()
            val newSet = ActiveSet(
                setNumber = exercise.sets.size + 1,
                weightKg = lastSet?.weightKg ?: 20f,
                reps = lastSet?.reps ?: 10,
                isCompleted = false
            )
            exercise.sets.add(newSet)
            _activeWorkout.value = session.copy()
        }
    }

    fun changeExercise(exerciseIndex: Int, newExerciseId: String) {
        viewModelScope.launch {
            val newEntity = repository.getExerciseById(newExerciseId) ?: return@launch
            val session = _activeWorkout.value ?: return@launch

            if (exerciseIndex in session.exercises.indices) {
                val oldEx = session.exercises[exerciseIndex]
                val updatedEx = ActiveExercise(
                    exerciseId = newEntity.id,
                    name = newEntity.name,
                    muscleGroup = newEntity.muscleGroup,
                    equipment = newEntity.equipment,
                    defaultRestSeconds = newEntity.defaultRestSeconds,
                    alternativeIds = newEntity.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    sets = oldEx.sets, // Preserves sets and reps as requested
                    instructions = newEntity.execution,
                    tips = newEntity.tips,
                    commonMistakes = newEntity.commonMistakes
                )
                session.exercises[exerciseIndex] = updatedEx
                _activeWorkout.value = session.copy()
            }
        }
    }

    fun setCurrentExerciseIndex(index: Int) {
        _activeWorkout.value?.let { session ->
            if (index in session.exercises.indices) {
                session.currentExerciseIndex = index
                _activeWorkout.value = session.copy(currentExerciseIndex = index)
            }
        }
    }

    // Rest Timer System
    fun startRestTimer(seconds: Int = defaultRestTime.value) {
        restTimerJob?.cancel()
        restSecondsRemaining.value = seconds
        isRestTimerRunning.value = true

        restTimerJob = viewModelScope.launch {
            while (restSecondsRemaining.value > 0) {
                delay(1000)
                restSecondsRemaining.value--
            }
            isRestTimerRunning.value = false
            vibrateAlert()
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        isRestTimerRunning.value = false
        restSecondsRemaining.value = 0
    }

    fun adjustRestTimer(deltaSeconds: Int) {
        val current = restSecondsRemaining.value
        val newTime = (current + deltaSeconds).coerceAtLeast(0)
        restSecondsRemaining.value = newTime
        if (newTime == 0) {
            stopRestTimer()
        }
    }

    private fun vibrateAlert() {
        try {
            val context = getApplication<Application>().applicationContext
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(500)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback if device lacks hardware vibrator
        }
    }

    fun finishWorkout() {
        val session = _activeWorkout.value ?: return
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        viewModelScope.launch {
            val completedSets = mutableListOf<CompletedSetEntity>()
            session.exercises.forEach { exercise ->
                exercise.sets.filter { it.isCompleted }.forEach { set ->
                    completedSets.add(
                        CompletedSetEntity(
                            workoutSessionId = 0, // Assigned in repo
                            exerciseId = exercise.exerciseId,
                            exerciseName = exercise.name,
                            setNumber = set.setNumber,
                            weightKg = set.weightKg,
                            reps = set.reps,
                            isPR = set.isPR,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            repository.finishWorkout(
                planId = session.planId,
                workoutName = session.workoutName,
                startTimeMillis = session.startTimeMillis,
                endTimeMillis = System.currentTimeMillis(),
                completedSets = completedSets
            )

            _activeWorkout.value = null
            screenStack.clear()
            _currentScreen.value = Screen.HISTORY
        }
    }

    fun cancelWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkout.value = null
        viewModelScope.launch {
            repository.clearActiveWorkoutState()
        }
        navigateBack()
    }

    // Hydration
    fun addWater(ml: Int) {
        viewModelScope.launch {
            repository.addWater(ml)
        }
    }

    // Weight & Progress
    fun addWeightRecord(weightKg: Float, note: String) {
        viewModelScope.launch {
            repository.addWeightRecord(weightKg, note)
        }
    }

    fun deleteWeightRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteWeightRecord(id)
        }
    }

    fun addMeasurement(
        rightArm: Float,
        leftArm: Float,
        chest: Float,
        waist: Float,
        abdomen: Float,
        hips: Float,
        rightThigh: Float,
        leftThigh: Float,
        calves: Float
    ) {
        viewModelScope.launch {
            repository.addMeasurement(
                BodyMeasurementEntity(
                    dateString = FormatUtils.todayDateString(),
                    timestamp = System.currentTimeMillis(),
                    rightArm = rightArm,
                    leftArm = leftArm,
                    chest = chest,
                    waist = waist,
                    abdomen = abdomen,
                    hips = hips,
                    rightThigh = rightThigh,
                    leftThigh = leftThigh,
                    calves = calves
                )
            )
        }
    }

    fun addEvolutionPhoto(angle: String, uri: String, note: String) {
        viewModelScope.launch {
            repository.addPhoto(angle, uri, note)
        }
    }

    fun deleteEvolutionPhoto(id: Long) {
        viewModelScope.launch {
            repository.deletePhoto(id)
        }
    }

    // Cardio
    fun addCardio(type: String, durationMinutes: Int, distanceKm: Float, calories: Int, notes: String) {
        viewModelScope.launch {
            repository.addCardio(type, durationMinutes, distanceKm, calories, notes)
        }
    }

    // Goals
    fun addGoal(title: String, type: String, target: Float, current: Float, unit: String, deadline: String) {
        viewModelScope.launch {
            repository.addGoal(title, type, target, current, unit, deadline)
        }
    }

    fun updateGoalProgress(goal: GoalEntity, newValue: Float) {
        viewModelScope.launch {
            val completed = newValue >= goal.targetValue
            repository.updateGoal(goal.copy(currentValue = newValue, isCompleted = completed))
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    // Demo Mode Trigger
    fun loadDemoMode() {
        viewModelScope.launch {
            authRepository.loginDemo()
            _currentScreen.value = Screen.DASHBOARD
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.ensureInitialized()
            _activeWorkout.value = null
            screenStack.clear()
            _currentScreen.value = Screen.DASHBOARD
        }
    }
}
