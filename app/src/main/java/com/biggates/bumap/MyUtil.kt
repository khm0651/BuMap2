package com.biggates.bumap

import android.content.Context
import android.util.TypedValue

class MyUtil {
    companion object{
        fun Int2Dp(context : Context, num : Int) : Int{
            var dm = context.resources.displayMetrics
            var px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,num.toFloat(),dm).toInt()

            return (px / dm.density).toInt()
        }
    }
}