package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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
import com.example.data.local.entities.WorkoutExerciseEntity
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.data.local.entities.WorkoutScheduleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutScheduleEntity::class,
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
    version = 2,
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workout_exercises` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `workoutId` TEXT NOT NULL,
                        `exerciseId` TEXT NOT NULL,
                        `orderIndex` INTEGER NOT NULL DEFAULT 0,
                        `exerciseName` TEXT NOT NULL,
                        `exerciseType` TEXT NOT NULL DEFAULT 'Musculação',
                        `sets` INTEGER NOT NULL DEFAULT 3,
                        `reps` INTEGER NOT NULL DEFAULT 10,
                        `weightKg` REAL NOT NULL DEFAULT 20.0,
                        `restSeconds` INTEGER NOT NULL DEFAULT 60,
                        `durationSeconds` INTEGER NOT NULL DEFAULT 0,
                        `distanceKm` REAL NOT NULL DEFAULT 0.0,
                        `speedKmh` REAL NOT NULL DEFAULT 0.0,
                        `incline` REAL NOT NULL DEFAULT 0.0,
                        `calories` INTEGER NOT NULL DEFAULT 0,
                        `notes` TEXT NOT NULL DEFAULT '',
                        `customSetsJson` TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workout_schedules` (
                        `dayOfWeek` INTEGER PRIMARY KEY NOT NULL,
                        `workoutId` TEXT,
                        `workoutName` TEXT,
                        `isRestDay` INTEGER NOT NULL DEFAULT 0,
                        `status` TEXT NOT NULL DEFAULT 'PENDENTE'
                    )
                """.trimIndent())

                try {
                    db.execSQL("ALTER TABLE `workout_plans` ADD COLUMN `createdAt` INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE `workout_plans` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}

                try {
                    db.execSQL("ALTER TABLE `exercises` ADD COLUMN `exerciseType` TEXT NOT NULL DEFAULT 'Musculação'")
                    db.execSQL("ALTER TABLE `exercises` ADD COLUMN `isCustom` INTEGER NOT NULL DEFAULT 0")
                } catch (_: Exception) {}

                try {
                    db.execSQL("ALTER TABLE `completed_workouts` ADD COLUMN `notes` TEXT NOT NULL DEFAULT ''")
                } catch (_: Exception) {}

                try {
                    db.execSQL("ALTER TABLE `completed_sets` ADD COLUMN `durationSeconds` INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE `completed_sets` ADD COLUMN `distanceKm` REAL NOT NULL DEFAULT 0.0")
                    db.execSQL("ALTER TABLE `completed_sets` ADD COLUMN `notes` TEXT NOT NULL DEFAULT ''")
                    db.execSQL("ALTER TABLE `completed_sets` ADD COLUMN `exerciseType` TEXT NOT NULL DEFAULT 'Musculação'")
                } catch (_: Exception) {}
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): ForjaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ForjaDatabase::class.java,
                    "forjagym_database"
                )
                    .addMigrations(MIGRATION_1_2)
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
