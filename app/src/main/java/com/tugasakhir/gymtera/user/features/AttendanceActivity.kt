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
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.databinding.ActivityAttendanceBinding
import com.tugasakhir.gymtera.user.HomeActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Suppress("DEPRECATION")
class AttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val targetLatitude = -4.3476
    private val targetLongitude = 104.3587
    private val radius = 10.0

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

            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 1000 * 5
                maxWaitTime = 10000
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    val userLatitude = location?.latitude
                    val userLongitude = location?.longitude

                    // Check if the user's location is within the specified radius
                    if (userLatitude?.let {
                            userLongitude?.let { it1 ->
                                isWithinRadius(
                                    it, it1
                                )
                            }
                        } == true) {
                        binding.presensi.isEnabled = true
                        MotionToast.createColorToast(
                            this@AttendanceActivity,
                            "Presensi Berhasil",
                            "Anda berhasil melakukan presensi",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@AttendanceActivity, R.font.ft_regular)
                        )
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
        }
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
        val deltaLongitude = Math.toRadians(userLongitude - targetLongitude)
        val deltaLatitude = Math.toRadians(userLatitude - targetLatitude)

        val a =
            sin(deltaLatitude / 2) * sin(deltaLatitude / 2) + cos(Math.toRadians(targetLatitude)) * cos(
                Math.toRadians(userLatitude)
            ) * sin(deltaLongitude / 2) * sin(deltaLongitude / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = 6371 * c * 1000

        return distance <= radius
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