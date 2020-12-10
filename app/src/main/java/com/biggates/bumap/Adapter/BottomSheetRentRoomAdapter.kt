package com.biggates.bumap.Adapter

import android.content.Context
import android.content.Intent
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginRight
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.RentRoomInfo
import com.biggates.bumap.Model.RentRoomMarker
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ui.home.RentRoomPopUpActivity
import kotlinx.android.synthetic.main.bottom_sheet_rent_room_layout.view.*
import kotlinx.android.synthetic.main.rent_room_request_list_layout.view.*

class BottomSheetRentRoomAdapter (private var mContext : Context, private var rentRoomList : ArrayList<MutableMap.MutableEntry<String,RentRoomMarker>>)
    : RecyclerView.Adapter<BottomSheetRentRoomAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull item : View) : RecyclerView.ViewHolder(item){
        var img = item.rent_room_img_bottom_sheet
        var rentRoomName = item.rent_room_name_bottom_sheet
        var infoLayout = item.price_info_bottom_sheet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_rent_room_layout,parent,false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.infoLayout.removeAllViews()

        holder.rentRoomName.text = rentRoomList[position].key
        var rentRoomInfoMap = hashMapOf<String,ArrayList<RentRoomInfo>>()
        for(i in rentRoomList[position].value.info.indices){

            if(rentRoomInfoMap.containsKey(rentRoomList[position].value.info[i]["num"])){
                var rentRoomInfo = RentRoomInfo(rentRoomList[position].value.info[i])
                rentRoomInfoMap[rentRoomList[position].value.info[i]["num"]]!!.add(rentRoomInfo)
            }else{
                var rentRoomInfo = RentRoomInfo(rentRoomList[position].value.info[i])
                rentRoomInfoMap.put(rentRoomList[position].value.info[i]["num"]!!, arrayListOf(rentRoomInfo))
            }
        }

        for(infoList in rentRoomInfoMap){

            var infoView = View.inflate(mContext,R.layout.rent_room_request_list_layout,null)
            var maxYearPrice = 0
            var minYearPrice = 0
            var maxHalfYearPrice = 0
            var minHalfYearPrice = 0
            for(value in infoList.value){

                if(minYearPrice == 0 && value.info["yearPrice"]!! != "") minYearPrice = value.info["yearPrice"]!!.toInt()
                if(maxYearPrice == 0 && value.info["yearPrice"]!! != "") maxYearPrice = value.info["yearPrice"]!!.toInt()

                if(minHalfYearPrice == 0 && value.info["halfYearPrice"]!! != "") minHalfYearPrice = value.info["halfYearPrice"]!!.toInt()
                if(maxHalfYearPrice == 0 && value.info["halfYearPrice"]!! != "") maxHalfYearPrice = value.info["halfYearPrice"]!!.toInt()

                if(value.info["yearPrice"]!! != ""){
                    if(minYearPrice > value.info["yearPrice"]!!.toInt()) minYearPrice = value.info["yearPrice"]!!.toInt()
                    if(maxYearPrice < value.info["yearPrice"]!!.toInt()) maxYearPrice = value.info["yearPrice"]!!.toInt()
                }

                if(value.info["halfYearPrice"]!! != ""){
                    if(minHalfYearPrice > value.info["halfYearPrice"]!!.toInt()) minHalfYearPrice = value.info["halfYearPrice"]!!.toInt()
                    if(maxHalfYearPrice < value.info["halfYearPrice"]!!.toInt()) maxHalfYearPrice = value.info["halfYearPrice"]!!.toInt()
                }

            }
            var yearPriceRange = ""
            var halfYearPriceRange = ""
            if(minYearPrice == maxYearPrice) yearPriceRange = maxYearPrice.toString()
            else yearPriceRange = "${minYearPrice} ~ ${maxYearPrice}"

            if(minHalfYearPrice == maxHalfYearPrice) halfYearPriceRange = maxHalfYearPrice.toString()
            else halfYearPriceRange = "${minHalfYearPrice} ~ ${maxHalfYearPrice}"

            var yearPriceSpannableString = SpannableString("년세 : ${yearPriceRange} ( 만원 )")
            var halfYearPriceSpannableString = SpannableString("반년세 : ${halfYearPriceRange} ( 만원 )")
            yearPriceSpannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.hamburgerBlack,null)), yearPriceSpannableString.lastIndexOf("("), yearPriceSpannableString.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            yearPriceSpannableString.setSpan( RelativeSizeSpan(0.65f), yearPriceSpannableString.lastIndexOf("("), yearPriceSpannableString.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            halfYearPriceSpannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.hamburgerBlack,null)), halfYearPriceSpannableString.lastIndexOf("("), halfYearPriceSpannableString.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            halfYearPriceSpannableString.setSpan( RelativeSizeSpan(0.65f), halfYearPriceSpannableString.lastIndexOf("("), halfYearPriceSpannableString.lastIndexOf(")")+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            infoView.num_rent_room_request.text = "기준 인원 : ${infoList.key}"
            infoView.year_pirce_rent_room_request.text = yearPriceSpannableString
            infoView.half_year_pirce_rent_room_request.text = halfYearPriceSpannableString

            var param = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            param.rightMargin = MyUtil.Dp2Px(mContext,10)
            infoView.layoutParams = param

            infoView.setOnClickListener {
                mContext.startActivity(Intent(mContext,RentRoomPopUpActivity::class.java)
                    .putExtra("num",infoList.key)
                    .putExtra("list",infoList.value)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            holder.infoLayout.addView(infoView)
        }


    }

    override fun getItemCount(): Int {
        return rentRoomList.size
    }

}