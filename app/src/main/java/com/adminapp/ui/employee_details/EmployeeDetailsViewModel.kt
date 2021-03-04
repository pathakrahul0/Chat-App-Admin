package com.adminapp.ui.employee_details

import android.util.Log
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeDetailsViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance().collection("employees")

    private val isEmployeeExist = MediatorLiveData<Boolean>()
    val isEmployeeExists: LiveData<Boolean> = isEmployeeExist

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad



    fun addEmployee(name: String, phone: String, createdAt: Long, updatedAt: Long) {
        isLoad.value = true
        val node = database.document()
        database.whereEqualTo("phone", phone).get().addOnCompleteListener { task ->
            isLoad.value = false
            if (task.isSuccessful) {
                if (task.result?.size()!! > 0) {
                    for (document in task.result!!) {
                        if (document.exists()) {
                            Log.d("TAG", "username already exists")
                            isEmployeeExist.value = false
                        }
                    }
                } else {
                    val user = Employee(
                        id = node.id,
                        name = name,
                        phone = phone,
                        profileImageUrl = "",
                        chatRoomReceiver = ArrayList(),
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        timeStamp = 0L,
                        false
                    )

                    node.set(user)
                        .addOnSuccessListener {
                            isEmployeeExist.value = true
                        }.addOnFailureListener {
                        }
                    Log.d("TAG", "username does not exists")
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.exception)
            }
        }.addOnFailureListener{
            isLoad.value = false
        }


    }

    fun updateEmployee(id: String, name: String, phone: String, updatedAt: Long) {
        isLoad.value = true

        val node = database.document(id)
        node.update(
            mapOf(
                "name" to name,
                "phone" to phone,
                "updatedAt" to updatedAt
            )
        ).addOnSuccessListener {
            isLoad.value = false

        }.addOnFailureListener {
            isLoad.value = false

        }

    }

}