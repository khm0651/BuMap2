package com.biggates.bumap.ui.schedule

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Adapter.ScheduleAdapter
import com.biggates.bumap.MainActivity

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*

@RequiresApi(Build.VERSION_CODES.N)
class ScheduleFragment : Fragment() {

    var isSelectRecyclerViewScroll = 0

    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromSearchToHome()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        var view =  inflater.inflate(R.layout.fragment_schedule, container, false)
        LectureScheduleViewModel.isViewLoading.observe(viewLifecycleOwner, Observer {
            if(!it){
                view.progressbar_schedule.visibility = View.GONE
                var select_recycler_view = view.recycler_view_schedule
                var linearLayoutManager = LinearLayoutManager(context)
                select_recycler_view.layoutManager = linearLayoutManager

                var lectureList : ArrayList<String> = arrayListOf()
                var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!
                lectureSchedule.forEach { name, lecture ->
                    (lecture as LinkedTreeMap<String, Any>).forEach { key, l ->
                        (l as LinkedTreeMap<String,Any>).forEach { lectureName, u ->
                            lectureList.add(lectureName)
                        }
                    }
                }
                view.select_layout_schedule.setOnClickListener {
                    if(view.recycler_view_schedule.visibility == View.GONE){
                        view.recycler_view_schedule.visibility = View.VISIBLE
                    }else{
                        view.recycler_view_schedule.visibility = View.GONE
                    }
                }

                view.recycler_view_schedule.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        isSelectRecyclerViewScroll = newState
                    }

                })

                var tiemTableFragment = TiemTableFragment()
                var fm = childFragmentManager
                fm.beginTransaction().add(R.id.schedule_framlayout, tiemTableFragment).commit()
                var scheduleAdapter = ScheduleAdapter(context!!,lectureList,fm,view.recycler_view_schedule,view.select_layout_title)
                select_recycler_view.adapter = scheduleAdapter

            }
            else view.progressbar_schedule.visibility = View.VISIBLE
        })








        return view
    }

    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_nav_schedule_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }

}
