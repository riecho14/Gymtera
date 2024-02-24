package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.ExerciseData
import com.tugasakhir.gymtera.data.SessionAdapter
import com.tugasakhir.gymtera.data.TrainingData
import com.tugasakhir.gymtera.databinding.ActivityAllBodyBinding
import java.util.Locale

@Suppress("DEPRECATION", "NAME_SHADOWING")
class AllBodyActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DIFFICULTY = "extra_difficulty"
        const val EXTRA_SELECTED_TRAINING = "extra_selected_training"
        private const val REQUEST_DETAIL_SESSION = 1
    }

    private lateinit var binding: ActivityAllBodyBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBodyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        val difficulty = intent.getStringExtra(EXTRA_DIFFICULTY)
        val selectedTraining = intent.getParcelableExtra<TrainingData>(EXTRA_SELECTED_TRAINING)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, TrainingActivity::class.java)
            startActivity(intent)
            finish()
        }

        // RecyclerView options
        val recyclerView = binding.rvSession
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference

        // Check if selectedTraining is not null and difficulty is not null
        if (selectedTraining != null && difficulty != null) {
            val query = databaseReference.child("Equipments").orderByKey()

            // Add a ValueEventListener to retrieve the data
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exerciseDataList = mutableListOf<ExerciseData>()

                    for (equipmentSnapshot in snapshot.children) {
                        if (equipmentSnapshot.hasChild("Exercise")) {
                            val exerciseSnapshot = equipmentSnapshot.child("Exercise")

                            for (exerciseSnapshot in exerciseSnapshot.children) {
                                val exerciseData =
                                    exerciseSnapshot.getValue(ExerciseData::class.java)
                                if (exerciseData != null) {
                                    exerciseData.exerciseId = exerciseSnapshot.key
                                    exerciseData.equipmentId = equipmentSnapshot.key

                                    if (exerciseData.exerMuscle != null && exerciseData.exerMuscle!!.isNotEmpty()) {
                                        exerciseDataList.add(exerciseData)
                                    }
                                }
                            }
                        }
                    }

                    // Shuffle the exercise data list to make it random
                    exerciseDataList.shuffle()

                    // Filter exercises based on difficulty level
                    val selectedExercises = when {
                        difficulty.equals("beginner", ignoreCase = true) -> {
                            exerciseDataList.filter {
                                it.exerCategory?.toLowerCase(Locale.ROOT)
                                    ?.contains("beginner") == true
                            }
                        }

                        difficulty.equals("intermediate", ignoreCase = true) -> {
                            exerciseDataList.filter {
                                it.exerCategory?.toLowerCase(Locale.ROOT)
                                    ?.contains("beginner") == true || it.exerCategory?.toLowerCase(
                                    Locale.ROOT
                                )?.contains("intermediate") == true
                            }
                        }

                        difficulty.equals("advanced", ignoreCase = true) -> {
                            // Prioritize advanced exercises first, then intermediate, and finally beginner
                            exerciseDataList.filter {
                                it.exerCategory?.toLowerCase(Locale.ROOT)
                                    ?.contains("advanced") == true
                            } + exerciseDataList.filter {
                                it.exerCategory?.toLowerCase(Locale.ROOT)
                                    ?.contains("intermediate") == true
                            } + exerciseDataList.filter {
                                it.exerCategory?.toLowerCase(Locale.ROOT)
                                    ?.contains("beginner") == true
                            }
                        }

                        else -> emptyList()
                    }

                    // Take one exercise from each muscle group
                    val groupedExercises = selectedExercises.groupBy { it.exerMuscle }
                    val finalExercises = groupedExercises.values.mapNotNull { it.firstOrNull() }

                    // Set up RecyclerView with the filtered data
                    val sessionAdapter = SessionAdapter(difficulty)

                    // Sort finalExercises based on difficulty
                    val sortedExercises = when (difficulty.toLowerCase(Locale.ROOT)) {
                        "beginner" -> finalExercises.sortedBy { it.exerCategory?.toLowerCase(Locale.ROOT) == "beginner" }
                        "intermediate" -> finalExercises.sortedByDescending {
                            it.exerCategory?.toLowerCase(Locale.ROOT) == "advanced" || it.exerCategory?.toLowerCase(
                                Locale.ROOT
                            ) == "intermediate"
                        }

                        "advanced" -> finalExercises.sortedByDescending {
                            it.exerCategory?.toLowerCase(Locale.ROOT) == "advanced"
                        }

                        else -> finalExercises
                    }

                    recyclerView.adapter = sessionAdapter
                    sessionAdapter.submitList(sortedExercises)

                    sessionAdapter.onItemCheckedListener =
                        object : SessionAdapter.OnItemCheckedListener {
                            override fun onItemChecked(allItemsChecked: Boolean) {
                                if (allItemsChecked && binding.selesai.visibility != View.VISIBLE) {
                                    val slideUpAnimation = AnimationUtils.loadAnimation(
                                        this@AllBodyActivity, R.anim.slide_up
                                    )
                                    binding.selesai.startAnimation(slideUpAnimation)
                                }
                                binding.selesai.visibility =
                                    if (allItemsChecked) View.VISIBLE else View.GONE
                            }
                        }

                    sessionAdapter.setOnItemClickListener { _, _, position ->
                        val clickedItem = sessionAdapter.getItem(position)
                        clickedItem?.let {
                            startDetailActivityForResult(
                                exerciseId = clickedItem.exerciseId,
                                equipmentId = clickedItem.equipmentId
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })

        }

        binding.selesai.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startDetailActivityForResult(exerciseId: String?, equipmentId: String?) {
        val intent = Intent(this@AllBodyActivity, DetailSessionActivity::class.java)
        intent.putExtra("exerciseId", exerciseId)
        intent.putExtra("equipmentId", equipmentId)
        startActivityForResult(intent, REQUEST_DETAIL_SESSION)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DETAIL_SESSION && resultCode == RESULT_OK) {
            // Handle the result if needed
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TrainingActivity::class.java)
        startActivity(intent)
        finish()
    }
}
