package com.adminapp.ui.employee_login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
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

    private val tag = "EmployeeLoginViewModel"

    private val auth = FirebaseAuth.getInstance()

    private val isVerified = MediatorLiveData<Boolean>()
    val isVerifiedUser: LiveData<Boolean> = isVerified

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad

     val isHideSendOtpBtns = MediatorLiveData<Boolean>()
    val isHideSendOtpBtn: LiveData<Boolean> = isHideSendOtpBtns

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private var storedVerificationId: String? = null

    private val database = FirebaseFirestore.getInstance().collection("employees")


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

        auth.setLanguageCode(Locale.getDefault().language)

        val userNameQuery: Query = FirebaseFirestore
            .getInstance()
            .collection("employees")
            .whereEqualTo("phone", phone)

        val option = PhoneAuthOptions
            .newBuilder(auth)
            .setPhoneNumber("+91" + phone!!)
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        userNameQuery
            .get()
            .addOnCompleteListener { task ->
                isLoad.value = false
                if (task.isSuccessful) {
                    if (task.result?.size()!! > 0) {
                        for (document in task.result!!) {
                            if (document.exists()) {
                                preference.setUserId(document.data["id"].toString())
                                preference.setUserPhone(document.data["phone"].toString())
                                preference.setUserName(document.data["name"].toString())
                                preference.setUserProfilePhoto(document.data["profileImageUrl"].toString())
                                PhoneAuthProvider.verifyPhoneNumber(option)
                                Log.d("TAG", "username already exists")
                            } else {
                                isVerified.value = false
                                Log.d("TAG", "username does not exists")
                            }
                        }
                    } else {
                        addEmployee(phone, activity)
                        Log.d("TAG", "username does not exists")
                    }

                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }.addOnFailureListener {
                isLoad.value = false

            }

    }

    fun resendVerificationCode(phoneNumber: String, activity: EmployeeLoginActivity) {
        isLoad.value = true
        val optionsBuilder = PhoneAuthOptions
            .newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        isLoad.value = false
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            isHideSendOtpBtns.value = true
            isLoad.value = false
            Log.d(tag, "onVerificationCompleted: $credential")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            isHideSendOtpBtns.value = false
            isLoad.value = false
            Log.d(tag, "onVerificationFailed: ${e.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(tag, "onCodeSent:  $verificationId $token")
            isLoad.value = false
            isHideSendOtpBtns.value = !(verificationId.isEmpty() && token.toString().isEmpty())
            storedVerificationId = verificationId
            resendToken = token
        }
    }


    fun verifyPhoneNumberWithCode(code: String) {
        isLoad.value = true
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isLoad.value = false
                if (task.isSuccessful) {

                    isVerified.value = true
                    Log.d("TAG", "Login Complete")
                } else {
                    Log.d("TAG", "Login Failed")
                }
            }.addOnFailureListener {
                isLoad.value = false
            }
    }


    private fun addEmployee(phone: String, activity: EmployeeLoginActivity) {
        val option = PhoneAuthOptions
            .newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        val node = FirebaseFirestore.getInstance().collection("employees")
            .document()
        val user = Employee(
            id = node.id,
            name = "Unknown Employee",
            phone = phone,
            profileImageUrl = "",
            chatRoomReceiver = ArrayList(),
            createdAt = Date().time,
            updatedAt = Date().time,
            timeStamp = Date().time,
            false
        )

        node.set(user)
            .addOnSuccessListener {
                preference.setUserId(node.id)
                preference.setUserPhone(phone)
                preference.setUserName("Unknown Employee")
                preference.setUserProfilePhoto("")
                PhoneAuthProvider.verifyPhoneNumber(option)

            }.addOnFailureListener {

            }
        Log.d("TAG", "username does not exists")
    }


}


