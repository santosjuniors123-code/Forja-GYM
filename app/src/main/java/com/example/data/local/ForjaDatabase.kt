package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ExerciseDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WorkoutDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        ActiveWorkoutStateEntity::class,
        CompletedWorkoutEntity::class,
        CompletedSetEntity::class,
        PersonalRecordEntity::class,
        WeightRecordEntity::class,
        BodyMeasurementEntity::class,
        EvolutionPhotoEntity::class,
        HydrationRecordEntity::class,
        GoalEntity::class,
        AchievementEntity::class,
        CardioRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ForjaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: ForjaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): ForjaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ForjaDatabase::class.java,
                    "forjagym_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        private suspend fun populateDatabase(db: ForjaDatabase) {
            db.exerciseDao().insertAll(SeedData.exercises)
            db.workoutDao().insertPlans(SeedData.initialPlans)
            db.userDao().insertAchievements(SeedData.initialAchievements)
        }
    }
}
