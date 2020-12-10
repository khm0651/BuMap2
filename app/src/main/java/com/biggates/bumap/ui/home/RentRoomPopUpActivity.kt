package com.biggates.bumap.ui.home

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.RentRoomPopUpAdapter
import com.biggates.bumap.Model.RentRoomInfo
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.activity_rent_room_pop_up.*

class RentRoomPopUpActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_room_pop_up)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val layout = rent_room_pop_up
        val layoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT)

        layoutParam.width = displayMetrics.widthPixels - MyUtil.Dp2Px(applicationContext,70)
        layoutParam.height = MyUtil.Dp2Px(applicationContext,400)
        layout.layoutParams = layoutParam

        val list = intent.getParcelableArrayListExtra<RentRoomInfo>("list")!!
        val num = intent.getStringExtra("num")!!
        rent_room_pop_up_num.text = "${num}인기준"

        val recyclerView = recycler_view_rent_room_pop_up
        val adapter = RentRoomPopUpAdapter(applicationContext, list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

}
