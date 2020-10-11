package com.biggates.bumap.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.biggates.bumap.ui.schedule.ViewPageCheckFragment
import com.biggates.bumap.ui.schedule.ViewPageNoticeFragment
import com.biggates.bumap.ui.schedule.ViewPageWorkFragment

class ViewPageAdapter(private var title: String?, fragment: Fragment)
    : FragmentStateAdapter(fragment){


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                ViewPageNoticeFragment.newInstance(title!!)
            }

            1-> {
                ViewPageWorkFragment.newInstance(title!!)
            }

            2-> {
               ViewPageCheckFragment.newInstance(title!!)
            }
            else -> Fragment()

        }
    }


}