package com.biggates.bumap.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.BuMapNotice
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.notice_bumap_layout.view.*

class NoticeBuMapAdapter(private var mContext : Context, private var buMapNotices : ArrayList<BuMapNotice>)
    :RecyclerView.Adapter<NoticeBuMapAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull item : View) : RecyclerView.ViewHolder(item){
        var title = item.notice_bumap_title
        var content = item.notice_bumap_content
        var date = item.notice_bumap_date
        var layout = item.notice_bumap_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.notice_bumap_layout,parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.visibility = View.GONE
        holder.title.text = buMapNotices[position].title
        holder.content.text = buMapNotices[position].content
        holder.date.text = buMapNotices[position].date
        holder.layout.setOnClickListener {
            if(holder.content.visibility == View.VISIBLE) {
                holder.content.visibility = View.GONE
                holder.date.background = null
            }
            else {
                holder.content.visibility = View.VISIBLE
                holder.date.background = mContext.resources.getDrawable(R.drawable.bottom_solid_black,null)
                holder.date.setPadding(0,0,0,18)
            }

        }
    }

    override fun getItemCount(): Int {
        return buMapNotices.size
    }

}