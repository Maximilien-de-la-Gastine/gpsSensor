package com.isep.gpssensor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonAccelerometer = findViewById<Button>(R.id.buttonAccelerometer)
        buttonAccelerometer.setOnClickListener {
            val intent = Intent(this, Accelerometer::class.java)
            startActivity(intent)
        }

        val buttonCompass = findViewById<Button>(R.id.buttonCompass)
        buttonCompass.setOnClickListener {
            val intent = Intent(this, Compass::class.java)
            startActivity(intent)
        }
    }
}
