package com.labrescue.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.labrescue.app.data.model.Attempt
import com.labrescue.app.data.model.Mistake
import com.labrescue.app.data.model.UserAccount
import com.labrescue.app.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        UserAccount::class,
        Attempt::class,
        Mistake::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun attemptDao(): AttemptDao
    abstract fun mistakeDao(): MistakeDao

    companion object {
        private const val DATABASE_NAME = "labrescue.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

