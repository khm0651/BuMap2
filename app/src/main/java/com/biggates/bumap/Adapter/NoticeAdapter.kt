package com.biggates.bumap.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.Notice
import com.biggates.bumap.R
import com.biggates.bumap.ui.webview.NoticeWebViewActivity
import kotlinx.android.synthetic.main.notice_layout.view.*

class NoticeAdapter(private var mContext: Context, private var mNoticeList: ArrayList<Notice>)
    :RecyclerView.Adapter<NoticeAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){

        var notice_date = itemView.notice_date
        var notice_title = itemView.notice_title
        var notice_num = itemView.notice_num
        var notice_layout = itemView.notice_layout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.notice_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNoticeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.notice_date.text = mNoticeList[position].getArtclDate()
        holder.notice_title.text = mNoticeList[position].getArtclTitle()
        holder.notice_num.text = mNoticeList[position].getArtclnum()
        holder.notice_layout.setOnClickListener {
            var intent = Intent(mContext,NoticeWebViewActivity::class.java)
            intent.putExtra("href",mNoticeList[position].getHref())
            mContext.startActivity(intent)
        }


    }
}