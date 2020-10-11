package com.biggates.bumap.ViewModel.building

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object BuBuilding {

    private val _buBuildings = MutableLiveData<HashMap<String,Building>>().apply {
        value = hashMapOf()
    }
    val buBuilding : LiveData<HashMap<String,Building>> = _buBuildings

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadBuBuilding(){
        val ref = FirebaseDatabase.getInstance().reference.child("BuildingInfo")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"백석대 건물정보 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(snapshot in dataSnapshot.children){
                    var building = Building()
                    for(b in snapshot.child("floor").children){
                        var floor = Floor()
                        for(f in b.children){
                            floor.roomNumber.put(f.key.toString(),f.getValue(Room::class.java)!!)
                        }
                        building.floor.put(b.key.toString(),floor)
                    }

                    building.high = snapshot.child("high").value.toString()
                    building.location = snapshot.child("location").getValue(Location::class.java)!!
                    building.low = snapshot.child("low").value.toString()
                    building.name = snapshot.child("name").value.toString()
                    _buBuildings.value!!.put(snapshot.key.toString(),building)
                }

                Toast.makeText(context,"백석대 건물정보 불러오기 완료",Toast.LENGTH_SHORT).show()

            }
        })
    }
}