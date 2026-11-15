package com.labrescue.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.labrescue.app.data.model.UserAccount

@Dao
interface UserAccountDao {

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): UserAccount?

    @Query("SELECT * FROM user_accounts")
    suspend fun getAllAccounts(): List<UserAccount>

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getAccountCount(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAccount(account: UserAccount)

    @Update
    suspend fun updateAccount(account: UserAccount)

    @Query("DELETE FROM user_accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)
}

