package com.biggates.bumap.ui.schedule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Adapter.ScheduleAdapter
import com.biggates.bumap.MainActivity
import com.biggates.bumap.Model.LoginParam

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.biggates.bumap.ui.login.LoginActivity
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*

@RequiresApi(Build.VERSION_CODES.M)
class ScheduleFragment : Fragment() {

    var REQUEST_CODE = 100
    var isSelectRecyclerViewScroll = 0

    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromScheduleToHome()
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
        var view =  inflater.inflate(R.layout.fragment_schedule, container, false)

        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        LectureScheduleViewModel.viewStart.observe(viewLifecycleOwner, Observer {
            if(it){

                LectureScheduleViewModel.isViewLoading.observe(viewLifecycleOwner, Observer {
                    if(!it){
                        view.progressbar_schedule.visibility = View.GONE
                        var select_recycler_view = view.recycler_view_schedule
                        var linearLayoutManager = LinearLayoutManager(context)
                        select_recycler_view.layoutManager = linearLayoutManager

                        var lectureList : ArrayList<String> = arrayListOf()
                        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!
                        lectureSchedule.keys.forEach { name ->
                            var lecture = lectureSchedule[name] as LinkedTreeMap<String, Any>
                            lecture.keys.forEach { key ->
                                var l = lecture[key] as LinkedTreeMap<String, Any>
                                l.keys.forEach { lectureName ->
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


            }
        })


        return view
    }

    private fun moveFromScheduleToHome() {
        findNavController().navigate(R.id.action_nav_schedule_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        var pref = requireActivity().getSharedPreferences("userInfo",0)
        var isAutoLogin = pref.getBoolean("isAutoLogin",false)
        if(!isAutoLogin){
            var intent = Intent(context,LoginActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            if(resultCode == 202) LectureScheduleViewModel._viewStart.postValue(true)
            if(resultCode == 400) moveFromScheduleToHome()
        }
    }
}


