package com.biggates.bumap.Adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.Bus
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.bus.BusViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.busitem_layout.view.*
import kotlinx.android.synthetic.main.fragment_bus.view.*


class BusAdapter(
    private var mContext: Context,
    var busList: ArrayList<String>,
    private var view: View,
    private var imm: InputMethodManager
)
    :RecyclerView.Adapter<BusAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull item : View) : RecyclerView.ViewHolder(item){
        var title = item.busitem_title
        var layout = item.busitem_layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.busitem_layout,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = busList[position]
        holder.layout.setOnClickListener {

            view.select_layout_title.text = busList[position]
            view.recycler_view_bus.visibility = View.GONE
            view.recycler_view_bus_search.visibility = View.GONE
            view.search_edit_bus.clearFocus()
            view.search_edit_bus.text.clear()
            imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0);

            var bus_image_wrapper = view.bus_image_wrapper
            bus_image_wrapper.removeAllViews()
            view.toHomeLayout.removeAllViews()
            view.toBuLayout.removeAllViews()

            var busListInfo = BusViewModel.busList.value!!
            for(bus in busListInfo){
                if(bus.busStation == busList[position]){
                    for(i in 0 until bus.img!!.size){
                        var imageView = ImageView(mContext)
                        var weightWidth  = (1.toFloat() / bus.img!!.size)
                        var imageParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
                        if(bus.img!!.size ==1){
                            imageParam.width = view.width
                        }else{
                            imageParam.width = view.width / 2
                        }
                        imageView.setPadding(10,10,10,10)
                        imageView.layoutParams = imageParam
                        bus_image_wrapper.addView(imageView)
                        Picasso.get().load(bus.img!![i]).fit().into(imageView)


                    }

                    view.bus_price.text = "가격 : ${bus.pirce}"
                    createToBuTable(bus,view)
                    createToHomeTable(bus,view)
                    break
                }
            }

            view.scrrollView_bus.visibility = View.VISIBLE

        }
    }


    private fun createToHomeTable(bus: Bus, view: View) {

        var titletextview = TextView(mContext)
        var titletextViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        titletextViewParam.topMargin = MyUtil.Int2Dp(mContext,15)
        titletextview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        titletextview.text = "하교"
        titletextview.layoutParams = titletextViewParam

        view.toHomeLayout.addView(titletextview)

        bus.toHome!!.forEach { tohome ->
            var linearLayout = LinearLayout(mContext)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
            linearLayoutParam.topMargin = MyUtil.Int2Dp(mContext,30)
            linearLayout.layoutParams = linearLayoutParam
            linearLayout.orientation = LinearLayout.HORIZONTAL

            var textview = TextView(mContext)
            var textViewParam = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f)
            textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
            textview.text = tohome.key
            textview.layoutParams = textViewParam

            var textview2 = TextView(mContext)
            var textViewParam2 = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.8f)
            textview2.layoutParams = textViewParam2
            textview2.setLineSpacing(MyUtil.Dp2Px(mContext!!,5).toFloat(),MyUtil.Dp2Px(mContext!!,1).toFloat())
            textview2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
            textview2.text = tohome.value

            linearLayout.setPadding(0,0,20,0)
            linearLayout.addView(textview)
            linearLayout.addView(textview2)
            view.toHomeLayout.addView(linearLayout)
        }

        var linearLayout2 = LinearLayout(mContext)
        var linearLayoutParam2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
        linearLayoutParam2.topMargin = MyUtil.Int2Dp(mContext,30)
        linearLayout2.layoutParams = linearLayoutParam2
        linearLayout2.orientation = LinearLayout.HORIZONTAL

        var takePlaceTextview = TextView(mContext)
        var takePlaceTextviewParam = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f)
        takePlaceTextview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        takePlaceTextview.text = "타는곳"
        takePlaceTextview.layoutParams = takePlaceTextviewParam

        var takePlaceTextview2 = TextView(mContext)
        var takePlaceTextviewParam2 = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.8f)
        takePlaceTextview2.layoutParams = takePlaceTextviewParam2
        takePlaceTextview2.setLineSpacing(MyUtil.Dp2Px(mContext!!,5).toFloat(),MyUtil.Dp2Px(mContext!!,1).toFloat())
        takePlaceTextview2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)

        takePlaceTextview2.text = bus.toHomeTakePlace

        linearLayout2.setPadding(0,0,20,0)
        linearLayout2.addView(takePlaceTextview)
        linearLayout2.addView(takePlaceTextview2)
        view.toHomeLayout.addView(linearLayout2)

    }


    private fun createToBuTable(bus: Bus, view: View) {

        var titletextview = TextView(mContext)
        var titletextViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        titletextViewParam.topMargin = MyUtil.Int2Dp(mContext,15)
        titletextview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        titletextview.text = "등교"
        titletextview.layoutParams = titletextViewParam

        view.toBuLayout.addView(titletextview)


        bus.toBu!!.forEach { tobu ->
            var linearLayout = LinearLayout(mContext)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
            linearLayoutParam.topMargin = MyUtil.Int2Dp(mContext,30)
            linearLayout.layoutParams = linearLayoutParam
            linearLayout.orientation = LinearLayout.HORIZONTAL

            var textview = TextView(mContext)
            var textViewParam = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f)
            textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
            textview.text = tobu.key
            textview.layoutParams = textViewParam

            var textview2 = TextView(mContext)
            var textViewParam2 = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.8f)
            textview2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
            textview2.text = tobu.value
            textview2.setLineSpacing(MyUtil.Dp2Px(mContext!!,5).toFloat(),MyUtil.Dp2Px(mContext!!,1).toFloat())
            textview2.layoutParams = textViewParam2

            linearLayout.setPadding(0,0,20,0)
            linearLayout.addView(textview)
            linearLayout.addView(textview2)
            view.toBuLayout.addView(linearLayout)
        }

        var linearLayout2 = LinearLayout(mContext)
        var linearLayoutParam2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f)
        linearLayoutParam2.topMargin = MyUtil.Int2Dp(mContext,30)
        linearLayout2.layoutParams = linearLayoutParam2
        linearLayout2.orientation = LinearLayout.HORIZONTAL

        var takePlaceTextview = TextView(mContext)
        var takePlaceTextviewParam = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.2f)
        takePlaceTextview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        takePlaceTextview.text = "타는곳"
        takePlaceTextview.layoutParams = takePlaceTextviewParam

        var takePlaceTextview2 = TextView(mContext)
        var takePlaceTextviewParam2 = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,0.8f)
        takePlaceTextview2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
        takePlaceTextview2.text = bus.toBuTakePlace
        takePlaceTextview2.setLineSpacing(MyUtil.Dp2Px(mContext!!,5).toFloat(),MyUtil.Dp2Px(mContext!!,1).toFloat())
        takePlaceTextview2.layoutParams = takePlaceTextviewParam2

        linearLayout2.setPadding(0,0,20,0)
        linearLayout2.addView(takePlaceTextview)
        linearLayout2.addView(takePlaceTextview2)
        view.toBuLayout.addView(linearLayout2)

    }




}

