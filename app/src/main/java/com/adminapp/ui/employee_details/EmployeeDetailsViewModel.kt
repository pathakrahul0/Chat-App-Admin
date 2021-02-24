package com.adminapp.ui.employee_details

import androidx.lifecycle.ViewModel
import com.adminapp.model.ChatRoom
import com.adminapp.model.Employee
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeDetailsViewModel : ViewModel() {
    private val database = FirebaseFirestore.getInstance()


    fun addEmployee(name: String, phone: String, createdAt: Long, updatedAt: Long) {
        val node = database
            .collection("employees")
            .document()
        val user = Employee(
            id = node.id,
            name = name,
            phone = phone,
            chatRoom = ArrayList(),
            chatRoomReceiver = ArrayList(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            timeStamp = 0L
        )

        node.set(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
    }

    fun updateEmployee(id: String, name: String, phone: String, updatedAt: Long) {
        val node = database
            .collection("employees")
            .document(id)

        node.update(
            mapOf(
                "name" to name,
                "phone" to phone,
                "updatedAt" to updatedAt
            )
        ).addOnSuccessListener {

        }.addOnFailureListener {
            // Write failed
            // ...
        }

    }

}