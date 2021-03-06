package com.biggates.bumap.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.R
import com.biggates.bumap.ui.schedule.LectureInfoFragment
import kotlinx.android.synthetic.main.lecture_layout.view.*

class ScheduleAdapter(
    private var mContext: Context,
    private var lectureList: ArrayList<String>,
    private var fm: FragmentManager,
    private var recyclerViewSchedule: RecyclerView,
    private var selectLayoutTitle: TextView
)
    :RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(){

    inner class ViewHolder (@NonNull item : View) : RecyclerView.ViewHolder(item){
        var title = item.lecture_title
        var layout = item.lecture_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.lecture_layout,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lectureList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = lectureList[position]
        holder.layout.setOnClickListener {
            recyclerViewSchedule.visibility = View.GONE
            selectLayoutTitle.text = lectureList[position]
            var transaction = fm.beginTransaction().apply {
                var lectureInfoFragment = LectureInfoFragment.newInstance(lectureList[position])
                replace(R.id.schedule_framlayout, lectureInfoFragment)
            }
            transaction.commit()
        }
    }
}