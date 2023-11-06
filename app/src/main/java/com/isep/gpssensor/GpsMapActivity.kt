package com.isep.gpssensor

// GpsMapActivity.kt
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class GpsMapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Important pour osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.gpsmap)

        mapView = findViewById(R.id.map)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Centrez la carte sur un point (exemple : New York)
        val mapController = mapView.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(40.73, -73.99) // New York
        mapController.setCenter(startPoint)
    }

    public override fun onResume() {
        super.onResume()
        // Ceci redémarre les services de tuiles
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        // Ceci arrête les services de tuiles
        mapView.onPause()
    }
}
