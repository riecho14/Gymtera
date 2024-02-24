package com.tugasakhir.gymtera.user

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.admin.AdminLoginActivity
import com.tugasakhir.gymtera.databinding.ActivityForgotPasswordBinding
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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

        // Click Listener
        binding.btReset.setOnClickListener {
            val btReset = findViewById<MaterialButton>(R.id.bt_reset)
            btReset.isEnabled = false

            // Loading
            val progressBar: ProgressBar = findViewById(R.id.loading)
            val cubeGrid: Sprite = CubeGrid()
            progressBar.indeterminateDrawable = cubeGrid
            progressBar.visibility = View.VISIBLE

            val email = binding.textEmail.editText?.text.toString()

            if (email.isEmpty() || !isEmailValid(email) || !email.endsWith(
                    "itera.ac.id"
                )
            ) {
                btReset.isEnabled = true
                progressBar.visibility = View.GONE
                if (email.isEmpty() || !isEmailValid(email) || !email.endsWith("itera.ac.id")) {
                    binding.textEmail.error = getString(R.string.email_error)
                } else {
                    binding.textEmail.isErrorEnabled = false
                }
                return@setOnClickListener
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    btReset.isEnabled = false
                    progressBar.visibility = View.VISIBLE

                    if (task.isSuccessful) {
                        MotionToast.createColorToast(
                            this,
                            "Reset Password Berhasil",
                            "Silakan periksa email Anda",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, R.font.ft_regular)
                        )

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        MotionToast.createColorToast(
                            this,
                            "Reset Password Gagal",
                            "Terjadi kesalahan",
                            MotionToastStyle.SUCCESS,
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