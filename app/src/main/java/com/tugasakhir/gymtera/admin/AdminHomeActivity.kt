package com.tugasakhir.gymtera.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.addon.Preferences
import com.tugasakhir.gymtera.admin.features.AdminAttendanceActivity
import com.tugasakhir.gymtera.admin.features.AdminEquipmentActivity
import com.tugasakhir.gymtera.data.UserData
import com.tugasakhir.gymtera.databinding.ActivityAdminHomeBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        userDatabaseRef =
            FirebaseDatabase.getInstance(getString(R.string.ref_url)).getReference("Users")

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    val preferences = Preferences(this)
                    preferences.prefClear()

                    auth.signOut()
                    MotionToast.createColorToast(
                        this,
                        "Logout Berhasil",
                        "Anda berhasil keluar dari akun",
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.ft_regular)
                    )
                    val intent = Intent(this, AdminLoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

        // Get profile name
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            userDatabaseRef.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)
                        val username = userData?.fullname ?: getString(R.string.loading)
                        binding.profileName.text = username
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Nothing
                    }
                })
        }

        // Binding components
        binding.alatGerakan.setOnClickListener {
            val intent = Intent(this, AdminEquipmentActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.attendance.setOnClickListener {
            val intent = Intent(this, AdminAttendanceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}