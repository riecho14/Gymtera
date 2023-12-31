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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.EquipmentAdapter
import com.tugasakhir.gymtera.data.EquipmentData
import com.tugasakhir.gymtera.databinding.ActivityEquipmentBinding
import com.tugasakhir.gymtera.user.HomeActivity

@Suppress("DEPRECATION")
class EquipmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEquipmentBinding
    private lateinit var databaseReference: DatabaseReference
    private val equipmentAdapter = EquipmentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Database
        databaseReference =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).reference.child("Equipments")

        // RecyclerView options
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val equipmentList = mutableListOf<EquipmentData>()
                for (data in snapshot.children) {
                    val equipment = data.getValue(EquipmentData::class.java)
                    equipment?.let {
                        equipmentList.add(it)
                    }
                }
                equipmentList.sortBy { it.toolName }
                equipmentAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.SlideInLeft)
                equipmentAdapter.submitList(equipmentList)

                equipmentAdapter.setOnItemClickListener { _, _, position ->
                    val clickedItem = equipmentAdapter.getItem(position)
                    clickedItem?.let {
                        val intent = Intent(this@EquipmentActivity, ExerciseActivity::class.java)

                        intent.putExtra("equipmentId", snapshot.children.elementAt(position).key)
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
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Binding components
        val recyclerView = binding.rvEquipmentList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = equipmentAdapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}