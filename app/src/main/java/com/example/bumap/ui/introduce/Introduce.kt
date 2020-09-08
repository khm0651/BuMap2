package com.example.bumap.ui.introduce

import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.*
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bumap.Adapter.IntroduceAdapter
import com.example.bumap.Adapter.IntroduceBtnAdapter
import com.example.bumap.Model.*
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
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_introduce.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue
class RoomMaker {
    var room : HashMap<String,Marker> = HashMap()
}
class Introduce : FragmentActivity(), OnMapReadyCallback {

    var building = Building()
    var floor_maker : HashMap<String, RoomMaker> = HashMap()
    var selectFloor = "1"
    var total_list : HashMap<String,String> = HashMap()
    var isFirst : Boolean = true
    lateinit var v:RelativeLayout

    lateinit var btnAdapter : IntroduceBtnAdapter
    lateinit var introduceAdapter : IntroduceAdapter
    lateinit var mapFragment: MapFragment
    lateinit var introduce: Introduce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.NoTitleBar);
        setContentView(R.layout.activity_introduce)
        v = introduce_layout
        val fm = supportFragmentManager
        val lat = intent.getDoubleExtra("lat",0.0)!!
        val lng = intent.getDoubleExtra("lng",0.0)!!
        val placeName = intent.getStringExtra("placename")
        val d_name = intent.getStringExtra("d_name")
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(lat, lng), 17.0))
            .mapType(NaverMap.MapType.Basic)
        mapFragment = fm.findFragmentById(R.id.map2) as MapFragment?
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
                    if(dataSnapshot?.key.toString().equals("location")) building.location = dataSnapshot?.getValue(
                        Location::class.java) as Location

                    if (dataSnapshot?.key.toString().equals("floor")) {
                            var floor = Floor()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                var roomNumber =
                                    RoomNumber()
                                var floor_num = dataSnapshot.key.toString()
                                dataSnapshot?.children?.forEach{dataSnapshot:DataSnapshot ->
                                    roomNumber.room.set(dataSnapshot.key.toString(),dataSnapshot.getValue(
                                        Room::class.java) as Room
                                    )
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
                introduce = this@Introduce
                mapFragment.getMapAsync(this@Introduce)
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }

        })



    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMapReady(naverMap: NaverMap) {

        if(isFirst){
            val infoWindow = InfoWindow()
            var recyclerView : RecyclerView = recycler_view_room_list
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.setHasFixedSize(true)


            var btn_recyclerView : RecyclerView = floor_btn_layout
            var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(applicationContext)
            btn_recyclerView.layoutManager = linearLayoutManager
            btn_recyclerView.setHasFixedSize(true)


            //버튼 생성
            var btn_list : ArrayList<String> = arrayListOf()
            for( i in building.high.toInt() downTo  building.low.toInt()){
                if(i==0) continue
                if(i<0){
                    btn_list.add("B"+i.absoluteValue.toString())
                }else{
                    btn_list.add(i.toString())

                }
            }

            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(applicationContext!!) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker!!.tag.toString()
                }
            }


            btn_recyclerView.scrollToPosition(btn_list.size-1)




            if(total_list.size > 0){

                val listener = Overlay.OnClickListener { overlay: Overlay ->
                    val marker = overlay as Marker
                    if (marker.infoWindow == null) { // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                        infoWindow.open(marker)

                    } else { // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                        infoWindow.close()
                    }
                    true
                }

                if(floor_maker.isEmpty()){
                    for(floor in building.floor.get("floor")?.roomNumber?.keys!!){
                        var roomMaker =
                            RoomMaker()

                        if(floor.equals("1")){
                            for(roomNumber in building.floor.get("floor")?.roomNumber?.get(floor)!!.room.keys){
                                if(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name == "화장실"){
                                    var marker = Marker()
                                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.map = naverMap
                                    marker.icon = OverlayImage.fromResource(R.drawable.toilet)
                                    marker.width=50
                                    marker.height=50
                                    marker.zIndex=-10
                                    roomMaker.room.set(roomNumber,marker)
                                }else{
                                    var marker = Marker()
                                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.map = naverMap
                                    marker.width =40
                                    marker.height=60
                                    roomMaker.room.set(roomNumber,marker)
                                }

                            }

                        }else{
                            for(roomNumber in building.floor.get("floor")?.roomNumber?.get(floor)!!.room.keys){
                                if(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name == "화장실"){
                                    var marker = Marker()
                                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.icon = OverlayImage.fromResource(R.drawable.toilet)
                                    marker.width=50
                                    marker.height=50
                                    marker.zIndex=-10
                                    roomMaker.room.set(roomNumber,marker)
                                }else{
                                    var marker = Marker()
                                    marker.tag = building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lat.toDouble(), building.floor.get("floor")?.roomNumber?.get(floor)!!.room.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.width =40
                                    marker.height=60
                                    roomMaker.room.set(roomNumber,marker)
                                }
                            }
                        }
                        floor_maker.set(floor,roomMaker)
                    }
                }


                view_toilet.setOnClickListener { v:View->
                    floor_maker.forEach { t, u ->
                        u.room.forEach { t, u ->
                            u.map = null
                        }
                    }
                    floor_maker["1"]!!.room.forEach { t, u ->
                        if(t.contains("toilet")){
                            u.map = naverMap
                        }

                    }

                    var showToilet = arrayListOf<String>()
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
                            if(s.startsWith("화장실",0)){
                                showToilet.add(s)
                            }
                        }
                    }

                    introduceAdapter = IntroduceAdapter(this@Introduce,showToilet,floor_maker,naverMap,introduce,mapFragment)
                    recyclerView.adapter = introduceAdapter
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

                    introduceAdapter = IntroduceAdapter(this@Introduce,showAll,floor_maker,naverMap,introduce,mapFragment)
                    recyclerView.adapter = introduceAdapter
                }




                var showFloor = arrayListOf<String>()
                total_list.get(selectFloor)!!.split(",").forEach { s->
                    showFloor.add(s)
                }
                introduceAdapter = IntroduceAdapter(applicationContext, showFloor, floor_maker,naverMap,introduce,mapFragment)
                recyclerView.adapter = introduceAdapter

                if(btn_list.size <=5){
                    var layoutParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
                    layoutParam.addRule(RelativeLayout.CENTER_VERTICAL)
                    layoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    layoutParam.rightMargin = 60
                    btn_recyclerView.layoutParams = layoutParam

                }
                btnAdapter = IntroduceBtnAdapter(applicationContext,btn_list,recyclerView,total_list,
                    floor_maker,mapFragment,naverMap,introduce,btn_recyclerView,btn_list,v)
                btn_recyclerView.adapter = btnAdapter


            } else{
                recyclerView.visibility = View.GONE
                introduce_not_yet_layout.visibility = View.VISIBLE
            }
        }






    }
}
