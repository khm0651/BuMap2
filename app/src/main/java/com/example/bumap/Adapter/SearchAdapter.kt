package com.example.bumap.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bumap.MainActivity
import com.example.bumap.Model.Location
import com.example.bumap.R
import com.example.bumap.ui.home.HomeFragment
import com.example.bumap.ui.search.SearchFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_result_layout.view.*

class SearchAdapter (private var mContext : Context, private var mSearchList : ArrayList<String>, private var mLocationList : ArrayList<Location>)
    :RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView : View) : RecyclerView.ViewHolder(itemView){
        val search_result_text = itemView.search_result_text
        val search_result_layout = itemView.search_result_layout
        val search_result_info_text = itemView.search_result_info_text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.search_result_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mSearchList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var nameArr = mSearchList[position].split("-")
        if(nameArr.size>1){
            holder.search_result_text.text = nameArr[0]
            holder.search_result_info_text.text = nameArr[1]
        }else{
            holder.search_result_text.text = nameArr[0]
            holder.search_result_info_text.text = ""
        }

        holder.search_result_layout.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("lat",mLocationList[position].lat)
            bundle.putString("lng",mLocationList[position].lng)
            bundle.putBoolean("isShowPredictTime",true)
            holder.itemView.findNavController().navigate(R.id.action_searchFragment_to_nav_home,bundle)
            (mContext as MainActivity).search_edit.isEnabled = false
            (mContext as MainActivity).search_edit.visibility = View.GONE
            (mContext as MainActivity).search_text.visibility = View.VISIBLE
            (mContext as MainActivity).search_edit.clearFocus()
            (mContext as MainActivity).search_edit.text.clear()
            (mContext as MainActivity).recycler_view_search.adapter = null


        }
    }

}