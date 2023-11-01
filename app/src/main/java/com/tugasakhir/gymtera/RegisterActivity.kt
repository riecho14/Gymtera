package com.tugasakhir.gymtera

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.tugasakhir.gymtera.data.UserData
import com.tugasakhir.gymtera.databinding.ActivityRegisterBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        // Text Watcher
        binding.textName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textName.isErrorEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.textEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.textEmail.isErrorEnabled = false
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!isEmailValid(email)) {
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
        binding.btDaftar.setOnClickListener {
            val name = binding.textName.editText?.text.toString()
            val email = binding.textEmail.editText?.text.toString()
            val password = binding.textPassword.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (name.isEmpty()) {
                    binding.textName.error = getString(R.string.name_error)
                } else {
                    binding.textName.isErrorEnabled = false
                }

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
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid
                            val database = FirebaseDatabase.getInstance(getString(R.string.ref_url))
                            val userRef = database.getReference("Users")

                            if (userId != null) {
                                val userData = UserData(name, "pengunjung")
                                userRef.child(userId).setValue(userData)
                            }

                            MotionToast.createColorToast(
                                this,
                                "Daftar Akun Berhasil",
                                "Silahkan masuk menggunakan akun yang telah terdaftar",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this, R.font.ft_regular)
                            )

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            MotionToast.createColorToast(
                                this,
                                "Daftar Akun Gagal",
                                "Silakan isi data sesuai dengan ketentuan yang diberikan",
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}