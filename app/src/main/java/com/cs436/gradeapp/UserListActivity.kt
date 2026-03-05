package com.cs436.gradeapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs436.gradeapp.storage.UserStorage
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val recyclerView = findViewById<RecyclerView>(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with an empty list
        val adapter = UserAdapter()
        recyclerView.adapter = adapter

        // Observe LiveData and update adapter
        UserStorage.getUsers(this).observe(this) { users ->
            adapter.updateUsers(users)
        }

        // Set up the custom back button
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Close the activity and return to the previous screen
        }
    }
}
