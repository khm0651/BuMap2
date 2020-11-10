package com.biggates.bumap.ui.bus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.biggates.bumap.MainActivity

import com.biggates.bumap.R
import kotlinx.android.synthetic.main.app_bar_main.*


class ShuttleBusFragment : Fragment() {

//    private var onBackPressedCallback = object : OnBackPressedCallback(true){
//        override fun handleOnBackPressed() {
//            moveFromBusToHome()
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_shuttle_bus, container, false)

        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        return view
    }

//    private fun moveFromBusToHome() {
//        findNavController().navigate(R.id.action_busFragment_to_nav_home)
//        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
//    }

}
