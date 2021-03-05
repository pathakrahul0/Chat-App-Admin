package com.adminapp.ui.employee_list_fragment

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class EmployeeListViewModel : ViewModel() {


    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList

    private var employeesLists = ArrayList<Employee>()

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad

    fun getEmployees() {
        isLoad.value = true
        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, it1 ->
                isLoad.value = false
                if (it1 != null) {
                    Log.w(TAG, "Listen failed.", it1)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    employeesLists.clear()
                    for (employee in snapshot.documentChanges) {
                        Log.d("Users List ", "" + employee.document.get("name"))
                        if (employee.type == DocumentChange.Type.ADDED &&
                            !employee.document.get("phone")?.equals("Group")!!
                        ) {
                            employeesLists.add(
                                Employee(
                                    id = employee.document.get("id").toString(),
                                    name = employee.document.get("name").toString(),
                                    phone = employee.document.get("phone").toString(),
                                    profileImageUrl = employee.document.get("profileImageUrl")
                                        .toString(),
                                    timeStamp = employee.document.getLong("timeStamp")!!,
                                    chatRoom = ArrayList(),
                                    chatRoomReceiver = ArrayList(),
                                    createdAt = employee.document.getLong("createdAt")!!,
                                    updatedAt = employee.document.getLong("updatedAt")!!,
                                    isSelected = false
                                )
                            )
                        } else if (employee.type == DocumentChange.Type.REMOVED &&
                            !employee.document.get("phone")?.equals("Group")!!
                        ) {
                            val ids = employee.document.get("id").toString()
                            for (id in 0 until employeesLists.size) {
                                if (employeesLists[id].id == ids) {
                                    employeesLists.removeAt(id)
                                    break
                                }
                            }

                        } else if (employee.type == DocumentChange.Type.MODIFIED &&
                            !employee.document.get("phone")?.equals("Group")!!
                        ) {

                            val ids = employee.document.get("id").toString()
                            for (id in 0 until employeesLists.size) {
                                if (employeesLists[id].id == ids) {
                                    employeesLists.removeAt(id)
                                    break
                                }
                            }
                        }
                    }
                    employeeList.value = employeesLists
                } else
                    Log.d(TAG, "Current data: null")

            }

    }

}