package com.example.androiddevicecontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the WebAppService
        val serviceIntent = Intent(this, WebAppService::class.java)
        startService(serviceIntent)

        finish()
    }
}