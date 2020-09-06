package com.example.bumap.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.bumap.R
import kotlinx.android.synthetic.main.activity_year_month_picker.view.*
import java.util.*


class YearMonthPickerActivity(private var year : String, private var month : String) : DialogFragment() {

    var cal: Calendar = Calendar.getInstance()
    private val MAX_YEAR = cal.get(Calendar.YEAR)+1
    private val MIN_YEAR = cal.get(Calendar.YEAR)
    private var listener: OnDateSetListener? = null


    fun setListener(listener: OnDateSetListener?) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity!!.layoutInflater

        var dialog = inflater.inflate(R.layout.activity_year_month_picker,null)

        var monthPicker : NumberPicker = dialog.findViewById(R.id.picker_month)
        var yearPicker : NumberPicker = dialog.findViewById(R.id.picker_year)

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = month.toInt()
        
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year.toInt()
        
        builder.setView(dialog)
        builder.setNegativeButton("취소", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.cancel()
            }
        })
        builder.setPositiveButton("확인",object  : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                listener!!.onDateSet(null,yearPicker.value, monthPicker.value,0)
                dialog!!.cancel()
            }

        })

        return builder.create()
    }
}
