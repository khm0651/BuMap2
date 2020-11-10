package com.biggates.bumap.ui.schedule

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.biggates.bumap.MyUtil
import com.biggates.bumap.R
import com.biggates.bumap.ViewModel.schedule.LectureScheduleViewModel
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_time_table_info.*
import kotlinx.android.synthetic.main.empty_work_layout.view.*
import kotlinx.android.synthetic.main.video_layout.view.*
import kotlinx.android.synthetic.main.work_layout.view.*
import kotlinx.android.synthetic.main.work_layout.view.period
import kotlinx.android.synthetic.main.work_layout.view.presentImg
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TimeTableInfoActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table_info)

        var displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var layout = time_table_info_layout!!
        var layoutParam = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParam.width = displayMetrics.widthPixels - MyUtil.Dp2Px(applicationContext, 70)
        layoutParam.height = MyUtil.Dp2Px(applicationContext, 400)
        layout.layoutParams = layoutParam
        var name = intent.getStringExtra("name").toString().trim()
        var noticeList = ArrayList<String>()
        var lectureSchedule = LectureScheduleViewModel.lectureSchedule.value!!.getSchedule()!!

        lectureSchedule.keys.forEach { user ->
            var lecture = lectureSchedule[user] as LinkedTreeMap<String, Any>
            lecture.keys.forEach { l ->
                var lectureList = lecture[l] as LinkedTreeMap<String, Any>
                var info = lectureList[name] as LinkedTreeMap<String, Any>

                info.keys.forEach { todoName ->

                    var weekList = ArrayList<String>()
                    var fomatter = SimpleDateFormat("yyyy.MM.dd")
                    var calendar = Calendar.getInstance()
                    var nowday = calendar.get(Calendar.DAY_OF_WEEK)
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                    if (nowday == 1) calendar.add(Calendar.DATE, -7)
                    for (i in 0 until 7) {
                        calendar.add(Calendar.DATE, 1)
                        weekList.add(fomatter.format(calendar.time))
                    }
                    when (todoName) {
                        "과제" -> {
                            var todoList = info[todoName] as LinkedTreeMap<String, Any>
                            todoList.keys.forEach { index ->
                                var title =
                                    (todoList[index] as LinkedTreeMap<String, String>).get("title")!!
                                        .trim()
                                var content =
                                    (todoList[index] as LinkedTreeMap<String, String>).get("content")!!
                                        .trim()
                                var time =
                                    (todoList[index] as LinkedTreeMap<String, LinkedTreeMap<String, String>>).get(
                                        "time"
                                    )!!.get("제출기간")!!.trim()
                                var present =
                                    (todoList[index] as LinkedTreeMap<String, LinkedTreeMap<String, String>>).get(
                                        "isPresent"
                                    )!!.get("제출여부")!!.trim()

                                var timeSplit = time!!.split("~")
                                var startTime = timeSplit[0].trim().split(" ")[0].trim()
                                var endTime = timeSplit[1].trim().split(" ")[0].trim()
                                var startC = Calendar.getInstance()
                                var endC = Calendar.getInstance()
                                var isTodo = false

                                startC.set(
                                    startTime.split("-")[0].toInt(),
                                    startTime.split("-")[1].toInt() - 1,
                                    startTime.split("-")[2].toInt()
                                )
                                endC.set(
                                    endTime.split("-")[0].toInt(),
                                    endTime.split("-")[1].toInt() - 1,
                                    endTime.split("-")[2].toInt()
                                )
                                var diffDay =
                                    (((endC.timeInMillis - startC.timeInMillis) / 1000) / (24 * 60 * 60)).toInt()
                                var periodList = ArrayList<String>()

                                periodList.add(fomatter.format(startC.time))
                                for (i in 0 until diffDay) {
                                    startC.add(Calendar.DATE, 1)
                                    periodList.add(fomatter.format(startC.time))
                                }

                                for (period in periodList) {
                                    if (isTodo) break
                                    for (week in weekList) {
                                        if (period == week) {
                                            isTodo = true
                                            break
                                        }

                                    }
                                }
                                if (isTodo) {
                                    var layout = layoutInflater.inflate(R.layout.work_layout, null)
                                    layout.title_work.text = title
                                    layout.period.text = time
                                    if (present == "제출완료") layout.presentImg.setImageResource(R.drawable.check)
                                    else layout.presentImg.setImageResource(R.drawable.cancel)
                                    workLayout.addView(layout)
                                }

                            }

                            if (workLayout.childCount == 1) {
                                var empty_layout =
                                    layoutInflater.inflate(R.layout.empty_work_layout, null)
                                empty_layout.empty_title.text = "해당 주차의 과제가 없습니다."
                                workLayout.addView(empty_layout)
                            }

                        }

                        "학습목차" -> {
                            var todoList = info[todoName] as LinkedTreeMap<String, Any>
                            todoList.keys.forEach { period ->
                                var p = period
                                if (period.indexOf("주") - 1 >= 0 && period[period.indexOf("주") - 1].equals(
                                        ' '
                                    )
                                ) p = p.replaceFirst(" ", "")

                                var keySplit = p.split("~")
                                var endTime = keySplit[1].trim()
                                p = keySplit[0].trim()
                                var title = "${p.substring(0, p.lastIndexOf(" ")).trim()} "
                                var startTime = p.substring(p.lastIndexOf(" "), p.length).trim()

                                var startC = Calendar.getInstance()
                                var endC = Calendar.getInstance()
                                var isTodo = false

                                startC.set(
                                    startTime.split("-")[0].toInt(),
                                    startTime.split("-")[1].toInt() - 1,
                                    startTime.split("-")[2].toInt()
                                )
                                endC.set(
                                    endTime.split("-")[0].toInt(),
                                    endTime.split("-")[1].toInt() - 1,
                                    endTime.split("-")[2].toInt()
                                )
                                var diffDay =
                                    (((endC.timeInMillis - startC.timeInMillis) / 1000) / (24 * 60 * 60)).toInt()
                                var periodList = ArrayList<String>()

                                periodList.add(fomatter.format(startC.time))
                                for (i in 0 until diffDay) {
                                    startC.add(Calendar.DATE, 1)
                                    periodList.add(fomatter.format(startC.time))
                                }

                                for (period in periodList) {
                                    if (isTodo) break
                                    for (week in weekList) {
                                        if (period == week) {
                                            isTodo = true
                                            break
                                        }

                                    }
                                }

                                if (isTodo) {
                                    var list =
                                        todoList[period] as LinkedTreeMap<String, LinkedTreeMap<String, String>>
                                    list.keys.forEach { key ->
                                        var content = list[key]!!
                                        if (key.startsWith("video")) {
                                            var v_title = content.get("title").toString()
                                            var time = content.get("state").toString()

                                            if (time != "학습안함") {
                                                var right = time.substring(
                                                    time.lastIndexOf("/") + 1,
                                                    time.length
                                                ).trim()
                                                var left =
                                                    time.substring(0, time.indexOf("/")).trim()
                                                var r_right = right.replace(" ", "")
                                                var r_left = left.replace(" ", "")


                                                var rc = Calendar.getInstance()
                                                var rc_m = ""
                                                var rc_s = ""
                                                if (r_right.contains("분") && r_right.contains("초")) {
                                                    rc_m =
                                                        r_right.substring(0, r_right.indexOf("분"))
                                                            .trim()
                                                    rc_s = r_right.substring(
                                                        r_right.indexOf("분") + 1,
                                                        r_right.indexOf("초")
                                                    ).trim()
                                                    rc.set(Calendar.MINUTE, rc_m.toInt())
                                                    rc.set(Calendar.SECOND, rc_s.toInt())
                                                } else {
                                                    if (r_right.contains("초")) {
                                                        rc_s = r_right.substring(
                                                            r_right.indexOf("분") + 1,
                                                            r_right.indexOf("초")
                                                        ).trim()
                                                        rc.set(Calendar.SECOND, rc_s.toInt())
                                                    } else rc.set(Calendar.SECOND, 0)

                                                    if (r_right.contains("분")) {
                                                        rc_m = r_right.substring(
                                                            0,
                                                            r_right.indexOf("분")
                                                        ).trim()
                                                        rc.set(Calendar.MINUTE, rc_m.toInt())
                                                    } else rc.set(Calendar.MINUTE, 0)
                                                }

                                                var lc = Calendar.getInstance()
                                                var lc_m = ""
                                                var lc_s = ""

                                                if (r_left.contains("분") && r_left.contains("초")) {
                                                    lc_m = r_left.substring(0, r_left.indexOf("분"))
                                                        .trim()
                                                    lc_s = r_left.substring(
                                                        r_left.indexOf("분") + 1,
                                                        r_left.indexOf("초")
                                                    ).trim()
                                                    lc.set(Calendar.MINUTE, lc_m.toInt())
                                                    lc.set(Calendar.SECOND, lc_s.toInt())
                                                } else {
                                                    if (r_left.contains("초")) {
                                                        lc_s = r_left.substring(
                                                            r_left.indexOf("분") + 1,
                                                            r_left.indexOf("초")
                                                        ).trim()
                                                        lc.set(Calendar.SECOND, lc_s.toInt())
                                                    } else lc.set(Calendar.SECOND, 0)

                                                    if (r_left.contains("분")) {
                                                        lc_m =
                                                            r_left.substring(0, r_left.indexOf("분"))
                                                                .trim()
                                                        lc.set(Calendar.MINUTE, lc_m.toInt())
                                                    } else lc.set(Calendar.MINUTE, 0)
                                                }

                                                var layout = layoutInflater.inflate(
                                                    R.layout.video_layout,
                                                    null
                                                )
                                                layout.title_video.text = v_title
                                                layout.time_video.text = time
                                                layout.period.text = "${startTime} ~ ${endTime}"
                                                if (lc.compareTo(rc) < 0) layout.presentImg.setImageResource(
                                                    R.drawable.cancel
                                                )
                                                else layout.presentImg.setImageResource(R.drawable.check)
                                                videoLayout.addView(layout)
                                            } else {
                                                var layout = layoutInflater.inflate(
                                                    R.layout.video_layout,
                                                    null
                                                )
                                                layout.title_video.text = v_title
                                                layout.time_video.text = "학습안함"
                                                layout.period.text = "${startTime} ~ ${endTime}"
                                                layout.presentImg.setImageResource(R.drawable.cancel)
                                                videoLayout.addView(layout)
                                            }
                                        }
                                    }

                                }

                            }

                            if (videoLayout.childCount == 1) {
                                var empty_layout =
                                    layoutInflater.inflate(R.layout.empty_work_layout, null)
                                empty_layout.empty_title.text = "해당 주차의 영상이 없습니다."
                                videoLayout.addView(empty_layout)
                            }

                        }
                    }

                }

            }
        }
    }

}

