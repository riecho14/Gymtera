package com.tugasakhir.gymtera.user.features

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.databinding.ActivityAttendanceBinding
import com.tugasakhir.gymtera.user.HomeActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


@Suppress("DEPRECATION")
class AttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val targetLatitude = -5.359790
    private val targetLongitude = 105.315717
    private val radius = 999999999999999999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bottom1.setOnClickListener {
            val intent = Intent(this, HistoryAttendanceActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.apply {
            presensi.setOnClickListener {
                if (isLocationEnabled()) {
                    attendanceUser()
                } else {
                    showLocationEnableDialog()
                }
            }
        }
    }

    private fun attendanceUser() {
        if (checkLocationPermission()) {
            binding.presensi.isEnabled = false

            if (!hasUserAttendedToday()) {
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1
                    isWaitForAccurateLocation = true
                }

                val locationCallback = object : LocationCallback() {
                    @SuppressLint("SimpleDateFormat")
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        val userLatitude = location?.latitude
                        val userLongitude = location?.longitude

                        Log.d(
                            "LocationInfo",
                            "User Latitude: $userLatitude, User Longitude: $userLongitude"
                        )

                        // Check if the user's location is within the specified radius
                        if (userLatitude?.let {
                                userLongitude?.let { it1 ->
                                    isWithinRadius(
                                        it, it1
                                    )
                                }
                            } == true) {

                            // Check current time
                            val currentTimeMillis = System.currentTimeMillis()
                            val date = java.util.Date(currentTimeMillis)
                            val dateString = java.text.SimpleDateFormat("dd-MM-yyyy").format(date)
                            val timeString = java.text.SimpleDateFormat("HH:mm").format(date)

                            saveAttendanceData(dateString, timeString)

                            MotionToast.createColorToast(
                                this@AttendanceActivity,
                                "Presensi Berhasil",
                                "Anda berhasil melakukan presensi",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this@AttendanceActivity, R.font.ft_regular)
                            )

                            val intent = Intent(
                                this@AttendanceActivity, HistoryAttendanceActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            binding.presensi.isEnabled = true
                            MotionToast.createColorToast(
                                this@AttendanceActivity,
                                "Presensi Gagal",
                                "Anda berada di luar jangkauan presensi",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this@AttendanceActivity, R.font.ft_regular)
                            )
                        }

                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } else {
                binding.presensi.isEnabled = true
                MotionToast.createColorToast(
                    this@AttendanceActivity,
                    "Presensi Gagal",
                    "Anda sudah melakukan presensi hari ini",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@AttendanceActivity, R.font.ft_regular)
                )
            }
        }
    }

    private fun saveAttendanceData(dateString: String, timeString: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid
        val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
        val userRef = database.getReference("Users")

        userId?.let {
            val attendanceData = HashMap<String, String>()
            attendanceData["date"] = dateString
            attendanceData["time"] = timeString

            userRef.child(it).child("Attendance").push().setValue(attendanceData)

            val sharedPreferences = getPreferences(MODE_PRIVATE)
            sharedPreferences.edit().putString("lastAttendanceDate", dateString).apply()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun hasUserAttendedToday(): Boolean {
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val lastAttendanceDate = sharedPreferences.getString("lastAttendanceDate", "")

        val currentDate =
            java.text.SimpleDateFormat("dd-MM-yyyy").format(System.currentTimeMillis())

        return lastAttendanceDate == currentDate
    }

    @SuppressLint("ServiceCast")
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showLocationEnableDialog() {
        AlertDialog.Builder(this).setTitle("Aktifkan Lokasi")
            .setMessage("Lokasi diperlukan untuk melakukan presensi. Silakan aktifkan lokasi Anda.")
            .setPositiveButton("Aktifkan") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun isWithinRadius(userLatitude: Double, userLongitude: Double): Boolean {
        val lat1 = Math.toRadians(userLatitude)
        val lat2 = Math.toRadians(targetLatitude)
        val lon1 = Math.toRadians(userLongitude)
        val lon2 = Math.toRadians(targetLongitude)

        val d = acos(
            sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1)
        ) * 6371 * 1000

        Log.d(
            "RadiusInfo", "Radius: $d"
        )

        return d <= radius
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fineLocationPermission =
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val coarseLocationPermission =
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

            if (fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
                false
            }
        } else {
            true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}