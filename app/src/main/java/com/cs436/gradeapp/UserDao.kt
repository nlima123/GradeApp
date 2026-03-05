package com.cs436.gradeapp.storage

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cs436.gradeapp.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getUsers(): LiveData<List<User>>  // ✅ No 'suspend' with LiveData

    @Insert
    suspend fun insertUser(user: User)

    @Insert
    suspend fun insertUsers(users: List<User>)  // ✅ Use concrete List, not LiveData

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}
