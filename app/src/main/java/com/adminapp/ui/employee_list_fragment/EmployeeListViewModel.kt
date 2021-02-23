package com.adminapp.ui.employee_list_fragment

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*


class EmployeeListViewModel : ViewModel() {
    private lateinit var firestoreListener: ListenerRegistration
    private val database = FirebaseFirestore.getInstance().collection("employees")
    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList
    private var employeesLists = ArrayList<Employee>()


    fun getEmployees() {
        firestoreListener = database.addSnapshotListener { snapshot, it1 ->
            if (it1 != null) {
                Log.w(TAG, "Listen failed.", it1)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                employeesLists.clear()
                for (employee in snapshot.documentChanges) {
                    Log.d("Users List ", "" + employee.document.get("name"))
                    if (employee.type == DocumentChange.Type.ADDED) {
                        employeesLists.add(
                            Employee(
                                id = employee.document.get("id").toString(),
                                name = employee.document.get("name").toString(),
                                phone = employee.document.get("phone").toString(),
                                createdAt = employee.document.get("createdAt").toString().toLong(),
                                updatedAt = employee.document.get("updatedAt").toString().toLong(),
                            )
                        )
                    } else if (employee.type == DocumentChange.Type.REMOVED) {
                        val ids = employee.document.get("id").toString()
                        for (id in 0 until employeesLists.size) {
                            if (employeesLists[id].id == ids) {
                                employeesLists.removeAt(id)
                                break
                            }
                        }

                    } else if (employee.type == DocumentChange.Type.MODIFIED) {

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