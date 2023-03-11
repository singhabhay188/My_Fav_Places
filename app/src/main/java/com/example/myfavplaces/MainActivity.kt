package com.example.myfavplaces

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

data class Place(val title:String,val description:String,val latitude:Double,val longitude:Double):java.io.Serializable

data class userplace(val heading:String,val places:List<Place>):java.io.Serializable

public val MAPS_EXTRA = "com.example.myfavplaces.MAPS_EXTRADATA"
private val TAG="MainActivity"
private val REQUEST_CODE=1234
public val MAP_TITLE = "com.example.myfavplaces.MAP_TITLE"

private var datasource = mutableListOf<userplace>()
lateinit var mapAdapter:Maps_custom_adapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvid = findViewById<RecyclerView>(R.id.rvid)

        datasource = generateSampleData().toMutableList()

        rvid.layoutManager = LinearLayoutManager(this)

        mapAdapter = Maps_custom_adapter(this,datasource,object:Maps_custom_adapter.onClickListener{
            override fun onClick(position:Int) {
                val intent = Intent(this@MainActivity,DisplayMapsActivity::class.java)
                intent.putExtra(MAPS_EXTRA,datasource[position])
                startActivity(intent)
            }
        })

        rvid.adapter= mapAdapter

        //when user clicked we want to move to new activity
        val fav_add_button = findViewById<FloatingActionButton>(R.id.fav_add_button)
        fav_add_button.setOnClickListener {
            Log.i(TAG,"Action Button Clicked!")
            favButtonTask()
        }
    }

    private fun favButtonTask(){
        val view = LayoutInflater.from(this).inflate(R.layout.dailogue_add_title,null)
        val dailogue = AlertDialog.Builder(this)
            .setTitle("Create a Title")
            .setView(view)
            .setPositiveButton("OK",null)
            .setNegativeButton("Cancel",null)
            .show()

        dailogue.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val titletext:String = view.findViewById<EditText>(R.id.titleid).text.trim().toString()
            if(titletext==""){
                Toast.makeText(this, "Please fill the title to proceed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dailogue.dismiss()
            val intent = Intent(this@MainActivity,CreateMapsActivity::class.java)

            intent.putExtra(MAP_TITLE,titletext)
            startActivityForResult(intent,REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Log.i(TAG,"Got data back from user")
            val cuserplace = data?.getSerializableExtra(MAPS_EXTRA) as userplace
            datasource.add(cuserplace)
            mapAdapter.notifyItemInserted(datasource.size-1)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun generateSampleData(): List<userplace> {
        return listOf(
            userplace(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            userplace("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            userplace("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            userplace("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            userplace("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }
}