package com.example.favspot

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.favspot.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

         lat = intent.getDoubleExtra("lat", 0.0)
         long = intent.getDoubleExtra("long", 0.0)
         name = intent.getStringExtra("name").toString()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val position = LatLng(lat, long)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))

        val marker = googleMap.addMarker(MarkerOptions().position(position).title("$name"))
        mMap.setOnMapLongClickListener { latLng ->

            val latitude = latLng.latitude
            val longitude = latLng.longitude
            val resultIntent = Intent()

            resultIntent.putExtra("latitude", latitude)
            resultIntent.putExtra("longitude", longitude)

            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }
    }
}
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

