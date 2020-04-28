package com.example.bumap.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_introduce.*
import kotlin.math.absoluteValue

class Introduce : FragmentActivity(), OnMapReadyCallback {

    var building =Building()
    var floor_btn_arr : HashMap<String,Button> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.NoTitleBar);
        setContentView(R.layout.activity_introduce)

        val fm = supportFragmentManager
        val lat = intent.getDoubleExtra("lat",0.0)!!
        val lng = intent.getDoubleExtra("lng",0.0)!!
        val placeName = intent.getStringExtra("placename")
        val d_name = intent.getStringExtra("d_name")
        Log.d("test",lng.toString());
        Log.d("test",lat.toString());
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

                    Log.d("fire-base", "1 "+dataSnapshot?.key.toString())
                    if (dataSnapshot?.key.toString().equals("floor")) {
                            var floor = Floor()
                            dataSnapshot?.children?.forEach { dataSnapshot: DataSnapshot ->
                                floor.room.set(
                                    dataSnapshot.key.toString(),
                                    dataSnapshot.getValue(Room::class.java) as Room
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

        for(i in building.high.toInt() downTo building.low.toInt()){
            if(i == 0) continue
            var floor_btn : Button = Button(this)
            floor_btn.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            if(i<0) floor_btn.setText("B"+i.absoluteValue.toString()) else floor_btn.setText(i.toString())
            floor_layout.addView(floor_btn)
            floor_btn_arr.set(i.toString(),floor_btn)
        }

    }
}
