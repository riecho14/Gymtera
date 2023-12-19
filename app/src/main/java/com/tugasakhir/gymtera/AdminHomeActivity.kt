package com.tugasakhir.gymtera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tugasakhir.gymtera.databinding.ActivityHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}