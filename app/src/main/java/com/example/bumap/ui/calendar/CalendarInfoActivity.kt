package com.example.bumap.ui.calendar

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.bumap.R
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
        var layoutParam = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.width = displayMetrics.widthPixels - 350
        layoutParam.height = displayMetrics.heightPixels - 1350
        layout.layoutParams = layoutParam

        day_calendar_info.text = "${day}일"


        for(i in 0 until contentArray.size){
            var textView = TextView(applicationContext)
            var textViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            if(i==0){
                textViewParam.topMargin = 50
            }else{
                textViewParam.topMargin = 30
            }

            textView.layoutParams = textViewParam
            textView.text = "●  ${contentArray[i]}"
            textView.textSize = 16F
            textView.lineHeight = 100
            textView.setPadding(60,0,60,0)
            layout.addView(textView)
        }


    }
}
