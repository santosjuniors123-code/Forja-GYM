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
import com.example.data.local.entities.WorkoutExerciseEntity
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.data.local.entities.WorkoutScheduleEntity
import com.example.data.model.ActiveExercise
import com.example.data.model.ActiveSet
import com.example.data.model.ActiveWorkoutSession
import com.example.data.model.ConfiguredSet
import com.example.data.model.CustomSetsParser
import com.example.data.model.DayOfWeekPt
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthState
import com.example.data.repository.ForjaRepository
import com.example.data.utils.FormatUtils
import com.example.ui.navigation.Screen
import java.util.Calendar
import java.util.UUID
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

    // Summary of last completed workout for celebration modal
    val lastCompletedWorkoutSummary = MutableStateFlow<CompletedWorkoutEntity?>(null)

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

    val weeklySchedule: StateFlow<List<WorkoutScheduleEntity>> = repository.getWeeklySchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authState: StateFlow<AuthState> = authRepository.authState

    // ==========================================
    // WORKOUT EDITING & CUSTOMIZATION STATE
    // ==========================================
    private val _selectedWorkoutPlanForEdit = MutableStateFlow<WorkoutPlanEntity?>(null)
    val selectedWorkoutPlanForEdit: StateFlow<WorkoutPlanEntity?> = _selectedWorkoutPlanForEdit.asStateFlow()

    private val _editingWorkoutExercises = MutableStateFlow<List<WorkoutExerciseEntity>>(emptyList())
    val editingWorkoutExercises: StateFlow<List<WorkoutExerciseEntity>> = _editingWorkoutExercises.asStateFlow()

    // ==========================================
    // WORKOUT HISTORY DETAIL STATE
    // ==========================================
    private val _selectedCompletedWorkout = MutableStateFlow<CompletedWorkoutEntity?>(null)
    val selectedCompletedWorkout: StateFlow<CompletedWorkoutEntity?> = _selectedCompletedWorkout.asStateFlow()

    private val _selectedCompletedWorkoutSets = MutableStateFlow<List<CompletedSetEntity>>(emptyList())
    val selectedCompletedWorkoutSets: StateFlow<List<CompletedSetEntity>> = _selectedCompletedWorkoutSets.asStateFlow()

    // ==========================================
    // TODAY'S WORKOUT OVERRIDE (Somente hoje)
    // ==========================================
    val todayOverridePlan = MutableStateFlow<WorkoutPlanEntity?>(null)
    val todayOverrideIsRest = MutableStateFlow<Boolean>(false)
    val hasTodayOverride = MutableStateFlow<Boolean>(false)

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
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

    // ==========================================
    // SCHEDULE SYSTEM (MINHA SEMANA)
    // ==========================================

    fun getTodayDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return DayOfWeekPt.fromCalendar(calendar).dayNumber
    }

    fun setScheduleDay(dayOfWeek: Int, workoutId: String?, workoutName: String?, isRestDay: Boolean) {
        viewModelScope.launch {
            repository.setScheduleDay(dayOfWeek, workoutId, workoutName, isRestDay)
        }
    }

    fun updateScheduleStatus(dayOfWeek: Int, status: String) {
        viewModelScope.launch {
            repository.updateScheduleStatus(dayOfWeek, status)
        }
    }

    fun rescheduleDayWorkout(fromDay: Int, toDay: Int, action: String) {
        viewModelScope.launch {
            repository.rescheduleDayWorkout(fromDay, toDay, action)
        }
    }

    fun overrideTodayWorkout(plan: WorkoutPlanEntity?, isRest: Boolean, updateWeeklySchedule: Boolean) {
        hasTodayOverride.value = true
        todayOverridePlan.value = plan
        todayOverrideIsRest.value = isRest
        if (updateWeeklySchedule) {
            val today = getTodayDayOfWeek()
            setScheduleDay(
                dayOfWeek = today,
                workoutId = if (isRest) null else plan?.id,
                workoutName = if (isRest) "Dia de Descanso" else (plan?.name ?: "Treino Programado"),
                isRestDay = isRest
            )
        }
    }

    fun clearTodayOverride() {
        hasTodayOverride.value = false
        todayOverridePlan.value = null
        todayOverrideIsRest.value = false
    }

    // ==========================================
    // WORKOUT CRUD & EDITING SYSTEM
    // ==========================================

    fun openCreateWorkout() {
        _selectedWorkoutPlanForEdit.value = null
        _editingWorkoutExercises.value = emptyList()
        navigateTo(Screen.EDIT_WORKOUT)
    }

    fun openEditWorkout(plan: WorkoutPlanEntity) {
        _selectedWorkoutPlanForEdit.value = plan
        viewModelScope.launch {
            val exercises = repository.getWorkoutExercisesOnce(plan.id)
            if (exercises.isNotEmpty()) {
                _editingWorkoutExercises.value = exercises
            } else if (plan.exerciseIds.isNotBlank()) {
                val exIds = plan.exerciseIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val entities = repository.getExercisesByIds(exIds).associateBy { it.id }
                val converted = exIds.mapIndexedNotNull { index, id ->
                    val entity = entities[id] ?: return@mapIndexedNotNull null
                    val defaultSets = (1..entity.defaultSets).map { ConfiguredSet(setNumber = it, reps = entity.defaultReps, weightKg = 20f) }
                    WorkoutExerciseEntity(
                        workoutId = plan.id,
                        exerciseId = entity.id,
                        orderIndex = index,
                        exerciseName = entity.name,
                        exerciseType = entity.exerciseType,
                        sets = entity.defaultSets,
                        reps = entity.defaultReps,
                        weightKg = 20f,
                        restSeconds = entity.defaultRestSeconds,
                        notes = "",
                        customSetsJson = CustomSetsParser.toJson(defaultSets)
                    )
                }
                _editingWorkoutExercises.value = converted
            } else {
                _editingWorkoutExercises.value = emptyList()
            }
            navigateTo(Screen.EDIT_WORKOUT)
        }
    }

    fun duplicateWorkout(planId: String) {
        viewModelScope.launch {
            repository.duplicateWorkout(planId)
        }
    }

    fun deleteWorkout(planId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(planId)
        }
    }

    fun addExerciseToEditingWorkout(
        exercise: ExerciseEntity,
        sets: Int = 3,
        reps: Int = 10,
        weightKg: Float = 20f,
        restSeconds: Int = 60,
        exerciseType: String = "Musculação",
        customSets: List<ConfiguredSet> = emptyList(),
        durationSeconds: Int = 0,
        distanceKm: Float = 0f,
        speedKmh: Float = 0f,
        incline: Float = 0f,
        notes: String = ""
    ) {
        val currentList = _editingWorkoutExercises.value.toMutableList()
        val nextOrder = currentList.size
        val effectiveCustomSets = if (customSets.isNotEmpty()) {
            customSets
        } else {
            (1..sets).map { ConfiguredSet(setNumber = it, reps = reps, weightKg = weightKg, durationSeconds = durationSeconds, distanceKm = distanceKm, speedKmh = speedKmh, incline = incline) }
        }
        val newEx = WorkoutExerciseEntity(
            workoutId = _selectedWorkoutPlanForEdit.value?.id ?: "",
            exerciseId = exercise.id,
            orderIndex = nextOrder,
            exerciseName = exercise.name,
            exerciseType = exerciseType.ifBlank { exercise.exerciseType },
            sets = sets,
            reps = reps,
            weightKg = weightKg,
            restSeconds = restSeconds,
            durationSeconds = durationSeconds,
            distanceKm = distanceKm,
            speedKmh = speedKmh,
            incline = incline,
            notes = notes,
            customSetsJson = CustomSetsParser.toJson(effectiveCustomSets)
        )
        currentList.add(newEx)
        _editingWorkoutExercises.value = currentList
    }

    fun updateEditingExercise(updated: WorkoutExerciseEntity, index: Int) {
        val list = _editingWorkoutExercises.value.toMutableList()
        if (index in list.indices) {
            list[index] = updated
            _editingWorkoutExercises.value = list
        }
    }

    fun removeEditingExercise(index: Int) {
        val list = _editingWorkoutExercises.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            // Re-index
            val reindexed = list.mapIndexed { idx, item -> item.copy(orderIndex = idx) }
            _editingWorkoutExercises.value = reindexed
        }
    }

    fun duplicateEditingExercise(index: Int) {
        val list = _editingWorkoutExercises.value.toMutableList()
        if (index in list.indices) {
            val item = list[index]
            val duplicate = item.copy(
                id = 0,
                exerciseName = "${item.exerciseName} (cópia)",
                orderIndex = index + 1
            )
            list.add(index + 1, duplicate)
            val reindexed = list.mapIndexed { idx, itm -> itm.copy(orderIndex = idx) }
            _editingWorkoutExercises.value = reindexed
        }
    }

    fun moveEditingExercise(fromIndex: Int, toIndex: Int) {
        val list = _editingWorkoutExercises.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            val reindexed = list.mapIndexed { idx, itm -> itm.copy(orderIndex = idx) }
            _editingWorkoutExercises.value = reindexed
        }
    }

    fun saveWorkout(
        name: String,
        description: String,
        code: String,
        targetMuscles: String,
        estimatedMinutes: Int,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val existingPlan = _selectedWorkoutPlanForEdit.value
            val currentExercises = _editingWorkoutExercises.value

            val workoutId: String
            if (existingPlan == null) {
                val newPlan = repository.createWorkout(
                    name = name,
                    description = description,
                    code = code.ifBlank { "Custom" },
                    targetMuscles = targetMuscles.ifBlank { "Geral" },
                    estimatedMinutes = estimatedMinutes
                )
                workoutId = newPlan.id
            } else {
                workoutId = existingPlan.id
                val updatedPlan = existingPlan.copy(
                    name = name,
                    description = description,
                    code = code,
                    targetMuscles = targetMuscles,
                    estimatedMinutes = estimatedMinutes,
                    exerciseIds = currentExercises.joinToString(",") { it.exerciseId },
                    updatedAt = System.currentTimeMillis()
                )
                repository.updatePlan(updatedPlan)
            }

            // Sync exercises in DB
            repository.syncWorkoutExercisesForPlan(workoutId, currentExercises)

            _selectedWorkoutPlanForEdit.value = null
            _editingWorkoutExercises.value = emptyList()
            onComplete()
        }
    }

    fun createCustomExercise(
        name: String,
        muscleGroup: String,
        equipment: String,
        exerciseType: String,
        onCreated: (ExerciseEntity) -> Unit
    ) {
        viewModelScope.launch {
            val newEx = repository.createCustomExercise(
                name = name,
                muscleGroup = muscleGroup,
                equipment = equipment,
                exerciseType = exerciseType
            )
            onCreated(newEx)
        }
    }

    // ==========================================
    // WORKOUT HISTORY DETAIL
    // ==========================================

    fun openWorkoutHistoryDetail(workout: CompletedWorkoutEntity) {
        _selectedCompletedWorkout.value = workout
        viewModelScope.launch {
            val sets = repository.getSetsForSessionOnce(workout.id)
            _selectedCompletedWorkoutSets.value = sets
            navigateTo(Screen.WORKOUT_HISTORY_DETAIL)
        }
    }

    // ==========================================
    // WORKOUT EXECUTION SYSTEM
    // ==========================================

    fun startWorkout(planId: String) {
        viewModelScope.launch {
            val plan = repository.getPlanById(planId) ?: return@launch
            val workoutExercises = repository.getWorkoutExercisesOnce(planId)

            val activeExercises = mutableListOf<ActiveExercise>()

            if (workoutExercises.isNotEmpty()) {
                // Use customized workout exercises
                workoutExercises.forEach { we ->
                    val recentSets = repository.getRecentSetsForExercise(we.exerciseId)
                    val prevPerfString = if (recentSets.isNotEmpty()) {
                        "Último: " + recentSets.take(4).joinToString(", ") { "${it.weightKg.toInt()}kg × ${it.reps}" }
                    } else null

                    val configuredSets = CustomSetsParser.fromJson(we.customSetsJson, defaultSets = we.sets, defaultReps = we.reps, defaultWeight = we.weightKg)
                    val activeSets = configuredSets.mapIndexed { idx, cs ->
                        ActiveSet(
                            setNumber = idx + 1,
                            weightKg = cs.weightKg,
                            reps = cs.reps,
                            durationSeconds = cs.durationSeconds,
                            distanceKm = cs.distanceKm,
                            isCompleted = false,
                            notes = cs.notes
                        )
                    }.toMutableList()

                    val baseExercise = repository.getExerciseById(we.exerciseId)

                    activeExercises.add(
                        ActiveExercise(
                            exerciseId = we.exerciseId,
                            name = we.exerciseName,
                            muscleGroup = baseExercise?.muscleGroup ?: "Geral",
                            equipment = baseExercise?.equipment ?: "Halteres",
                            exerciseType = we.exerciseType,
                            defaultRestSeconds = we.restSeconds,
                            alternativeIds = baseExercise?.alternativeIds?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                            sets = activeSets,
                            instructions = baseExercise?.execution ?: "",
                            tips = baseExercise?.tips ?: "",
                            commonMistakes = baseExercise?.commonMistakes ?: "",
                            notes = we.notes,
                            previousPerformance = prevPerfString
                        )
                    )
                }
            } else {
                // Fallback to plan.exerciseIds
                val exerciseIds = plan.exerciseIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val exerciseEntities = repository.getExercisesByIds(exerciseIds)
                val exerciseMap = exerciseEntities.associateBy { it.id }

                exerciseIds.forEach { id ->
                    val entity = exerciseMap[id] ?: return@forEach
                    val recentSets = repository.getRecentSetsForExercise(id)
                    val lastWeight = recentSets.firstOrNull()?.weightKg ?: 20f
                    val prevPerfString = if (recentSets.isNotEmpty()) {
                        "Último: " + recentSets.take(4).joinToString(", ") { "${it.weightKg.toInt()}kg × ${it.reps}" }
                    } else null

                    val sets = (1..entity.defaultSets).map { setIndex ->
                        ActiveSet(
                            setNumber = setIndex,
                            weightKg = lastWeight,
                            reps = entity.defaultReps,
                            isCompleted = false
                        )
                    }.toMutableList()

                    activeExercises.add(
                        ActiveExercise(
                            exerciseId = entity.id,
                            name = entity.name,
                            muscleGroup = entity.muscleGroup,
                            equipment = entity.equipment,
                            exerciseType = entity.exerciseType,
                            defaultRestSeconds = entity.defaultRestSeconds,
                            alternativeIds = entity.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            sets = sets,
                            instructions = entity.execution,
                            tips = entity.tips,
                            commonMistakes = entity.commonMistakes,
                            notes = "",
                            previousPerformance = prevPerfString
                        )
                    )
                }
            }

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

    fun completeSet(exerciseIndex: Int, setIndex: Int, weightKg: Float, reps: Int, notes: String = "") {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            val exercise = session.exercises[exerciseIndex]
            if (setIndex in exercise.sets.indices) {
                val currentSet = exercise.sets[setIndex]
                val updatedSet = currentSet.copy(
                    weightKg = weightKg,
                    reps = reps,
                    notes = notes.ifBlank { currentSet.notes },
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
                durationSeconds = lastSet?.durationSeconds ?: 0,
                distanceKm = lastSet?.distanceKm ?: 0f,
                isCompleted = false
            )
            exercise.sets.add(newSet)
            _activeWorkout.value = session.copy()
        }
    }

    fun removeSetFromExercise(exerciseIndex: Int, setIndex: Int) {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            val exercise = session.exercises[exerciseIndex]
            if (setIndex in exercise.sets.indices && exercise.sets.size > 1) {
                exercise.sets.removeAt(setIndex)
                // Re-index
                exercise.sets.forEachIndexed { i, s -> exercise.sets[i] = s.copy(setNumber = i + 1) }
                _activeWorkout.value = session.copy()
            }
        }
    }

    fun changeExercise(exerciseIndex: Int, newExerciseId: String) {
        viewModelScope.launch {
            val newEntity = repository.getExerciseById(newExerciseId) ?: return@launch
            val session = _activeWorkout.value ?: return@launch

            if (exerciseIndex in session.exercises.indices) {
                val oldEx = session.exercises[exerciseIndex]
                val recentSets = repository.getRecentSetsForExercise(newEntity.id)
                val prevPerfString = if (recentSets.isNotEmpty()) {
                    "Último: " + recentSets.take(4).joinToString(", ") { "${it.weightKg.toInt()}kg × ${it.reps}" }
                } else null

                val updatedEx = ActiveExercise(
                    exerciseId = newEntity.id,
                    name = newEntity.name,
                    muscleGroup = newEntity.muscleGroup,
                    equipment = newEntity.equipment,
                    exerciseType = newEntity.exerciseType,
                    defaultRestSeconds = newEntity.defaultRestSeconds,
                    alternativeIds = newEntity.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    sets = oldEx.sets,
                    instructions = newEntity.execution,
                    tips = newEntity.tips,
                    commonMistakes = newEntity.commonMistakes,
                    notes = oldEx.notes,
                    previousPerformance = prevPerfString
                )
                session.exercises[exerciseIndex] = updatedEx
                _activeWorkout.value = session.copy()
            }
        }
    }

    // In-workout modifications
    fun addExerciseToActiveWorkout(exercise: ExerciseEntity) {
        val session = _activeWorkout.value ?: return
        viewModelScope.launch {
            val recentSets = repository.getRecentSetsForExercise(exercise.id)
            val prevPerfString = if (recentSets.isNotEmpty()) {
                "Último: " + recentSets.take(4).joinToString(", ") { "${it.weightKg.toInt()}kg × ${it.reps}" }
            } else null
            val defaultWeight = recentSets.firstOrNull()?.weightKg ?: 20f

            val sets = (1..exercise.defaultSets).map {
                ActiveSet(
                    setNumber = it,
                    weightKg = defaultWeight,
                    reps = exercise.defaultReps,
                    isCompleted = false
                )
            }.toMutableList()

            val newActiveEx = ActiveExercise(
                exerciseId = exercise.id,
                name = exercise.name,
                muscleGroup = exercise.muscleGroup,
                equipment = exercise.equipment,
                exerciseType = exercise.exerciseType,
                defaultRestSeconds = exercise.defaultRestSeconds,
                alternativeIds = exercise.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                sets = sets,
                instructions = exercise.execution,
                tips = exercise.tips,
                commonMistakes = exercise.commonMistakes,
                previousPerformance = prevPerfString
            )

            session.exercises.add(newActiveEx)
            _activeWorkout.value = session.copy()
        }
    }

    fun removeExerciseFromActiveWorkout(index: Int) {
        val session = _activeWorkout.value ?: return
        if (index in session.exercises.indices && session.exercises.size > 1) {
            session.exercises.removeAt(index)
            if (session.currentExerciseIndex >= session.exercises.size) {
                session.currentExerciseIndex = session.exercises.size - 1
            }
            _activeWorkout.value = session.copy()
        }
    }

    fun updateActiveExerciseNotes(exerciseIndex: Int, notes: String) {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            session.exercises[exerciseIndex].notes = notes
            _activeWorkout.value = session.copy()
        }
    }

    fun updateActiveExerciseRestSeconds(exerciseIndex: Int, restSeconds: Int) {
        val session = _activeWorkout.value ?: return
        if (exerciseIndex in session.exercises.indices) {
            val ex = session.exercises[exerciseIndex]
            session.exercises[exerciseIndex] = ex.copy(defaultRestSeconds = restSeconds)
            _activeWorkout.value = session.copy()
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

    fun toggleRestTimerPause() {
        if (isRestTimerRunning.value) {
            restTimerJob?.cancel()
            isRestTimerRunning.value = false
        } else if (restSecondsRemaining.value > 0) {
            startRestTimer(restSecondsRemaining.value)
        }
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

    fun finishWorkout(onFinished: ((CompletedWorkoutEntity) -> Unit)? = null) {
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
                            durationSeconds = set.durationSeconds,
                            distanceKm = set.distanceKm,
                            isPR = set.isPR,
                            notes = set.notes.ifBlank { exercise.notes },
                            exerciseType = exercise.exerciseType,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            val savedId = repository.finishWorkout(
                planId = session.planId,
                workoutName = session.workoutName,
                startTimeMillis = session.startTimeMillis,
                endTimeMillis = System.currentTimeMillis(),
                completedSets = completedSets,
                workoutNotes = session.notes
            )

            val summaryEntity = repository.getCompletedWorkoutById(savedId)
            lastCompletedWorkoutSummary.value = summaryEntity
            _activeWorkout.value = null

            if (onFinished != null && summaryEntity != null) {
                onFinished(summaryEntity)
            }
        }
    }

    fun cancelWorkout(savePartial: Boolean) {
        val session = _activeWorkout.value
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        if (savePartial && session != null) {
            val anyCompleted = session.exercises.any { it.sets.any { s -> s.isCompleted } }
            if (anyCompleted) {
                finishWorkout()
                return
            }
        }

        _activeWorkout.value = null
        viewModelScope.launch {
            repository.clearActiveWorkoutState()
        }
        navigateBack()
    }

    fun dismissCelebrationModal() {
        lastCompletedWorkoutSummary.value = null
        navigateTo(Screen.HISTORY)
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
