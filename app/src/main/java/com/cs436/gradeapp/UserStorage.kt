package com.cs436.gradeapp.storage

import android.content.Context
import androidx.lifecycle.LiveData
import com.cs436.gradeapp.User

object UserStorage {

    private lateinit var userDao: UserDao

    // Initialize the DAO if it is not already initialized
    private fun initialize(context: Context) {
        if (!::userDao.isInitialized) {
            val db = AppDatabase.getDatabase(context)
            userDao = db.userDao()
        }
    }

    // Function to get the list of users (No need for suspend or withContext)
    fun getUsers(context: Context): LiveData<List<User>> {
        initialize(context)
        return userDao.getUsers()  // Directly return LiveData from DAO
    }

    // Function to add a user
    suspend fun addUser(user: User, context: Context) {
        initialize(context)
        userDao.insertUser(user)
    }

    // Function to remove a user
    suspend fun removeUser(user: User, context: Context) {
        initialize(context)
        userDao.deleteUser(user)
    }

    // Function to update a user's data
    suspend fun updateUser(user: User, context: Context) {
        initialize(context)
        userDao.updateUser(user)
    }

    // function to clear all the users
    suspend fun clearAllUsers(context: Context) {
        initialize(context)
        userDao.clearUsers()
    }
}

