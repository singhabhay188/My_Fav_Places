package com.example.myfavplaces
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

data class Place(val title:String,val description:String,val latitude:Double,val longitude:Double):java.io.Serializable

data class userplace(val heading:String,val places:List<Place>):java.io.Serializable

public val MAPS_EXTRA = "com.example.myfavplaces.MAPS_EXTRADATA"
private val TAG="MainActivity"
private val REQUEST_CODE=1234
public val MAP_TITLE = "com.example.myfavplaces.MAP_TITLE"
private const val FILE_NAME = "com.example.myfavplaces.userplace.data"
private var datasource = mutableListOf<userplace>()
lateinit var mapAdapter:Maps_custom_adapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvid = findViewById<RecyclerView>(R.id.rvid)


        //assign data source from reading from the file. by using deserialize and convert file to List<userplace>
        datasource = deserializeUserPlaces(this).toMutableList()

        rvid.layoutManager = LinearLayoutManager(this)

        mapAdapter =
            Maps_custom_adapter(this, datasource, object : Maps_custom_adapter.onClickListener {
                override fun onClick(position: Int) {
                    val intent = Intent(this@MainActivity, DisplayMapsActivity::class.java)
                    intent.putExtra(MAPS_EXTRA, datasource[position])
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                }

                override fun onlongClick(position: Int) {
                    //delete the element at current position but before that display a dailog to confirm it
                    val dailog = AlertDialog.Builder(this@MainActivity)
                        .setMessage("Are you sure to Delete ${datasource.get(position).heading}")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("Cancel",null).show()

                    //if ok clicked delete the currenty entry position
                    dailog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                        removeElement(position)
                        dailog.dismiss()
                    }
                }
            })

        rvid.adapter = mapAdapter

        //when user clicked we want to move to new activity
        val fav_add_button = findViewById<FloatingActionButton>(R.id.fav_add_button)
        fav_add_button.setOnClickListener {
            Log.i(TAG, "Action Button Clicked!")
            favButtonTask()
        }
    }

    private fun removeElement(position: Int) {
        datasource.removeAt(position)
        //notify the adapter that a element is deleted
        mapAdapter.notifyItemRemoved(position)
        //also serialize this in the data stored on phone
        serializeUserPlaces(this, datasource)
    }

    private fun favButtonTask() {
        val view = LayoutInflater.from(this).inflate(R.layout.dailogue_add_title, null)
        val dailogue = AlertDialog.Builder(this)
            .setTitle("Create a Title")
            .setView(view)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .show()

        dailogue.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val titletext: String = view.findViewById<EditText>(R.id.titleid).text.trim().toString()
            if (titletext == "") {
                Toast.makeText(this, "Please fill the title to proceed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dailogue.dismiss()
            val intent = Intent(this@MainActivity, CreateMapsActivity::class.java)

            intent.putExtra(MAP_TITLE, titletext)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Got data back from user")
            val cuserplace = data?.getSerializableExtra(MAPS_EXTRA) as userplace
            datasource.add(cuserplace)
            mapAdapter.notifyItemInserted(datasource.size - 1)

            //as a new item is inserted update it into the local file
            serializeUserPlaces(this, datasource)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    //to save data we need to implement three members
    private fun serializeUserPlaces(context: Context, userplaces: List<userplace>) {
        Log.i(TAG, "serialize UserPlaces")
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userplaces) }
    }

    private fun deserializeUserPlaces(context: Context): List<userplace> {
        Log.i(TAG, "Deserialize UserPlaces")
        val datafile = getDataFile(context)
        if (!datafile.exists()) {
            Log.i(TAG, "Data does not exist")
            return emptyList()
        }
        ObjectInputStream(FileInputStream(datafile)).use { return it.readObject() as List<userplace> }
    }

    private fun getDataFile(context: Context): File {
        Log.i(TAG, "Getting the data from the file!!")
        return File(context.filesDir, FILE_NAME)
    }
}
