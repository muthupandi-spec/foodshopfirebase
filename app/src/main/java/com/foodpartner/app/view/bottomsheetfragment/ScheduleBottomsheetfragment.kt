package com.foodpartner.app.view.bottomsheetfragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.foodpartner.app.ResponseMOdel.Orderresponsenmodel
import com.foodpartner.app.appControl.AppController
import com.foodpartner.app.databinding.FragmentOrderdetailBinding
import com.foodpartner.app.databinding.FragmentScheduleBinding
import com.foodpartner.app.network.Constant
import com.foodpartner.app.view.adapter.FoodItemAdapter
import com.foodpartner.app.view.eventmodel.Timepickermodel
import com.foodpartner.app.view.responsemodel.RestuarantModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kotlintest.app.utility.interFace.CommonInterface
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Calendar

class ScheduleBottomsheetfragment(var openclosetime: String) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentScheduleBinding
    var time: String = ""
    var starttime: String? = null
    var endtime: String? = null
    var hourr: Int = 0
    var miniute: Int = 0
    var am_pm: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleBinding.inflate(layoutInflater, container, false)
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayString = when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }
        binding.resettxt.setText(dayString)
        if (openclosetime.equals("opentime")) {
            time = "start"
            binding.opentime.isChecked = true
            binding.closetime.isChecked = false
        } else if (openclosetime.equals("closetime")) {
            time = "end"
            binding.opentime.isChecked = false
            binding.closetime.isChecked = true
        }

        binding.opentime.setOnClickListener {
            time = "start"
        }
        binding.closetime.setOnClickListener {
            time = "end"
            println("end")
        }


        binding.confirm.setOnClickListener {
            if (time == "start") {
                Constant.scheduletime = "starttime"
                val hour1 = if (hourr < 10) "0" + hourr else hourr
                val min = if (miniute < 10) "0" + miniute else miniute

                if(hour1 == "00" && min == "00"){
                    var calendar = Calendar.getInstance()
                    var simpleDateFormat = SimpleDateFormat("HH:mm aaa")
                    var currentTime = simpleDateFormat.format(calendar.time).toString()
                    println("current"+currentTime)
//                    homevi.sharHelp.putInUser("starttime", starttime.toString())

                    dismiss()
                }else{
                    starttime = "$hour1 : $min $am_pm"
                    EventBus.getDefault().postSticky(Timepickermodel(
                        starttime.toString(),endtime.toString()))
                    dismiss()
                }

            } else if (time == "end") {
                Constant.scheduletime = "endtime"
                val hour1 = if (hourr < 10) "0$hourr" else hourr
                val min = if (miniute < 10) "0$miniute" else miniute
                if(hour1.equals("00")&&min.equals("00")){
                    var calendar = Calendar.getInstance()
                    var simpleDateFormat = SimpleDateFormat("HH:mm aaa")
                    var currentTime = simpleDateFormat.format(calendar.time).toString()
                    println("current$currentTime")
                    EventBus.getDefault().postSticky(
                        Timepickermodel(endtime.toString(), currentTime.toString())
                    )
                    dismiss()
                }else{
                    endtime = "$hour1 : $min $am_pm"
                    EventBus.getDefault().postSticky(
                        Timepickermodel(
                           starttime.toString(),
                            endtime.toString()
                        )
                    )
                    dismiss()
                }
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.timePicker.setOnTimeChangedListener { _, hour, minute ->
            hourr = hour
            miniute = minute
            println("minute______________"+minute)
            am_pm = ""
            // AM_PM decider logic
            when {
                hourr == 0 -> {
                    hourr += 12
                    am_pm = "AM"
                }

                hourr == 12 -> am_pm = "PM"
                hour > 12 -> {
                    hourr -= 12
                    am_pm = "PM"
                }

                else -> am_pm = "AM"
            }
            val hour1 = if (hourr < 10) "0" + hourr else hourr
            val min = if (miniute < 10) "0" + miniute else miniute
            val msg = "$hour1 : $min $am_pm"
            starttime = "$hour1 : $min $am_pm"
        }



    }




}