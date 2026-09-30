package com.example.data.repository

import com.example.data.local.ForjaDatabase
import com.example.data.local.SeedData
import com.example.data.local.entities.AchievementEntity
import com.example.data.local.entities.ActiveWorkoutStateEntity
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
import com.example.data.model.ConfiguredSet
import com.example.data.model.CustomSetsParser
import com.example.data.utils.FormatUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class ForjaRepository(private val database: ForjaDatabase) {

    private val userDao = database.userDao()
    private val exerciseDao = database.exerciseDao()
    private val workoutDao = database.workoutDao()
    private val progressDao = database.progressDao()

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val count = exerciseDao.getCount()
        if (count == 0) {
            exerciseDao.insertAll(SeedData.exercises)
            workoutDao.insertPlans(SeedData.initialPlans)
            userDao.insertAchievements(SeedData.initialAchievements)
        }

        // Initialize default weekly schedule if empty
        val existingSchedule = workoutDao.getScheduleOnce()
        if (existingSchedule.isEmpty()) {
            val defaultSchedule = listOf(
                WorkoutScheduleEntity(dayOfWeek = 1, workoutId = "treino_a", workoutName = "Treino A - Peito, Tríceps e Abdômen", isRestDay = false, status = "PENDENTE"),
                WorkoutScheduleEntity(dayOfWeek = 2, workoutId = "treino_b", workoutName = "Treino B - Costas, Bíceps e Trapézio", isRestDay = false, status = "PENDENTE"),
                WorkoutScheduleEntity(dayOfWeek = 3, workoutId = null, workoutName = "Dia de Descanso", isRestDay = true, status = "DESCANSO"),
                WorkoutScheduleEntity(dayOfWeek = 4, workoutId = "treino_c", workoutName = "Treino C - Quadríceps, Posterior e Panturrilhas", isRestDay = false, status = "PENDENTE"),
                WorkoutScheduleEntity(dayOfWeek = 5, workoutId = "treino_d", workoutName = "Treino D - Ombros e Deltoides", isRestDay = false, status = "PENDENTE"),
                WorkoutScheduleEntity(dayOfWeek = 6, workoutId = null, workoutName = "Dia de Descanso", isRestDay = true, status = "DESCANSO"),
                WorkoutScheduleEntity(dayOfWeek = 7, workoutId = null, workoutName = "Dia de Descanso", isRestDay = true, status = "DESCANSO")
            )
            workoutDao.insertSchedule(defaultSchedule)
        }

        // Initialize workout_exercises for plans if empty
        val allPlans = workoutDao.getAllPlans().firstOrNull() ?: SeedData.initialPlans
        for (plan in allPlans) {
            val existingEx = workoutDao.getWorkoutExercisesOnce(plan.id)
            if (existingEx.isEmpty() && plan.exerciseIds.isNotBlank()) {
                val exIds = plan.exerciseIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val exEntities = exerciseDao.getExercisesByIds(exIds).associateBy { it.id }
                val list = exIds.mapIndexedNotNull { index, id ->
                    val entity = exEntities[id] ?: return@mapIndexedNotNull null
                    val defaultSetsList = (1..entity.defaultSets).map { setNum ->
                        ConfiguredSet(setNumber = setNum, reps = entity.defaultReps, weightKg = 20f)
                    }
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
                        customSetsJson = CustomSetsParser.toJson(defaultSetsList)
                    )
                }
                workoutDao.insertWorkoutExercises(list)
            }
        }

        val existingProfile = userDao.getUserProfileOnce()
        if (existingProfile == null) {
            val defaultProfile = UserProfileEntity(
                id = 1,
                name = "Atleta",
                email = "",
                age = 26,
                gender = "Masculino",
                heightCm = 178f,
                initialWeightKg = 80f,
                currentWeightKg = 80f,
                targetWeightKg = 75f,
                mainGoal = "Hipertrofia",
                trainingLevel = "Intermediário (1-3 anos)",
                availableDays = 5,
                currentStreak = 1,
                bestStreak = 1,
                lastWorkoutDate = "",
                isRegistered = true,
                isOnboarded = true,
                isDemoUser = false,
                dailyWaterGoalMl = 3000
            )
            userDao.saveUserProfile(defaultProfile)
            progressDao.insertWeightRecord(
                WeightRecordEntity(
                    weightKg = 80f,
                    dateString = FormatUtils.todayDateString(),
                    timeString = FormatUtils.formatTime(System.currentTimeMillis()),
                    note = "Peso inicial",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // User Profile
    fun getUserProfile(): Flow<UserProfileEntity?> = userDao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userDao.saveUserProfile(profile)
    }

    suspend fun updateOnboarding(
        name: String,
        age: Int,
        gender: String,
        heightCm: Float,
        currentWeightKg: Float,
        targetWeightKg: Float,
        mainGoal: String,
        trainingLevel: String,
        availableDays: Int
    ) = withContext(Dispatchers.IO) {
        val existing = userDao.getUserProfileOnce()
        val updated = (existing ?: UserProfileEntity()).copy(
            name = name,
            age = age,
            gender = gender,
            heightCm = heightCm,
            initialWeightKg = currentWeightKg,
            currentWeightKg = currentWeightKg,
            targetWeightKg = targetWeightKg,
            mainGoal = mainGoal,
            trainingLevel = trainingLevel,
            availableDays = availableDays,
            isOnboarded = true,
            isRegistered = true
        )
        userDao.saveUserProfile(updated)
        // Also record initial weight
        progressDao.insertWeightRecord(
            WeightRecordEntity(
                weightKg = currentWeightKg,
                dateString = FormatUtils.todayDateString(),
                timeString = FormatUtils.formatTime(System.currentTimeMillis()),
                note = "Peso inicial cadastrado",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Exercises
    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()
    fun getExercisesByMuscleGroup(group: String): Flow<List<ExerciseEntity>> = exerciseDao.getExercisesByMuscleGroup(group)
    fun getFavoriteExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getFavoriteExercises()

    suspend fun getExerciseById(id: String): ExerciseEntity? = withContext(Dispatchers.IO) {
        exerciseDao.getExerciseById(id)
    }

    suspend fun getExercisesByIds(ids: List<String>): List<ExerciseEntity> = withContext(Dispatchers.IO) {
        exerciseDao.getExercisesByIds(ids)
    }

    suspend fun toggleFavorite(id: String, isFav: Boolean) = withContext(Dispatchers.IO) {
        exerciseDao.toggleFavorite(id, isFav)
    }

    suspend fun createCustomExercise(
        name: String,
        muscleGroup: String,
        equipment: String,
        exerciseType: String = "Musculação",
        instructions: String = "Exercício personalizado criado pelo atleta.",
        tips: String = "",
        commonMistakes: String = ""
    ): ExerciseEntity = withContext(Dispatchers.IO) {
        val id = "custom_" + UUID.randomUUID().toString().take(8)
        val entity = ExerciseEntity(
            id = id,
            name = name,
            muscleGroup = muscleGroup,
            equipment = equipment,
            level = "Personalizado",
            defaultSets = 3,
            defaultReps = 10,
            defaultRestSeconds = 60,
            preparation = "Ajuste a posição inicial e postura corporal adequada.",
            execution = instructions,
            commonMistakes = commonMistakes.ifBlank { "Execução rápida sem controle de cadência." },
            tips = tips.ifBlank { "Mantenha o foco na contração muscular máxima." },
            alternativeIds = "",
            isFavorite = false,
            mediaType = "placeholder",
            mediaPlaceholderLabel = "Exercício Personalizado",
            exerciseType = exerciseType,
            isCustom = true
        )
        exerciseDao.insertExercise(entity)
        entity
    }

    // Workout Plans (CRUD)
    fun getAllPlans(): Flow<List<WorkoutPlanEntity>> = workoutDao.getAllPlans()

    suspend fun getPlanById(id: String): WorkoutPlanEntity? = withContext(Dispatchers.IO) {
        workoutDao.getPlanById(id)
    }

    suspend fun createWorkout(
        name: String,
        description: String = "",
        code: String = "Custom",
        targetMuscles: String = "Corpo Inteiro",
        estimatedMinutes: Int = 60
    ): WorkoutPlanEntity = withContext(Dispatchers.IO) {
        val id = "workout_" + UUID.randomUUID().toString().take(8)
        val plan = WorkoutPlanEntity(
            id = id,
            code = code,
            name = name,
            description = description.ifBlank { "Treino personalizado forjado pelo atleta." },
            targetMuscles = targetMuscles,
            estimatedMinutes = estimatedMinutes,
            exerciseIds = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        workoutDao.insertPlan(plan)
        plan
    }

    suspend fun updatePlan(plan: WorkoutPlanEntity) = withContext(Dispatchers.IO) {
        workoutDao.updatePlan(plan.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun duplicateWorkout(planId: String): WorkoutPlanEntity = withContext(Dispatchers.IO) {
        val original = workoutDao.getPlanById(planId) ?: throw IllegalArgumentException("Treino não encontrado")
        val newId = "workout_" + UUID.randomUUID().toString().take(8)
        val newCode = if (original.code.length <= 2) "${original.code}2" else original.code
        val duplicatedPlan = original.copy(
            id = newId,
            code = newCode,
            name = "${original.name} — cópia",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        workoutDao.insertPlan(duplicatedPlan)

        // Duplicate workout exercises
        val exercises = workoutDao.getWorkoutExercisesOnce(planId)
        val duplicatedExercises = exercises.map {
            it.copy(id = 0, workoutId = newId)
        }
        if (duplicatedExercises.isNotEmpty()) {
            workoutDao.insertWorkoutExercises(duplicatedExercises)
        }
        duplicatedPlan
    }

    suspend fun deleteWorkout(planId: String) = withContext(Dispatchers.IO) {
        workoutDao.deletePlan(planId)
        workoutDao.deleteWorkoutExercisesForPlan(planId)
        // Clear from schedule if present
        val schedule = workoutDao.getScheduleOnce()
        for (day in schedule) {
            if (day.workoutId == planId) {
                workoutDao.saveScheduleDay(day.copy(workoutId = null, workoutName = "Dia de Descanso", isRestDay = true, status = "DESCANSO"))
            }
        }
    }

    // Workout Exercises (Customized per workout)
    fun getWorkoutExercises(workoutId: String): Flow<List<WorkoutExerciseEntity>> =
        workoutDao.getWorkoutExercises(workoutId)

    suspend fun getWorkoutExercisesOnce(workoutId: String): List<WorkoutExerciseEntity> = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutExercisesOnce(workoutId)
    }

    suspend fun addExerciseToWorkout(we: WorkoutExerciseEntity): Long = withContext(Dispatchers.IO) {
        val current = workoutDao.getWorkoutExercisesOnce(we.workoutId)
        val nextOrder = current.size
        val entityWithOrder = we.copy(orderIndex = nextOrder)
        val newId = workoutDao.insertWorkoutExercise(entityWithOrder)

        // Sync exerciseIds string in plan
        syncPlanExerciseIds(we.workoutId)
        newId
    }

    suspend fun updateWorkoutExercise(we: WorkoutExerciseEntity) = withContext(Dispatchers.IO) {
        workoutDao.updateWorkoutExercise(we)
        syncPlanExerciseIds(we.workoutId)
    }

    suspend fun removeExerciseFromWorkout(id: Long, workoutId: String) = withContext(Dispatchers.IO) {
        workoutDao.deleteWorkoutExercise(id)
        syncPlanExerciseIds(workoutId)
    }

    suspend fun reorderWorkoutExercises(workoutId: String, reorderedList: List<WorkoutExerciseEntity>) = withContext(Dispatchers.IO) {
        reorderedList.forEachIndexed { index, item ->
            workoutDao.updateWorkoutExercise(item.copy(orderIndex = index))
        }
        syncPlanExerciseIds(workoutId)
    }

    suspend fun syncWorkoutExercisesForPlan(workoutId: String, exercises: List<WorkoutExerciseEntity>) = withContext(Dispatchers.IO) {
        workoutDao.deleteWorkoutExercisesForPlan(workoutId)
        val entities = exercises.mapIndexed { index, ex ->
            ex.copy(id = 0, workoutId = workoutId, orderIndex = index)
        }
        if (entities.isNotEmpty()) {
            workoutDao.insertWorkoutExercises(entities)
        }
        syncPlanExerciseIds(workoutId)
    }

    private suspend fun syncPlanExerciseIds(workoutId: String) {
        val exercises = workoutDao.getWorkoutExercisesOnce(workoutId)
        val idsString = exercises.joinToString(",") { it.exerciseId }
        val muscles = exercises.map { it.exerciseType }.distinct().joinToString(", ")
        val plan = workoutDao.getPlanById(workoutId)
        if (plan != null) {
            workoutDao.updatePlan(
                plan.copy(
                    exerciseIds = idsString,
                    targetMuscles = if (muscles.isNotBlank()) muscles else plan.targetMuscles,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    // Weekly Schedule
    fun getWeeklySchedule(): Flow<List<WorkoutScheduleEntity>> = workoutDao.getSchedule()

    suspend fun getWeeklyScheduleOnce(): List<WorkoutScheduleEntity> = withContext(Dispatchers.IO) {
        workoutDao.getScheduleOnce()
    }

    suspend fun setScheduleDay(dayOfWeek: Int, workoutId: String?, workoutName: String?, isRestDay: Boolean) = withContext(Dispatchers.IO) {
        val entry = WorkoutScheduleEntity(
            dayOfWeek = dayOfWeek,
            workoutId = workoutId,
            workoutName = if (isRestDay) "Dia de Descanso" else (workoutName ?: "Treino Programado"),
            isRestDay = isRestDay,
            status = if (isRestDay) "DESCANSO" else "PENDENTE"
        )
        workoutDao.saveScheduleDay(entry)
    }

    suspend fun updateScheduleStatus(dayOfWeek: Int, status: String) = withContext(Dispatchers.IO) {
        val current = workoutDao.getScheduleForDay(dayOfWeek)
        if (current != null) {
            workoutDao.saveScheduleDay(current.copy(status = status))
        }
    }

    suspend fun rescheduleDayWorkout(fromDay: Int, toDay: Int, action: String) = withContext(Dispatchers.IO) {
        val fromEntry = workoutDao.getScheduleForDay(fromDay) ?: return@withContext
        when (action) {
            "MARK_LOST" -> {
                workoutDao.saveScheduleDay(fromEntry.copy(status = "PERDIDO"))
            }
            "SKIP" -> {
                workoutDao.saveScheduleDay(fromEntry.copy(status = "DESCANSO", isRestDay = true, workoutName = "Treino Pulado"))
            }
            "MOVE" -> {
                val toEntry = workoutDao.getScheduleForDay(toDay)
                workoutDao.saveScheduleDay(
                    fromEntry.copy(status = "DESCANSO", isRestDay = true, workoutId = null, workoutName = "Dia de Descanso")
                )
                if (toEntry != null) {
                    workoutDao.saveScheduleDay(
                        toEntry.copy(
                            workoutId = fromEntry.workoutId,
                            workoutName = fromEntry.workoutName,
                            isRestDay = false,
                            status = "PENDENTE"
                        )
                    )
                }
            }
        }
    }

    // Active Workout
    fun getActiveWorkoutState(): Flow<ActiveWorkoutStateEntity?> = workoutDao.getActiveWorkoutState()

    suspend fun getActiveWorkoutStateOnce(): ActiveWorkoutStateEntity? = withContext(Dispatchers.IO) {
        workoutDao.getActiveWorkoutStateOnce()
    }

    suspend fun saveActiveWorkoutState(state: ActiveWorkoutStateEntity) = withContext(Dispatchers.IO) {
        workoutDao.saveActiveWorkoutState(state)
    }

    suspend fun clearActiveWorkoutState() = withContext(Dispatchers.IO) {
        workoutDao.clearActiveWorkoutState()
    }

    // Previous performance comparison (Rule 16)
    suspend fun getLastPerformanceForExercise(exerciseId: String): String? = withContext(Dispatchers.IO) {
        val recentSets = workoutDao.getRecentSetsForExercise(exerciseId)
        if (recentSets.isEmpty()) return@withContext null

        val lastSessionId = recentSets.first().workoutSessionId
        val sessionSets = recentSets.filter { it.workoutSessionId == lastSessionId }
        val formattedSets = sessionSets.map { set ->
            if (set.weightKg > 0f) "${set.reps}x ${set.weightKg.toInt()}kg"
            else if (set.durationSeconds > 0) "${set.durationSeconds}s"
            else "${set.reps} reps"
        }.joinToString(", ")
        "Último treino: $formattedSets"
    }

    // Completed Workouts
    fun getAllCompletedWorkouts(): Flow<List<CompletedWorkoutEntity>> = workoutDao.getAllCompletedWorkouts()
    fun getTotalCompletedCount(): Flow<Int> = workoutDao.getTotalCompletedCount()

    fun getSetsForSession(sessionId: Long): Flow<List<CompletedSetEntity>> =
        workoutDao.getSetsForSessionFlow(sessionId)

    suspend fun getSetsForSessionOnce(sessionId: Long): List<CompletedSetEntity> = withContext(Dispatchers.IO) {
        workoutDao.getSetsForSession(sessionId)
    }

    suspend fun getCompletedWorkoutById(id: Long): CompletedWorkoutEntity? = withContext(Dispatchers.IO) {
        workoutDao.getCompletedWorkoutById(id)
    }

    suspend fun finishWorkout(
        planId: String,
        workoutName: String,
        startTimeMillis: Long,
        endTimeMillis: Long,
        completedSets: List<CompletedSetEntity>,
        workoutNotes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val durationSeconds = ((endTimeMillis - startTimeMillis) / 1000).toInt().coerceAtLeast(1)
        val totalVolume = completedSets.sumOf { (it.weightKg * it.reps).toDouble() }.toFloat()
        val totalSets = completedSets.size
        val totalReps = completedSets.sumOf { it.reps }

        val workoutEntity = CompletedWorkoutEntity(
            planId = planId,
            workoutName = workoutName,
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            durationSeconds = durationSeconds,
            totalVolumeKg = totalVolume,
            totalSets = totalSets,
            totalReps = totalReps,
            dateString = FormatUtils.todayDateString(),
            notes = workoutNotes
        )
        val workoutId = workoutDao.insertCompletedWorkout(workoutEntity)

        // Save sets with the newly generated session id
        val setsWithId = completedSets.map { it.copy(workoutSessionId = workoutId) }
        workoutDao.insertCompletedSets(setsWithId)

        // Clear active workout state
        workoutDao.clearActiveWorkoutState()

        // Update schedule status for today to "REALIZADO"
        val todayCalendar = java.util.Calendar.getInstance()
        val todayPt = com.example.data.model.DayOfWeekPt.fromCalendar(todayCalendar)
        updateScheduleStatus(todayPt.dayNumber, "REALIZADO")

        // Update user profile streaks & last workout date
        val profile = userDao.getUserProfileOnce()
        if (profile != null) {
            val today = FormatUtils.todayDateString()
            val newStreak = if (profile.lastWorkoutDate == today) {
                profile.currentStreak
            } else {
                profile.currentStreak + 1
            }
            val best = maxOf(newStreak, profile.bestStreak)
            userDao.saveUserProfile(
                profile.copy(
                    currentStreak = newStreak,
                    bestStreak = best,
                    lastWorkoutDate = today
                )
            )
        }

        // Check & unlock workout achievements
        checkWorkoutAchievements(totalSets)

        workoutId
    }

    // PR Management
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>> = workoutDao.getAllPersonalRecords()

    suspend fun checkAndUpdatePR(exerciseId: String, exerciseName: String, weightKg: Float, reps: Int): Boolean = withContext(Dispatchers.IO) {
        val currentPR = workoutDao.getPRForExercise(exerciseId)
        val isNewPR = currentPR == null || weightKg > currentPR.maxWeightKg || (weightKg == currentPR.maxWeightKg && reps > currentPR.repsAtMaxWeight)

        if (isNewPR && weightKg > 0f) {
            val pr = PersonalRecordEntity(
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                maxWeightKg = weightKg,
                repsAtMaxWeight = reps,
                dateString = FormatUtils.todayDateString(),
                timestamp = System.currentTimeMillis()
            )
            workoutDao.savePersonalRecord(pr)

            // Unlock First PR achievement
            unlockAchievement("first_pr")
            return@withContext true
        }
        return@withContext false
    }

    suspend fun getRecentSetsForExercise(exerciseId: String): List<CompletedSetEntity> = withContext(Dispatchers.IO) {
        workoutDao.getRecentSetsForExercise(exerciseId)
    }

    // Weight Records
    fun getAllWeightRecords(): Flow<List<WeightRecordEntity>> = progressDao.getAllWeightRecords()

    suspend fun addWeightRecord(weightKg: Float, note: String = "") = withContext(Dispatchers.IO) {
        val record = WeightRecordEntity(
            weightKg = weightKg,
            dateString = FormatUtils.todayDateString(),
            timeString = FormatUtils.formatTime(System.currentTimeMillis()),
            note = note,
            timestamp = System.currentTimeMillis()
        )
        progressDao.insertWeightRecord(record)
        // Also update current weight in profile
        val profile = userDao.getUserProfileOnce()
        if (profile != null) {
            userDao.saveUserProfile(profile.copy(currentWeightKg = weightKg))
        }
    }

    suspend fun deleteWeightRecord(id: Long) = withContext(Dispatchers.IO) {
        progressDao.deleteWeightRecord(id)
    }

    // Body Measurements
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>> = progressDao.getAllMeasurements()

    suspend fun addMeasurement(measurement: BodyMeasurementEntity) = withContext(Dispatchers.IO) {
        progressDao.insertMeasurement(measurement)
    }

    // Photos
    fun getAllPhotos(): Flow<List<EvolutionPhotoEntity>> = progressDao.getAllPhotos()

    suspend fun addPhoto(angle: String, imageUri: String, note: String = "") = withContext(Dispatchers.IO) {
        val photo = EvolutionPhotoEntity(
            dateString = FormatUtils.todayDateString(),
            timestamp = System.currentTimeMillis(),
            angle = angle,
            imageUri = imageUri,
            note = note
        )
        progressDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(id: Long) = withContext(Dispatchers.IO) {
        progressDao.deletePhoto(id)
    }

    // Hydration
    fun getTodayHydration(): Flow<HydrationRecordEntity?> {
        return userDao.getHydrationForDate(FormatUtils.todayDateString())
    }

    fun getRecentHydration(): Flow<List<HydrationRecordEntity>> = userDao.getRecentHydration()

    suspend fun addWater(mlToAdd: Int) = withContext(Dispatchers.IO) {
        val today = FormatUtils.todayDateString()
        val current = userDao.getHydrationForDateOnce(today)
        val profile = userDao.getUserProfileOnce()
        val target = profile?.dailyWaterGoalMl ?: 3000

        val newConsumed = (current?.consumedMl ?: 0) + mlToAdd
        val updated = HydrationRecordEntity(
            dateString = today,
            consumedMl = newConsumed,
            targetMl = target
        )
        userDao.saveHydration(updated)

        if (newConsumed >= target) {
            unlockAchievement("water_goal")
        }
    }

    // Cardio
    fun getAllCardio(): Flow<List<CardioRecordEntity>> = progressDao.getAllCardio()

    suspend fun addCardio(activityType: String, durationMinutes: Int, distanceKm: Float, calories: Int, notes: String = "") = withContext(Dispatchers.IO) {
        val cardio = CardioRecordEntity(
            activityType = activityType,
            durationMinutes = durationMinutes,
            distanceKm = distanceKm,
            estimatedCalories = calories,
            dateString = FormatUtils.todayDateString(),
            timestamp = System.currentTimeMillis(),
            notes = notes
        )
        progressDao.insertCardio(cardio)
        unlockAchievement("first_cardio")
    }

    // Goals
    fun getAllGoals(): Flow<List<GoalEntity>> = userDao.getAllGoals()

    suspend fun addGoal(title: String, type: String, targetValue: Float, currentValue: Float, unit: String, deadline: String) = withContext(Dispatchers.IO) {
        val goal = GoalEntity(
            title = title,
            type = type,
            targetValue = targetValue,
            currentValue = currentValue,
            unit = unit,
            deadline = deadline,
            isCompleted = currentValue >= targetValue
        )
        userDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) = withContext(Dispatchers.IO) {
        userDao.updateGoal(goal)
    }

    suspend fun deleteGoal(id: Long) = withContext(Dispatchers.IO) {
        userDao.deleteGoal(id)
    }

    // Achievements
    fun getAllAchievements(): Flow<List<AchievementEntity>> = userDao.getAllAchievements()
    fun getUnlockedAchievementsCount(): Flow<Int> = userDao.getUnlockedCount()

    private suspend fun unlockAchievement(id: String) {
        val today = FormatUtils.todayDateString()
        userDao.updateAchievement(
            AchievementEntity(
                id = id,
                title = SeedData.initialAchievements.find { it.id == id }?.title ?: "",
                description = SeedData.initialAchievements.find { it.id == id }?.description ?: "",
                category = SeedData.initialAchievements.find { it.id == id }?.category ?: "Geral",
                isUnlocked = true,
                unlockedDate = today
            )
        )
    }

    private suspend fun checkWorkoutAchievements(completedSetsCount: Int) {
        val totalWorkouts = workoutDao.getTotalCompletedCount().firstOrNull() ?: 1
        unlockAchievement("first_workout")
        if (totalWorkouts >= 5) unlockAchievement("workouts_5")
        if (totalWorkouts >= 10) unlockAchievement("workouts_10")
        if (totalWorkouts >= 25) unlockAchievement("workouts_25")
        if (totalWorkouts >= 50) unlockAchievement("workouts_50")
        if (totalWorkouts >= 100) unlockAchievement("workouts_100")
    }

    // DEMO DATA GENERATOR
    suspend fun populateDemoData() = withContext(Dispatchers.IO) {
        val demoProfile = UserProfileEntity(
            id = 1,
            name = "Alexandre Silva",
            email = "alexandre.forja@exemplo.com",
            age = 28,
            gender = "Masculino",
            heightCm = 180f,
            initialWeightKg = 86.5f,
            currentWeightKg = 82.0f,
            targetWeightKg = 78.0f,
            mainGoal = "Hipertrofia",
            trainingLevel = "Intermediário (1-3 anos)",
            availableDays = 5,
            currentStreak = 4,
            bestStreak = 12,
            lastWorkoutDate = FormatUtils.todayDateString(),
            isRegistered = true,
            isOnboarded = true,
            isDemoUser = true,
            dailyWaterGoalMl = 3200
        )
        userDao.saveUserProfile(demoProfile)

        // Seed demo weight history
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        progressDao.insertWeightRecord(WeightRecordEntity(weightKg = 86.5f, dateString = "2026-09-01", timeString = "08:00", note = "Início do protocolo", timestamp = now - 27 * oneDay))
        progressDao.insertWeightRecord(WeightRecordEntity(weightKg = 85.2f, dateString = "2026-09-08", timeString = "08:15", note = "Semana 1 concluída", timestamp = now - 20 * oneDay))
        progressDao.insertWeightRecord(WeightRecordEntity(weightKg = 84.1f, dateString = "2026-09-15", timeString = "07:50", note = "Ajuste na dieta", timestamp = now - 13 * oneDay))
        progressDao.insertWeightRecord(WeightRecordEntity(weightKg = 83.0f, dateString = "2026-09-22", timeString = "08:00", note = "Consistência nos treinos", timestamp = now - 6 * oneDay))
        progressDao.insertWeightRecord(WeightRecordEntity(weightKg = 82.0f, dateString = FormatUtils.todayDateString(), timeString = "08:00", note = "Evolução sólida (-4.5kg)", timestamp = now))

        // Seed demo measurements
        progressDao.insertMeasurement(
            BodyMeasurementEntity(
                dateString = "2026-09-01",
                timestamp = now - 27 * oneDay,
                rightArm = 38.5f,
                leftArm = 38.0f,
                chest = 104f,
                waist = 89f,
                abdomen = 92f,
                hips = 102f,
                rightThigh = 60f,
                leftThigh = 59.5f,
                calves = 38f
            )
        )
        progressDao.insertMeasurement(
            BodyMeasurementEntity(
                dateString = FormatUtils.todayDateString(),
                timestamp = now,
                rightArm = 39.5f,
                leftArm = 39.2f,
                chest = 107f,
                waist = 84f,
                abdomen = 86f,
                hips = 101f,
                rightThigh = 62f,
                leftThigh = 61.8f,
                calves = 39f
            )
        )

        // Seed demo hydration
        userDao.saveHydration(HydrationRecordEntity(dateString = FormatUtils.todayDateString(), consumedMl = 2250, targetMl = 3200))

        // Seed demo PRs
        workoutDao.savePersonalRecord(PersonalRecordEntity("supino_reto_barra", "Supino Reto com Barra", 110f, 6, "2026-09-24", now - 4 * oneDay))
        workoutDao.savePersonalRecord(PersonalRecordEntity("agachamento_livre_barra", "Agachamento Livre com Barra", 140f, 5, "2026-09-22", now - 6 * oneDay))
        workoutDao.savePersonalRecord(PersonalRecordEntity("levantamento_terra_romeno", "Levantamento Terra Romeno", 130f, 8, "2026-09-20", now - 8 * oneDay))
        workoutDao.savePersonalRecord(PersonalRecordEntity("desenvolvimento_halteres", "Desenvolvimento com Halteres", 32f, 8, "2026-09-18", now - 10 * oneDay))

        // Seed demo completed workouts
        workoutDao.insertCompletedWorkout(
            CompletedWorkoutEntity(
                planId = "treino_a",
                workoutName = "Treino A - Peito, Tríceps e Abdômen",
                startTimeMillis = now - 2 * oneDay - 3600000L,
                endTimeMillis = now - 2 * oneDay,
                durationSeconds = 3600,
                totalVolumeKg = 8450f,
                totalSets = 18,
                totalReps = 175,
                dateString = FormatUtils.formatDate(now - 2 * oneDay),
                notes = "Treino forte, boa conexão mente-músculo."
            )
        )
        workoutDao.insertCompletedWorkout(
            CompletedWorkoutEntity(
                planId = "treino_b",
                workoutName = "Treino B - Costas, Bíceps e Trapézio",
                startTimeMillis = now - oneDay - 3900000L,
                endTimeMillis = now - oneDay,
                durationSeconds = 3900,
                totalVolumeKg = 9120f,
                totalSets = 20,
                totalReps = 190,
                dateString = FormatUtils.formatDate(now - oneDay),
                notes = "Subiu carga na remada curvada."
            )
        )

        // Seed demo goals
        userDao.insertGoal(GoalEntity(title = "Alcançar 80kg", type = "PESO", targetValue = 80f, currentValue = 82f, unit = "kg", deadline = "31/10/2026", isCompleted = false))
        userDao.insertGoal(GoalEntity(title = "Supino 120kg", type = "CARGA", targetValue = 120f, currentValue = 110f, unit = "kg", deadline = "15/11/2026", isCompleted = false))
        userDao.insertGoal(GoalEntity(title = "Treinar 5x por semana", type = "FREQUENCIA", targetValue = 5f, currentValue = 4f, unit = "dias/sem", deadline = "Semanal", isCompleted = false))
        userDao.insertGoal(GoalEntity(title = "Beber 3.2L de água", type = "HIDRATACAO", targetValue = 3200f, currentValue = 2250f, unit = "ml", deadline = "Diário", isCompleted = false))

        // Seed demo cardio
        progressDao.insertCardio(CardioRecordEntity(activityType = "Corrida", durationMinutes = 30, distanceKm = 4.5f, estimatedCalories = 320, dateString = FormatUtils.todayDateString(), timestamp = now - 1800000L, notes = "Cardio pós-treino moderado"))

        // Unlock demo achievements
        unlockAchievement("first_workout")
        unlockAchievement("first_pr")
        unlockAchievement("streak_7")
        unlockAchievement("first_cardio")
    }
}
