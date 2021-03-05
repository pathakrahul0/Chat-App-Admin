package com.adminapp.ui.employee_list_activity


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel
class EmployeeListActivityViewModel
@Inject constructor(
    private val preference: Preference,
) : ViewModel() {

    private val tag = "EmployeeListActivityViewModel"

    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList

    private var employeesLists = ArrayList<Employee>()

    private var chatRoomSenders: ArrayList<String>? = ArrayList()

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad




    fun getEmployees() {
        isLoad.value = true

        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .document(preference.getUserId()!!)
            .get()
            .addOnSuccessListener {

                if (it.data?.get("chatRoomReceiver") != null)
                    chatRoomSenders = it.data?.get("chatRoomReceiver") as ArrayList<String>
                getEmployeesList()
            }.addOnFailureListener {
                isLoad.value = false
                Log.d(tag, "getEmployees OnFailure")
            }

    }

    private fun getEmployeesList() {
        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                isLoad.value = false
                if (task.isSuccessful) {
                    employeesLists.clear()
                    employeeList.value = employeesLists
                    if (task.result?.size()!! > 0) {
                        for (document in task.result!!) {
                            val receiverList = document.get("chatRoomReceiver") as ArrayList<String>
                            if (!preference.getUserId().equals(document.get("id").toString())) {
                                if (document.get("phone")!! == "Group") {
                                    for (receiver in receiverList) {
                                        if (chatRoomSenders?.contains(receiver)!!)
                                            addEmployee(document)
                                    }
                                } else addEmployee(document)
                            }
                        }
                    }
                    employeeList.value = employeesLists
                }

            }
            .addOnFailureListener {
                isLoad.value = false
            }
    }

    private fun addEmployee(document: QueryDocumentSnapshot) {
        employeesLists.add(
            Employee(
                id = document.get("id").toString(),
                name = document.get("name").toString(),
                phone = document.get("phone").toString(),
                profileImageUrl = document.get("profileImageUrl").toString(),
                timeStamp = document.getLong("timeStamp")!!,
                chatRoom = ArrayList(),
                chatRoomReceiver = ArrayList(),
                createdAt = document.getLong("createdAt")!!,
                updatedAt = document.getLong("updatedAt")!!,
                isSelected = false
            )
        )
    }

}