package com.adminapp.ui.employee_login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.prefrences.Preference
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmployeeLoginViewModel
@Inject constructor(
    private val preference: Preference,
) : ViewModel() {

    private var employeeId: String? = null
    val rootRef = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val allUsersRef = rootRef.collection("employees")
    private var storedVerificationId: String? = null
    private val isVerifed = MediatorLiveData<Boolean>()
    val isVerifedUser: LiveData<Boolean> = isVerifed
    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken



    fun validatePhone(phone: String): Boolean {
        if (phone.isEmpty())
            return false
        else if (phone.length < 10)
            return false
        return true
    }
    fun validateOTP(otp: String): Boolean {
        if (otp.isEmpty())
            return false
        else if (otp.length < 6)
            return false
        return true
    }


    fun getUserRef(phone: String?, activity: EmployeeLoginActivity) {
        isLoad.value = true
        val userNameQuery: Query = allUsersRef.whereEqualTo("phone", phone)
        auth.setLanguageCode(Locale.getDefault().language)
        val option = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91" + phone!!)
            .setTimeout(60, TimeUnit.SECONDS).setActivity(activity)
            .setCallbacks(callbacks).build()
        userNameQuery.get().addOnCompleteListener { task ->
            isLoad.value = false
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.exists()) {
                        employeeId = document.data["id"].toString()
                        PhoneAuthProvider.verifyPhoneNumber(option)
                        Log.d("TAG", "username already exists")
                    } else {
                        isVerifed.value = false
                        Log.d("TAG", "username does not exists")
                    }
                }
            } else {
                isLoad.value = false
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }.addOnFailureListener {
            isLoad.value = false
        }

    }
    fun resendVerificationCode(phoneNumber: String, activity: EmployeeLoginActivity) {
        isLoad.value = true
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(activity).setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        isLoad.value = false
    }
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
        override fun onVerificationFailed(e: FirebaseException) {}
        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            isLoad.value = false
            storedVerificationId = verificationId
            resendToken = token
        }
    }


    fun verifyPhoneNumberWithCode(code: String) {
        isLoad.value = true
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoad.value = false
                if (task.isSuccessful) {
                    val user = task.result?.user
                    preference.setUserId(employeeId)
                    isVerifed.value = true
                    Log.d("TAG", "Login Complete")
                } else {
                    Log.d("TAG", "Login Failed")
                }
            }.addOnFailureListener {
                isLoad.value = false
            }
    }
}


