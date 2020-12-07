package com.biggates.bumap.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.RentRoomListAdapter
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.activity_rent_room_list.*

class RentRoomListActivity : AppCompatActivity() {

    lateinit var viewModel : RentRoomViewModel
    var adpater : RentRoomListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_room_list)
        viewModel = ViewModelProviders.of(this).get(RentRoomViewModel::class.java)
        viewModel.loadRentRoomRequest()
        var recyclerView = recycler_view_rent_room_list
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.rentRoomIsLoading.observe(this, Observer {
            if(it) porgress_rent_room_list.visibility = View.VISIBLE
            else porgress_rent_room_list.visibility = View.GONE
        })

        viewModel.rentRoom.observe(this, Observer {
            adpater = RentRoomListAdapter(applicationContext,viewModel)
            recyclerView.adapter = adpater
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 200){
            viewModel.loadRentRoomRequest()
        }
    }
}
