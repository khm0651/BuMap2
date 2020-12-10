package com.biggates.bumap.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.biggates.bumap.Model.RentRoom
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_rent_room_detail.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

class RentRoomDetailActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var rentRoom : RentRoom
    lateinit var viewModel : RentRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_room_detail)
        viewModel = ViewModelProviders.of(this).get(RentRoomViewModel::class.java)
        rentRoom = intent.getParcelableExtra("rentRoom")
        var options = NaverMapOptions()
            .mapType(NaverMap.MapType.Basic)
            .zoomControlEnabled(false)
            .locationButtonEnabled(false)
            .camera(CameraPosition(LatLng(rentRoom.lat.toDouble(), rentRoom.lng.toDouble()), 17.5))

        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.rent_room_detail_map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.rent_room_detail_map, it).commit()
            }

        rent_room_ok.setOnClickListener {
            var rentRoomName = rentRoom.buildingName.replace(" ","")
            rentRoomName = rentRoomName.toUpperCase()
            FirebaseDatabase.getInstance().reference.child("rentRoom").child(rentRoomName).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    registerRentRoom(dataSnapshot)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        rent_room_cancel.setOnClickListener {
            finish()
        }
        mapFragment.getMapAsync(this)
    }

    private fun registerRentRoom(dataSnapshot: DataSnapshot) {
        var list = arrayListOf<RentRoom>()

        for(info in dataSnapshot.child("info").children){
            list.add(info.getValue(RentRoom::class.java)!!)
        }
        var simpleFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var cal = Calendar.getInstance()
        rentRoom.updateDate = simpleFormatter.format(cal.time)
        list.add(rentRoom)
        var minYearPrice = 0
        var maxYearPrice = 0
        var minHalfYearPrice = 0
        var maxHalfYearPrice = 0
        var yearPriceRange = ""
        var halfYearPriceRange = ""
        for(rentRoomInfo in list){
            if(minYearPrice == 0 && rentRoomInfo.yearPrice != "") minYearPrice = rentRoomInfo.yearPrice.toInt()
            if(maxYearPrice == 0 && rentRoomInfo.yearPrice != "") maxYearPrice = rentRoomInfo.yearPrice.toInt()

            if(minHalfYearPrice == 0 && rentRoomInfo.halfYearPrice != "") minHalfYearPrice = rentRoomInfo.halfYearPrice.toInt()
            if(maxHalfYearPrice == 0 && rentRoomInfo.halfYearPrice != "") maxHalfYearPrice = rentRoomInfo.halfYearPrice.toInt()

            if(rentRoomInfo.yearPrice != ""){
                if(minYearPrice > rentRoomInfo.yearPrice.toInt()) minYearPrice = rentRoomInfo.yearPrice.toInt()
                if(maxYearPrice < rentRoomInfo.yearPrice.toInt()) maxYearPrice = rentRoomInfo.yearPrice.toInt()
            }

            if(rentRoomInfo.halfYearPrice != ""){
                if(minHalfYearPrice > rentRoomInfo.halfYearPrice.toInt()) minHalfYearPrice = rentRoomInfo.halfYearPrice.toInt()
                if(maxHalfYearPrice < rentRoomInfo.halfYearPrice.toInt()) maxHalfYearPrice = rentRoomInfo.halfYearPrice.toInt()
            }

        }

        if(minYearPrice == maxYearPrice) yearPriceRange = maxYearPrice.toString()
        else yearPriceRange = "${minYearPrice} ~ ${maxYearPrice}"

        if(minHalfYearPrice == maxHalfYearPrice) halfYearPriceRange = maxHalfYearPrice.toString()
        else halfYearPriceRange = "${minHalfYearPrice} ~ ${maxHalfYearPrice}"

        FirebaseDatabase.getInstance().reference.child("rentRoomRequest").child(rentRoom.id).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                if(!dataSnapshot.hasChild("location")){
                    var m = hashMapOf<String,String>()
                    m.put("lat",rentRoom.lat)
                    m.put("lng",rentRoom.lng)
                    dataSnapshot.ref.child("location").setValue(m).addOnCompleteListener {
                        if(it.isSuccessful){
                            dataSnapshot.ref.child("info").setValue(list).addOnCompleteListener {
                                if(it.isSuccessful) {
                                    var rangeMap = hashMapOf<String,String>()
                                    rangeMap.put("yearPriceRange",yearPriceRange)
                                    rangeMap.put("halfYearPriceRange",halfYearPriceRange)
                                    dataSnapshot.ref.child("range").setValue(rangeMap).addOnCompleteListener {
                                        if(it.isSuccessful){
                                            Toast.makeText(this,"등록 완료",Toast.LENGTH_SHORT).show()
                                            setResult(200)
                                            finish()
                                        }else{
                                            Toast.makeText(this,"범위 등록 실패",Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }else{
                                    Toast.makeText(this,"정보 등록 실패",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(this,"위치 등록 실패",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    dataSnapshot.ref.child("info").setValue(list).addOnCompleteListener {
                        if(it.isSuccessful) {
                            var rangeMap = hashMapOf<String,String>()
                            rangeMap.put("yearPriceRange",yearPriceRange)
                            rangeMap.put("halfYearPriceRange",halfYearPriceRange)
                            dataSnapshot.ref.child("range").setValue(rangeMap).addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this,"등록 완료",Toast.LENGTH_SHORT).show()
                                    setResult(200)
                                    finish()
                                }else{
                                    Toast.makeText(this,"범위 등록 실패",Toast.LENGTH_SHORT).show()
                                }
                            }

                        }else{
                            Toast.makeText(this,"정보 등록 실패",Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }else{
                Toast.makeText(this,"서버 오류",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext!!) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker!!.tag.toString()
            }
        }
        var marker = Marker()
        marker.position = LatLng(rentRoom.lat.toDouble(), rentRoom.lng.toDouble())
        marker.width= MyUtil.Dp2Px(applicationContext, 15)
        marker.height= MyUtil.Dp2Px(applicationContext, 20)
        marker.tag = "기준 : ${rentRoom.num}\n년세 : ${rentRoom.yearPrice}\n반년세 : ${rentRoom.halfYearPrice}"
        marker.map = naverMap

        infoWindow.open(marker)
    }
}
