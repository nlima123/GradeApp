package com.cs436.gradeapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.cs436.gradeapp.storage.UserStorage

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val guestButton = findViewById<Button>(R.id.guestButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val viewUsersButton = findViewById<Button>(R.id.viewUsersButton)

        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        guestButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedPassword = sharedPrefs.getString(username, null)
            if (savedPassword != null && savedPassword == password) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val editor = sharedPrefs.edit()
            editor.putString(username, password)
            editor.apply()

            // Add user to Room database
            lifecycleScope.launch {
                UserStorage.addUser(
                    User(name = username),  // You can customize gpa/credits if needed
                    this@LoginActivity
                )
            }

            Toast.makeText(this, "User registered. You can now log in.", Toast.LENGTH_SHORT).show()
        }

        viewUsersButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        val clearDbButton = findViewById<Button>(R.id.clearDatabaseButton)
        clearDbButton.setOnClickListener {
            lifecycleScope.launch {
                UserStorage.clearAllUsers(this@LoginActivity)
                Toast.makeText(this@LoginActivity, "All users deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val helpButton = findViewById<Button>(R.id.helpButton)
        val message = "Profiles for unsaved users will not be stored"
        helpButton.setOnClickListener {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
