package com.adminapp.ui.admin_login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth


class AdminLoginViewModel : ViewModel() {

    val TAG = "AdminLoginViewModel"

    private val isAdminLogin = MutableLiveData<Boolean>()
    var adminLogin: LiveData<Boolean> = isAdminLogin

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad


    fun adminLogin(email: String, password: String ) {
        isLoad.value = true
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoad.value = false
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    isAdminLogin.value = true
                } else {
                    isAdminLogin.value = false
                }
            }.addOnFailureListener {
                isAdminLogin.value = false
                isLoad.value = false
            }
    }

}