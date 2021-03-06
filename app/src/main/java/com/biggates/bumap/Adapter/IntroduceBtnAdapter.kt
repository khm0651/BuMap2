package com.biggates.bumap.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.biggates.bumap.ui.introduce.Introduce
import com.biggates.bumap.ui.introduce.RoomMaker
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import kotlinx.android.synthetic.main.activity_introduce.view.*
import kotlinx.android.synthetic.main.btn_layout.view.*

class IntroduceBtnAdapter (private var mContext : Context, private var mBtnList : ArrayList<String>,
                           private var recyclerView: RecyclerView, private var total_list : HashMap<String,String>,
                           private var floor_maker : HashMap<String, RoomMaker>, private var mapFragment:MapFragment,
                           private var naverMap : NaverMap, private var introduce : Introduce, private var btn_recyclerView : RecyclerView,
                           private var btn_list : ArrayList<String>, private var layout : RelativeLayout)
    :RecyclerView.Adapter<IntroduceBtnAdapter.ViewHolder>(){


    inner class ViewHolder (@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        var btn_layout_text = itemView.btn_layout_text
        var btn_layout = itemView.btn_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.btn_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mBtnList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        btn_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy >= 0){
                    //scroll down
                    scroll_state = "down"
                }else{
                    //scroll up
                    scroll_state = "up"
                }
            }
        })
        holder.btn_layout_text.text = mBtnList[position]
        holder.btn_layout.setOnClickListener {
            var selectFloor = holder.btn_layout_text.text.toString()
            var showFloor = arrayListOf<String>()

            floor_maker.keys.forEach { k ->
                var roomMaker = floor_maker[k]!!
                roomMaker.room.keys.forEach { key ->
                    roomMaker.room[key]!!.map = null
                }
            }

            if(total_list.containsKey(selectFloor)){
                for(roomNumber in floor_maker.get(selectFloor)!!.room.keys){
                    floor_maker.get(selectFloor)!!.room.get(roomNumber)!!.map=naverMap
                }
                total_list.get(selectFloor)!!.split(",").forEach { s->
                    showFloor.add(s)
                }
            }

            if(showFloor.isEmpty()){
                layout.introduce_not_yet_layout.visibility = View.VISIBLE

            }else{
                layout.introduce_not_yet_layout.visibility = View.GONE
            }

            introduce.isFirst=false
            mapFragment.getMapAsync(introduce)
            showFloor.sortWith(object : Comparator<String>{
                override fun compare(o1: String, o2: String): Int {
                    var a = o1.split("+")[1]
                    var b = o2.split("+")[1]
                    if(a.contains(Regex.fromLiteral("[a-zA-Z]"))) return -1
                    else if(b.contains(Regex.fromLiteral("[a-zA-Z]"))) return 1
                    return a.compareTo(b)
                }

            })
            var introduceAdapter = IntroduceAdapter(mContext, showFloor, floor_maker,naverMap,introduce,mapFragment)
            recyclerView.adapter = introduceAdapter
            introduceAdapter.notifyDataSetChanged()

            if(mBtnList.size %2 ==0){
                if(position < 3){
                    btn_recyclerView.scrollToPosition(0)
                }else if (position < (mBtnList.size/2) ){
                    if(pre_select_floor.toInt()>selectFloor.toInt() || (scroll_state.equals("up") && pre_select_floor<selectFloor && position.toInt()<5) ){
                        btn_recyclerView.scrollToPosition(position+2)
                    }else{
                        btn_recyclerView.scrollToPosition(position-2)
                    }
                }else if (position < mBtnList.size-2){
                    btn_recyclerView.scrollToPosition(position+2)
                }else{
                    btn_recyclerView.scrollToPosition(mBtnList.size-1)
                }
            }else{
                if(position < 3){
                    btn_recyclerView.scrollToPosition(0)
                }else if (position <= (mBtnList.size/2) ){
                    if(pre_select_floor.startsWith("B")){
                        pre_select_floor = pre_select_floor.replace("B","-")
                    }
                    if(pre_select_floor.toInt()>selectFloor.toInt() || (scroll_state.equals("up") && pre_select_floor<selectFloor && position<5)){
                        btn_recyclerView.scrollToPosition(position+2)
                    }else{
                        btn_recyclerView.scrollToPosition(position-2)
                    }
                }else if (position < mBtnList.size-2){
                    btn_recyclerView.scrollToPosition(position+2)
                }else{
                    btn_recyclerView.scrollToPosition(mBtnList.size-1)
                }
            }

            pre_select_floor = selectFloor


        }


    }

    companion object{

        var pre_select_floor = "1"
        var scroll_state = ""
    }

}