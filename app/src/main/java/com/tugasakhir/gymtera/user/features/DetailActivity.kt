package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.ExerciseData
import com.tugasakhir.gymtera.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exerciseId = intent.getStringExtra("exerciseId")
        val equipmentId = intent.getStringExtra("equipmentId")

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            intent.putExtra("equipmentId", equipmentId)
            startActivity(intent)
            finish()
        }

        val exerciseReference =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")
                .child(equipmentId ?: "").child("Exercise").child(exerciseId ?: "")

        exerciseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exerciseData = snapshot.getValue(ExerciseData::class.java)

                exerciseData?.let {
                    binding.exerciseName.text = it.exerName
                    binding.exerciseDesc.text = it.exerDesc
                    Glide.with(this@DetailActivity).load(it.exerImage).into(binding.exerciseImg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val equipmentId = intent.getStringExtra("equipmentId")
        val intent = Intent(this, ExerciseActivity::class.java)
        intent.putExtra("equipmentId", equipmentId)
        startActivity(intent)
        finish()
    }
}