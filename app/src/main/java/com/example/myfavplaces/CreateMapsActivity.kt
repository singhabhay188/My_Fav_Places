package com.example.myfavplaces

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myfavplaces.databinding.ActivityCreateMapsBinding
import com.google.android.gms.maps.model.Marker


private val TAG:String = "CreateMapsActivity"
public val allMarkers:MutableList<Marker> = mutableListOf<Marker>()

class CreateMapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //change the title of the current activity
        supportActionBar?.title = intent.getStringExtra(MAP_TITLE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //check if the item clicked is equal to menu
        if(item.itemId == R.id.saveid){
            Log.i(TAG,"Tapped On Save !!")
            if(allMarkers.isEmpty()){
                Toast.makeText(this, "Add atleast one marker to save.", Toast.LENGTH_SHORT).show()
                return true
            }

            //else we will return the nwe userplace to main activity
            //before that we need to convert list of mutable markers to list of place that
            val cPlaces = allMarkers.map { marker -> Place(marker.title!!,marker.snippet!!,marker.position.latitude,marker.position.longitude) }
            val cuserplace = userplace(intent.getStringExtra(MAP_TITLE).toString(),cPlaces)

            val data = Intent()
            data.putExtra(MAPS_EXTRA,cuserplace)
            setResult(Activity.RESULT_OK,data)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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

        //change the view to delhi
        val delhi = LatLng(28.7,77.1)
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi))  but this is not zooming in to zoom into longer
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi,8f))

        allMarkers.clear()

        mMap.setOnInfoWindowClickListener {markertodelete->
            Log.i(TAG,"Marker removed called")
            allMarkers.remove(markertodelete)
            markertodelete.remove()
        }
        mMap.setOnMapLongClickListener{clickedposition->
            Log.i(TAG,"Long Clicked on Map Done")
            //before adding marker on long click listening we would display a dialogue box to enter the tile and the descrpition for marker info
            showAlertDialogue(clickedposition)
        }
    }

    private fun showAlertDialogue(clickedposition: LatLng){
        val view = LayoutInflater.from(this).inflate(R.layout.dailogue_view_addmarker,null)
        val dailogue = AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(view)
            .setPositiveButton("OK",null)
            .setNegativeButton("Cancel",null)
            .show()

        dailogue.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val titletext:String = view.findViewById<EditText>(R.id.titleid).text.trim().toString()
            val descriptiontext:String = view.findViewById<EditText>(R.id.descriptionid).text.trim().toString()
            if(titletext=="" || descriptiontext==""){
                Toast.makeText(this, "Please fill the details to proceed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dailogue.dismiss()
            val cmarker = mMap.addMarker(MarkerOptions().position(clickedposition).title(titletext).snippet(descriptiontext))
            allMarkers.add(cmarker!!)
        }
    }
}