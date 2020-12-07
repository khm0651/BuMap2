package com.biggates.bumap.ViewModel.building

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.RentRoom
import com.google.firebase.database.FirebaseDatabase

object RentRoom  {
    private val _rentRoomList = MutableLiveData<ArrayList<RentRoom>>().apply {
        value = arrayListOf()
    }

    val rentRoomList = _rentRoomList

    private val _rentRoomIsLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

    val rentRoomIsLoading = _rentRoomIsLoading

    fun loadRentRoomList (context : Context){
        FirebaseDatabase.getInstance().reference.child("rentRoom")
    }
}