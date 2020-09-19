package com.biggates.bumap.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.NoticeAdapter
import com.biggates.bumap.Interface.RetrofitService
import com.biggates.bumap.MainActivity
import com.biggates.bumap.R
import com.biggates.bumap.Singleton.NoticeList
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_notice.view.*
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
            view.progressbar_notice.visibility = View.VISIBLE
            noticeAdapter = NoticeAdapter(context!!, arrayListOf())
        }else{
            view.progressbar_notice.visibility = View.GONE
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
