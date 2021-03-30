package com.ldh.verticalseekbarsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ldh.verticalseekbarsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            seekbar.setOnProgressChangeListener {
                tvValue.text = it.toString()
            }
            seekbar.firstProgress = 50
        }
    }
}