package com.tugasakhir.gymtera.admin.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.ExerciseData
import com.tugasakhir.gymtera.databinding.ActivityAdminDetailBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class AdminDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exerciseId = intent.getStringExtra("exerciseId")
        val equipmentId = intent.getStringExtra("equipmentId")

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminExerciseActivity::class.java)
            intent.putExtra("equipmentId", equipmentId)
            startActivity(intent)
            finish()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.deleteExer -> {
                    deleteExercise()
                    true
                }

                else -> false
            }
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
                    Glide.with(this@AdminDetailActivity).load(it.exerImage)
                        .into(binding.exerciseImg)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun deleteExercise() {
        val exerciseId = intent.getStringExtra("exerciseId")
        val equipmentId = intent.getStringExtra("equipmentId")

        val exerciseReference =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")
                .child(equipmentId ?: "").child("Exercise").child(exerciseId ?: "")

        exerciseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                MotionToast.createColorToast(
                    this,
                    "Hapus Berhasil",
                    "Data telah dihapus",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )

                val intent = Intent(this, AdminExerciseActivity::class.java)
                intent.putExtra("equipmentId", equipmentId)
                startActivity(intent)
                finish()
            } else {
                MotionToast.createColorToast(
                    this,
                    "Hapus Gagal",
                    "Data tidak terhapus",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val equipmentId = intent.getStringExtra("equipmentId")
        val intent = Intent(this, AdminExerciseActivity::class.java)
        intent.putExtra("equipmentId", equipmentId)
        startActivity(intent)
        finish()
    }
}