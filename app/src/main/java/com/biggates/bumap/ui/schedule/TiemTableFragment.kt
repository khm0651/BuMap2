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
import kotlin.properties.Delegates
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.M)
class TiemTableFragment : Fragment() {

    lateinit var time_table_layout : RelativeLayout
    lateinit var v : View
    private var color : Array<Int> = arrayOf(R.color.timeTable1,R.color.timeTable2,R.color.timeTable3,R.color.timeTable4,R.color.timeTable5,R.color.timeTable6,R.color.timeTable7,R.color.timeTable8)
    var layoutCount = 12
    var timeLayoutHeight by Delegates.notNull<Int>()

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
        timeLayoutHeight = MyUtil.Dp2Px(context!!,70)
        time_table_layout = view.time_table_layout
        LectureScheduleViewModel.isViewLoading.observe(viewLifecycleOwner, Observer {
            if(!it) createTimeTable()
        })




        return view
    }



    private fun createTimeTable() {

        createTime()


        for(i in 1 .. layoutCount){
            var linearLayout = LinearLayout(context)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
            linearLayout.layoutParams=linearLayoutParam
            linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_mon.addView(linearLayout)
        }

        for(i in 1 .. layoutCount){
            var linearLayout = LinearLayout(context)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
            linearLayout.layoutParams=linearLayoutParam
            linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_tue.addView(linearLayout)

        }

        for(i in 1 .. layoutCount){
            var linearLayout = LinearLayout(context)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
            linearLayout.layoutParams=linearLayoutParam
            linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_wed.addView(linearLayout)

        }

        for(i in 1 .. layoutCount){
            var linearLayout = LinearLayout(context)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
            linearLayout.layoutParams=linearLayoutParam
            linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_thu.addView(linearLayout)

        }

        for(i in 1 .. layoutCount){
            var linearLayout = LinearLayout(context)
            var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,timeLayoutHeight)
            linearLayout.layoutParams=linearLayoutParam
            linearLayout.background = ContextCompat.getDrawable(context!!,R.drawable.all_solid)
            v.time_table_fri.addView(linearLayout)

        }

        var randNum = 0
        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!
        lectureSchedule.keys.forEach {name ->
            var lecture = lectureSchedule[name] as LinkedTreeMap<String,Any>
            lecture.keys.forEach { l ->
                var lectureList = lecture[l] as LinkedTreeMap<String,Any>
                lectureList.keys.forEach { lectureName ->
                    if(lectureName.lastIndexOf("(") >= 0){

                        var temp = lectureName.substring(lectureName.lastIndexOf("("),lectureName.length)

                        if(temp.contains("[월화수목금사]".toRegex())){
                            temp = temp.replace("[a-zA-Z(),]".toRegex(),"").trim()
                            var name = lectureName.substring(0,lectureName.lastIndexOf("("))

                            if(temp.contains("사")) temp = temp.substring(0,temp.indexOf("사"))
                            if(temp.length > 0){


                                var day = temp.substring(0,1)
                                if(day.contains("[월화수목금]".toRegex())){

                                    temp = temp.substring(1,temp.length)
                                    var timelist = temp.trim().split(" ")
                                    var timeMap = hashMapOf<String,ArrayList<String>>()
                                    var timeArrayList = arrayListOf<String>()

                                    for(i in timelist){
                                        if(i.contains("[월화수목금]".toRegex())) {
                                            if(timeArrayList.isNotEmpty()) {
                                                timeMap.put(day,timeArrayList)
                                                timeArrayList = arrayListOf()
                                            }
                                            day=i
                                        }
                                        else {
                                            timeArrayList.add(i)
                                        }
                                    }
                                    timeMap.put(day,timeArrayList)

                                    timeMap.keys.forEach { day ->
                                        var timelist = timeMap[day]!!
                                        for(i in 0 until timelist.size){
                                            var t = timelist[i].toInt() -1
                                            if(i==0){
                                                var textView = TextView(context)
                                                var param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                                                param.setMargins(20,20,20,20)
                                                textView.layoutParams = param
                                                textView.gravity = Gravity.CENTER
                                                textView.text = name
                                                textView.setTypeface(textView.typeface, Typeface.BOLD)
                                                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10F)
                                                textView.setTextColor(resources.getColor(R.color.white,null))
                                                when(day){
                                                    "월" ->{
                                                        (v.time_table_mon.get(t) as LinearLayout).addView(textView)
                                                        v.time_table_mon.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_mon.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "화" ->{
                                                        (v.time_table_tue.get(t) as LinearLayout).addView(textView)
                                                        v.time_table_tue.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_tue.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "수" ->{
                                                        (v.time_table_wed.get(t) as LinearLayout).addView(textView)
                                                        v.time_table_wed.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_wed.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "목" ->{
                                                        (v.time_table_thu.get(t) as LinearLayout).addView(textView)
                                                        v.time_table_thu.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_thu.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "금" ->{
                                                        (v.time_table_fri.get(t) as LinearLayout).addView(textView)
                                                        v.time_table_fri.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_fri.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }
                                                }
                                            }else{
                                                when(day){
                                                    "월" ->{
                                                        v.time_table_mon.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_mon.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "화" ->{
                                                        v.time_table_tue.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_tue.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "수" ->{
                                                        v.time_table_wed.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_wed.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "목" ->{
                                                        v.time_table_thu.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_thu.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }

                                                    "금" ->{
                                                        v.time_table_fri.get(t).setBackgroundColor(resources.getColor(color[randNum],null))
                                                        v.time_table_fri.get(t).setOnClickListener {
                                                            startActivity(Intent(context,TimeTableInfoActivity::class.java)
                                                                .putExtra("name",lectureName))
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                        randNum++
                                        if(randNum == color.size){
                                            randNum=0
                                        }
                                    }

                                }

                            }

                        }



                    }
                }
            }
        }

    }

    private fun createTime() {
        var startTime = 9
        for(i in 0 until layoutCount){
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
