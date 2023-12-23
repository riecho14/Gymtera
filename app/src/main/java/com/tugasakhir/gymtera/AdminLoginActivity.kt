package com.tugasakhir.gymtera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.gymtera.databinding.ActivityAdminLoginBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AdminLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // Text Watcher
        binding.textEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textEmail.isErrorEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!isEmailValid(email) || !email.endsWith("itera.ac.id")) {
                    binding.textEmail.error = getString(R.string.email_error)
                    binding.textEmail.isErrorEnabled = true
                }
            }
        })

        binding.textPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textPassword.isErrorEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (password.length < 8) {
                    binding.textPassword.error = getString(R.string.password_error)
                    binding.textPassword.isErrorEnabled = true
                }
            }
        })

        // Click Listener
        binding.btMasuk.setOnClickListener {
            val btMasuk = findViewById<MaterialButton>(R.id.bt_masuk)
            btMasuk.isEnabled = false

            // Loading
            val progressBar: ProgressBar = findViewById(R.id.loading)
            val cubeGrid: Sprite = CubeGrid()
            progressBar.indeterminateDrawable = cubeGrid
            progressBar.visibility = View.VISIBLE

            val email = binding.textEmail.editText?.text.toString()
            val password = binding.textPassword.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                btMasuk.isEnabled = true
                progressBar.visibility = View.GONE
                if (email.isEmpty()) {
                    binding.textEmail.error = getString(R.string.email_error)
                } else {
                    binding.textEmail.isErrorEnabled = false
                }

                if (password.isEmpty()) {
                    binding.textPassword.error = getString(R.string.password_error)
                } else {
                    binding.textPassword.isErrorEnabled = false
                }
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        btMasuk.isEnabled = true
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid
                            val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
                            val userRef = database.getReference("Users")

                            if (userId != null) {
                                userRef.child(userId).get().addOnSuccessListener { snapshot ->
                                    val userData =
                                        snapshot.getValue(com.tugasakhir.gymtera.data.UserData::class.java)
                                    if (userData?.role == "pengelola") {
                                        MotionToast.createColorToast(
                                            this,
                                            "Login Berhasil",
                                            "Anda berhasil masuk ke akun",
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.ft_regular)
                                        )

                                        val intent = Intent(this, AdminHomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        MotionToast.createColorToast(
                                            this,
                                            "Login Gagal",
                                            "Silakan periksa kembali akun anda",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.ft_regular)
                                        )
                                    }
                                }
                            }
                        } else {
                            MotionToast.createColorToast(
                                this,
                                "Login Gagal",
                                "Silakan periksa kembali akun anda",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this, R.font.ft_regular)
                            )
                        }
                    }
            }
        }

        binding.bottom1.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}