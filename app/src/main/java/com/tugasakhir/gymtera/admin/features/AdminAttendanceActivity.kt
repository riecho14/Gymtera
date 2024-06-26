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
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class AdminAttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAttendanceBinding
    private var completeAttendanceList = mutableListOf<UserData>()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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

                R.id.statistics -> {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    startActivity(intent)
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

        // Sheet for raw data
        val rawDataSheet = workbook.createSheet(excelSheetName)

        // Headers for raw data
        val headerRow = rawDataSheet.createRow(0)
        headerRow.createCell(0).setCellValue("Nama")
        headerRow.createCell(1).setCellValue("Tanggal")
        headerRow.createCell(2).setCellValue("Waktu")

        // Fetch and export raw data
        fetchAttendanceData { dataList ->
            dataList.forEachIndexed { index, userData ->
                val row = rawDataSheet.createRow(index + 1)
                row.createCell(0).setCellValue(userData.fullname ?: "")
                row.createCell(1)
                    .setCellValue(parseDate(userData.date)?.let { dateFormat.format(it) })
                row.createCell(2).setCellValue(userData.time ?: "")
            }

            // Sheet for statistics
            val statsSheet = workbook.createSheet("Statistics")

            // Calculate statistics
            val dailyStats = calculateDailyStats(dataList)
            val monthlyStats = calculateMonthlyStats(dataList)
            val yearlyStats = calculateYearlyStats(dataList)

            // Export statistics
            exportStatsToSheet(statsSheet, dailyStats, "Daily Attendance")
            exportStatsToSheet(statsSheet, monthlyStats, "Monthly Attendance")
            exportStatsToSheet(statsSheet, yearlyStats, "Yearly Attendance")

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

    private fun calculateDailyStats(dataList: List<UserData>): Map<String, Int> {
        val dailyStats = mutableMapOf<String, Int>()
        for (userData in dataList) {
            val date = parseDate(userData.date)?.let { dateFormat.format(it) }
            date?.let {
                dailyStats[date] = dailyStats.getOrDefault(date, 0) + 1
            }
        }
        return dailyStats
    }

    private fun calculateMonthlyStats(dataList: List<UserData>): Map<String, Int> {
        val monthlyStats = mutableMapOf<String, Int>()
        val uniqueMonths = HashSet<String>()

        for (userData in dataList) {
            val date = parseDate(userData.date)
            val month = date?.let { SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(it) }
            month?.let {
                if (!uniqueMonths.contains(month)) {
                    uniqueMonths.add(month)
                    monthlyStats[month] = monthlyStats.getOrDefault(month, 0) + 1
                }
            }
        }
        return monthlyStats
    }

    private fun calculateYearlyStats(dataList: List<UserData>): Map<String, Int> {
        val yearlyStats = mutableMapOf<String, Int>()
        val uniqueYears = HashSet<String>()

        for (userData in dataList) {
            val date = parseDate(userData.date)
            val year = date?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it) }
            year?.let {
                if (!uniqueYears.contains(year)) {
                    uniqueYears.add(year)
                    yearlyStats[year] = yearlyStats.getOrDefault(year, 0) + 1
                }
            }
        }
        return yearlyStats
    }

    private fun exportStatsToSheet(sheet: XSSFSheet, stats: Map<String, Int>, title: String) {
        var nextRowNum = sheet.lastRowNum + 1

        if (title != "Daily Attendance") {
            sheet.createRow(nextRowNum++)
        }

        val titleRow = sheet.createRow(nextRowNum++)
        titleRow.createCell(0).setCellValue(title)

        val headerRow = sheet.createRow(nextRowNum++)
        headerRow.createCell(0).setCellValue("Tanggal")
        headerRow.createCell(1).setCellValue("Jumlah Pengunjung")

        stats.entries.forEach { (date, count) ->
            val dataRow = sheet.createRow(nextRowNum++)
            dataRow.createCell(0).setCellValue(date)
            dataRow.createCell(1).setCellValue(count.toDouble())
        }

        sheet.createRow(nextRowNum++)
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
                val sortedAttendanceList =
                    completeAttendanceList.sortedByDescending { parseDate(it.date) }
                callback(sortedAttendanceList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun parseDate(dateString: String?): Date? {
        return try {
            dateFormat.parse(dateString ?: "")
        } catch (e: Exception) {
            null
        }
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