package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.HistoryAdapter
import com.tugasakhir.gymtera.data.UserData
import com.tugasakhir.gymtera.databinding.ActivityHistoryAttendanceBinding

@Suppress("DEPRECATION")
class HistoryAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryAttendanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inisialisasi RecyclerView
        val recyclerView = binding.rvAttendanceList
        recyclerView.layoutManager = LinearLayoutManager(this)
        val historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
        val userRef = database.getReference("Users").child(userId.orEmpty()).child("Attendance")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceList = mutableListOf<UserData>()

                for (attendanceSnapshot in snapshot.children) {
                    val attendanceData = attendanceSnapshot.getValue(UserData::class.java)
                    attendanceData?.let {
                        attendanceList.add(it)
                    }
                }
                val sortedAttendanceList = attendanceList.sortedWith(compareByDescending<UserData> {
                    it.date?.split("-")?.get(2).orEmpty()
                }.thenByDescending { it.date?.split("-")?.get(1).orEmpty() }
                    .thenByDescending { it.date?.split("-")?.get(0).orEmpty() })
                historyAdapter.submitList(sortedAttendanceList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AttendanceActivity::class.java)
        startActivity(intent)
        finish()
    }
}