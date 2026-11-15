package com.labrescue.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labrescue.app.data.model.Mistake
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {

    @Query("""
        SELECT mistakes.* FROM mistakes 
        INNER JOIN attempts ON mistakes.attempt_id = attempts.id 
        WHERE attempts.user_id = :userId 
        ORDER BY mistakes.timestamp DESC
    """)
    fun observeMistakesForUser(userId: String): Flow<List<Mistake>>

    @Query("""
        SELECT mistakes.* FROM mistakes 
        INNER JOIN attempts ON mistakes.attempt_id = attempts.id 
        WHERE attempts.user_id = :userId 
        ORDER BY mistakes.timestamp DESC
    """)
    suspend fun getMistakesForUser(userId: String): List<Mistake>

    @Query("SELECT * FROM mistakes ORDER BY timestamp DESC")
    fun observeAllMistakes(): Flow<List<Mistake>>

    @Query("SELECT * FROM mistakes ORDER BY timestamp DESC")
    suspend fun getAllMistakes(): List<Mistake>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: Mistake)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistakes(mistakes: List<Mistake>)

    @Update
    suspend fun updateMistake(mistake: Mistake)

    @Delete
    suspend fun deleteMistake(mistake: Mistake)

    @Query("""
        DELETE FROM mistakes WHERE attempt_id IN (
            SELECT id FROM attempts WHERE user_id = :userId
        )
    """)
    suspend fun deleteAllMistakesForUser(userId: String)

    @Query("DELETE FROM mistakes")
    suspend fun deleteAllMistakes()
}

