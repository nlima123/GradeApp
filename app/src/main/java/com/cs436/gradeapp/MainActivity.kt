package com.cs436.gradeapp

// --- Imports ---
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.text.toFloat

class MainActivity : AppCompatActivity() {

    private lateinit var classContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.the_toolbar)
        toolbar.title = getString(R.string.app_name)

        val viewUsersButton = findViewById<Button>(R.id.viewUsersButton)

        // Input fields
        val semesterGPA = findViewById<EditText>(R.id.editSemesterGPA)
        val semesterTargetGPA = findViewById<EditText>(R.id.editTargetSemesterGPA)
        val cumulativeGPA = findViewById<EditText>(R.id.editCumulativeGPA)
        val targetCumulativeGPA = findViewById<EditText>(R.id.editTargetCumulativeGPA)
        val priorGPA = findViewById<EditText>(R.id.editPreviousGPA)
        val priorCredits = findViewById<EditText>(R.id.editPreviousCredits)
        val calculateButton = findViewById<Button>(R.id.c_button)
        val resetButton = findViewById<Button>(R.id.resetButton)

        classContainer = findViewById(R.id.classes)

        // Add the first class row initially
        addClassRow()

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve saved GPA and credits
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedGPA = sharedPreferences.getFloat("cumulativeGPA", 0.0f)
        val savedCredits = sharedPreferences.getInt("totalCredits", 0)

        // Set the retrieved values into the corresponding EditTexts
        cumulativeGPA.setText(savedGPA.toString())
        priorCredits.setText(savedCredits.toString())

        calculateButton.setOnClickListener {
            calculateAndDisplayResults(
                semesterGPA,
                semesterTargetGPA,
                cumulativeGPA,
                targetCumulativeGPA,
                priorGPA,
                priorCredits
            )
        }

        resetButton.setOnClickListener {
            resetApp()
        }

        viewUsersButton.setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        backApp()
    }

    private fun addClassRow() {
        val inflater = LayoutInflater.from(this)
        val classView = inflater.inflate(R.layout.classes_taking_layout, classContainer, false)

        val switch = classView.findViewById<SwitchCompat>(R.id.class_y_n)

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Add a new class row only once per switch
                switch.isEnabled = false
                addClassRow()
            }
        }

        classContainer.addView(classView)
    }

    private fun backApp() {
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // Method to save user data
    fun saveUserData(userId: String, cumulativeGPA: Double, totalCredits: Int) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("$userId-cumulativeGPA", cumulativeGPA.toFloat())
        editor.putInt("$userId-totalCredits", totalCredits)
        editor.apply()
    }

    // Method to load user data
    fun loadUserData(userId: String): Pair<Double, Int> {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val cumulativeGPA = sharedPreferences.getFloat("$userId-cumulativeGPA", 0.0f).toDouble()
        val totalCredits = sharedPreferences.getInt("$userId-totalCredits", 0)
        return Pair(cumulativeGPA, totalCredits)
    }

    private fun calculateAndDisplayResults(
        semesterGPA: EditText,
        semesterTargetGPA: EditText,
        cumulativeGPA: EditText,
        targetCumulativeGPA: EditText,
        priorGPA: EditText,
        priorCredits: EditText
    ) {
        try {
            val sGPA = semesterGPA.text.toString().toDoubleOrNull() ?: 0.0
            val sTargetGPA = semesterTargetGPA.text.toString().toDoubleOrNull() ?: 0.0
            val cGPA = cumulativeGPA.text.toString().toDoubleOrNull() ?: 0.0
            val cTargetGPA = targetCumulativeGPA.text.toString().toDoubleOrNull() ?: 0.0
            val pGPA = priorGPA.text.toString().toDoubleOrNull() ?: 0.0
            val pCredits = priorCredits.text.toString().toDoubleOrNull() ?: 0.0

            var sCredits = 0.0
            var totalGPA = 0.0 // To accumulate the GPA of all classes

            for (i in 0 until classContainer.childCount) {
                val classView = classContainer.getChildAt(i)

                val retakeBox = classView.findViewById<CheckBox>(R.id.retake_box)
                val radioGroup = classView.findViewById<RadioGroup>(R.id.radioGroup)
                val gradeInput = classView.findViewById<EditText>(R.id.class_grade_input)

                if (retakeBox == null || radioGroup == null || gradeInput == null) {
                    Toast.makeText(this, "One or more class inputs are missing or invalid.", Toast.LENGTH_SHORT).show()
                    continue
                }

                val isRetake = retakeBox.isChecked

                val selectedId = radioGroup.checkedRadioButtonId
                val credit = if (!isRetake && selectedId != -1) {
                    val radioButton = classView.findViewById<RadioButton>(selectedId)
                    radioButton.text.toString().toDoubleOrNull() ?: 0.0
                } else 0.0

                val numericGrade = gradeInput?.text?.toString()?.toDoubleOrNull()
                val gpaGrade = numericGrade?.let { convertGradeToGPA(it) } ?: 0.0

                sCredits += credit

                // Accumulate GPA * Credits for the class
                totalGPA += gpaGrade * credit
            }

            // Calculate new SGPA (Weighted GPA based on credits)
            val newSGPA = if (sCredits > 0) {
                val calculatedSGPA = totalGPA / sCredits
                // Round to the nearest tenth
                String.format("%.1f", calculatedSGPA).toDouble()
            } else 0.0

            // Calculate the new cumulative GPA
            val tCredits = pCredits + sCredits
            val newCGPA = ((pGPA * pCredits + newSGPA * sCredits) / tCredits).let {
                String.format("%.1f", it).toDouble()
            }

            val newPGPA = sGPA

            val diffST = sTargetGPA - sGPA
            val diffCT = cTargetGPA - cGPA

            semesterGPA.setText(newSGPA.toString())
            semesterTargetGPA.setText("4.0")
            cumulativeGPA.setText(newCGPA.toString())
            targetCumulativeGPA.setText("4.0")
            priorGPA.setText(newPGPA.toString())
            priorCredits.setText(pCredits.toString())

            val message = """
            Semester GPA: %.2f
            To Reach Semester Target: %.2f
            The Semester Credits: %.2f
            To Reach Cumulative Target: %.2f
            The Total Credits: %.2f
        """.trimIndent().format(newSGPA, diffST, sCredits, diffCT, tCredits)

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

            val total1 = tCredits.toInt()
            // Save user data after calculations
            saveUserData("userId", newCGPA, total1) // Replace "userId" with actual user ID

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers in all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertGradeToGPA(score: Double): Double {
        return when {
            score >= 94 -> 3.4
            score >= 90 -> 3.7
            score >= 87 -> 3.3
            score >= 84 -> 3.0
            score >= 80 -> 2.7
            score >= 77 -> 2.3
            score >= 74 -> 2.0
            score >= 70 -> 1.7
            score >= 67 -> 1.3
            score >= 64 -> 1.0
            score >= 60 -> 0.7
            else -> 0.0
        }
    }

    private fun resetApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

}
