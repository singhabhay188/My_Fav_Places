package com.example.myfavplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myfavplaces.databinding.ActivityDisplayMapsBinding

class DisplayMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDisplayMapsBinding
    private lateinit var usermap:userplace
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usermap = intent.getSerializableExtra(MAPS_EXTRA) as userplace

        supportActionBar?.title = usermap.heading

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        for(place in  usermap.places){
            val x = LatLng(place.latitude, place.longitude)
            mMap.addMarker(MarkerOptions().position(x).title(place.title).snippet(place.description))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x,7f))
        }
    }
}