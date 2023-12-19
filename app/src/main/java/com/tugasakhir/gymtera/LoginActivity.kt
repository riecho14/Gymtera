package com.tugasakhir.gymtera

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.gymtera.databinding.ActivityLoginBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            val email = binding.textEmail.editText?.text.toString()
            val password = binding.textPassword.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
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
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid
                            val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
                            val userRef = database.getReference("Users")

                            if (userId != null) {
                                userRef.child(userId).get().addOnSuccessListener { snapshot ->
                                    val userData =
                                        snapshot.getValue(com.tugasakhir.gymtera.data.UserData::class.java)
                                    if (userData?.role == "pengunjung") {
                                        MotionToast.createColorToast(
                                            this,
                                            "Login Berhasil",
                                            "Anda berhasil masuk ke akun",
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(this, R.font.ft_regular)
                                        )

                                        val intent = Intent(this, HomeActivity::class.java)
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
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.bottom2.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}