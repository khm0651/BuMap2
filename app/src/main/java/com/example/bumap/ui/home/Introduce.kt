package com.example.bumap.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.example.bumap.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import kotlinx.android.synthetic.main.activity_introduce.*
import kotlin.math.absoluteValue
class RoomMaker {
    var room : HashMap<String,Marker> = HashMap()
}
class Introduce : FragmentActivity(), OnMapReadyCallback {

    var building =Building()
    var floor_btn_arr : HashMap<String,Button> = HashMap()
    var floor_maker : HashMap<String,RoomMaker> = HashMap()
    var selectFloor = "1"
    var isMarkerOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.NoTitleBar);
        setContentView(R.layout.activity_introduce)

        val fm = supportFragmentManager
        val lat = intent.getDoubleExtra("lat",0.0)!!
        val lng = intent.getDoubleExtra("lng",0.0)!!
        val placeName = intent.getStringExtra("placename")
        val d_name = intent.getStringExtra("d_name")
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(lat, lng), 17.0))
            .mapType(NaverMap.MapType.Basic)
        val mapFragment = fm.findFragmentById(R.id.map2) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map2, it).commit()
            }
        placeNameText.setText(placeName)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child(d_name)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                dataSnapshot.children.forEach { dataSnapshot: DataSnapshot? ->

                    if (dataSnapshot?.key.toString().equals("floor")) {
                            var floor = Floor()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                var roomNumber = RoomNumber()
                                dataSnapshot?.children?.forEach{dataSnapshot:DataSnapshot ->
                                    roomNumber.room.set(dataSnapshot.key.toString(),dataSnapshot.getValue(Room::class.java) as Room)
                                }
                                floor.roomNumber.set(
                                    dataSnapshot.key.toString(),
                                    roomNumber
                                )

                            }
                            building.floor.set(dataSnapshot?.key.toString(), floor)
                        }
                        if(dataSnapshot?.key.toString().equals("high")) building.high = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("low")) building.low = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("name")) building.name = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("location")) building.location = dataSnapshot?.getValue(Location::class.java) as Location


                }
                mapFragment.getMapAsync(this@Introduce)
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }

        })



    }

    override fun onMapReady(naverMap: NaverMap) {

        var floor_layout : LinearLayout = floor_btn_layout
        val context = applicationContext
        val infoWindow = InfoWindow()

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(context!!) {
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

        for(floor in building.floor.get("floor")?.roomNumber?.keys!!){
;           var roomMaker = RoomMaker()
            if(floor.equals("1")){
                for(roomNumber in building.floor.get("floor")?.roomNumber?.get(floor)!!.room.keys){
                    Log.d("test",roomNumber);
                    var marker = Marker()
                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                    marker.onClickListener = listener
                    marker.map = naverMap
                    roomMaker.room.set(roomNumber,marker)
                }
            }else{
                for(roomNumber in building.floor.get("floor")?.roomNumber?.get(floor)!!.room.keys){
                    Log.d("test",roomNumber);
                    var marker = Marker()
                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                    marker.onClickListener = listener
                    roomMaker.room.set(roomNumber,marker)
                }
            }
            floor_maker.set(floor,roomMaker)
        }

        for(i in building.high.toInt() downTo building.low.toInt()){
            if(i == 0) continue
            var floor_btn : Button = Button(this)
            floor_btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            if(i<0) floor_btn.setText("B"+i.absoluteValue.toString()) else floor_btn.setText(i.toString())
            floor_layout.addView(floor_btn)
            floor_btn_arr.set(i.toString(),floor_btn)
            floor_btn.setOnClickListener{v: View? ->
                if(isMarkerOn && floor_btn.text.equals(selectFloor)){
                    for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                        floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                    }
                    isMarkerOn=false
                }else if(!floor_btn.text.equals(selectFloor)){
                    for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                        floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                    }
                    for(roomNumber in floor_maker.get(floor_btn.text.toString())!!.room.keys){
                        floor_maker.get(floor_btn.text.toString())!!.room.get(roomNumber)!!.map=naverMap
                    }
                    selectFloor=floor_btn.text.toString()
                    isMarkerOn=true
                }else{
                    for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                        floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=naverMap
                    }
                    isMarkerOn=true
                }
            }
        }


    }
}
