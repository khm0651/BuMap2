package com.biggates.bumap.ui.bus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.biggates.bumap.Adapter.BusViewPageAdapter
import com.biggates.bumap.MainActivity
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.frame_bus_layout.view.*


class BusFrameFragment : Fragment() {

    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromBusToHome()
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
        var view = inflater.inflate(R.layout.frame_bus_layout, container, false)

        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        var busViewPageAdapter = BusViewPageAdapter( this)
        var viewPager = view.viewpage_bus
        viewPager.adapter = busViewPageAdapter

        view.schoolBus.setOnClickListener {
            viewPager.setCurrentItem(0)
        }
        view.shuttleBus.setOnClickListener {
            viewPager.setCurrentItem(1)
        }

        viewPager.isUserInputEnabled = false

        return view
    }

    private fun moveFromBusToHome() {
        findNavController().navigate(R.id.action_busFrameFragment_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }

}
