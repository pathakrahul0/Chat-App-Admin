package com.adminapp.ui.employee_login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import java.util.concurrent.TimeUnit

class EmployeeLoginViewModel : ViewModel() {
    val rootRef = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val allUsersRef = rootRef.collection("employees")
    private var storedVerificationId: String? = null
    private var code: String? = null
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken


    fun getUserRef(phone: String?, activity: EmployeeLoginActivity) {
        val userNameQuery: Query = allUsersRef.whereEqualTo("phone", phone)
        auth.setLanguageCode(Locale.getDefault().language)
        val option = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91" + phone!!)
            .setTimeout(60, TimeUnit.SECONDS).setActivity(activity)
            .setCallbacks(callbacks).build()
        userNameQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    if (document.exists()) {
                        PhoneAuthProvider.verifyPhoneNumber(option)
                        Log.d("TAG", "username already exists")
                    } else {
                        Log.d("TAG", "username does not exists")
                    }
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }

    }

    fun verifyPhoneNumberWithCode(code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    fun resendVerificationCode(
        phoneNumber: String,
        activity: EmployeeLoginActivity
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth).setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(activity).setCallbacks(callbacks).setForceResendingToken(resendToken)
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d("TAG", "Login Complete")
                } else {
                    Log.d("TAG", "Login Failed")
                }
            }
    }

    var callbacks = object :
        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {}
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token
        }
    }
}


