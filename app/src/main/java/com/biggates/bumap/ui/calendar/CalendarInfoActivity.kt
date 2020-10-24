package com.biggates.bumap.ui.calendar

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import kotlinx.android.synthetic.main.activity_calendar_info.*

class CalendarInfoActivity : Activity() {



    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_info)

        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var day = intent.getStringExtra("day")!!
        var contentArray = intent.getStringArrayListExtra("contents")!!
        var layout = calendar_info_layout!!
        var scrollLayout = item_list_view!!
        var layoutParam = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        layoutParam.width = displayMetrics.widthPixels - MyUtil.Dp2Px(applicationContext,70)
        layoutParam.height = MyUtil.Dp2Px(applicationContext,400)
        layout.layoutParams = layoutParam

        day_calendar_info.text = "${day}일"


        for(i in 0 until contentArray.size){
            var textView = TextView(applicationContext)
            var textViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            if(i==0){
                textViewParam.topMargin = MyUtil.Dp2Px(applicationContext,25)
            }else{
                textViewParam.topMargin = MyUtil.Dp2Px(applicationContext,20)
            }

            textView.layoutParams = textViewParam
            textView.text = "●  ${contentArray[i]}"
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
            textView.lineHeight = MyUtil.Dp2Px(applicationContext,27)
            textView.setPadding(60,0,60,0)
            scrollLayout.addView(textView)
        }


    }
}
