package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.ExerciseAdapter
import com.tugasakhir.gymtera.data.ExerciseData
import com.tugasakhir.gymtera.databinding.ActivityExerciseBinding

@Suppress("DEPRECATION")
class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding
    private var equipmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        equipmentId = intent.getStringExtra("equipmentId")

        // Inisialisasi RecyclerView
        val recyclerView = binding.rvExerciseList
        recyclerView.layoutManager = LinearLayoutManager(this)
        val exerciseAdapter = ExerciseAdapter()
        recyclerView.adapter = exerciseAdapter

        val exerciseReference =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")
                .child(equipmentId ?: "").child("Exercise")

        exerciseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exerciseList = mutableListOf<ExerciseData>()

                for (data in snapshot.children) {
                    val exercise = data.getValue(ExerciseData::class.java)
                    exercise?.let {
                        exerciseList.add(it)
                    }
                }
                exerciseList.sortBy { it.exerName }
                exerciseAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInLeft)
                exerciseAdapter.submitList(exerciseList)

                exerciseAdapter.setOnItemClickListener { _, _, position ->
                    val clickedItem = exerciseAdapter.getItem(position)
                    clickedItem?.let {
                        val intent = Intent(this@ExerciseActivity, DetailActivity::class.java)

                        intent.putExtra("exerciseId", snapshot.children.elementAt(position).key)
                        intent.putExtra("equipmentId", equipmentId)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, EquipmentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EquipmentActivity::class.java)
        startActivity(intent)
        finish()
    }
}