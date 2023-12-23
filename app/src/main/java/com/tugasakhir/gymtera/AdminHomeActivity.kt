package com.tugasakhir.gymtera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tugasakhir.gymtera.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}