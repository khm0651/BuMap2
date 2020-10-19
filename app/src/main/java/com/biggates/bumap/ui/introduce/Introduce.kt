package com.biggates.bumap.ui.introduce

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Adapter.IntroduceAdapter
import com.biggates.bumap.Adapter.IntroduceBtnAdapter
import com.biggates.bumap.Model.*
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.building.BuBuilding
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
import java.util.*
import kotlin.Comparator
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

    @RequiresApi(Build.VERSION_CODES.N)
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


        building = BuBuilding.buBuilding.value!!.get(d_name)!!
        BuBuilding.buBuilding.value!!.get(d_name!!)!!.floor.forEach { floorKey, f ->
            var str = ""
            f.roomNumber.forEach { roomKey, r ->
                str += "${r.name}+${roomKey}+${placeName}+${floorKey}층,"
            }
            total_list.put(floorKey,str.substring(0,str.length-1))
            str =""

        }

        introduce = this@Introduce
        mapFragment.getMapAsync(this@Introduce)

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
                    for(floor in building.floor.keys){
                        var roomMaker =
                            RoomMaker()

                        if(floor.equals("1")){
                            for(roomNumber in building.floor.get(floor)!!.roomNumber.keys){
                                if(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name == "화장실"){
                                    var marker = Marker()
                                    marker.tag = building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lat.toDouble(), building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.map = naverMap
                                    marker.icon = OverlayImage.fromResource(R.drawable.toilet)
                                    marker.width= MyUtil.Dp2Px(applicationContext,15)
                                    marker.height= MyUtil.Dp2Px(applicationContext,15)
                                    marker.zIndex=-10
                                    roomMaker.room.set(roomNumber,marker)
                                }else{
                                    var marker = Marker()
                                    marker.tag = building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lat.toDouble(), building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.map = naverMap
                                    marker.width= MyUtil.Dp2Px(applicationContext,10)
                                    marker.height= MyUtil.Dp2Px(applicationContext,15)
                                    roomMaker.room.set(roomNumber,marker)
                                }

                            }

                        }else{
                            for(roomNumber in building.floor.get(floor)!!.roomNumber.keys){
                                if(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name == "화장실"){
                                    var marker = Marker()
                                    marker.tag = building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lat.toDouble(), building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.icon = OverlayImage.fromResource(R.drawable.toilet)
                                    marker.width= MyUtil.Dp2Px(applicationContext,15)
                                    marker.height= MyUtil.Dp2Px(applicationContext,15)
                                    marker.zIndex=-10
                                    roomMaker.room.set(roomNumber,marker)
                                }else{
                                    var marker = Marker()
                                    marker.tag = building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.name
                                    marker.position = LatLng(building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lat.toDouble(), building.floor.get(floor)?.roomNumber?.get(roomNumber)!!.location.lng.toDouble())
                                    marker.onClickListener = listener
                                    marker.width= MyUtil.Dp2Px(applicationContext,10)
                                    marker.height= MyUtil.Dp2Px(applicationContext,15)
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

                    sort.sortWith(object : Comparator<String>{
                        override fun compare(o1: String, o2: String): Int {
                            var a = o1
                            var b = o2

                            if(a.startsWith("B") && b.startsWith("B")) return a.compareTo(b)
                            else if (a.startsWith("B")) return 1
                            else if (b.startsWith("B")) return -1
                            else return b.toInt().compareTo(a.toInt())

                        }

                    })
                    for(i in sort.indices){
                        var tempArr = ArrayList<String>()
                        total_list.get(sort.get(i))?.split(",")!!.forEach {
                            tempArr.add(it)
                        }
                        tempArr.sortWith(object : Comparator<String>{
                            override fun compare(o1: String, o2: String): Int {
                                var a = o1.split("+")[1].split("-")[0]
                                var b = o2.split("+")[1].split("-")[0]
                                var a_len = o1.split("+")[1].split("-").size
                                var b_len = o2.split("+")[1].split("-").size

                                if(a.startsWith("B") && b.startsWith("B")){
                                    var a_t = a.substring(a.indexOf("B")+1,a.length)
                                    var b_t = b.substring(a.indexOf("B")+1,b.length)
                                    if(a_t.toInt().compareTo(b_t.toInt()) == 0) return a_len.compareTo(b_len)
                                    return a_t.toInt().compareTo(b_t.toInt())
                                }
                                else{

                                    var regex = "[a-zA-Z?]".toRegex()
                                    if (a.contains(regex) && b.contains(regex)) return a.compareTo(b)
                                    else if(a.contains(regex)) return 1
                                    else if(b.contains(regex)) return -1
                                    if(a.toInt().compareTo(b.toInt()) == 0) return a_len.compareTo(b_len)
                                    return a.toInt().compareTo(b.toInt())

                                }

                            }

                        })

                        for(i in tempArr) showAll.add(i)

                    }



                    introduceAdapter = IntroduceAdapter(this@Introduce,showAll,floor_maker,naverMap,introduce,mapFragment)
                    recyclerView.adapter = introduceAdapter
                }




                var showFloor = arrayListOf<String>()
                total_list.get(selectFloor)!!.split(",").forEach { s->
                    showFloor.add(s)
                }
                showFloor.sortWith(object : Comparator<String>{
                    override fun compare(o1: String, o2: String): Int {
                        var a = o1.split("+")[1].split("-")[0]
                        var b = o2.split("+")[1].split("-")[0]
                        var a_len = o1.split("+")[1].split("-").size
                        var b_len = o2.split("+")[1].split("-").size

                        var regex = "[a-zA-Z?]".toRegex()
                        if (a.contains(regex) && b.contains(regex)) return a.compareTo(b)
                        else if(a.contains(regex)) return 1
                        else if(b.contains(regex)) return -1
                        if(a.toInt().compareTo(b.toInt()) == 0) return a_len.compareTo(b_len)
                        return a.toInt().compareTo(b.toInt())
                    }

                })
                introduceAdapter = IntroduceAdapter(applicationContext, showFloor, floor_maker,naverMap,introduce,mapFragment)
                recyclerView.adapter = introduceAdapter

                if(btn_list.size <=5){
                    var layoutParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
                    layoutParam.addRule(RelativeLayout.CENTER_VERTICAL)
                    layoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    layoutParam.rightMargin = MyUtil.Dp2Px(applicationContext,15)
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
