package com.biggates.bumap.ui.rentRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.google.firebase.database.FirebaseDatabase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_rent_room_info.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class RentRoomInfoActivity : AppCompatActivity(), OnMapReadyCallback {

    var lat by Delegates.notNull<Double>()
    var lng by Delegates.notNull<Double>()
    var clickListener = View.OnClickListener {

        when{
            TextUtils.isEmpty(num_edit.text) -> Toast.makeText(this,"기준 인원을 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(place_edit.text) -> Toast.makeText(this,"건물명을 입력해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(year_price_edit.text) && TextUtils.isEmpty(half_year_price_edit.text) -> Toast.makeText(this,"년세 또는 반년세를 입력해주세요",Toast.LENGTH_SHORT).show()

            else -> {
                btn_ok.setOnClickListener(null)
                registerRentRoom()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_room_info)

        lat = intent.getDoubleExtra("lat",0.0)
        lng = intent.getDoubleExtra("lng",0.0)
        var options = NaverMapOptions()
            .mapType(NaverMap.MapType.Basic)
            .zoomControlEnabled(false)
            .locationButtonEnabled(false)
            .camera(CameraPosition(LatLng(lat, lng), 15.0))

        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.rent_room_info_map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.rent_room_info_map, it).commit()
            }

        btn_ok.setOnClickListener (clickListener)

        btn_cancel.setOnClickListener {
            finish()
        }
        mapFragment.getMapAsync(this)
    }

    private fun registerRentRoom() {
        var fomatter = SimpleDateFormat("yyyy-MM-dd")
        var fomatter2 = SimpleDateFormat("yyyyMMddHHmmss")
        var calendar = Calendar.getInstance()
        var map = hashMapOf<String,String>()
        var id = "${fomatter2.format(calendar.time)}-${place_edit.text}"
        map.put("buildingName",place_edit.text.toString())
        map.put("yearPrice",year_price_edit.text.toString())
        map.put("halfYearPrice",half_year_price_edit.text.toString())
        map.put("date",fomatter.format(calendar.time))
        map.put("lat",lat.toString())
        map.put("lng",lng.toString())
        map.put("id",id)
        map.put("num",num_edit.text.toString())

        FirebaseDatabase.getInstance().reference.child("rentRoomRequest").child(id).setValue(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(applicationContext,"등록 요청 완료",Toast.LENGTH_SHORT).show()
                setResult(200)
                finish()
            }else{
                btn_ok.setOnClickListener(clickListener)
                Toast.makeText(applicationContext,"서버 오류",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {

        var marker = Marker()
        marker.position = LatLng(lat,lng)
        marker.icon = MarkerIcons.BLACK
        marker.map = naverMap
        marker.width= MyUtil.Dp2Px(applicationContext, 15)
        marker.height= MyUtil.Dp2Px(applicationContext, 20)
    }
}
