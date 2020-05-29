package com.example.bumap.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bumap.R


class HBaseAdapter(context: Context, item:ArrayList<String>) : BaseAdapter(){
    private val mContext = context
    private val mItem = item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder : ViewHolder
        var view = convertView
        Log.d("coord",mItem.size.toString())
        var split = mItem[position].split("+")
        if (view == null){
            viewHolder=ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.room_list,parent,false)
            viewHolder.titleTextView = view.findViewById(R.id.room_list_title)
            viewHolder.subTextView = view.findViewById(R.id.room_list_sub)
            view.tag = viewHolder
            if((split[1].get(0) >= 'a' && split[1].get(0) <= 'z') || (split[1].contains("ATM"))){
                viewHolder.titleTextView.text = split[0]
            }else{
                viewHolder.titleTextView.text = split[0]+" ("+split[1]+")"
            }
            viewHolder.subTextView.text = split[2]+" "+split[3]
            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }
        if((split[1].get(0) >= 'a' && split[1].get(0) <= 'z') || (split[1].contains("ATM"))){
            viewHolder.titleTextView.text = split[0]
        }else{
            viewHolder.titleTextView.text = split[0]+" ("+split[1]+")"
        }
        viewHolder.subTextView.text = split[2]+" "+split[3]
        return view
    }

    override fun getItem(position: Int): Any {
        return mItem[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mItem.size
    }

    inner class ViewHolder{
        lateinit var titleTextView: TextView
        lateinit var subTextView: TextView
    }
}