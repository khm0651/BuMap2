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

object Copy {
    private val _copyBuildings = MutableLiveData<HashMap<String, BuildingSubInfo>>().apply {
        value = hashMapOf()
    }
    val copyBuildings : LiveData<HashMap<String, BuildingSubInfo>> = _copyBuildings

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadCopyBuildings(){
        val ref = FirebaseDatabase.getInstance().reference.child("search").child("copy")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"복사점 건물정보 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(c in dataSnapshot.children){
                    var copy = c.getValue(BuildingSubInfo::class.java)!!
                    _copyBuildings.value!!.put(c.key.toString(),copy)
                }
            }
        })
    }
}