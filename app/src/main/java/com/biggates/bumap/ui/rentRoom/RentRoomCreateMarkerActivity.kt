package com.biggates.bumap.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ui.rentRoom.RentRoomInfoActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_rent_room_create_marker.*

class RentRoomCreateMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    lateinit var mapFragment : MapFragment
    lateinit var newMarker : Marker
    var REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_room_create_marker)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        var options = NaverMapOptions()
            .mapType(NaverMap.MapType.Basic)
            .zoomControlEnabled(false)
            .locationButtonEnabled(true)
            .camera(CameraPosition(LatLng(36.839533958, 127.1846484710), 15.0))

        val fm = supportFragmentManager
        mapFragment = fm.findFragmentById(R.id.rent_room_map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.rent_room_map, it).commit()
            }

        markerCreateAllow.setOnClickListener {
            startActivityForResult(Intent(this,RentRoomInfoActivity::class.java)
                .putExtra("lat",newMarker.position.latitude)
                .putExtra("lng",newMarker.position.longitude),REQUEST_CODE)
        }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        newMarker = Marker()
        newMarker.icon = MarkerIcons.BLACK
        newMarker.width= MyUtil.Dp2Px(applicationContext, 15)
        newMarker.height= MyUtil.Dp2Px(applicationContext, 20)
        newMarker!!.position = LatLng(
            36.839533958,
            127.1846484710
        )
        newMarker.map = naverMap
        
        var markerCreateListener = NaverMap.OnCameraChangeListener { reason, animated ->
            if(reason == CameraUpdate.REASON_GESTURE){
                val cameraPosition = naverMap.cameraPosition
                var latlng = LatLng(
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude
                )
                newMarker!!.position = latlng
            }
        }

        naverMap.addOnCameraChangeListener(markerCreateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 200) finish()
    }

}
