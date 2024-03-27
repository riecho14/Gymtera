package com.tugasakhir.gymtera.admin.features

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.admin.AdminHomeActivity
import com.tugasakhir.gymtera.data.AdminHistoryAdapter
import com.tugasakhir.gymtera.data.UserData
import com.tugasakhir.gymtera.databinding.ActivityAdminAttendanceBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class AdminAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAttendanceBinding
    private var completeAttendanceList = mutableListOf<UserData>()
    val historyAdapter = AdminHistoryAdapter()

    companion object {
        private const val REQUEST_WRITE_STORAGE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            // Request permission
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE
            )
        }

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exportData -> {
                    exportDataToExcel()
                    true
                }

                else -> false
            }
        }

        // Search
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { search(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { search(it) }
                return true
            }

        })

        // Inisialisasi RecyclerView
        val recyclerView = binding.rvAttendanceList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
        val usersRef = database.getReference("Users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceList = mutableListOf<UserData>()

                for (userSnapshot in snapshot.children) {
                    userSnapshot.key
                    val attendanceRef = userSnapshot.child("Attendance")

                    for (attendanceSnapshot in attendanceRef.children) {
                        val attendanceData = attendanceSnapshot.getValue(UserData::class.java)
                        attendanceData?.let {
                            it.fullname =
                                userSnapshot.child("fullname").getValue(String::class.java)
                            attendanceList.add(it)
                        }
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

    private fun exportDataToExcel() {
        val excelFileName = "data_presensi.xlsx"
        val excelSheetName = "Data Presensi"

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(excelSheetName)

        // Headers
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Nama")
        headerRow.createCell(1).setCellValue("Tanggal")
        headerRow.createCell(2).setCellValue("Waktu")

        // Fetch data
        fetchAttendanceData { dataList ->
            // Data
            for ((index, userData) in dataList.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(userData.fullname ?: "")
                row.createCell(1).setCellValue(userData.date ?: "")
                row.createCell(2).setCellValue(userData.time ?: "")
            }

            // Save the workbook
            try {
                val fileOutputStream =
                    FileOutputStream(getExternalFilesDir(null)?.absolutePath + "/" + excelFileName)
                workbook.write(fileOutputStream)
                fileOutputStream.close()
                workbook.close()
                MotionToast.createColorToast(
                    this,
                    "Ekspor Berhasil",
                    "Data diekspor ke $excelFileName",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            } catch (e: IOException) {
                e.printStackTrace()
                MotionToast.createColorToast(
                    this,
                    "Kesalahan",
                    "Gagal melakukan export data",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.ft_regular)
                )
            }
        }
    }

    private fun fetchAttendanceData(callback: (List<UserData>) -> Unit) {
        val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
        val usersRef = database.getReference("Users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completeAttendanceList.clear()

                for (userSnapshot in snapshot.children) {
                    userSnapshot.key
                    val attendanceRef = userSnapshot.child("Attendance")

                    for (attendanceSnapshot in attendanceRef.children) {
                        val attendanceData = attendanceSnapshot.getValue(UserData::class.java)
                        attendanceData?.let {
                            it.fullname =
                                userSnapshot.child("fullname").getValue(String::class.java)
                            completeAttendanceList.add(it)
                        }
                    }
                }
                val sortedAttendanceList = completeAttendanceList.sortedByDescending { it.date }
                callback(sortedAttendanceList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun search(query: String) {
        fetchAttendanceData { dataList ->
            val searchResult = dataList.filter {
                it.fullname?.contains(query, ignoreCase = true) ?: false
            }
            historyAdapter.submitList(searchResult)
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