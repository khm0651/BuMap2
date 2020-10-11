package com.biggates.bumap.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biggates.bumap.Adapter.ViewPageAdapter

import com.biggates.bumap.R
import kotlinx.android.synthetic.main.fragment_lecture_info.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



class LectureInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_lecture_info, container, false)

        var viewPageAdapter = ViewPageAdapter(title, this)
        var viewPager = view.viewpage_lecture_info
        viewPager.adapter = viewPageAdapter

        view.notice_btn_lecture_info.setOnClickListener {
            viewPager.setCurrentItem(0)
        }
        view.work_btn_lecture_info.setOnClickListener {
            viewPager.setCurrentItem(1)
        }
        view.check_btn_lecture_info.setOnClickListener {
            viewPager.setCurrentItem(2)
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            LectureInfoFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}
