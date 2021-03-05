package com.adminapp.ui.group

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.model.ChatRoom
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class GroupViewModel
@Inject constructor(
    val preference: Preference
) : ViewModel() {

    private var chatRooms: ArrayList<ChatRoom>? = ArrayList()
    private var chatRoomReceivers: ArrayList<String>? = ArrayList()
    private var employeeIds: ArrayList<String>? = ArrayList()
    var chatRoomId: String? = null


    private val isUpdate = MediatorLiveData<Boolean>()
    val isUpdated: LiveData<Boolean> = isUpdate

    private val database = FirebaseFirestore.getInstance()
    private val isEmployeeExist = MediatorLiveData<Boolean>()
    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad

    private var employeeList = MutableLiveData<ArrayList<Employee>>()
    var employeesData: LiveData<ArrayList<Employee>> = employeeList
    private var employeesLists = ArrayList<Employee>()


    fun getEmployees() {
        employeesLists.clear()
        FirebaseFirestore.getInstance().collection("employees")
            .orderBy("name", Query.Direction.ASCENDING).addSnapshotListener { snapshot, it1 ->
                if (it1 != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", it1)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    for (employee in snapshot.documentChanges) {
                        if (
                            !preference.getUserId().equals(employee.document.get("id").toString())
                            && !employee.document.get("phone")?.equals("Group")!!
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
                        }
                    }
                    employeeList.value = employeesLists
                } else
                    Log.d(ContentValues.TAG, "Current data: null")
            }
    }


    fun createGroup(name: String, employeeIds: ArrayList<String>) {
        this.employeeIds?.clear()
        this.employeeIds?.addAll(employeeIds)
        isLoad.value = true
        val node = database.collection("employees").document()
        chatRoomId = node.id
        this.employeeIds?.add(chatRoomId!!)
        val user = Employee(
            id = node.id,
            name = name,
            phone = "Group",
            profileImageUrl = "",
            chatRoom = ArrayList(),
            chatRoomReceiver = ArrayList(),
            createdAt = Date().time,
            updatedAt = Date().time,
            timeStamp = Date().time,
            isSelected = false
        )

        node.set(user)
            .addOnSuccessListener {
                isLoad.value = false
                for (employeeId in this.employeeIds!!) {
                    FirebaseFirestore.getInstance()
                        .collection("employees")
                        .document(employeeId)
                        .get()
                        .addOnSuccessListener {
                            chatRooms?.clear()
                            chatRoomReceivers?.clear()
                            if (it.data?.get("chatRoomReceiver") != null)
                                chatRoomReceivers?.addAll(it.data?.get("chatRoomReceiver") as ArrayList<String>)
                            if (it.data?.get("chatRoom") != null)
                                chatRooms?.addAll(it.data?.get("chatRoom") as ArrayList<ChatRoom>)
                            chatRoomReceivers?.add(chatRoomId!!)
                            val chatRoom = ChatRoom()
                            chatRoom.id = chatRoomId
                            chatRoom.chatId = chatRoomId
                            chatRooms?.add(chatRoom)
                            FirebaseFirestore
                                .getInstance()
                                .collection("employees")
                                .document(employeeId)
                                .update(
                                    mapOf(
                                        "chatRoom" to chatRooms,
                                        "chatRoomReceiver" to chatRoomReceivers
                                    )
                                )
                                .addOnSuccessListener {
                                    chatRooms?.clear()
                                    chatRoomReceivers?.clear()
                                }
                        }
                    isUpdate.value = true
                }

            }.addOnFailureListener {
                isLoad.value = false
            }

    }




}