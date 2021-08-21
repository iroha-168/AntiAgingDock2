package com.antiaginglab.antiagingdockapp2

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var dp = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Dialog,
            this,
            year,
            month,
            day
        )

        dp.datePicker.calendarViewShown = false
        return dp
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val editTxtBirthday = activity?.findViewById<EditText>(R.id.edit_txt_birthday)
        editTxtBirthday?.setText(String.format("%d/%02d/%02d", year, month+1, dayOfMonth))
    }
}