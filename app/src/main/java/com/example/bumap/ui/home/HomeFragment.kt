package com.example.bumap.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bumap.R
import com.google.firebase.database.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.InfoWindow.DefaultTextAdapter
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf


class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    var buPlace:HashMap<String,LatLng> =
        HashMap<String, LatLng>()
    var placeName = arrayOf(
        "학생복지관",
        "목양관",
        "백석홀",
        "인성관",
        "은혜관",
        "자유관",
        "창조관",
        "백석학술정보관",
        "지혜관",
        "진리관",
        "교수회관",
        "음악관",
        "승리관",
        "생활관",
        "글로벌외식산업관",
        "본부동",
        "체육관",
        "조형관",
        "예술대학동"
    )
    var lat = doubleArrayOf(
        36.84067149455031,
        36.84096804048713,
        36.83949817622067,
        36.83943918986522,
        36.83865921671607,
        36.8385077093117,
        36.83750497408212,
        36.83779665070615,
        36.83875974818527,
        36.840167531007694,
        36.83971621180102,
        36.84012975281361,
        36.84180402931098,
        36.84256247647251,
        36.837169093598945,
        36.83930008181875,
        36.841361075498014,
        36.840873386391095,
        36.8387467774056
    )
    var lng = doubleArrayOf(
        127.18245069150657,
        127.18362033438393,
        127.18256704147348,
        127.1835171044122,
        127.18196470541153,
        127.1831779542112,
        127.18230471070564,
        127.1839869000512,
        127.18429855933977,
        127.18453879140225,
        127.18478214426176,
        127.18528504289549,
        127.1857502675112,
        127.18512338682183,
        127.18493789329511,
        127.18597708221137,
        127.1872479173602,
        127.18844000257769,
        127.187425511696
    )

    var markerList:ArrayList<Marker> = ArrayList()
    var markerFlag:Boolean = false;
    var buildingArr : HashMap<String,Building> = HashMap()

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
                var building =Building()
                dataSnapshot.children.forEach { dataSnapshot: DataSnapshot? ->

                    Log.d("fire-base", "1 "+dataSnapshot?.key.toString())

                    dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                        if (dataSnapshot?.key.toString().equals("floor")) {
                            var floor = Floor()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                floor.room.set(
                                    dataSnapshot.key.toString(),
                                    dataSnapshot.getValue(Room::class.java) as Room
                                )
                            }
                            building.floor.set(dataSnapshot.key.toString(), floor)
                        }
                        if(dataSnapshot?.key.toString().equals("high")) building.high = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("low")) building.low = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("name")) building.name = dataSnapshot?.value as String
                        if(dataSnapshot?.key.toString().equals("location")) building.location = dataSnapshot?.getValue(Location::class.java) as Location
                    }

                    buildingArr.set(building.name,building)
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


//
//        myRef.child("high").setValue("4")
//        myRef.child("low").setValue("-1")
//        myRef.child("location").child("lat").setValue("36.83949817622067")
//        myRef.child("location").child("lng").setValue("127.18256704147348")



        mapFragment.getMapAsync(this);
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
        val uiSettings = naverMap.uiSettings
        val infoWindow = InfoWindow()
        uiSettings.isZoomControlEnabled=false
        uiSettings.isLocationButtonEnabled=true
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
                    intent.putExtra("lat",marker.position.latitude!!);
                    intent.putExtra("lng",marker.position.longitude!!);
                    intent.putExtra("placename",marker.tag.toString().split("-")[0]);
                    Log.d("test",marker.position.latitude.toString());
                    Log.d("test",marker.position.longitude.toString());
                    startActivity(intent)

                    true
                }
            } else { // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }

        for (i in placeName.indices) {
            val marker = Marker()
            marker.position = LatLng(lat[i], lng[i])
            marker.tag = placeName[i] + "-" + Integer.toString(i)
            marker.map = naverMap
            marker.onClickListener = listener
            markerList.add(marker)
            Log.d("test", placeName[i] + "-" + i)
        }


        var marker = Marker()
        marker.position = LatLng(36.839951, 127.182626)
        marker.map = naverMap
        marker.tag = "CCD팀"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.839967, 127.182725)
        marker.map = naverMap
        marker.tag = "문화사역팀"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.839966, 127.182678)
        marker.map = naverMap
        marker.tag = "소울메이트"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.839907, 127.182573)
        marker.map = naverMap
        marker.tag = "???"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.839913, 127.182701)
        marker.map = naverMap
        marker.tag = "B109"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.839883, 127.182671)
        marker.map = naverMap
        marker.tag = "B108"
        marker.onClickListener = listener




        markerBtn.setOnClickListener{ v:View->
            if(!markerFlag){
                for(i in markerList.indices){
                    markerList.get(i).map=null;
                }
                markerFlag=true;
            }else{
                for(i in markerList.indices){
                    markerList.get(i).map=naverMap;
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
