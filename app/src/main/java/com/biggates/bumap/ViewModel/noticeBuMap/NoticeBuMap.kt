package com.biggates.bumap.ViewModel.noticeBuMap

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.BuMapNotice
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object NoticeBuMap {
    private val _buNotice = MutableLiveData<ArrayList<BuMapNotice>>().apply {
        value = arrayListOf()
    }
    val buNotice : LiveData<ArrayList<BuMapNotice>> = _buNotice

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadBuNotice(){
        val ref = FirebaseDatabase.getInstance().reference.child("noticeBuMap")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"BuMap 공지사항 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(snapshot in dataSnapshot.children){
                    var buNotice = snapshot.getValue(BuMapNotice::class.java)!!
                    _buNotice.value!!.add(buNotice)

                }


            }
        })
    }

}