package com.cs436.gradeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.cs436.gradeapp.LoginActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isUserLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_launcher)
        }
    }


    fun onStartButtonClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val isUserLoggedIn: Boolean
        get() {
            val prefs = getSharedPreferences("userProfiles", Context.MODE_PRIVATE)
            return prefs.getBoolean("isLoggedIn", false)
        }
}
