package com.biggates.bumap.ui.schedule

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.ViewPageWorkAdapter

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_view_page_work.view.*


class ViewPageWorkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_view_page_work, container, false)

        var recyclerView = view.view_page_work_recycler
        var linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()

        var lectureWorkSchedule : LinkedTreeMap<String, LinkedTreeMap<String, String>>? = null
        lectureSchedule!!.forEach { user, lecture ->
            lectureWorkSchedule = (((lecture as LinkedTreeMap<String, Any>)["lecture"] as LinkedTreeMap<String, Any>)[title] as LinkedTreeMap<String, Any>)["과제"] as LinkedTreeMap<String, LinkedTreeMap<String, String>>?
        }

        var viewPageWorkAdapter = ViewPageWorkAdapter(context!!,lectureWorkSchedule)
        recyclerView.adapter = viewPageWorkAdapter

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            ViewPageWorkFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}
