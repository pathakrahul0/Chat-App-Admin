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
import com.google.firebase.firestore.Query
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
        .orderBy("name", Query.Direction.ASCENDING)
    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList
    private var employeesLists = ArrayList<Employee>()
    private var chatRoomSenders: ArrayList<String>? = ArrayList()


    fun getEmployees() {
        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .document(preference.getUserId()!!)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("chatRoomReceiver") != null)
                    chatRoomSenders = it.data?.get("chatRoomReceiver") as ArrayList<String>
                getEmployeesList()
            }

    }

    private fun getEmployeesList() {

        fireStoreListener = database.addSnapshotListener { snapshot, it1 ->
            if (it1 != null) {
                Log.w(ContentValues.TAG, "Listen failed.", it1)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                employeesLists.clear()
                for (employee in snapshot.documentChanges) {
                    Log.d("Users List ", "" + employee.document.get("name"))
                    val receiverList =
                        employee.document.get("chatRoomReceiver") as ArrayList<String>

                    if (employee.type == DocumentChange.Type.ADDED) {
                        if (!preference.getUserId()
                                .equals(employee.document.get("id").toString())
                        ) {
                            if (employee.document.get("phone")!! == "Group") {
                                for (receiver in receiverList) {
                                    if (chatRoomSenders?.contains(receiver)!!)
                                        employeesLists.add(
                                            Employee(
                                                id = employee.document.get("id").toString(),
                                                name = employee.document.get("name").toString(),
                                                phone = employee.document.get("phone").toString(),
                                                timeStamp = employee.document.getLong("timeStamp")!!,
                                                chatRoomReceiver = ArrayList(),
                                                createdAt = employee.document.getLong("createdAt")!!,
                                                updatedAt = employee.document.getLong("updatedAt")!!,
                                                isSelected = false
                                            )
                                        )
                                }
                            } else employeesLists.add(
                                Employee(
                                    id = employee.document.get("id").toString(),
                                    name = employee.document.get("name").toString(),
                                    phone = employee.document.get("phone").toString(),
                                    timeStamp = employee.document.getLong("timeStamp")!!,
                                    chatRoomReceiver = ArrayList(),
                                    createdAt = employee.document.getLong("createdAt")!!,
                                    updatedAt = employee.document.getLong("updatedAt")!!,
                                    isSelected = false
                                )
                            )
                        }
                    }
                }
                employeeList.value = employeesLists
            } else
                Log.d(ContentValues.TAG, "Current data: null")

        }

    }


}