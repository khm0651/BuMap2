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

object ConvenienceStore {
    private val _convenienceStoreBuildings = MutableLiveData<HashMap<String, BuildingSubInfo>>().apply {
        value = hashMapOf()
    }
    val convenienceStoreBuildings : LiveData<HashMap<String, BuildingSubInfo>> = _convenienceStoreBuildings

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadConvenienceStoreBuildings(){
        val ref = FirebaseDatabase.getInstance().reference.child("search").child("convenienceStore")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"편의점 건물정보 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(c in dataSnapshot.children){
                    var convenienceStore = c.getValue(BuildingSubInfo::class.java)!!
                    _convenienceStoreBuildings.value!!.put(c.key.toString(),convenienceStore)
                }
            }
        })
    }
}