package com.biggates.bumap.ui.calendar

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.biggates.bumap.MainActivity
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.calendar.CalendarViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.biggates.bumap.ViewModel.bus.BusViewModel


class CalendarFragment : Fragment() {

    lateinit var dayCalendar :GridLayout
    private var calendarList =  ArrayList<String>()
    private lateinit var year : String
    private lateinit var month : String
    private lateinit var  day : String
    private lateinit var nowDate : String
    private var allCalendar = arrayListOf<com.biggates.bumap.Model.Calendar>()

    private var isLoading = Observer<Boolean> {
        if(it)view!!.progressbar_calendar.visibility = View.VISIBLE
        else {
            allCalendar = CalendarViewModel.calendarList.value!!
            var cal = GregorianCalendar() // 오늘 날짜

            var calendar = GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) , 1, 0, 0, 0);
            year = cal.get(Calendar.YEAR).toString()
            month = (cal.get(Calendar.MONTH)+1).toString()
            day = cal.get(Calendar.DATE).toString()
            nowDate = "${year}.${month}.${day}"
            createCalendar(calendar)
            view!!.progressbar_calendar.visibility = View.GONE
        }
    }
    private var select : LinearLayout? = null
    private lateinit var v : View
    lateinit var noticeViewModel : CalendarViewModel

    var d = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

        calendarList.clear()
        dayCalendar.removeAllViews()

        calendar_bar_month.text = "${monthOfYear}월"
        calendar_bar_year.text = "${year}년"
        var calendar = GregorianCalendar(year, monthOfYear-1 , 1, 0, 0, 0);
        this.year = year.toString()
        this.month = monthOfYear.toString()
        day = 1.toString()
        nowDate = "${year}.${month}.${day}"

        createCalendar(calendar)

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_calendar, container, false)
        v = view
        dayCalendar = v.day_calender
        var cal = GregorianCalendar()
        v.calendar_bar_month.text =  "${cal.get(Calendar.MONTH)+1}월"
        CalendarViewModel.isViewLoading.observe(viewLifecycleOwner,isLoading)

        if(findNavController().currentDestination.toString() != "home_fragment"){
            activity!!.app_bar_layout_main.visibility = View.GONE
        }

        view.calendar_bar.setOnClickListener {
            var pd = YearMonthPickerActivity(year,month)
            pd.setListener(d)
            pd.show(parentFragmentManager,"YearMonthPicker")
        }



        return view
    }

    private fun createCalendar(calendar: GregorianCalendar) {
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
        var max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일
        var dayOfPreMonth = calendar.getMaximum(Calendar.DAY_OF_MONTH)
        var dayOfNextMonth = 1
        for (j in 0 until dayOfWeek) {
            if(month == "1"){
                calendarList.add("${year.toInt() -1 }.12.${dayOfPreMonth}");  //비어있는 일자 타입
            }else{
                calendarList.add("${year}.${(month.toInt() - 1).toString()}.${dayOfPreMonth.toString()}");  //비어있는 일자 타입
            }

            dayOfPreMonth--
        }
        for (j in 1 .. max) {
            calendarList.add("${year}.${month}.${j.toString()}"); //일자 타입
        }
        for(j in calendarList.size until 42 ){
            if(month == "12"){
                calendarList.add("${(year.toInt() +1)}.1.${dayOfNextMonth}")
            }else{
                calendarList.add("${year}.${(month.toInt() + 1)}.${dayOfNextMonth}")
            }

            dayOfNextMonth++
        }


        val vto: ViewTreeObserver = dayCalendar.getViewTreeObserver()
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    dayCalendar.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                } else {
                    dayCalendar.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }

                for(i in 0 until 42){
                    var linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    linearLayoutParam.width = dayCalendar.width / 7
                    linearLayoutParam.height = dayCalendar.height / 6
                    var linearLayout = LinearLayout(context)
                    linearLayout.layoutParams = linearLayoutParam
                    linearLayout.orientation = LinearLayout.VERTICAL
                    linearLayout.tag = "${calendarList[i]}"

                    if(linearLayout.tag.toString().split(".")[1] != month){

                        if((Calendar.getInstance().get(Calendar.YEAR).toString() == linearLayout.tag.toString().split(".")[0])
                            || ((Calendar.getInstance().get(Calendar.YEAR)+1).toString() == linearLayout.tag.toString().split(".")[0])){
                            linearLayout.setOnClickListener(nextMonthLayoutListener)
                        }

                    }else{
                        linearLayout.setOnClickListener(dayLayoutListener)
                    }


                    var dayNumTextView = TextView(context)
                    var dayNumTextViewParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                    dayNumTextView.layoutParams = dayNumTextViewParam
                    dayNumTextView.text = calendarList[i].split(".")[2]
                    dayNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12F)
                    dayNumTextView.setPadding(10,0,0,0)
                    dayNumTextView.setTypeface(dayNumTextView.getTypeface(), Typeface.BOLD)
                    if(i%7==0) {
                        if(calendarList[i].split(".")[1] != month){
                            dayNumTextView.setTextColor(resources.getColor(R.color.notNowSunday))
                        }else{
                            dayNumTextView.setTextColor(resources.getColor(R.color.sunday))
                        }

                    }
                    else if(i%7==6) {
                        if(calendarList[i].split(".")[1] != month){
                            dayNumTextView.setTextColor(resources.getColor(R.color.notNowSaturday))
                        }else{
                            dayNumTextView.setTextColor(resources.getColor(R.color.saturday))
                        }
                    }else{
                        if(calendarList[i].split(".")[1] != month){
                            dayNumTextView.setTextColor(resources.getColor(R.color.notNowday))
                        }
                    }

                    linearLayout.addView(dayNumTextView)




                    for(c in allCalendar){
                        if(linearLayout.tag == "${c.getYear()}.${c.getMonth()}.${c.getDay()}"){
                            var contentTextView = TextView(context)
                            var textParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                            textParam.setMargins(5,15,30,0)
                            if(calendarList[i].split(".")[1] != month){
                                contentTextView.setTextColor(resources.getColor(R.color.notNowday))
                            }else{
                                contentTextView.setTextColor(resources.getColor(R.color.hamburgerBlack))
                            }
                            contentTextView.layoutParams = textParam
                            contentTextView.tag = c.getContent()
                            if(c.getContent().length>=9){
                                contentTextView.text = "${c.getContent().substring(0,9)} ..."
                            }else{
                                contentTextView.text = "${c.getContent()}"
                            }
                            contentTextView.setPadding(0,0,0,20)
                            contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7F)
                            contentTextView.background = resources.getDrawable(R.drawable.bottom_solid)
                            linearLayout.addView(contentTextView)
                            }

                    }

                    if(linearLayout.tag == nowDate){
                        select = linearLayout
                        select!!.background = resources.getDrawable(R.drawable.calendar_solid)
                    }
                    dayCalendar.addView(linearLayout)

                }

            }
        })
    }

    private var dayLayoutListener : View.OnClickListener = View.OnClickListener {

        it as LinearLayout

        if(select!!.tag.toString() == it.tag.toString()){
            var selectDay = it.tag.toString().split(".")[2]
            var contentArray = arrayListOf<String>()
            for(i in 1 until it.childCount){
                var tv = it.getChildAt(i) as TextView
                contentArray.add(tv.tag.toString())
            }
            startActivity(Intent(context,CalendarInfoActivity::class.java)
                .putExtra("day",selectDay)
                .putExtra("contents",contentArray))
        }else{
            select!!.background = null
            select = it as LinearLayout
            select!!.background = resources.getDrawable(R.drawable.calendar_solid)

        }

    }

    private var nextMonthLayoutListener : View.OnClickListener = View.OnClickListener {

        it as LinearLayout

        calendarList.clear()
        dayCalendar.removeAllViews()
        year = it.tag.toString().split(".")[0]
        month = it.tag.toString().split(".")[1]
        calendar_bar_month.text = "${month}월"
        calendar_bar_year.text = "${year}년"
        var calendar = GregorianCalendar(year.toInt(), month.toInt() , 1, 0, 0, 0);

        day = 1.toString()
        nowDate = "${year}.${month}.${day}"

        createCalendar(calendar)

    }

    private fun moveFromSearchToHome() {
        findNavController().navigate(R.id.action_nav_calendar_to_nav_home)
        (activity as MainActivity).app_bar_layout_main.visibility = View.VISIBLE
    }

    private var onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            moveFromSearchToHome()
        }

    }

}
