package com.adminapp.ui.employee_list_activity


import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class EmployeeListActivityViewModel
@Inject constructor(
    private val preference: Preference,
) : ViewModel() {
    private lateinit var fireStoreListener: ListenerRegistration
    private val database = FirebaseFirestore.getInstance().collection("employees")
    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList
    private var employeesLists = ArrayList<Employee>()


    fun getEmployees() {
        fireStoreListener = database.addSnapshotListener { snapshot, it1 ->
            if (it1 != null) {
                Log.w(ContentValues.TAG, "Listen failed.", it1)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                employeesLists.clear()
                for (employee in snapshot.documentChanges) {
                    Log.d("Users List ", "" + employee.document.get("name"))
                    if (employee.type == DocumentChange.Type.ADDED) {
                        if (!preference.getUserId()
                                .equals(employee.document.get("id").toString())
                        ) {
                            employeesLists.add(
                                Employee(
                                    id = employee.document.get("id").toString(),
                                    name = employee.document.get("name").toString(),
                                    phone = employee.document.get("phone").toString(),
                                    timeStamp = employee.document.getLong("timeStamp")!!,
                                    chatRoomReceiver = ArrayList(),
                                    chatRoom = ArrayList(),
                                    createdAt = employee.document.getLong("createdAt")!!,
                                    updatedAt = employee.document.getLong("updatedAt")!!
                                )
                            )
                        }
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
                        if (!preference.getUserId().equals(employee.document.get("id").toString()))
                            employeesLists.add(
                                Employee(
                                    id = employee.document.get("id").toString(),
                                    name = employee.document.get("name").toString(),
                                    phone = employee.document.get("phone").toString(),
                                    timeStamp = employee.document.getLong("timeStamp")!!,
                                    chatRoomReceiver = ArrayList(),
                                    chatRoom = ArrayList(),
                                    createdAt = employee.document.getLong("createdAt")!!,
                                    updatedAt = employee.document.getLong("updatedAt")!!
                                )
                            )
                    }
                }
                employeeList.value = employeesLists
            } else
                Log.d(ContentValues.TAG, "Current data: null")

        }
    }


}