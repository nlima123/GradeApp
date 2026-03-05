package com.cs436.gradeapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Room auto-generates the ID
    val name: String,
    val isLoggedIn: Boolean = false,
    val gpa: Double = 0.0,
    val credits: Int = 0
)
