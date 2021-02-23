package com.adminapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getDateTime(
            timestamp: Long
        ): String? {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                val netDate = Date(timestamp)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

}