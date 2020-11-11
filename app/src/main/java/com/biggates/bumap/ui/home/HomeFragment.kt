package com.biggates.bumap.ui.home

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.BtnKeywordAdapter
import com.biggates.bumap.GpsTracker
import com.biggates.bumap.MainActivity
import com.biggates.bumap.Model.BtnKeyword
import com.biggates.bumap.Model.Location
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.building.Cafe
import com.biggates.bumap.ui.ad.AdActivity
import com.biggates.bumap.ui.createMarker.CreateMarkerActivity
import com.biggates.bumap.ui.introduce.Introduce
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.InfoWindow.DefaultTextAdapter
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.btn_keyword_main.*
import kotlinx.android.synthetic.main.btn_keyword_main.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private var isShowPredictTime = false
    private var isShow = false
    private lateinit var btnKeywordAdapter: BtnKeywordAdapter
    lateinit var mapFragment : MapFragment
    var newMarker: Marker? = null
    var isMarkerLongTouch = false

    var markerList:HashMap<String, Marker> = HashMap()
    var cafeMarkerList : HashMap<String,Marker> = hashMapOf()
    var markerFlag:Boolean = false;
//    var buildingArr : HashMap<String,Building> = HashMap()
    var buildingArr : HashMap<String, HashMap<String, Location>> = HashMap()
    private lateinit var gpsTracker : GpsTracker

    private var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {

            if(isShow){
            (context as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
            view!!.predict_time_layout.visibility = View.GONE
            view!!.predict_tiem_text.text = ""
            isShow=false
            }else{
                startActivity(Intent(context, AdActivity::class.java))
            }

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false);
        val fm = childFragmentManager
        var options = NaverMapOptions()
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val btnKeywordLayout = view.btn_keyword_layout
        //검색 플로팅 키워드 버튼
        val btnKeywordList = arrayListOf<BtnKeyword>()
        for (title in resources.getStringArray(R.array.keyword_list)){
            btnKeywordList.add(BtnKeyword(title))
        }

        for (keyword in btnKeywordList){
            val button = Chip(context).apply {
                text = keyword.title
                elevation = 10F //그림자 설정
                setChipBackgroundColorResource(R.color.white)
                setChipIconResource(R.drawable.bus)
                chipIconSize = 50F
                chipStartPadding = 30F
                setOnClickListener {
                    Toast.makeText(context,this.text.toString(),Toast.LENGTH_SHORT).show()
                    when(this.text.toString()){
                        "카페" -> {
                            for(cafe in cafeMarkerList){
                                if(cafe.value.map != naverMap) cafe.value.map = naverMap
                                else cafe.value.map = null
                            }
                        }
                    }
                }
            }
            btnKeywordLayout.addView(button)
        }

//        btnKeywordAdapter = BtnKeywordAdapter(btnKeywordList)
//        view.btn_keyword_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)
//        view.btn_keyword_recyclerview.adapter = btnKeywordAdapter


        if(arguments!!.isEmpty){
            options = NaverMapOptions()
                .mapType(NaverMap.MapType.Basic)
                .zoomControlEnabled(false)
                .locationButtonEnabled(true)
                .camera(CameraPosition(LatLng(36.839533958, 127.1846484710), 15.0))
        }else{
            options = NaverMapOptions()
                .camera(
                    CameraPosition(
                        LatLng(
                            arguments!!.getString("lat")!!.toDouble(), arguments!!.getString(
                                "lng"
                            )!!.toDouble()
                        ), 17.5
                    )
                )
                .mapType(NaverMap.MapType.Basic)
                .zoomControlEnabled(false)
                .locationButtonEnabled(true)
            isShowPredictTime = arguments!!.getBoolean("isShowPredictTime")

        }

        mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }



        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("BuildingInfo")


                myRef.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach { dataSnapshot: DataSnapshot? ->
                            var d_name: String = dataSnapshot?.key.toString()
                            var name: String = ""
                            var location: Location = Location()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                if (dataSnapshot.key.toString().equals("name")) {
                                    name = dataSnapshot.value.toString()
                                } else if (dataSnapshot.key.toString().equals("location")) {
                                    location =
                                        dataSnapshot.getValue(Location::class.java) as Location
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

        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow

        if(isShowPredictTime){

            if (!checkLocationServicesStatus()) {

                showDialogForLocationServiceSetting();
            }else {

                var permissionState = checkRunTimePermission()
                if(permissionState){
                    gpsTracker = GpsTracker(context!!)
                    var myLat = gpsTracker.getLatitude()
                    var myLng = gpsTracker.getLongitude()
                    isShow = true
                    showPredictTimeLayout(myLat!!, myLng!!)
                }else{
                    var toast = Toast.makeText(context, "GPS권한 거부로인해 서비스 사용불가.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, Gravity.CENTER, 450)
                    toast.show()
                }
            }


        }

        val listener = Overlay.OnClickListener { overlay: Overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) { // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.open(marker)
                infoWindow.onClickListener = Overlay.OnClickListener { o: Overlay? ->
                    val intent = Intent(
                        context,
                        Introduce::class.java
                    )
                    var split : ArrayList<String> = marker.tag.toString().split("-") as ArrayList<String>
                    intent.putExtra("lat", marker.position.latitude!!);
                    intent.putExtra("lng", marker.position.longitude!!);
                    intent.putExtra("placename", split[0]);
                    intent.putExtra("d_name", split[1])
                    startActivity(intent)

                    true
                }

            } else { // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }

        for (k in buildingArr) {
            var isOpen = false
            if(!arguments!!.isEmpty){
                if(arguments!!.getString("lat")!!.toDouble().equals(
                        k.value.get(
                            k.value.keys.first().toString()
                        )?.lat!!.toDouble()
                    )
                    && arguments!!.getString("lng")!!.toDouble().equals(
                        k.value.get(
                            k.value.keys.first().toString()
                        )?.lng!!.toDouble()
                    )){
                        isOpen = true
                    }

            }
            val marker = Marker()
            marker.position = LatLng(
                k.value.get(k.value.keys.first().toString())?.lat!!.toDouble(), k.value.get(
                    k.value.keys.first().toString()
                )?.lng!!.toDouble()
            )
            marker.tag = k.value.keys.first().toString() + "-" +k.key
            marker.map = naverMap
            marker.onClickListener = listener
            marker.width= MyUtil.Dp2Px(context, 15)
            marker.height= MyUtil.Dp2Px(context, 20)
            markerList.set(k.key, marker)
            if(isOpen){
                infoWindow.open(marker)
                infoWindow.onClickListener = Overlay.OnClickListener { o: Overlay? ->
                    val intent = Intent(
                        context,
                        Introduce::class.java
                    )
                    var split : ArrayList<String> = marker.tag.toString().split("-") as ArrayList<String>
                    intent.putExtra("lat", marker.position.latitude!!);
                    intent.putExtra("lng", marker.position.longitude!!);
                    intent.putExtra("placename", split[0]);
                    intent.putExtra("d_name", split[1])
                    startActivity(intent)

                    true
                }
            }

        }

        for ( cafe in Cafe.cafeBuildings.value!!){
            var marker = Marker()
            marker.position = LatLng(cafe.value.location.lat.toDouble(),cafe.value.location.lng.toDouble())
            marker.width= MyUtil.Dp2Px(context, 15)
            marker.height= MyUtil.Dp2Px(context, 20)
            marker.map = null
            cafeMarkerList.put(cafe.value.name,marker)
        }

        markerCreate.setOnClickListener {
            val cameraPosition = naverMap.cameraPosition
            var latlng = LatLng(
                cameraPosition.target.latitude,
                cameraPosition.target.longitude
            )
            newMarker = Marker()
            newMarker!!.position = latlng
            newMarker!!.width= MyUtil.Dp2Px(context, 15)
            newMarker!!.height= MyUtil.Dp2Px(context, 20)
            newMarker!!.map = naverMap
            newMarker!!.icon = MarkerIcons.BLACK
            naverMap.addOnCameraChangeListener (markerCreateListener)
            markerCreateAllow.visibility = View.VISIBLE

        }

        markerCreateAllow.setOnClickListener {
            startActivityForResult(Intent(context,CreateMarkerActivity::class.java)
                .putExtra("lat",newMarker!!.position.latitude.toString())
                .putExtra("lng",newMarker!!.position.longitude.toString()),CREATE_MARKER_REQUEST_CODE)
        }



        markerBtn.setOnClickListener{ v: View->
            if(!markerFlag){
                for(k in markerList){
                    k.value.map=null;
                }
                markerFlag=true;
                v.markerImg.setImageResource(R.drawable.markeroff)
                v.markerText.text = "OFF"
            }else{
                for(k in markerList){
                    k.value.map=naverMap;
                }
                markerFlag=false;
                v.markerImg.setImageResource(R.drawable.markeron)
                v.markerText.text = "ON"
            }

        }
        naverMap.setOnMapClickListener { point, coord ->
            Log.d("coord", "${coord.latitude.toFloat()}, ${coord.longitude.toFloat()}")

        }


    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions,
                grantResults
            )) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

        if (requestCode === PERMISSIONS_REQUEST_CODE && grantResults.size === REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true


            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity as MainActivity,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        activity as MainActivity,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        context,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun checkRunTimePermission(): Boolean {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission: Int = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission: Int = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
            return true

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity as MainActivity,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.

                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                var a = ActivityCompat.requestPermissions(
                    activity as MainActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    activity as MainActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
            return false
        }
    }

    private fun showPredictTimeLayout(myLat: Double, myLng: Double) {

        (context as MainActivity).app_bar_layout_main.visibility = View.GONE
        view!!.predict_time_layout.visibility = View.VISIBLE
        view!!.predict_tiem_close.setOnClickListener {
            (context as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
            view!!.predict_time_layout.visibility = View.GONE
            view!!.predict_tiem_text.text = ""
            isShow=false
        }
        var targetLat = arguments!!.getString("lat")!!.toDouble()
        var targetLng = arguments!!.getString("lng")!!.toDouble()
        var myLocation = android.location.Location("myLocation")
        myLocation.latitude = myLat!!
        myLocation.longitude = myLng!!
        var targetLocation = android.location.Location("targetLocation")
        targetLocation.latitude = targetLat
        targetLocation.longitude = targetLng
        var distance = targetLocation.distanceTo(myLocation) /1000
        var predictTime = ((distance/3)*60).toInt()
        if(predictTime-1 < 0){
            view!!.predict_tiem_text.text = "목적지까지 예상 소요시간 : 0~${predictTime+1}분"
        }else{
            view!!.predict_tiem_text.text = "목적지까지 예상 소요시간 : ${predictTime-1}~${predictTime+1}분"
        }


    }

    private fun showDialogForLocationServiceSetting() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
                앱을 사용하기 위해서는 위치 서비스가 필요합니다.
                위치 설정을 수정하실래요?
                """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, id: Int) {
                val callGPSSettingIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            }
        })
        builder.setNegativeButton("취소", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id: Int) {
                dialog.cancel()
            }
        })
        builder.create().show()
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager: LocationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        checkRunTimePermission()
                        return
                    }
                }

            CREATE_MARKER_REQUEST_CODE -> {
                if(resultCode == 200){
                    Toast.makeText(context,"등록 완료",Toast.LENGTH_SHORT).show()
                    newMarker = null
                    naverMap.removeOnCameraChangeListener(markerCreateListener)
                    markerCreateAllow.visibility = View.GONE
                }
            }

        }
    }




    private var markerCreateListener = NaverMap.OnCameraChangeListener { reason, animated ->
        if(reason == CameraUpdate.REASON_GESTURE){
            val cameraPosition = naverMap.cameraPosition
            var latlng = LatLng(
                cameraPosition.target.latitude,
                cameraPosition.target.longitude
            )
            newMarker!!.position = latlng
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val PERMISSIONS_REQUEST_CODE = 100
        private const val CREATE_MARKER_REQUEST_CODE = 300
    }


}
