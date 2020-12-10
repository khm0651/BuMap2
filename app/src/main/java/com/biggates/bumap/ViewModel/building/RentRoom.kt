package com.biggates.bumap.ViewModel.building

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.RentRoom
import com.biggates.bumap.Model.RentRoomMarker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object RentRoom  {
    private val _rentRoomList = MutableLiveData<HashMap<String,RentRoomMarker>>().apply {
        value = hashMapOf()
    }

    val rentRoomList = _rentRoomList

    private val _rentRoomIsLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

    val rentRoomIsLoading = _rentRoomIsLoading

    fun loadRentRoomList (context : Context){

        FirebaseDatabase.getInstance().reference.child("rentRoom").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var map = hashMapOf<String,RentRoomMarker>()
                for(rentroom in dataSnapshot.children){
                    var r = RentRoomMarker()
                    r.rentRoomName = rentroom.key!!
                    r.info = rentroom.getValue(RentRoomMarker::class.java)!!.info
                    r.location = rentroom.getValue(RentRoomMarker::class.java)!!.location
                    r.range = rentroom.getValue(RentRoomMarker::class.java)!!.range
                    map.put(rentroom.key!!,r)
                }
                _rentRoomList.postValue(map)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}