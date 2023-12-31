package com.tugasakhir.gymtera.user.features

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.CubeGrid
import com.tugasakhir.gymtera.R
import com.tugasakhir.gymtera.databinding.ActivityBmiactivityBinding
import com.tugasakhir.gymtera.user.HomeActivity
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class BMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding
    private var height: Float = 0f
    private var weight: Float = 0f
    private var countWeight = 50
    private var countAge = 25
    private var chosen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        initComponents()
    }

    private fun initComponents() {
        binding.apply {
            Seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    val ht = progress.toString() + resources.getString(R.string.cm)
                    binding.heightTxt.text = ht
                    height = progress.toFloat() / 100
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            weightPlus.setOnClickListener {
                binding.weightTxt.text = countWeight++.toString()
            }

            weightMinus.setOnClickListener {
                binding.weightTxt.text = countWeight--.toString()
            }

            agePlus.setOnClickListener {
                binding.age.text = countAge++.toString()
            }

            ageMinus.setOnClickListener {
                binding.age.text = countAge--.toString()
            }

            calculate.setOnClickListener {
                if (!chosen) {
                    if (height.equals(0f)) {
                        MotionToast.createColorToast(
                            this@BMIActivity,
                            "Kesalahan",
                            "Tinggi badan tidak boleh kosong, silakan coba lagi",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@BMIActivity, R.font.ft_regular)
                        )
                    } else {
                        // Loading
                        val progressBar: ProgressBar = findViewById(R.id.loading)
                        val cubeGrid: Sprite = CubeGrid()
                        progressBar.indeterminateDrawable = cubeGrid
                        progressBar.visibility = View.VISIBLE
                        binding.calculate.isEnabled = false

                        weight = binding.weightTxt.text.toString().toFloat()

                        Handler().postDelayed({
                            calculateBMI(binding.age.text.toString())
                            progressBar.visibility = View.GONE
                            binding.calculate.isEnabled = true
                        }, 3000)
                    }
                } else {
                    MotionToast.createColorToast(
                        this@BMIActivity,
                        "Kesalahan",
                        "Silakan masukan pilihan jenis kelamin",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@BMIActivity, R.font.ft_regular)
                    )
                }
            }

            cardViewMale.setOnClickListener {
                if (chosen) {
                    maleTxt.setTextColor(Color.parseColor("#BFA045"))
                    maleTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.male_white, 0, 0)
                    cardViewFemale.isEnabled = false
                    chosen = false
                } else {
                    maleTxt.setTextColor(Color.parseColor("#8D8E99"))
                    maleTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_male, 0, 0)
                    cardViewFemale.isEnabled = true
                    chosen = true
                }
            }

            cardViewFemale.setOnClickListener {
                if (chosen) {
                    femaleTxt.setTextColor(Color.parseColor("#BFA045"))
                    femaleTxt.setCompoundDrawablesWithIntrinsicBounds(
                        0, R.drawable.female_white, 0, 0
                    )
                    cardViewMale.isEnabled = false
                    chosen = false

                } else {
                    femaleTxt.setTextColor(Color.parseColor("#8D8E99"))
                    femaleTxt.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_female, 0, 0)
                    cardViewMale.isEnabled = true
                    chosen = true
                }
            }
        }
    }

    private fun calculateBMI(age: String) {
        val bmi = weight / (height * height)

        val intent = Intent(this, ResultBMIActivity::class.java)
        intent.putExtra(ResultBMIActivity.EXTRA_BMI, bmi)
        intent.putExtra(ResultBMIActivity.EXTRA_AGE, age)
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}