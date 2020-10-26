package com.biggates.bumap.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.biggates.bumap.ui.introduce.Introduce
import com.biggates.bumap.ui.introduce.RoomMaker
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.room_list.view.*

class IntroduceAdapter (private var mContext : Context, private var mRoomList : ArrayList<String>,
                        private var floor_maker : HashMap<String, RoomMaker>, private var naverMap: NaverMap,
                        private var introduce: Introduce, private var mapFragment: MapFragment)
    :RecyclerView.Adapter<IntroduceAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var titleTextView = itemView.room_list_title
        var subTextView = itemView.room_list_sub
        var room_list_layout = itemView.room_list_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view =  LayoutInflater.from(mContext).inflate(R.layout.room_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mRoomList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var split = mRoomList[position].split("+")
        if((split[1].get(0) >= 'a' && split[1].get(0) <= 'z') || (split[1].contains("ATM"))){
            holder.titleTextView.text = split[0]
        }else{
            holder.titleTextView.text = split[0]+" ("+split[1]+")"
        }
        holder.subTextView.text = split[2]+" "+split[3]

        holder.room_list_layout.setOnClickListener {
            var split = mRoomList[position].split("+")
            var roomName = split[0]
            var floorNum = split[3].substring(0,split[3].length-1)
            var roomNum = split[1]
            val infoWindow = InfoWindow()

            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext!!) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker!!.tag.toString()
                }
            }

            floor_maker.keys.forEach { k ->
                var roomMaker = floor_maker[k]!!
                roomMaker.room.keys.forEach { key ->
                    roomMaker.room[key]!!.map = null
                }
            }

            var selectMarker = floor_maker.get(floorNum)!!.room.get(roomNum) as Marker
            floor_maker.get(floorNum)!!.room.get(roomNum)!!.map= naverMap
            infoWindow.open(selectMarker!!)

            introduce.isFirst=false
            mapFragment.getMapAsync(introduce)

        }
    }

}