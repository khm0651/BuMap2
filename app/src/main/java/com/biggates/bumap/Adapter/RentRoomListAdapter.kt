package com.biggates.bumap.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.biggates.bumap.ui.home.RentRoomDetailActivity
import com.biggates.bumap.ui.home.RentRoomViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.rent_room_layout.view.*

class RentRoomListAdapter(
    private var mContext: Context,
    private var viewModel: RentRoomViewModel
    )
    :RecyclerView.Adapter<RentRoomListAdapter.ViewHolder>() {

    private lateinit var context : Context
    inner class ViewHolder(@NonNull item : View) : RecyclerView.ViewHolder(item){
        var layout = item.rent_room_layout
        var rentRoomName = item.rent_room_name
        var requestDate = item.rent_room_date
        var cancel = item.rent_room_cancel
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RentRoomListAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.rent_room_layout,parent,false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RentRoomListAdapter.ViewHolder, position: Int) {

        holder.rentRoomName.text = viewModel.rentRoom.value!![position].buildingName
        holder.requestDate.text = viewModel.rentRoom.value!![position].date

        holder.layout.setOnClickListener {

            (context as Activity).startActivityForResult(Intent(context,RentRoomDetailActivity::class.java)
                .putExtra("rentRoom", viewModel.rentRoom.value!![position]),200)

        }

        holder.cancel.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("rentRoomRequest").child( viewModel.rentRoom.value!![position].id).removeValue().addOnCompleteListener {
                Toast.makeText(context,"삭제 완료",Toast.LENGTH_SHORT).show()
                viewModel.loadRentRoomRequest()
            }
        }


    }

    override fun getItemCount(): Int {
        return viewModel.rentRoom.value!!.size
    }

}