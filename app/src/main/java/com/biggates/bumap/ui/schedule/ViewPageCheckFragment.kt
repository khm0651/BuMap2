package com.biggates.bumap.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.biggates.bumap.R


class ViewPageCheckFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var title: String? = null

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
        return inflater.inflate(R.layout.fragment_view_page_check, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(title: String) =
            ViewPageCheckFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                }
            }
    }
}
