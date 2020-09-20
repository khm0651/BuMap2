package com.biggates.bumap.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.biggates.bumap.Adapter.NoticeAdapter
import com.biggates.bumap.MainActivity
import com.biggates.bumap.Model.Notice
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.notice.NoticeViewModel
import com.biggates.bumap.ViewModel.ViewModelFactory
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_notice.view.*

class NoticeFragment : Fragment() {

    lateinit var noticeAdapter : NoticeAdapter

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

        NoticeViewModel.noticeList.observe(viewLifecycleOwner,noticeListObserver)
        NoticeViewModel.isViewLoading.observe(viewLifecycleOwner,noticeIsLoadingObserver)

        var recyclerView = view.recycler_view_notice
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        noticeAdapter = NoticeAdapter(context!!, NoticeViewModel.noticeList.value?: arrayListOf())
        recyclerView.adapter = noticeAdapter

        view.up_btn_notice.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }



        return view
    }

    private val noticeListObserver = Observer<ArrayList<Notice>>{
            noticeAdapter.update(it)
    }

    private val noticeIsLoadingObserver = Observer<Boolean> {
        if(it) view!!.progressbar_notice.visibility = View.VISIBLE
        else view!!.progressbar_notice.visibility = View.GONE
    }

    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_nav_notice_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }
}
