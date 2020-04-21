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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false);
        val fm = childFragmentManager
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(36.839533958, 127.1846484710), 15.0))
            .mapType(NaverMap.MapType.Basic)
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("WelfareBuilding")
        myRef.child("high").setValue("5")
        myRef.child("low").setValue("-1")
        myRef.child("floor").child("B1").child("B105").child("name").setValue("복사점")
        myRef.child("floor").child("B1").child("B105").child("location").child("lat").setValue("36.840666")
        myRef.child("floor").child("B1").child("B105").child("location").child("lng").setValue("127.182209")

        myRef.child("floor").child("B1").child("B106").child("name").setValue("포로드")
        myRef.child("floor").child("B1").child("B106").child("location").child("lat").setValue("36.840634")
        myRef.child("floor").child("B1").child("B106").child("location").child("lng").setValue("127.182275")

        myRef.child("floor").child("B1").child("B107").child("name").setValue("네버마인드")
        myRef.child("floor").child("B1").child("B107").child("location").child("lat").setValue("36.840602")
        myRef.child("floor").child("B1").child("B107").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("B1").child("B108").child("name").setValue("열린문")
        myRef.child("floor").child("B1").child("B108").child("location").child("lat").setValue("36.840570")
        myRef.child("floor").child("B1").child("B108").child("location").child("lng").setValue("127.182407")

        myRef.child("floor").child("B1").child("B109").child("name").setValue("미화원휴게실")//
        myRef.child("floor").child("B1").child("B109").child("location").child("lat").setValue("36.840538")
        myRef.child("floor").child("B1").child("B109").child("location").child("lng").setValue("127.182407")

        myRef.child("floor").child("B1").child("B110").child("name").setValue("탁구장")
        myRef.child("floor").child("B1").child("B110").child("location").child("lat").setValue("36.840506")
        myRef.child("floor").child("B1").child("B110").child("location").child("lng").setValue("127.182539")

        myRef.child("floor").child("B1").child("cafeteria").child("name").setValue("밀겨울")
        myRef.child("floor").child("B1").child("cafeteria").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("B1").child("cafeteria").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("1").child("101").child("name").setValue("가운 보관실")
        myRef.child("floor").child("1").child("101").child("location").child("lat").setValue("36.840490")
        myRef.child("floor").child("1").child("101").child("location").child("lng").setValue("127.182726")

        myRef.child("floor").child("1").child("102").child("name").setValue("비품실")
        myRef.child("floor").child("1").child("102").child("location").child("lat").setValue("36.840505")
        myRef.child("floor").child("1").child("102").child("location").child("lng").setValue("127.182696")

        myRef.child("floor").child("1").child("103").child("name").setValue("총 대위원회실")
        myRef.child("floor").child("1").child("103").child("location").child("lat").setValue("36.840520")
        myRef.child("floor").child("1").child("103").child("location").child("lng").setValue("127.182666")

        myRef.child("floor").child("1").child("104").child("name").setValue("총 학회장실")
        myRef.child("floor").child("1").child("104").child("location").child("lat").setValue("36.840550")
        myRef.child("floor").child("1").child("104").child("location").child("lng").setValue("127.182606")

        myRef.child("floor").child("1").child("106").child("name").setValue("창고")
        myRef.child("floor").child("1").child("106").child("location").child("lat").setValue("36.840670")
        myRef.child("floor").child("1").child("106").child("location").child("lng").setValue("127.182366")

        myRef.child("floor").child("1").child("ATM").child("name").setValue("신한")
        myRef.child("floor").child("1").child("ATM").child("location").child("lat").setValue("36.840700")
        myRef.child("floor").child("1").child("ATM").child("location").child("lng").setValue("127.182426")

        myRef.child("floor").child("1").child("ATM").child("name").setValue("국민")
        myRef.child("floor").child("1").child("ATM").child("location").child("lat").setValue("36.840720")
        myRef.child("floor").child("1").child("ATM").child("location").child("lng").setValue("127.182446")

        myRef.child("floor").child("1").child("107").child("name").setValue("학생처")
        myRef.child("floor").child("1").child("107").child("location").child("lat").setValue("36.840700")
        myRef.child("floor").child("1").child("107").child("location").child("lng").setValue("127.182306")

        myRef.child("floor").child("1").child("110").child("name").setValue("회의실")
        myRef.child("floor").child("1").child("110").child("location").child("lat").setValue("36.840660")
        myRef.child("floor").child("1").child("110").child("location").child("lng").setValue("127.182186")

        myRef.child("floor").child("1").child("111").child("name").setValue("예비군 연대 본부")
        myRef.child("floor").child("1").child("111").child("location").child("lat").setValue("36.840630")
        myRef.child("floor").child("1").child("111").child("location").child("lng").setValue("127.182246")

        myRef.child("floor").child("1").child("112").child("name").setValue("교보재 창고")
        myRef.child("floor").child("1").child("112").child("location").child("lat").setValue("36.840600")
        myRef.child("floor").child("1").child("112").child("location").child("lng").setValue("127.182306")

        myRef.child("floor").child("1").child("toilet").child("name").setValue("화장실")
        myRef.child("floor").child("1").child("toilet").child("location").child("lat").setValue("36.840495")
        myRef.child("floor").child("1").child("toilet").child("location").child("lng").setValue("127.182516")

        myRef.child("floor").child("1").child("113").child("name").setValue("선거 사무실")
        myRef.child("floor").child("1").child("113").child("location").child("lat").setValue("36.840480")
        myRef.child("floor").child("1").child("113").child("location").child("lng").setValue("127.182546")

        myRef.child("floor").child("1").child("114").child("name").setValue("총학생회 창고")
        myRef.child("floor").child("1").child("114").child("location").child("lat").setValue("36.8404575")
        myRef.child("floor").child("1").child("114").child("location").child("lng").setValue("127.182591")

        myRef.child("floor").child("1").child("115").child("name").setValue("동문 준비 위원회")
        myRef.child("floor").child("1").child("115").child("location").child("lat").setValue("36.840435")
        myRef.child("floor").child("1").child("115").child("location").child("lng").setValue("127.182636")

        myRef.child("floor").child("1").child("116").child("name").setValue("졸업 준비 위원회")
        myRef.child("floor").child("1").child("116").child("location").child("lat").setValue("36.840420")
        myRef.child("floor").child("1").child("116").child("location").child("lng").setValue("127.182666")

        myRef.child("floor").child("2").child("201").child("name").setValue("구내 사진관")
        myRef.child("floor").child("2").child("201").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("201").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("202").child("name").setValue("기독교학부 학회실")
        myRef.child("floor").child("2").child("202").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("202").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("203").child("name").setValue("법행정경찰학부 학회실")
        myRef.child("floor").child("2").child("203").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("203").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("204").child("name").setValue("ICT학부 학회실")
        myRef.child("floor").child("2").child("204").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("204").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("205").child("name").setValue("사회복지학부 학회실")
        myRef.child("floor").child("2").child("205").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("205").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("206").child("name").setValue("경상학부 학회실")
        myRef.child("floor").child("2").child("206").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("206").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("207").child("name").setValue("어문학부 학회실")
        myRef.child("floor").child("2").child("207").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("207").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("208").child("name").setValue("관광학부 학회실")
        myRef.child("floor").child("2").child("208").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("208").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("209").child("name").setValue("사범학부 학회실")
        myRef.child("floor").child("2").child("209").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("209").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("210").child("name").setValue("디자인영상학부 학회실")
        myRef.child("floor").child("2").child("210").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("210").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("211").child("name").setValue("기념품점 학회실")
        myRef.child("floor").child("2").child("211").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("211").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("toilet").child("name").setValue("화장실")
        myRef.child("floor").child("2").child("toilet").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("toilet").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("2").child("convenience").child("name").setValue("세븐 일레븐")
        myRef.child("floor").child("2").child("convenience").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("2").child("convenience").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("301").child("name").setValue("WEB-P")
        myRef.child("floor").child("3").child("301").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("301").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("302").child("name").setValue("봄누리")
        myRef.child("floor").child("3").child("302").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("302").child("location").child("lng").setValue("127.182341")



        myRef.child("floor").child("3").child("304").child("name").setValue("비에스엠")
        myRef.child("floor").child("3").child("304").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("304").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("305").child("name").setValue("마루와사람들")
        myRef.child("floor").child("3").child("305").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("305").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("306").child("name").setValue("재미와의미")
        myRef.child("floor").child("3").child("306").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("306").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("307").child("name").setValue("레인그래픽")
        myRef.child("floor").child("3").child("307").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("307").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("308").child("name").setValue("이엠시스")
        myRef.child("floor").child("3").child("308").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("308").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("309").child("name").setValue("드로넷디에스피")
        myRef.child("floor").child("3").child("309").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("309").child("location").child("lng").setValue("127.182341")


        myRef.child("floor").child("3").child("311").child("name").setValue("로아")
        myRef.child("floor").child("3").child("311").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("311").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("313").child("name").setValue("안신기업")
        myRef.child("floor").child("3").child("313").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("313").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("314").child("name").setValue("클레어")
        myRef.child("floor").child("3").child("314").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("314").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("315").child("name").setValue("써니스카이")
        myRef.child("floor").child("3").child("315").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("315").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("316").child("name").setValue("비제이월드")
        myRef.child("floor").child("3").child("316").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("316").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("toilet").child("name").setValue("화장실")
        myRef.child("floor").child("3").child("toilet").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("toilet").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("319").child("name").setValue("창업지원단 단장실")
        myRef.child("floor").child("3").child("319").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("319").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("3").child("320").child("name").setValue("창업지원단 행정실")
        myRef.child("floor").child("3").child("320").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("3").child("320").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("401").child("name").setValue("동아리 율동실")
        myRef.child("floor").child("4").child("401").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("401").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("402").child("name").setValue("학생회 열람실")
        myRef.child("floor").child("4").child("402").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("402").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("403").child("name").setValue("기독교문화 예술학부 학회실")
        myRef.child("floor").child("4").child("403").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("403").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("404-1").child("name").setValue("모가세")
        myRef.child("floor").child("4").child("404-1").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("404-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("404-2").child("name").setValue("먹방")
        myRef.child("floor").child("4").child("404-2").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("404-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("404-3").child("name").setValue("깐따삐아")
        myRef.child("floor").child("4").child("404-3").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("404-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("405-1").child("name").setValue("블루스카이")
        myRef.child("floor").child("4").child("405-1").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("405-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("405-2").child("name").setValue("유사모")
        myRef.child("floor").child("4").child("405-2").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("405-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("405-3").child("name").setValue("RCY")
        myRef.child("floor").child("4").child("405-3").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("405-3").child("location").child("lng").setValue("127.182341")


        myRef.child("floor").child("4").child("405-4").child("name").setValue("주짓떼로")
        myRef.child("floor").child("4").child("405-4").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("405-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("406-1").child("name").setValue("소통")
        myRef.child("floor").child("4").child("406-1").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("406-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("406-2").child("name").setValue("PSP")
        myRef.child("floor").child("4").child("406-2").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("406-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("406-3").child("name").setValue("비결")
        myRef.child("floor").child("4").child("406-3").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("406-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("406-4").child("name").setValue("킬로스")
        myRef.child("floor").child("4").child("406-4").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("406-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("407-2").child("name").setValue("Noriter")
        myRef.child("floor").child("4").child("407-2").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("407-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("407-1").child("name").setValue("창업")
        myRef.child("floor").child("4").child("407-1").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("407-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("408").child("name").setValue("창업")
        myRef.child("floor").child("4").child("408").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("408").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("toilet").child("name").setValue("화장실")
        myRef.child("floor").child("4").child("toilet").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("toilet").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("409").child("name").setValue("보건학부 학회실")
        myRef.child("floor").child("4").child("409").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("409").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("4").child("410").child("name").setValue("스포츠과학부 학회실")
        myRef.child("floor").child("4").child("410").child("location").child("lat").setValue("36.840782")
        myRef.child("floor").child("4").child("410").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("501-1").child("name").setValue("EDFM")
        myRef.child("floor").child("5").child("501-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("501-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("501-2").child("name").setValue("FLOW")
        myRef.child("floor").child("5").child("501-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("501-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("501-3").child("name").setValue("ERsite")
        myRef.child("floor").child("5").child("501-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("501-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("501-4").child("name").setValue("수어사랑회")
        myRef.child("floor").child("5").child("501-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("501-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("502-1").child("name").setValue("IVF")
        myRef.child("floor").child("5").child("502-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("502-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("502-2").child("name").setValue("ESP")
        myRef.child("floor").child("5").child("502-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("502-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("502-3").child("name").setValue("틀")
        myRef.child("floor").child("5").child("502-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("502-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("502-4").child("name").setValue("청소도구실")
        myRef.child("floor").child("5").child("502-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("502-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("503").child("name").setValue("총동아리 연합회")
        myRef.child("floor").child("5").child("503").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("503").child("location").child("lng").setValue("127.182341")


        myRef.child("floor").child("5").child("504-1").child("name").setValue("D&M")
        myRef.child("floor").child("5").child("504-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("504-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("504-2").child("name").setValue("W.A.B")
        myRef.child("floor").child("5").child("504-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("504-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("504-3").child("name").setValue("B.G.S 경호 사범단")
        myRef.child("floor").child("5").child("504-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("504-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("505-1").child("name").setValue("MAST")
        myRef.child("floor").child("5").child("505-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("505-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("505-2").child("name").setValue("대한유도")
        myRef.child("floor").child("5").child("505-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("505-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("505-3").child("name").setValue("YMAN")
        myRef.child("floor").child("5").child("505-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("505-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("505-4").child("name").setValue("C.C.C")
        myRef.child("floor").child("5").child("505-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("505-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("506-1").child("name").setValue("하기오스")
        myRef.child("floor").child("5").child("506-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("506-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("506-2").child("name").setValue("JDM")
        myRef.child("floor").child("5").child("506-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("506-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("506-3").child("name").setValue("N.E.W")
        myRef.child("floor").child("5").child("506-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("506-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("506-4").child("name").setValue("SFC")
        myRef.child("floor").child("5").child("506-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("506-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("507-1").child("name").setValue("돌려차기")
        myRef.child("floor").child("5").child("507-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("507-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("507-2").child("name").setValue("백로")
        myRef.child("floor").child("5").child("507-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("507-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("507-3").child("name").setValue("J.O.Y")
        myRef.child("floor").child("5").child("507-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("507-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("507-4").child("name").setValue("해바라기")
        myRef.child("floor").child("5").child("507-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("507-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("507-5").child("name").setValue("아사도우")
        myRef.child("floor").child("5").child("507-5").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("507-5").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("508").child("name").setValue("5층 연습실")
        myRef.child("floor").child("5").child("508").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("508").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("toilet").child("name").setValue("화장실")
        myRef.child("floor").child("5").child("toilet").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("toilet").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("509-1").child("name").setValue("TOPS")
        myRef.child("floor").child("5").child("509-1").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("509-1").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("509-2").child("name").setValue("E&D")
        myRef.child("floor").child("5").child("509-2").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("509-2").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("509-3").child("name").setValue("CANON")
        myRef.child("floor").child("5").child("509-3").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("509-3").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("509-4").child("name").setValue("천기점")
        myRef.child("floor").child("5").child("509-4").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("509-4").child("location").child("lng").setValue("127.182341")

        myRef.child("floor").child("5").child("509-5").child("name").setValue("총동아리 연합회 창고")
        myRef.child("floor").child("5").child("509-5").child("location").child("lat").setValue("36.850782")
        myRef.child("floor").child("5").child("509-5").child("location").child("lng").setValue("127.182341")


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
        marker.position = LatLng(36.840720, 127.182446)
        marker.map = naverMap
        marker.tag = "국민"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840700, 127.182426)
        marker.map = naverMap
        marker.tag = "신한"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840670, 127.182366)
        marker.map = naverMap
        marker.tag = "창고"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840700, 127.182306)
        marker.map = naverMap
        marker.tag = "학생처"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840600, 127.182306)
        marker.map = naverMap
        marker.tag = "교보재 창고"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840630, 127.182246)
        marker.map = naverMap
        marker.tag = "예비군 연대 본부"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840660, 127.182186)
        marker.map = naverMap
        marker.tag = "회의실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840495, 127.182516)
        marker.map = naverMap
        marker.tag = "화장실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840480, 127.182546)
        marker.map = naverMap
        marker.tag = "선거 사무실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.8404575, 127.182591)
        marker.map = naverMap
        marker.tag = "총학생회창고"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840435, 127.182636)
        marker.map = naverMap
        marker.tag = "동문 준비 위원회"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840420, 127.182666)
        marker.map = naverMap
        marker.tag = "졸업 준비 위원회"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840550, 127.182606)
        marker.map = naverMap
        marker.tag = "총 학회장실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840520, 127.182666)
        marker.map = naverMap
        marker.tag = "총 대위원회실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840505, 127.182696)
        marker.map = naverMap
        marker.tag = "비품실"
        marker.onClickListener = listener

        marker = Marker()
        marker.position = LatLng(36.840490, 127.182726)
        marker.map = naverMap
        marker.tag = "가운 보관실"
        marker.onClickListener = listener

//        marker = Marker()
//        marker.position = LatLng(36.840602, 127.182341)
//        marker.map = naverMap //
//
//        marker = Marker()
//        marker.position = LatLng(36.840570, 127.182407)
//        marker.map = naverMap
//
//        marker = Marker()
//        marker.position = LatLng(36.840538, 127.182473)
//        marker.map = naverMap
//
//       marker = Marker()
//       marker.position = LatLng(36.840506, 127.182539)
//       marker.map = naverMap
//
//        marker = Marker()
//        marker.position = LatLng(36.840782, 127.182341)
//        marker.map = naverMap

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
            Log.d("coord", "${coord.latitude}, ${coord.longitude}")
        }


    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
