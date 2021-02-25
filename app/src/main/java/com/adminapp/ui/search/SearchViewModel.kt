package com.adminapp.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import com.adminapp.model.Employee
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject constructor(
    val preference: Preference
) : ViewModel() {


    private var employeesLists = ArrayList<Employee>()

    fun filterByName(name: String): ArrayList<Employee> {
        FirebaseFirestore.getInstance()
            .collection("employees")
            .orderBy("name", Query.Direction.ASCENDING)
            .whereGreaterThanOrEqualTo("name", name)
            .whereLessThan("name", name)
            .get().addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        if (document.exists()) {
                            if (!preference.getUserId().equals(document.get("id").toString())) {
                                employeesLists.add(
                                    Employee(
                                        id = document.get("id").toString(),
                                        name = document.get("name").toString(),
                                        phone = document.get("phone").toString(),
                                        timeStamp = document.getLong("timeStamp")!!,
                                        chatRoomReceiver = ArrayList(),
                                        chatRoom = ArrayList(),
                                        createdAt = document.getLong("createdAt")!!,
                                        updatedAt = document.getLong("updatedAt")!!
                                    )
                                )
                            }

                            Log.d("TAG", "username already exists")
                        } else {
                            Log.d("TAG", "username does not exists")
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ")
                }
            }
        return employeesLists
    }

}