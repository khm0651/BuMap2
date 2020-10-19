package com.biggates.bumap.ui.schedule

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.ViewPageNoticeAdapter
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.view_page_notice.view.*

@RequiresApi(Build.VERSION_CODES.N)
class ViewPageNoticeFragment : Fragment() {

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.view_page_notice, container, false)

        var recyclerView = view.view_page_notice_recycler
        var linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()

        var lectureNoticeSchedule : LinkedTreeMap<String, LinkedTreeMap<String, String>>? = null
        lectureSchedule!!.forEach { user, lecture ->
            lectureNoticeSchedule = (((lecture as LinkedTreeMap<String, Any>)["lecture"] as LinkedTreeMap<String, Any>)[title] as LinkedTreeMap<String, Any>)["공지사항"] as LinkedTreeMap<String, LinkedTreeMap<String, String>>?
        }

        if(lectureNoticeSchedule!!.isNullOrEmpty()) view.view_page_notice_empty.visibility = View.VISIBLE
        else view.view_page_notice_empty.visibility = View.GONE

        var viewPageNoticeAdapter = ViewPageNoticeAdapter(context!!,lectureNoticeSchedule)
        recyclerView.adapter = viewPageNoticeAdapter

        return view
    }


    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            ViewPageNoticeFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}
