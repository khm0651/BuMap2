package com.example.bumap.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.map.overlay.Marker

class HomeViewModel : ViewModel() {


    private val _buildingArr = MutableLiveData<HashMap<String,Location>>().apply {
        var markerList:HashMap<String, Marker> = HashMap()
        var buildingArr : HashMap<String,Location> = HashMap()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference


        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                    var name : String =""
                    var location : Location = Location()
                    Log.d("fire-base","2")
                    dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                        if(dataSnapshot.key.toString().equals("name")){
                            name = dataSnapshot.value.toString()
                        }else if(dataSnapshot.key.toString().equals("location")){
                            location = dataSnapshot.getValue(Location::class.java) as Location
                        }
                    }
                    buildingArr.set(name,location)
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }
        })

        value = buildingArr
    }
    val building: LiveData<HashMap<String,Location>> = _buildingArr
}