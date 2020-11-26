package com.biggates.bumap.ui.createMarker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.biggates.bumap.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_marker.*

class CreateMarkerActivity : AppCompatActivity() {

    private var category : ArrayList<String> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_marker)
        var lat = intent.getStringExtra("lat")!!
        var lng = intent.getStringExtra("lng")!!
        lat_edit.setText(lat)
        lng_edit.setText(lng)

        category_cafe.setOnClickListener {
            if(category.contains("cafe")){
                category.remove("cafe")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("cafe")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_beer.setOnClickListener {
            if(category.contains("beer")){
                category.remove("beer")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("beer")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_food.setOnClickListener {
            if(category.contains("food")){
                category.remove("food")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("food")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_convenienceStore.setOnClickListener {
            if(category.contains("convenienceStore")){
                category.remove("convenienceStore")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("convenienceStore")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_sing.setOnClickListener {
            if(category.contains("sing")){
                category.remove("sing")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("sing")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_busStation.setOnClickListener {
            if(category.contains("busStation")){
                category.remove("busStation")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("busStation")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        category_copy.setOnClickListener {
            if(category.contains("copy")){
                category.remove("copy")
                it.background = resources.getDrawable(R.drawable.marker_solid,null)
            }else{
                category.add("copy")
                it.background = resources.getDrawable(R.drawable.check_marker_solid,null)
            }
        }

        btn_ok.setOnClickListener {
            check()
        }

    }

    private fun check() {
        when{
            category.isEmpty() -> Toast.makeText(applicationContext,"카테고리를 선택해주세요",Toast.LENGTH_SHORT).show()

            TextUtils.isEmpty(place_edit.text.toString()) -> Toast.makeText(applicationContext,"장소명을 입력해주세요",Toast.LENGTH_SHORT).show()

            else->{
                for(c in category){
                    var map = HashMap<String,Any>()
                    var locationMap = HashMap<String,String>()
                    map.put("name",place_edit.text.toString())
                    locationMap.put("lat",lat_edit.text.toString())
                    locationMap.put("lng",lng_edit.text.toString())
                    map.put("location",locationMap)
                    FirebaseDatabase.getInstance().reference.child("search").child(c).child(place_edit.text.toString()).setValue(map).addOnCompleteListener {
                        if(it.isSuccessful){
                            setResult(200)
                            finish()
                        }else{
                            Toast.makeText(applicationContext,"서버에 문제가 있습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }
}
