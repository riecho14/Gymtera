package com.tugasakhir.gymtera.admin.features

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.data.UserData
import com.tugasakhir.gymtera.databinding.ActivityStatisticsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class StatisticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, AdminAttendanceActivity::class.java)
            startActivity(intent)
            finish()
        }

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

                val monthlyStats = calculateMonthlyStats(sortedAttendanceList)
                setupBarChart(monthlyStats)

                val yearylyStats = calculateYearlyStats(sortedAttendanceList)
                setupPieChart(yearylyStats)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
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

    private fun parseDate(dateString: String?): Date? {
        return try {
            dateString?.let { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupBarChart(monthlyStats: Map<String, Int>) {
        val chart = binding.monthlyGraph

        val months = monthlyStats.keys.toTypedArray()
        val visitors = monthlyStats.values.map { it.toFloat() }

        val entries: ArrayList<BarEntry> = ArrayList()
        for (i in months.indices) {
            entries.add(BarEntry(i.toFloat(), visitors[i]))
        }

        // Custom Bar
        val dataSet = BarDataSet(entries, "Jumlah Pengunjung Perbulan")
        dataSet.color = Color.rgb(191, 160, 69)

        val barData = BarData(dataSet)
        chart.data = barData

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        chart.axisRight.isEnabled = false

        val leftAxis = chart.axisLeft
        leftAxis.granularity = 1f

        // Formatter float to decimal
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val dataFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return barEntry.y.toInt().toString()
            }
        }
        dataSet.valueFormatter = dataFormatter

        //Other Set
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        xAxis.setDrawGridLines(false)
        chart.invalidate()
    }

    private fun setupPieChart(yearlyStats: Map<String, Int>) {
        val chart = binding.yearlyGraph

        val years = yearlyStats.keys.toTypedArray()
        val visitors = yearlyStats.values.map { it.toFloat() }

        val entries: ArrayList<PieEntry> = ArrayList()
        for (i in years.indices) {
            entries.add(PieEntry(visitors[i], years[i]))
        }

        val dataSet = PieDataSet(entries, "Jumlah Pengunjung Pertahun")
        dataSet.colors = mutableListOf(Color.rgb(191, 160, 69), Color.rgb(255, 193, 7))

        val pieData = PieData(dataSet)
        chart.data = pieData

        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setEntryLabelTextSize(12f)
        chart.setEntryLabelColor(Color.BLACK)

        // Formatter untuk label nilai di dalam diagram pie
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
        chart.setEntryLabelTextSize(12f)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
        chart.setEntryLabelTextSize(12f)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
        chart.setEntryLabelTextSize(12f)

        val dataFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        dataSet.valueFormatter = dataFormatter

        chart.invalidate()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminAttendanceActivity::class.java)
        startActivity(intent)
        finish()
    }
}