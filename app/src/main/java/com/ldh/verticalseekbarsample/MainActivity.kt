package com.ldh.verticalseekbarsample

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.ldh.verticalseekbarsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {

        }

        testTouchListener()
    }

    private fun testTouchListener() {
        binding.root.setOnTouchListener { view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Log.d("SEEKBAR_TOUCH", "RECT: 좌우 __ ${thumbRect?.left} ~ ${thumbRect?.right}, 상하 __ ${thumbRect?.top} ~ ${thumbRect?.bottom}")
                    // Log.d("SEEKBAR_TOUCH", "실제 클릭: x: ${event.x}, y: ${event.y}")
                }

                // 3.coerceIn(3..4)
                MotionEvent.ACTION_MOVE -> {
                    // Log.d("SEEKBAR_TOUCH", "RECT: 좌우 __ ${thumbRect?.left} ~ ${thumbRect?.right}, 상하 __ ${thumbRect?.top} ~ ${thumbRect?.bottom}")
                        val (x, y) = event.x.toInt() to event.y.toInt()
                        Log.d("SEEKBAR_TOUCH", "메인 실제 클릭: x: ${event.x}, y: ${event.y}")
                }

                MotionEvent.ACTION_UP -> {
                    view.performClick()
                }
            }
            true
        }
    }
}