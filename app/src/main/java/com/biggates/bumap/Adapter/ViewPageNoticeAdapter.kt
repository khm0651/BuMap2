package com.biggates.bumap.Adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.view_page_notice_layout.view.*

class ViewPageNoticeAdapter(private var mContext: Context, private var noticeList: LinkedTreeMap<String, LinkedTreeMap<String, String>>?)
    : RecyclerView.Adapter<ViewPageNoticeAdapter.ViewHolder>() {

    inner class ViewHolder(item : View) : RecyclerView.ViewHolder(item){
        var title = item.notice_title
        var time = item.notice_date
        var content = item.notice_content
        var layout = item.notice_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.view_page_notice_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if(noticeList == null)return 0
        else return noticeList!!.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.visibility = View.GONE
        holder.title.background = null
        holder.title.setPadding(0,0,0,0)

        holder.title.text = noticeList!!.get(position.toString())!!["title"]
        holder.time.text = (noticeList!!.get(position.toString())!!["info"] as LinkedTreeMap<String, String>)["작성일"]
        holder.content.text = noticeList!!.get(position.toString())!!["content"]
        holder.layout.setOnClickListener {
            if(holder.content.visibility == View.GONE) {
                holder.content.visibility = View.VISIBLE
                holder.title.background = mContext.resources.getDrawable(R.drawable.bottom_solid_black,null)
                holder.title.setPadding(0,0,0,10)
            }
            else {
                holder.content.visibility = View.GONE
                holder.title.background = null
                holder.title.setPadding(0,0,0,0)
            }
        }
    }

}