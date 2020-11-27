package com.biggates.bumap.ui.modification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.biggates.bumap.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_modification_check.*

class ModificationCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_check)
        var placeName = intent.getStringExtra("placeName")
        var buildingName = intent.getStringExtra("buildingName")
        var floor = intent.getStringExtra("floor")
        var roomName = intent.getStringExtra("roomName")
        var roomNumber = intent.getStringExtra("roomNumber")
        var lat = intent.getDoubleExtra("lat",0.0)
        var lng = intent.getDoubleExtra("lng",0.0)

        buildingName_et.setText(placeName)
        floor_et.setText(floor)
        roomName_et.setText(roomName)
        roomNum_et.setText(roomNumber)
        lat_et.setText(lat.toString())
        lng_et.setText(lng.toString())

        btn_ok.setOnClickListener {
            if(TextUtils.isEmpty(reason_et.text)){
                Toast.makeText(applicationContext,"수정사유를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }else{
                var map = hashMapOf<String,Any>()
                map.put("placeName",placeName)
                map.put("buildingName",buildingName)
                map.put("floor",floor)
                map.put("roomName",roomName_et.text.toString())
                map.put("roomNumber",roomNum_et.text.toString())
                map.put("lat",lat)
                map.put("lng",lng)
                map.put("reason",reason_et.text.toString())
                FirebaseDatabase.getInstance().reference.child("modifications").push().setValue(map).addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(applicationContext,"수정 요청 완료",Toast.LENGTH_SHORT).show()
                        setResult(200)
                        finish()
                    }
                    else Toast.makeText(applicationContext,"수정 요청 실패",Toast.LENGTH_SHORT).show()
                }

            }
        }

        btn_cancel.setOnClickListener {
            finish()
        }

    }
}
