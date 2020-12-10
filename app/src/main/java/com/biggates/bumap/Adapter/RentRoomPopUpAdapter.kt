package com.biggates.bumap.Adapter

import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.RentRoomInfo
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.rent_room_pop_up_layout.view.*

class RentRoomPopUpAdapter(private var mContext : Context, private var list : ArrayList<RentRoomInfo>)
    :RecyclerView.Adapter<RentRoomPopUpAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull item : View) : RecyclerView.ViewHolder(item){
        var yearPrice = item.year_price_pop_up
        var halfYearPrice = item.half_year_price_pop_up
        var requestDate = item.request_date_pop_up
        var layout = item.rent_room_pop_up_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.rent_room_pop_up_layout,parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var yearPrice = SpannableString("년세 : ${list[position].info["yearPrice"]} ( 만원 )")
        var halfYearPrice = SpannableString("반년세 : ${list[position].info["halfYearPrice"]} ( 만원 )")
        yearPrice.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.browser_actions_title_color,null)), yearPrice.lastIndexOf("("), yearPrice.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        yearPrice.setSpan( RelativeSizeSpan(0.65f), yearPrice.lastIndexOf("("), yearPrice.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        halfYearPrice.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.browser_actions_title_color,null)), halfYearPrice.lastIndexOf("("), halfYearPrice.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        halfYearPrice.setSpan( RelativeSizeSpan(0.65f), halfYearPrice.lastIndexOf("("), halfYearPrice.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.yearPrice.text = yearPrice
        holder.halfYearPrice.text = halfYearPrice
        holder.requestDate.text = "등록 날짜 : ${list[position].info["date"]}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

}