package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import com.example.data.local.entities.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    // Hydration
    @Query("SELECT * FROM hydration_records WHERE dateString = :dateString")
    fun getHydrationForDate(dateString: String): Flow<HydrationRecordEntity?>

    @Query("SELECT * FROM hydration_records WHERE dateString = :dateString")
    suspend fun getHydrationForDateOnce(dateString: String): HydrationRecordEntity?

    @Query("SELECT * FROM hydration_records ORDER BY dateString DESC LIMIT 30")
    fun getRecentHydration(): Flow<List<HydrationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHydration(record: HydrationRecordEntity)

    // Goals
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, id DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: Long)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(list: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE id IN (:ids)")
    suspend fun getExercisesByIds(ids: List<String>): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name ASC")
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteExercises(): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercises SET isFavorite = :isFav WHERE id = :id")
    suspend fun toggleFavorite(id: String, isFav: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans ORDER BY code ASC")
    fun getAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getPlanById(id: String): WorkoutPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WorkoutPlanEntity>)

    @Update
    suspend fun updatePlan(plan: WorkoutPlanEntity)

    // Active state for recovery
    @Query("SELECT * FROM active_workout_state WHERE id = 1")
    fun getActiveWorkoutState(): Flow<ActiveWorkoutStateEntity?>

    @Query("SELECT * FROM active_workout_state WHERE id = 1")
    suspend fun getActiveWorkoutStateOnce(): ActiveWorkoutStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveWorkoutState(state: ActiveWorkoutStateEntity)

    @Query("DELETE FROM active_workout_state WHERE id = 1")
    suspend fun clearActiveWorkoutState()

    // Completed sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedWorkout(workout: CompletedWorkoutEntity): Long

    @Query("SELECT * FROM completed_workouts ORDER BY startTimeMillis DESC")
    fun getAllCompletedWorkouts(): Flow<List<CompletedWorkoutEntity>>

    @Query("SELECT * FROM completed_workouts WHERE dateString = :dateString")
    fun getCompletedWorkoutsForDate(dateString: String): Flow<List<CompletedWorkoutEntity>>

    @Query("SELECT COUNT(*) FROM completed_workouts")
    fun getTotalCompletedCount(): Flow<Int>

    // Completed sets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedSets(sets: List<CompletedSetEntity>)

    @Query("SELECT * FROM completed_sets WHERE workoutSessionId = :sessionId ORDER BY id ASC")
    suspend fun getSetsForSession(sessionId: Long): List<CompletedSetEntity>

    @Query("SELECT * FROM completed_sets WHERE exerciseId = :exerciseId ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentSetsForExercise(exerciseId: String): List<CompletedSetEntity>

    // PRs
    @Query("SELECT * FROM personal_records ORDER BY timestamp DESC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId")
    suspend fun getPRForExercise(exerciseId: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePersonalRecord(pr: PersonalRecordEntity)
}

@Dao
interface ProgressDao {
    // Weights
    @Query("SELECT * FROM weight_records ORDER BY timestamp ASC")
    fun getAllWeightRecords(): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeight(): WeightRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(record: WeightRecordEntity)

    @Query("DELETE FROM weight_records WHERE id = :id")
    suspend fun deleteWeightRecord(id: Long)

    // Body Measurements
    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurementEntity)

    // Photos
    @Query("SELECT * FROM evolution_photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<EvolutionPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: EvolutionPhotoEntity)

    @Query("DELETE FROM evolution_photos WHERE id = :id")
    suspend fun deletePhoto(id: Long)

    // Cardio
    @Query("SELECT * FROM cardio_records ORDER BY timestamp DESC")
    fun getAllCardio(): Flow<List<CardioRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardio(cardio: CardioRecordEntity)

    @Query("DELETE FROM cardio_records WHERE id = :id")
    suspend fun deleteCardio(id: Long)
}
