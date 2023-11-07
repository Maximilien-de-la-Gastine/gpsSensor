package com.isep.gpssensor

import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.time.LocalTime


class Gyroscope : AppCompatActivity(), SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private lateinit var textFootstep: TextView             // Number of footsteps
    private lateinit var textAcceleration: TextView         // Current acceleration magnitude
    private lateinit var textRotation: TextView             // Current rotation magnitude
    private lateinit var accelerometerActivated:Button
    private lateinit var gyroscopeActivated:Button


    // Define and initialize global variables
    open var totalNumberFootstep: Int = 0           // Footsteps counter

    open var accelerometerVals = Array(3) {(0).toFloat()}       // Current accelerometer values
    open var oldAccelerometerVals = Array(3) {(0).toFloat()}    // Previous accelerometer values

    open var gravityVals = Array(3) {(0).toFloat()}             // Current gravity values

    open var gyroscopeVals = Array(3) {(0).toFloat()}           // Current gyroscope values
    open var oldGyroscopeVals = Array(3) {(0).toFloat()}        // Previous gyroscope values



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gyroscope)

        // Button -> go to main menu
        val buttonReturnToMenu = findViewById<Button>(R.id.buttonMenu)
        buttonReturnToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Button -> reset number of steps
        val buttonReset = findViewById<Button>(R.id.buttonReset)
        buttonReset.setOnClickListener {
            totalNumberFootstep = 0
        }


        // Attribute TextViews
        textFootstep = findViewById(R.id.textNumberFootstep)
        textAcceleration = findViewById(R.id.textViewAcceleration)
        textRotation = findViewById(R.id.textViewRotation)


        setUpSensorStuff()      // Modify settings of sensors

    }




    private fun setUpSensorStuff() {

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Listen accelerometer
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,          // Delay = 0.2s
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Listen gravity sensor
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.also { gravity ->
            sensorManager.registerListener(
                this,
                gravity,
                SensorManager.SENSOR_DELAY_NORMAL,          // Delay = 0.2s
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }


        // Listen gyroscope sensor
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also { gyroscope ->
            sensorManager.registerListener(
                this,
                gyroscope,
                SensorManager.SENSOR_DELAY_NORMAL,          // Delay = 0.2s
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {


        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {       // Gravity sensor changes
            val gravityX = event.values[0]
            val gravityY = event.values[1]
            val gravityZ = event.values[2]

            gravityVals = arrayOf(gravityX, gravityY, gravityZ) // Store values in the array
        }


        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            oldAccelerometerVals = accelerometerVals        // Keep previous accelerometer values

            val accelerometerX = event.values[0] - gravityVals[0]       // Remove gravitational acceleration
            val accelerometerY = event.values[1] - gravityVals[1]
            val accelerometerZ = event.values[2] - gravityVals[2]

            accelerometerVals = arrayOf(accelerometerX, accelerometerY, accelerometerZ)
        }



        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {

            oldGyroscopeVals = gyroscopeVals                // Keep previous gyroscope values

            val gyroscopeX = event.values[0]
            val gyroscopeY = event.values[1]
            val gyroscopeZ = event.values[2]

            gyroscopeVals = arrayOf(gyroscopeX, gyroscopeY, gyroscopeZ)
        }

        movementDetection()

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }


    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }


    private fun movementDetection(){

        // Compute acceleration and rotation rate magnitude
        val acceleration = accelerometerVals[0] * accelerometerVals[0] + accelerometerVals[1] * accelerometerVals[1] + accelerometerVals[2] * accelerometerVals[2]
        val rotation = gyroscopeVals[0] * gyroscopeVals[0] + gyroscopeVals[1] * gyroscopeVals[1] + gyroscopeVals[2] * gyroscopeVals[2]

        if (isWalking()) {
            totalNumberFootstep++
        }

        textFootstep.setText(totalNumberFootstep.toString())
        textAcceleration.setText(acceleration.toString())
        textRotation.setText(rotation.toString())
    }


    private fun isWalking():Boolean{
        // Conditions for Accelerometer
        var sumBool = 0         // Number of conditions verified
        if (accelerometerVals[0] * oldAccelerometerVals[0] < 0) { sumBool ++ }  // Detect movement on X-axis
        if (accelerometerVals[1] * oldAccelerometerVals[1] < 0) { sumBool ++ }  // Y-axis
        if (accelerometerVals[2] * oldAccelerometerVals[2] < 0) { sumBool ++ }  // Z-axis

        // Detect sufficient movement
        val accMagnitude = accelerometerVals[0] * accelerometerVals[0] + accelerometerVals[1] * accelerometerVals[1] + accelerometerVals[2] * accelerometerVals[2]

        // Condition : at least a movement in 2-dimension && sufficient magnitude
        val condAccelerometer:Boolean = (sumBool > 1) && (accMagnitude > 20)        // threshold defined experimentally

        sumBool = 0
        if (gyroscopeVals[0] * oldGyroscopeVals[0] < 0) { sumBool ++ }      // Rotation around X
        if (gyroscopeVals[1] * oldGyroscopeVals[1] < 0) { sumBool ++ }      // around Y
        if (gyroscopeVals[2] * oldGyroscopeVals[2] < 0) { sumBool ++ }      // around Z

        val gyroMagnitude = gyroscopeVals[0] * gyroscopeVals[0] + gyroscopeVals[1] * gyroscopeVals[1] + gyroscopeVals[2] * gyroscopeVals[2]
        val condGyroscope:Boolean = (sumBool > 1) && (gyroMagnitude > 5)

        accelerometerActivated = findViewById(R.id.accActivated)
        gyroscopeActivated = findViewById(R.id.gyroActivated)



        // Show which
        if (condAccelerometer) {
            accelerometerActivated.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            accelerometerActivated.setBackgroundColor(Color.parseColor("#000000"))
        }

        if (condGyroscope) {
            gyroscopeActivated.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            gyroscopeActivated.setBackgroundColor(Color.parseColor("#000000"))
        }


        return condAccelerometer && condGyroscope
    }
}