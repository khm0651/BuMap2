package com.biggates.bumap.ui.home

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.biggates.bumap.Model.RentRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RentRoomViewModel(application : Application) : AndroidViewModel(application){

    private val _rentRoomList = MutableLiveData<ArrayList<RentRoom>>().apply {
        value = arrayListOf<RentRoom>()
    }
    private val _rentRoomIsLoading = MutableLiveData<Boolean>().apply { value = false }
    val rentRoom : LiveData<ArrayList<RentRoom>> = _rentRoomList
    val rentRoomIsLoading = _rentRoomIsLoading

    fun loadRentRoomRequest(){
        _rentRoomIsLoading.postValue(true)
        var list = arrayListOf<RentRoom>()
        FirebaseDatabase.getInstance().reference.child("rentRoomRequest").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(request in dataSnapshot.children){
                    list.add(request.getValue(RentRoom::class.java)!!)
                }
                _rentRoomList.postValue(list)
                _rentRoomIsLoading.postValue(false)
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(getApplication(),"서버오류",Toast.LENGTH_SHORT).show()
            }

        })
    }
}