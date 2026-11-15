package com.labrescue.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labrescue.app.data.model.Attempt
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {

    @Query("SELECT * FROM attempts WHERE user_id = :userId ORDER BY completed_at DESC")
    fun observeAttemptsForUser(userId: String): Flow<List<Attempt>>

    @Query("SELECT * FROM attempts WHERE user_id = :userId ORDER BY completed_at DESC")
    suspend fun getAttemptsForUser(userId: String): List<Attempt>

    @Query("SELECT * FROM attempts ORDER BY completed_at DESC")
    fun observeAllAttempts(): Flow<List<Attempt>>

    @Query("SELECT * FROM attempts ORDER BY completed_at DESC")
    suspend fun getAllAttempts(): List<Attempt>

    @Query("SELECT * FROM attempts WHERE id = :attemptId LIMIT 1")
    suspend fun getAttemptById(attemptId: String): Attempt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: Attempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempts(attempts: List<Attempt>)

    @Update
    suspend fun updateAttempt(attempt: Attempt)

    @Delete
    suspend fun deleteAttempt(attempt: Attempt)

    @Query("DELETE FROM attempts WHERE id = :attemptId")
    suspend fun deleteAttemptById(attemptId: String)

    @Query("DELETE FROM attempts WHERE user_id = :userId")
    suspend fun deleteAllAttemptsForUser(userId: String)

    @Query("DELETE FROM attempts")
    suspend fun deleteAllAttempts()
}

