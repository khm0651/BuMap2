package com.biggates.bumap.ViewModel.shuttleBus

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.biggates.bumap.Model.ShuttleBus
import com.biggates.bumap.Model.ShuttleBusPlaceInfo
import com.biggates.bumap.Model.ShuttleBusTime
import com.biggates.bumap.ViewModel.building.BuBuilding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ShuttleBus {

    private val _ShuttleBus = MutableLiveData<ShuttleBus>().apply {
        value = ShuttleBus()
    }
    val shuttleBus : LiveData<ShuttleBus> = _ShuttleBus

    private var context : Context? = null
    fun getContext() = context
    fun setContext(context: Context){
        this.context = context
    }

    fun loadShuttleBus(){
        val ref = FirebaseDatabase.getInstance().reference.child("shuttleBus")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var shuttleBus = ShuttleBus()
                var shuttleBusTime = ShuttleBusTime()
                var shuttleBusPlaceInfo = ShuttleBusPlaceInfo()

                shuttleBus.inquiry = snapshot.child("문의").value.toString()
                shuttleBus.notice = snapshot.child("셔틀버스 운행공지").value.toString()
                shuttleBus.period = snapshot.child("운행기간").value.toString()
                shuttleBus.price = snapshot.child("이용요금").value.toString()
                shuttleBusTime.notice = snapshot.child("시간표").child("공지").value.toString()
                var time = hashMapOf<String,HashMap<String,String>>()
                snapshot.child("시간표").child("금요일").children.forEach { timeSanpshot ->
                    var map = hashMapOf<String,String>()
                    timeSanpshot.children.forEach{t ->
                        map.put(t.key.toString(),t.value.toString())
                    }
                    time.put(timeSanpshot.key.toString(),map)
                }
                shuttleBusTime.friday = time
                time = hashMapOf()
                snapshot.child("시간표").child("월요일 ~ 목요일").children.forEach { timeSanpshot ->
                    var map = hashMapOf<String,String>()
                    timeSanpshot.children.forEach{t ->
                        map.put(t.key.toString(),t.value.toString())
                    }
                    time.put(timeSanpshot.key.toString(),map)
                }
                shuttleBusTime.other = time
                shuttleBus.time = shuttleBusTime

                var placeMap = hashMapOf<String,String>()
                snapshot.child("위치").children.forEach { placeSnapshot ->
                    placeMap.put(placeSnapshot.key.toString(),placeSnapshot.value.toString())
                }

                shuttleBusPlaceInfo.info = placeMap
                shuttleBus.placeInfo = shuttleBusPlaceInfo

                _ShuttleBus.postValue(shuttleBus)
            }


            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,"500원 버스 불러오기 실패\n서버 또는 네트워크에 문제가 생겼습니다.", Toast.LENGTH_LONG).show()
            }

        })
    }
}