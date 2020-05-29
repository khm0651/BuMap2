package com.example.bumap.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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
    var total_list : HashMap<String,String> = HashMap()
    var selectMarker : Marker? = null

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
                    var str = ""
                    if(dataSnapshot?.key.toString().equals("high")) building.high = dataSnapshot?.value as String
                    if(dataSnapshot?.key.toString().equals("low")) building.low = dataSnapshot?.value as String
                    if(dataSnapshot?.key.toString().equals("name")) {
                        building.name = dataSnapshot?.value as String
                    }
                    if(dataSnapshot?.key.toString().equals("location")) building.location = dataSnapshot?.getValue(Location::class.java) as Location

                    if (dataSnapshot?.key.toString().equals("floor")) {
                            var floor = Floor()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                var roomNumber = RoomNumber()
                                var floor_num = dataSnapshot.key.toString()
                                dataSnapshot?.children?.forEach{dataSnapshot:DataSnapshot ->
                                    roomNumber.room.set(dataSnapshot.key.toString(),dataSnapshot.getValue(Room::class.java) as Room)
                                    str += (dataSnapshot.getValue(Room::class.java) as Room).name+"+"
                                    str+= dataSnapshot.key.toString()+"+"
                                    str+=placeName+"+"+floor_num+"층,"
                                }
                                floor.roomNumber.set(
                                    dataSnapshot.key.toString(),
                                    roomNumber
                                )

                                total_list.set(dataSnapshot?.key.toString(),str.substring(0,str.length-1))
                                str =""
                            }
                            building.floor.set(dataSnapshot?.key.toString(), floor)

                        }



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
        var view_all :Button = findViewById(R.id.view_all)

        var adapterListener = AdapterView.OnItemClickListener{parent, view, position, id ->
            var selectedItem = parent.getItemAtPosition(position) as String
            var split = selectedItem.split("+")
            var roomName = split[0]
            var floorNum = split[3].substring(0,split[3].length-1)
            var roomNum = split[1]

            for(i in floor_maker.get(selectFloor)!!.room.keys){
                if(selectMarker != null && selectMarker!!.equals(floor_maker.get(selectFloor)!!.room.get(i)))continue
                floor_maker.get(selectFloor)!!.room.get(i)?.map =null

            }
            isMarkerOn=false
            if(selectMarker == null){
                selectMarker = floor_maker.get(floorNum)!!.room.get(roomNum) as Marker
                selectMarker!!.map=naverMap
                infoWindow.open(selectMarker!!)

            }else{
                if(selectMarker!!.equals(floor_maker.get(floorNum)!!.room.get(roomNum)) && selectMarker!!.map == naverMap){
                    selectMarker!!.map=null
                    infoWindow.close()
                }else if(selectMarker!!.equals(floor_maker.get(floorNum)!!.room.get(roomNum)) && selectMarker!!.map == null){
                    selectMarker!!.map=naverMap
                    infoWindow.open(selectMarker!!)
                }else{
                    selectMarker!!.map=null
                    selectMarker = floor_maker.get(floorNum)!!.room.get(roomNum) as Marker
                    selectMarker!!.map=naverMap
                    infoWindow.open(selectMarker!!)
                }

            }

        }

        view_all.setOnClickListener { v:View ->
            var showAll = arrayListOf<String>()
            var sort = arrayListOf<String>()
            for(i in total_list.keys){
                sort.add(i)
            }
            for(i in sort.indices){
                if(sort.get(i)[0].toString().equals("B")){
                    sort.add(0,sort.get(i))
                    sort.removeAt(sort.size-1)
                }
            }
            for(i in sort.indices){
                (total_list.get(sort.get(i))?.split(",") as ArrayList<String>).forEach { s ->
                    showAll.add(s)
                }
            }

            room_list.adapter = HBaseAdapter(this@Introduce,showAll)
            room_list.onItemClickListener = adapterListener
        }

        var showFloor : ArrayList<String> = arrayListOf<String>()
        total_list.get(selectFloor)!!.split(",").forEach { s ->
            showFloor.add(s)
        }
        room_list.onItemClickListener = adapterListener
        room_list.adapter = HBaseAdapter(this@Introduce,showFloor)

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
                    marker.width =40
                    marker.height=60
                    roomMaker.room.set(roomNumber,marker)

                }
                showFloor = arrayListOf()
                total_list.get(selectFloor)!!.split(",").forEach { s->
                    showFloor.add(s)
                }
                room_list.adapter=HBaseAdapter(this@Introduce,showFloor)
                room_list.onItemClickListener = adapterListener
                room_list.deferNotifyDataSetChanged()
            }else{
                for(roomNumber in building.floor.get("floor")?.roomNumber?.get(floor)!!.room.keys){
                    Log.d("test",roomNumber);
                    var marker = Marker()
                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                    marker.onClickListener = listener
                    marker.width =40
                    marker.height=60
                    roomMaker.room.set(roomNumber,marker)
                }
            }
            floor_maker.set(floor,roomMaker)
        }

        for(i in building.high.toInt() downTo building.low.toInt()){
            if(i == 0) continue
            var floor_btn : Button = Button(this)
            floor_btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            var floor:String
            if(i<0){
                floor_btn.setText("B"+i.absoluteValue.toString())
                floor = "B"+i.absoluteValue.toString()
            } else {
                floor_btn.setText(i.toString())
                floor=i.toString()
            }
            floor_layout.addView(floor_btn)
            floor_btn_arr.set(i.toString(),floor_btn)
            if(floor_maker.containsKey(floor) ){
                floor_btn.setOnClickListener{v: View? ->
                    var clickFloor :String =floor_btn.text.toString()
                    if(selectMarker!=null) { //선택한 마커가 있는경우 끄기
                        selectMarker!!.map=null
                    }

                    if(isMarkerOn && floor_btn.text.equals(selectFloor) && floor_maker.containsKey(clickFloor)){ // 마커 끄기
                        for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                            floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                        }
                        isMarkerOn=false
                    }else if(!floor_btn.text.equals(selectFloor)){ // 다른층 마커 키기 및 리스트 메뉴 교체
                        if(floor_maker.containsKey(selectFloor)){
                            for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                                floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                            }
                        }

                        for(roomNumber in floor_maker.get(floor_btn.text.toString())!!.room.keys){
                            floor_maker.get(floor_btn.text.toString())!!.room.get(roomNumber)!!.map=naverMap
                        }

                        selectFloor=floor_btn.text.toString()
                        isMarkerOn=true

                    }else{ //마커 끈 상태에서 마커 키기
                        if(floor_maker.containsKey(clickFloor)){
                            for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                                floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=naverMap
                            }
                            isMarkerOn=true
                        }
                    }
                    showFloor = arrayListOf()
                    total_list.get(selectFloor)!!.split(",").forEach { s->
                        showFloor.add(s)
                    }
                    room_list.adapter=HBaseAdapter(this@Introduce,showFloor)
                    room_list.onItemClickListener = adapterListener
                }
            }else{
                floor_btn.setOnClickListener{v: View? ->
                    var clickFloor :String =floor_btn.text.toString()

                    if(selectMarker!=null) { //선택한 마커가 있는경우 끄기
                        selectMarker!!.map=null
                    }

                    if(isMarkerOn && floor_btn.text.equals(selectFloor)){ // 마커 끄기
                        for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                            floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                        }
                        isMarkerOn=false
                    }else if(!floor_btn.text.equals(selectFloor)){ // 다른층 마커 키기 및 리스트 메뉴 교체
                        if(floor_maker.containsKey(clickFloor)){
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
                                floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=null
                            }
                            selectFloor=floor_btn.text.toString()
                            isMarkerOn=false
                        }


                    }else{ //마커 끈 상태에서 마커 키기
                        if(floor_maker.containsKey((clickFloor))){
                            for (roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                                floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=naverMap
                            }
                            isMarkerOn=true
                        }
                    }

                    room_list.adapter=HBaseAdapter(this@Introduce, arrayListOf())

                }
            }

        }



    }
}
