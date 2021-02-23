package com.adminapp.prefrences


import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by Rahul on 09/04/2018.
 */
class Preference(var context: Context) {


    fun setUserId(UserId: String?) {
        setString("Id", UserId!!)
    }

    fun getUserId(): String? {
        return getString("Id", null)
    }

    fun setUserDetails(UserDetails: String?) {
        setString("UserDetails", UserDetails!!)
    }

    fun getUserDetails(): String? {
        return getString("UserDetails", null)
    }

    fun setUserName(UserName: String?) {
        setString("UserName", UserName!!)
    }

    fun getUserName(): String? {
        return getString("UserName", null)
    }

    fun setUserEmail(UserEmail: String?) {
        setString("UserEmail", UserEmail!!)
    }

    fun getUserEmail(): String? {
        return getString("UserEmail", null)
    }

    fun setUserPhone(UserPhone: String?) {
        setString("UserPhone", UserPhone!!)
    }

    fun getUserPhone(): String? {
        return getString("UserPhone", null)
    }

    fun setUserProfilePhoto(UserProfilePhoto: String?) {
        setString("UserProfilePhoto", UserProfilePhoto!!)
    }

    fun getUserProfilePhoto(): String? {
        return getString("UserProfilePhoto", null)
    }


    private fun getString(key: String, def: String?): String? {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        return prefs.getString(key, def)
    }

    fun setString(key: String, `val`: String) {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val e = prefs.edit()
        e.putString(key, `val`)
        e.apply()
    }

    fun setBoolean(key: String, `val`: Boolean) {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val e = prefs.edit()
        e.putBoolean(key, `val`)
        e.apply()
    }

    fun getBoolean(key: String): Boolean {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        return prefs.getBoolean(key, false)
    }


    fun clearData() {
        val prefs = PreferenceManager
            .getDefaultSharedPreferences(context)
        val e = prefs.edit()
        e.clear()
        e.apply()
    }


}

