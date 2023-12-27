package com.tugasakhir.gymtera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.gymtera.addon.Utils
import com.tugasakhir.gymtera.databinding.ActivityResultBmiactivityBinding

@Suppress("DEPRECATION")
class ResultBMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBmiactivityBinding

    companion object {
        const val EXTRA_BMI = "extra_bmi"
        const val EXTRA_AGE = "extra_age"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar menu
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT

        val topAppBar = binding.topAppBar
        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, BMIActivity::class.java)
            startActivity(intent)
            finish()
        }

        displayResult()
    }

    private fun displayResult() {
        val bmi = intent.getFloatExtra(EXTRA_BMI, 0f)
        val formattedBMI = String.format("%.1f", bmi)
        val age = intent.getStringExtra(EXTRA_AGE) ?: "N/A"

        binding.apply {
            yourBmi.text = formattedBMI
            condition.text = Utils.checkAdult(age.toInt(), bmi)
            ageTxt.text = SpannableString("$age (${category(age.toInt())})")

            val spannable = SpannableString("Saran : ${Utils.getSuggestions(bmi)}")
            suggestion.text = spannable
        }
    }

    private fun category(age: Int): String {
        val category: String = when (age) {
            in 2..19 -> {
                "Remaja"
            }

            else -> {
                "Dewasa"
            }
        }
        return category
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BMIActivity::class.java)
        startActivity(intent)
        finish()
    }
}