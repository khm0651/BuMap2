package com.biggates.bumap.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.BtnKeyword
import com.biggates.bumap.R

class BtnKeywordAdapter(private val items: List<BtnKeyword>) :
    RecyclerView.Adapter<BtnKeywordAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.btn_keyword_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemBtn: Button = itemView.findViewById(R.id.item_Button)

        fun bind(item: BtnKeyword) {
            itemBtn.text = item.title
        }
    }

}