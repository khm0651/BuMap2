package com.example.bumap.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bumap.Adapter.NoticeAdapter
import com.example.bumap.Interface.RetrofitService
import com.example.bumap.MainActivity
import com.example.bumap.Model.Notice
import com.example.bumap.R
import com.example.bumap.Singleton.NoticeList
import com.example.bumap.Singleton.RetrofitClient
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_notice.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NoticeFragment : Fragment() {

    private lateinit var myAPI: RetrofitService
    private lateinit var retrofit : Retrofit
    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromSearchToHome()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notice, container, false)

        if(findNavController().currentDestination.toString() != "fragment_home"){
            (context as MainActivity).app_bar_layout_main.visibility = View.GONE
        }

        var recyclerView = view.recycler_view_notice
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        var noticeAdapter : NoticeAdapter


        view.up_btn_notice.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        if(NoticeList.getList().isEmpty()){
            noticeAdapter = NoticeAdapter(context!!, arrayListOf())
        }else{
            noticeAdapter = NoticeAdapter(context!!, NoticeList.getList())
        }
        recyclerView.adapter = noticeAdapter


        return view
    }

    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_nav_notice_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }
}
