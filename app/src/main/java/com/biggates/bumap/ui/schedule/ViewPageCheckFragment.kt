package com.biggates.bumap.ui.schedule

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.ViewPageCheckAdapter

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_view_page_check.view.*


class ViewPageCheckFragment : Fragment() {
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
        var view = inflater.inflate(R.layout.fragment_view_page_check, container, false)

        var recyclerView = view.view_page_check_recycler
        var linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()

        var lectureCheckSchedule : LinkedTreeMap<String, LinkedTreeMap<String, String>>? = null
        lectureSchedule!!.forEach { user, lecture ->
            lectureCheckSchedule = (((lecture as LinkedTreeMap<String, Any>)["lecture"] as LinkedTreeMap<String, Any>)[title] as LinkedTreeMap<String, Any>)["학습목차"] as LinkedTreeMap<String, LinkedTreeMap<String, String>>?
        }

        var arr = arrayListOf<LinkedTreeMap<String, Any>>()
        lectureCheckSchedule!!.forEach { k, v ->
            var map = LinkedTreeMap<String,Any>()
            map.put(k,v)
            arr.add(map)
        }
        if(arr.isNotEmpty()){
            arr.sortWith(object : Comparator<LinkedTreeMap<String,Any>>{
                override fun compare(
                    o1: LinkedTreeMap<String, Any>?,
                    o2: LinkedTreeMap<String, Any>?
                ): Int {
                    var a = o1!!.keys.first().split(" ")[0].replace("[^0-9]".toRegex(),"").toInt()
                    var b = o2!!.keys.first().split(" ")[0].replace("[^0-9]".toRegex(),"").toInt()
                    return a.compareTo(b)
                }

            })
        }

        var viewPageCheckAdapter = ViewPageCheckAdapter(context!!,arr)
        recyclerView.adapter = viewPageCheckAdapter

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            ViewPageCheckFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}
