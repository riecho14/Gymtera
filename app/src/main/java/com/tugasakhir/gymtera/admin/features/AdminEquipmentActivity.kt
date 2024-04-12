package com.tugasakhir.gymtera.admin.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.admin.AdminHomeActivity
import com.tugasakhir.gymtera.data.EquipmentAdapter
import com.tugasakhir.gymtera.data.EquipmentData
import com.tugasakhir.gymtera.databinding.ActivityAdminEquipmentBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class AdminEquipmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminEquipmentBinding
    private lateinit var databaseReference: DatabaseReference
    private val equipmentAdapter = EquipmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEquipmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Database
        databaseReference =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")

        // RecyclerView options
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val equipmentList = mutableListOf<Pair<String, EquipmentData>>()
                for (data in snapshot.children) {
                    val equipmentId = data.key
                    val equipment = data.getValue(EquipmentData::class.java)
                    equipment?.let {
                        equipmentList.add(Pair(equipmentId!!, it))
                    }
                }
                equipmentList.sortBy { it.second.toolName }
                equipmentAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInLeft)
                equipmentAdapter.submitList(equipmentList.map { it.second })

                equipmentAdapter.setOnItemClickListener { _, _, position ->
                    val clickedItem = equipmentAdapter.getItem(position)
                    clickedItem?.let {
                        val equipmentId = equipmentList[position].first
                        val intent =
                            Intent(this@AdminEquipmentActivity, AdminExerciseActivity::class.java)
                        intent.putExtra("equipmentId", equipmentId)
                        startActivity(intent)
                        finish()
                    }
                }

                equipmentAdapter.setOnItemLongClickListener { adapter, _, position ->
                    val clickedItem = adapter.getItem(position)
                    clickedItem?.let {
                        val equipmentId = equipmentList[position].first
                        val builder = AlertDialog.Builder(this@AdminEquipmentActivity)
                        builder.setMessage("Apakah Anda yakin ingin menghapus equipment ini?")
                            .setPositiveButton("Ya") { _, _ ->
                                deleteEquipment(equipmentId)
                            }.setNegativeButton("Tidak", null)
                        builder.show()
                    }
                    true
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
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Binding components
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddEquipmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        val recyclerView = binding.rvEquipmentList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = equipmentAdapter
    }

    private fun deleteEquipment(equipmentId: String) {
        databaseReference.child(equipmentId).removeValue().addOnSuccessListener {
            MotionToast.createColorToast(
                this,
                "Hapus Berhasil",
                "Data telah dihapus",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.ft_regular)
            )
        }.addOnFailureListener {
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminHomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}