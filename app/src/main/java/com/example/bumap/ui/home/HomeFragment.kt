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
            .camera(CameraPosition(LatLng(36.83949817622067, 127.18256704147348), 17.5))
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




        return view;




//
//        myRef.child("high").setValue("4")
//        myRef.child("low").setValue("-1")
//        myRef.child("location").child("lat").setValue("36.83949817622067")
//        myRef.child("location").child("lng").setValue("127.18256704147348")




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


//        var marker = Marker()
//        marker.position = LatLng(36.839951, 127.182626)
//        marker.map = naverMap
//        marker.tag = "CCD팀"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839967, 127.182725)
//        marker.map = naverMap
//        marker.tag = "문화사역팀"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839966, 127.182678)
//        marker.map = naverMap
//        marker.tag = "소울메이트"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839907, 127.182573)
//        marker.map = naverMap
//        marker.tag = "???"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839913, 127.182701)
//        marker.map = naverMap
//        marker.tag = "B109"
//        marker.onClickListener = listener
//
//        marker = Marker()
//        marker.position = LatLng(36.839883, 127.182671)
//        marker.map = naverMap
//        marker.tag = "B108"
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
