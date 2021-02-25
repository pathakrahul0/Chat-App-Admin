package com.adminapp.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getDateTime(
            timestamp: Long,
            format: String,
        ): String? {
            return try {
                val sdf = SimpleDateFormat(format)
                val netDate = Date(timestamp)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }

        fun isValidMailId(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
        }
    }

}