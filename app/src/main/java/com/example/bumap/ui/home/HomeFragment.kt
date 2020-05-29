package com.example.bumap.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.bumap.R
import com.example.bumap.ui.gallery.GalleryViewModel
import com.google.firebase.database.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.InfoWindow.DefaultTextAdapter
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf


class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    var markerList:HashMap<String,Marker> = HashMap()
    var markerFlag:Boolean = false;
//    var buildingArr : HashMap<String,Building> = HashMap()
    var buildingArr : HashMap<String,HashMap<String,Location>> = HashMap()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false);
        val fm = childFragmentManager
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(36.840167531007694, 127.18453879140225), 17.5))
            .mapType(NaverMap.MapType.Basic)
            .zoomControlEnabled(false)
            .locationButtonEnabled(true)
            //.camera(CameraPosition(LatLng(36.839533958, 127.1846484710), 15.0))
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference


                myRef.addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { dataSnapshot:DataSnapshot? ->
                            var d_name : String =dataSnapshot?.key.toString()
                            var name : String =""
                            var location : Location = Location()
                            dataSnapshot?.children?.forEach { dataSnapshot:DataSnapshot->
                                if(dataSnapshot.key.toString().equals("name")){
                                    name = dataSnapshot.value.toString()
                                }else if(dataSnapshot.key.toString().equals("location")){
                                    location = dataSnapshot.getValue(Location::class.java) as Location
                                }
                            }
                            buildingArr.set(d_name, hashMapOf(name to location))
                        }
                        mapFragment.getMapAsync(this@HomeFragment);
                    }

                    override fun onCancelled(dataSnapshot: DatabaseError) {

                    }

                })





//        mapFragment.getMapAsync(this@HomeFragment);
        return view;









    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        val context = context
        val infoWindow = InfoWindow()

        infoWindow.adapter = object : DefaultTextAdapter(context!!) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker!!.tag.toString().split("-").toTypedArray()[0]
            }
        }


//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("TruthBuilding")
//        myRef.child("high").setValue("4")
//        myRef.child("low").setValue("-1")
//        myRef.child("location").child("lat").setValue("36.840167531007694")
//        myRef.child("location").child("lng").setValue("127.18453879140225")
//
//        myRef.child("floor").child("1").child("101").child("name").setValue("시설관리 처장실")
//        myRef.child("floor").child("1").child("101").child("location").child("lat").setValue("36.840544")
//        myRef.child("floor").child("1").child("101").child("location").child("lng").setValue("127.184800")
//
//        myRef.child("floor").child("1").child("102").child("name").setValue("시설관리처")
//        myRef.child("floor").child("1").child("102").child("location").child("lat").setValue("36.840514")
//        myRef.child("floor").child("1").child("102").child("location").child("lng").setValue("127.184770")
//
//        myRef.child("floor").child("1").child("103").child("name").setValue("관리본부장실")
//        myRef.child("floor").child("1").child("103").child("location").child("lat").setValue("36.840484")
//        myRef.child("floor").child("1").child("103").child("location").child("lng").setValue("127.184740")
//
//        myRef.child("floor").child("1").child("104").child("name").setValue("교무처장실 / 커리큘럼인증센터")
//        myRef.child("floor").child("1").child("104").child("location").child("lat").setValue("36.840214")
//        myRef.child("floor").child("1").child("104").child("location").child("lng").setValue("127.184740")
//
//        myRef.child("floor").child("1").child("105").child("name").setValue("여학생 회복실")
//        myRef.child("floor").child("1").child("105").child("location").child("lat").setValue("36.840084")
//        myRef.child("floor").child("1").child("105").child("location").child("lng").setValue("127.184420")
//
//        myRef.child("floor").child("1").child("106").child("name").setValue("보건실 / 장애학생휴게실")
//        myRef.child("floor").child("1").child("106").child("location").child("lat").setValue("36.840014")
//        myRef.child("floor").child("1").child("106").child("location").child("lng").setValue("127.184360")
//
//        myRef.child("floor").child("1").child("107").child("name").setValue("서점")
//        myRef.child("floor").child("1").child("107").child("location").child("lat").setValue("36.839824")
//        myRef.child("floor").child("1").child("107").child("location").child("lng").setValue("127.184200")
//
//        myRef.child("floor").child("1").child("108").child("name").setValue("우편취급국")
//        myRef.child("floor").child("1").child("108").child("location").child("lat").setValue("36.839744")
//        myRef.child("floor").child("1").child("108").child("location").child("lng").setValue("127.184340")
//
//        myRef.child("floor").child("1").child("109").child("name").setValue("안경점")
//        myRef.child("floor").child("1").child("109").child("location").child("lat").setValue("36.839774")
//        myRef.child("floor").child("1").child("109").child("location").child("lng").setValue("127.184370")
//
//        myRef.child("floor").child("1").child("toilet-2").child("name").setValue("화장실")
//        myRef.child("floor").child("1").child("toilet-2").child("location").child("lat").setValue("36.839804")
//        myRef.child("floor").child("1").child("toilet-2").child("location").child("lng").setValue("127.184400")
//
//        myRef.child("floor").child("1").child("110").child("name").setValue("대외협력처 창고")
//        myRef.child("floor").child("1").child("110").child("location").child("lat").setValue("36.840014")
//        myRef.child("floor").child("1").child("110").child("location").child("lng").setValue("127.184570")
//
//        myRef.child("floor").child("1").child("112").child("name").setValue("뷰리서점")
//        myRef.child("floor").child("1").child("112").child("location").child("lat").setValue("36.840084")
//        myRef.child("floor").child("1").child("112").child("location").child("lng").setValue("127.184620")
//
//        myRef.child("floor").child("1").child("113").child("name").setValue("교무처장실 / 커리큘럼인증센터")
//        myRef.child("floor").child("1").child("113").child("location").child("lat").setValue("36.840214")
//        myRef.child("floor").child("1").child("113").child("location").child("lng").setValue("127.184740")
//
//        myRef.child("floor").child("1").child("toilet").child("name").setValue("화장실")
//        myRef.child("floor").child("1").child("toilet").child("location").child("lat").setValue("36.840284")
//        myRef.child("floor").child("1").child("toilet").child("location").child("lng").setValue("127.184800")
//
//        myRef.child("floor").child("1").child("114").child("name").setValue("당직실")
//        myRef.child("floor").child("1").child("114").child("location").child("lat").setValue("36.840414")
//        myRef.child("floor").child("1").child("114").child("location").child("lng").setValue("127.184890")
//
//        myRef.child("floor").child("1").child("115").child("name").setValue("에스원상황실")
//        myRef.child("floor").child("1").child("115").child("location").child("lat").setValue("36.840444")
//        myRef.child("floor").child("1").child("115").child("location").child("lng").setValue("127.184920")
//
//        myRef.child("floor").child("1").child("116").child("name").setValue("방재실")
//        myRef.child("floor").child("1").child("116").child("location").child("lat").setValue("36.840474")
//        myRef.child("floor").child("1").child("116").child("location").child("lng").setValue("127.184950")



        val listener = Overlay.OnClickListener { overlay: Overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) { // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
                infoWindow.onClickListener = Overlay.OnClickListener { o: Overlay? ->
                    val intent = Intent(context,Introduce::class.java)
                    var split : ArrayList<String> = marker.tag.toString().split("-") as ArrayList<String>
                    intent.putExtra("lat",marker.position.latitude!!);
                    intent.putExtra("lng",marker.position.longitude!!);
                    intent.putExtra("placename", split[0]);
                    intent.putExtra("d_name",split[1])
                    Log.d("fire-base",split[0])
                    Log.d("fire-base",split[1])
                    startActivity(intent)

                    true
                }
            } else { // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }

        for (k in buildingArr) {
            val marker = Marker()
            Log.d("fire-base",k.value.keys.first())
            marker.position = LatLng(k.value.get(k.value.keys.first().toString())?.lat!!.toDouble(), k.value.get(k.value.keys.first().toString())?.lng!!.toDouble())
            marker.tag = k.value.keys.first().toString() + "-" +k.key
            marker.map = naverMap
            marker.onClickListener = listener
            markerList.set(k.key,marker)
        }


        var marker = Marker()
//        marker.position = LatLng(36.840084, 127.184620)
//        marker.map = naverMap
//        marker.tag = "뷰리서점"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.840014, 127.184570)
//        marker.map = naverMap
//        marker.tag = "대외협력처 창고"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839944, 127.184520)
//        marker.map = naverMap
//        marker.tag = "신한은행"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839804, 127.184400)
//        marker.map = naverMap
//        marker.tag = "화장실"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839774, 127.184370)
//        marker.map = naverMap
//        marker.tag = "안경점"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839744, 127.184340)
//        marker.map = naverMap
//        marker.tag = "우편취급국"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839824, 127.184200)
//        marker.map = naverMap
//        marker.tag = "서점"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839924, 127.184250)
//        marker.map = naverMap
//        marker.tag = "복사기"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.840014, 127.184360)
//        marker.map = naverMap
//        marker.tag = "보건실 / 장애학생휴게실"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.840084, 127.184420)
//        marker.map = naverMap
//        marker.tag = "여학생 회복실"
//        marker.onClickListener = listener
//
//
//
        marker = Marker()
        marker.position = LatLng(36.840284, 127.184600)
        marker.map = naverMap
        marker.tag = "백석정신아카데미 기독교대학실천원"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840344, 127.184650)
        marker.map = naverMap
        marker.tag = "기독교대학실천 원장실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840484, 127.184740)
        marker.map = naverMap
        marker.tag = "화장실"
        marker.onClickListener = listener

//        marker = Marker()
//        marker.position = LatLng(36.840514, 127.184770)
//        marker.map = naverMap
//        marker.tag = "시설관리처"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.840544, 127.184800)
//        marker.map = naverMap
//        marker.tag = "시설관리 처장실"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.840474, 127.184950)
//        marker.map = naverMap
//        marker.tag = "방재실"
//        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840444, 127.184920)
        marker.map = naverMap
        marker.tag = "학사부총장실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840354, 127.184850)
        marker.map = naverMap
        marker.tag = "백석정신아카데미"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840284, 127.184800)
        marker.map = naverMap
        marker.tag = "기독교대학실천원 연구위원실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840214, 127.184740)
        marker.map = naverMap
        marker.tag = "백석정신아카데미 부총재실"
        marker.onClickListener = listener

//        marker = Marker()
//        marker.position = LatLng(36.83972175, 127.182791)
//        marker.map = naverMap
//        marker.tag = "B101"
//        marker.onClickListener = listener
//
//
//
//        marker = Marker()
//        marker.position = LatLng(36.839643, 127.182931)
//        marker.map = naverMap
//        marker.tag = "창고"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839928, 127.182841)
//        marker.map = naverMap
//        marker.tag = "103"
//        marker.onClickListener = listener






        markerBtn.setOnClickListener{ v:View->
            if(!markerFlag){
                for(k in markerList){
                    k.value.map=null;
                }
                markerFlag=true;
            }else{
                for(k in markerList){
                    k.value.map=naverMap;
                }
                markerFlag=false;
            }

        }
        naverMap.setOnMapClickListener { point, coord ->
            Log.d("coord", "${coord.latitude.toFloat()}, ${coord.longitude.toFloat()}")
        }


    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
