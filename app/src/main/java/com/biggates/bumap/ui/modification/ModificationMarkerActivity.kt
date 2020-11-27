package com.biggates.bumap.ui.modification

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.biggates.bumap.Model.Room
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.building.BuBuilding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_modification_marker.*


@RequiresApi(Build.VERSION_CODES.N)
class ModificationMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    var REQUEST_CODE = 200
    var roomList = arrayListOf<HashMap<String, Room>>()
    var markerList = arrayListOf<Marker>()
    lateinit var roomName : String
    lateinit var roomNumber : String
    lateinit var floor : String
    lateinit var buildingName : String
    lateinit var placeName : String
    var selectMarker : Marker = Marker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_marker)
        roomName = intent.getStringExtra("info").split("+")[0]
        roomNumber = intent.getStringExtra("info").split("+")[1]
        floor = intent.getStringExtra("info").split("+")[2]
        buildingName = intent.getStringExtra("buildingName")
        placeName = intent.getStringExtra("placeName")
        var lat = intent.getDoubleExtra("lat",0.0)
        var lng = intent.getDoubleExtra("lng",0.0)

        val fm = supportFragmentManager
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(lat, lng), 17.0))
            .mapType(NaverMap.MapType.Basic)
            .zoomControlEnabled(false)
        var mapFragment = fm.findFragmentById(R.id.modificationMap) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.modificationMap, it).commit()
            }

        BuBuilding.buBuilding.value?.get(buildingName)!!.floor[floor]!!.roomNumber.forEach { key, room ->
            var map = HashMap<String,Room>()
            map.put(key,room)
            roomList.add(map)
        }

        markerModificationAllow.setOnClickListener {
            startActivityForResult(Intent(applicationContext,ModificationCheckActivity::class.java)
                .putExtra("lat",selectMarker.position.latitude)
                .putExtra("lng",selectMarker.position.longitude)
                .putExtra("buildingName",buildingName)
                .putExtra("placeName",placeName)
                .putExtra("roomName",roomName)
                .putExtra("roomNumber",roomNumber)
                .putExtra("floor",floor),REQUEST_CODE)

        }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.symbolScale = 0f
        val infoWindow = InfoWindow()
        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext!!) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker!!.tag.toString().split("-").toTypedArray()[0]
            }
        }

        val listener = Overlay.OnClickListener { overlay: Overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) { // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)

            } else { // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }

        var markerCreateListener = NaverMap.OnCameraChangeListener { reason, animated ->
            if(reason == CameraUpdate.REASON_GESTURE){
                val cameraPosition = naverMap.cameraPosition
                var latlng = LatLng(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                )
                selectMarker!!.position = latlng
            }
        }

        for(room in roomList){
            var marker = Marker()
            marker.position = LatLng(room[room.keys.first()]!!.location.lat.toDouble(),room[room.keys.first()]!!.location.lng.toDouble())
            marker.tag = "${room[room.keys.first()]!!.name}-${room.keys.first()}"
            marker.map = naverMap
            marker.onClickListener = listener
            if(room[room.keys.first()]!!.name == "화장실"){
                marker.icon = OverlayImage.fromResource(R.drawable.toilet)
                marker.width= MyUtil.Dp2Px(applicationContext,15)
                marker.height= MyUtil.Dp2Px(applicationContext,15)
            }else{
                marker.width= MyUtil.Dp2Px(applicationContext, 15)
                marker.height= MyUtil.Dp2Px(applicationContext, 20)
            }
            if(room.keys.first() == roomNumber) {
                selectMarker.position = LatLng(naverMap.cameraPosition.target.latitude,naverMap.cameraPosition.target.longitude)
                selectMarker.tag = "${room[room.keys.first()]!!.name}-${room.keys.first()}"
                selectMarker.width= MyUtil.Dp2Px(applicationContext, 15)
                selectMarker.height= MyUtil.Dp2Px(applicationContext, 20)
                selectMarker.map = naverMap
                selectMarker.icon = MarkerIcons.BLACK
                marker.alpha = 0.4f
            }
        }

        naverMap.addOnCameraChangeListener(markerCreateListener)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 200){
            finish()
        }
    }
}
