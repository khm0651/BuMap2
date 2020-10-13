package com.biggates.bumap.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.view_page_work_layout.view.*

class ViewPageWorkAdapter(private var mContext : Context, private var workList: LinkedTreeMap<String, LinkedTreeMap<String, String>>?)
    :RecyclerView.Adapter<ViewPageWorkAdapter.ViewHolder>(){

    inner class ViewHolder(item : View) : RecyclerView.ViewHolder(item){
        var time = item.work_date
        var title_layout = item.title_layout_work
        var title = item.title_work
        var isPresent = item.isPresent_work
        var content = item.content_work
        var layout = item.layout_work_view_page
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.view_page_work_layout,parent,false)
        return ViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var title = workList!!.get(position.toString())!!["title"].toString()
        var isIng = title.substring(title.lastIndexOf("["),title.length)

        var spannableString = SpannableString(title)
        if(isIng == "[진행중]") spannableString.setSpan( ForegroundColorSpan(Color.parseColor("#000000")), title.lastIndexOf("["), title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else spannableString.setSpan( ForegroundColorSpan(Color.parseColor("#FFFF6666")), title.lastIndexOf("["), title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.title.text = spannableString
        holder.time.text = (workList!!.get(position.toString())!!["time"] as LinkedTreeMap<String, String>)["제출기간"]
        holder.isPresent.text = (workList!!.get(position.toString())!!["isPresent"] as LinkedTreeMap<String, String>)["제출여부"]
        if(holder.isPresent.text == "미제출") holder.isPresent.setTextColor(mContext.resources.getColor(R.color.sunday,null))
        else holder.isPresent.setTextColor(mContext.resources.getColor(R.color.saturday,null))

        holder.content.text = workList!!.get(position.toString())!!["content"]
        holder.layout.setOnClickListener {
            if(holder.content.visibility == View.GONE) {
                holder.content.visibility = View.VISIBLE
                holder.title_layout.background = mContext.resources.getDrawable(R.drawable.bottom_solid_black,null)
                holder.title_layout.setPadding(0,0,0,18)
            }
            else {
                holder.content.visibility = View.GONE
                holder.title_layout.background = null
                holder.title_layout.setPadding(0,0,0,0)
            }
        }
    }

    override fun getItemCount(): Int {
        return workList!!.size
    }

}