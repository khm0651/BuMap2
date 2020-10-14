package com.biggates.bumap.Adapter

import android.content.Context
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
import kotlinx.android.synthetic.main.view_page_check_layout.view.*
import kotlinx.android.synthetic.main.view_page_check_state_layout.view.*
import kotlinx.android.synthetic.main.view_page_check_video_layout.view.*
import kotlinx.android.synthetic.main.view_page_check_video_layout.view.presentImg
import java.util.*
import kotlin.collections.ArrayList

class ViewPageCheckAdapter(private var mContext: Context, private var checkList: ArrayList<LinkedTreeMap<String, Any>>)
    :RecyclerView.Adapter<ViewPageCheckAdapter.ViewHolder>(){

    inner class ViewHolder(item : View) : RecyclerView.ViewHolder(item){
        var date = item.check_date
        var check_week_title = item.check_week_title
        var video_state_total_check = item.video_state_total_check
        var video_state_finish_check = item.video_state_finish_check
        var video_state_notFinish_check = item.video_state_notFinish_check
        var state_total_check = item.state_total_check
        var state_attendance_check = item.state_attendance_check
        var state_absent_check = item.state_absent_check
        var state_lateness_check = item.state_lateness_check
        var detail_view_page_check = item.detail_view_page_check
        var check_layout = item.check_layout
        var wrap_info = item.wrap_info

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.view_page_check_layout,parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.detail_view_page_check.removeAllViews()
        var key = checkList[position].keys.first()
        var title = key.split(" ")[0]
        var date = key.substring(key.indexOf(" ")+1,key.length)
        var totalVideoNum = 0
        var finishVideoNum = 0
        var notFinishVideoNum = 0
        var totalLectureNum = 0
        var attendanceLectureNum = 0
        var absentLectureNum = 0
        var latenessLectureNum = 0
        checkList[position].forEach { period, v ->
            var videoViewArr = arrayListOf<View>()
            var checkViewArr = arrayListOf<View>()
            (v as LinkedTreeMap<String,LinkedTreeMap<String,String>>).forEach { checkOrVideo, info  ->
                if(checkOrVideo.startsWith("video")){
                    totalVideoNum++
                    if(info["state"] == "학습안함") notFinishVideoNum++
                    else finishVideoNum++
                    videoViewArr.add(createVideoLayout(info))
                }
                else if(checkOrVideo.startsWith("check")) {
                    totalLectureNum++
                    if(info["state"] == "출 석") attendanceLectureNum++
                    else if(info["state"] == "결 석") absentLectureNum++
                    else latenessLectureNum++
                    checkViewArr.add(createCheckLayout(info))
                }
            }

            for(v in videoViewArr) holder.detail_view_page_check.addView(v)
            for(v in checkViewArr) holder.detail_view_page_check.addView(v)


        }

        holder.date.text = date
        holder.check_week_title.text = title
        holder.video_state_total_check.text = "total : ${totalVideoNum}"
        holder.state_total_check.text ="total : ${totalLectureNum}"
        holder.video_state_finish_check.text = "학습 : ${finishVideoNum}"
        holder.video_state_notFinish_check.text = "미학습 : ${notFinishVideoNum}"
        holder.state_attendance_check.text = "출석 : ${attendanceLectureNum}"
        holder.state_absent_check.text = "결석 : ${absentLectureNum}"
        holder.state_lateness_check.text = "지각 : ${latenessLectureNum}"
        holder.check_layout.setOnClickListener {
            if(holder.detail_view_page_check.visibility == View.GONE) {
                holder.detail_view_page_check.visibility = View.VISIBLE
                holder.wrap_info.background = mContext.resources.getDrawable(R.drawable.bottom_solid_black,null)
                holder.wrap_info.setPadding(0,0,0,18)
            }
            else {
                holder.detail_view_page_check.visibility = View.GONE
                holder.wrap_info.background = null
                holder.wrap_info.setPadding(0,0,0,0)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createCheckLayout(info: LinkedTreeMap<String, String>) : View {
        var checkLayout = LayoutInflater.from(mContext).inflate(R.layout.view_page_check_state_layout,null)
        var title = "${info["title"]} ( ${info["state"]} )"
        if(info["state"] == "출 석") checkLayout.presentImg.setImageResource(R.drawable.check)
        else checkLayout.presentImg.setImageResource(R.drawable.cancel)
        var spannableString = SpannableString(title)
        if(info["state"] == "출 석") spannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.saturday,null)), title.lastIndexOf("("), title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else if(info["state"] == "결 석")spannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.sunday,null)), title.lastIndexOf("("), title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        else spannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.lateness,null)), title.lastIndexOf("("), title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        checkLayout.title_check_state.text = spannableString

        return checkLayout
    }

    private fun createVideoLayout(info: LinkedTreeMap<String, String>) : View {

        var videoLayout = LayoutInflater.from(mContext).inflate(R.layout.view_page_check_video_layout,null)
        videoLayout.title_video.text = info["title"]
        videoLayout.time_video.text = info["state"]
        var time = info["state"].toString()
        if(time != "학습안함"){
            var right = time.substring(time.lastIndexOf("/")+1,time.length).trim()
            var left = time.substring(0,time.indexOf("/")).trim()
            var r_right = right.replace(" ","")
            var r_left = left.replace(" ","")
            var rc = Calendar.getInstance()
            rc.set(Calendar.MINUTE,r_right.substring(0,r_right.indexOf("분")-1).toInt())
            if(r_right.contains("초")) rc.set(Calendar.SECOND,r_right.substring(r_right.indexOf("분")+1,r_right.indexOf("초")).toInt())

            var lc = Calendar.getInstance()
            lc.set(Calendar.MINUTE,r_left.substring(0,r_left.indexOf("분")-1).toInt())
            if(r_left.contains("초")) lc.set(Calendar.SECOND,r_left.substring(r_left.indexOf("분")+1,r_left.indexOf("초")).toInt())

            if(lc.compareTo(rc) <1) videoLayout.presentImg.setImageResource(R.drawable.cancel)
            else videoLayout.presentImg.setImageResource(R.drawable.check)

        }else{
            videoLayout.presentImg.setImageResource(R.drawable.cancel)
        }

        return videoLayout
    }

    override fun getItemCount(): Int {
        return checkList!!.size
    }
}