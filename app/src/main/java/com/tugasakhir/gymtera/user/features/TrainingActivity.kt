package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.TrainingAdapter
import com.tugasakhir.gymtera.data.TrainingData
import com.tugasakhir.gymtera.databinding.ActivityTrainingBinding
import com.tugasakhir.gymtera.user.HomeActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class TrainingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrainingBinding
    private lateinit var adapter: TrainingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up button click listeners
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.beginner -> changeButtonTint(binding.beginner, isChecked)
                R.id.intermediate -> changeButtonTint(binding.intermediate, isChecked)
                R.id.advanced -> changeButtonTint(binding.advanced, isChecked)
            }
        }

        // RecyclerView options
        adapter = TrainingAdapter()
        binding.rvTrainingList.layoutManager = LinearLayoutManager(this)
        binding.rvTrainingList.adapter = adapter

        val trainingDataList = mutableListOf<TrainingData>()
        addTrainingData(trainingDataList)
        trainingDataList.sortBy { it.name }
        adapter.submitList(trainingDataList)
        adapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInLeft)

        adapter.setOnItemClickListener { _, _, position ->
            val selectedTraining = adapter.getItem(position)
            val selectedDifficulty = getSelectedDifficulty()

            if (selectedDifficulty != null) {
                val intent = Intent(this, ResultTrainingActivity::class.java).apply {
                    putExtra(ResultTrainingActivity.EXTRA_DIFFICULTY, selectedDifficulty)
                    putExtra(ResultTrainingActivity.EXTRA_SELECTED_TRAINING, selectedTraining)
                }
                startActivity(intent)
                finish()
            } else {
                MotionToast.createColorToast(
                    this,
                    "Kesalahan",
                    "Silakan pilih level latihan anda",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            }
        }
    }

    private fun getSelectedDifficulty(): String? {
        return when (binding.toggleButton.checkedButtonId) {
            R.id.beginner -> "beginner"
            R.id.intermediate -> "intermediate"
            R.id.advanced -> "advanced"
            else -> null
        }
    }

    private fun changeButtonTint(button: Button, isChecked: Boolean) {
        if (isChecked) {
            val color = Color.parseColor("#99BFA045")
            button.backgroundTintList = ColorStateList.valueOf(color)
        } else {
            button.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        }
    }

    private fun addTrainingData(
        trainingDataList: MutableList<TrainingData>
    ) {
        trainingDataList.add(TrainingData("Chest", R.drawable.chest_muscle))
        trainingDataList.add(TrainingData("Back", R.drawable.back_muscle))
        trainingDataList.add(TrainingData("Legs", R.drawable.legs_muscle))
        trainingDataList.add(TrainingData("Triceps", R.drawable.triceps_muscle))
        trainingDataList.add(TrainingData("Abs", R.drawable.abs_muscle))
        trainingDataList.add(TrainingData("Forearm", R.drawable.forearm_muscle))
        trainingDataList.add(TrainingData("Biceps", R.drawable.biceps_muscle))
        trainingDataList.add(TrainingData("Shoulders", R.drawable.shoulders_muscle))
        trainingDataList.add(TrainingData("Traps", R.drawable.traps_muscle))
        trainingDataList.add(TrainingData("Seluruh Tubuh", R.drawable.all_muscle))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}