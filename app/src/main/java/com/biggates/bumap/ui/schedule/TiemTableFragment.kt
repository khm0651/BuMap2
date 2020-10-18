package com.biggates.bumap.ui.schedule

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import com.biggates.bumap.Model.LectureSchedule
import com.biggates.bumap.MyUtil

import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import kotlinx.android.synthetic.main.fragment_tiem_table.view.*
import java.util.*
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.N)
class TiemTableFragment : Fragment() {

    lateinit var time_table_layout : RelativeLayout
    lateinit var v : View
    private var color : Array<Int> = arrayOf(R.color.timeTable1,R.color.timeTable2,R.color.timeTable3,R.color.timeTable4,R.color.timeTable5,R.color.timeTable6,R.color.timeTable7,R.color.timeTable8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_tiem_table, container, false)
        v =view
        time_table_layout = view.time_table_layout
        LectureScheduleViewModel.isViewLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                view.progressbar_time_table.visibility = View.VISIBLE
            }else{
                view.progressbar_time_table.visibility = View.GONE
                createTimeTable()

            }
        })




        return view
    }



    private fun createTimeTable() {
        val vto: ViewTreeObserver = time_table_layout.getViewTreeObserver()
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    time_table_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                } else {
                    time_table_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
                createTime()

                var timeLayoutHeight = v.time_table_time.height / 9
                for(i in 1 .. 9){
                    var linearLayout = LinearLayout(context)
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
                    linearLayout.layoutParams=linearLayoutParam
                    linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
                    v.time_table_mon.addView(linearLayout)
                }

                for(i in 1 .. 9){
                    var linearLayout = LinearLayout(context)
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
                    linearLayout.layoutParams=linearLayoutParam
                    linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
                    v.time_table_tue.addView(linearLayout)

                }

                for(i in 1 .. 9){
                    var linearLayout = LinearLayout(context)
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
                    linearLayout.layoutParams=linearLayoutParam
                    linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
                    v.time_table_wed.addView(linearLayout)

                }

                for(i in 1 .. 9){
                    var linearLayout = LinearLayout(context)
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
                    linearLayout.layoutParams=linearLayoutParam
                    linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
                    v.time_table_thu.addView(linearLayout)

                }

                for(i in 1 .. 9){
                    var linearLayout = LinearLayout(context)
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
                    linearLayout.layoutParams=linearLayoutParam
                    linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
                    v.time_table_fri.addView(linearLayout)

                }

                var randNum = 0
                var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!
                lectureSchedule.forEach { name , lecture ->
                    (lecture as LinkedTreeMap<String,Any>).forEach { key, l ->
                        (l as LinkedTreeMap<String,Any>).forEach { lectureName, u ->
                            var split = lectureName.split("(")
                            var name = split[0]
                            var temp = split[1].replace("[^가-힣0-9]".toRegex(),"")
                            var day = temp.substring(0,1)
                            var time = temp.substring(1,temp.length).toCharArray()

                            for(i in 0 until time.size){
                                var t = time[i].toString().toInt() -1
                                if(i==0){
                                    var textView = TextView(context)
                                    var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                                    param.setMargins(20,20,20,20)
                                    textView.layoutParams = param
                                    textView.gravity = Gravity.CENTER
                                    textView.text = name
                                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                                    textView.lineHeight = MyUtil.Dp2Px(context!!,20)
                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10F)
                                    textView.setTextColor(resources.getColor(R.color.white,null))
                                    when(day){
                                        "월" ->{
                                            (v.time_table_mon.get(t) as LinearLayout).addView(textView)
                                            v.time_table_mon.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_mon.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "화" ->{
                                            (v.time_table_tue.get(t) as LinearLayout).addView(textView)
                                            v.time_table_tue.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_tue.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "수" ->{
                                            (v.time_table_wed.get(t) as LinearLayout).addView(textView)
                                            v.time_table_wed.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_wed.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "목" ->{
                                            (v.time_table_thu.get(t) as LinearLayout).addView(textView)
                                            v.time_table_thu.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_thu.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "금" ->{
                                            (v.time_table_fri.get(t) as LinearLayout).addView(textView)
                                            v.time_table_fri.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_fri.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }
                                    }
                                }else{
                                    when(day){
                                        "월" ->{
                                            v.time_table_mon.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_mon.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "화" ->{
                                            v.time_table_tue.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_tue.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "수" ->{
                                            v.time_table_wed.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_wed.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "목" ->{
                                            v.time_table_thu.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_thu.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }

                                        "금" ->{
                                            v.time_table_fri.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                            v.time_table_fri.get(t).setOnClickListener {
                                                startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                    .putExtra("name",name))
                                            }
                                        }
                                    }
                                }

                            }
                            randNum++
                            if(randNum == 8){
                                randNum=0
                            }
                        }

                    }
                    vto.removeOnGlobalLayoutListener { this }
                }

            }

        })

    }

    private fun createTime() {
        var startTime = 9
        var timeLayoutHeight = v.time_table_time.height / 9
        for(i in 0 until 9){
            var textView = TextView(context)
            textView.height = timeLayoutHeight
            textView.text = startTime.toString()
            textView.gravity = Gravity.RIGHT
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9F)
            textView.setPadding(0,10,5,0)
            textView.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_time.addView(textView)
            startTime++
            if(startTime == 13){
                startTime = 1
            }
        }
    }


}
