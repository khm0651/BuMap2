package com.biggates.bumap.ViewModel.building

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.BuildingSubInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object Beer {
    private val _beerBuildings = MutableLiveData<HashMap<String, BuildingSubInfo>>().apply {
        value = hashMapOf()
    }
    val beerBuildings : LiveData<HashMap<String, BuildingSubInfo>> = _beerBuildings

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadBeerBuildings(){
        val ref = FirebaseDatabase.getInstance().reference.child("search").child("beer")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"술집 건물정보 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(b in dataSnapshot.children){
                    var beer = b.getValue(BuildingSubInfo::class.java)!!
                    _beerBuildings.value!!.put(b.key.toString(),beer)
                }
            }
        })
    }
}