package com.biggates.bumap.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.biggates.bumap.ui.bus.BusFragment
import com.biggates.bumap.ui.bus.ShuttleBusFragment

class BusViewPageAdapter (fragment: Fragment)
    : FragmentStateAdapter(fragment){


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                BusFragment()
            }

            1-> {
                ShuttleBusFragment()
            }

            else -> Fragment()

        }
    }


}