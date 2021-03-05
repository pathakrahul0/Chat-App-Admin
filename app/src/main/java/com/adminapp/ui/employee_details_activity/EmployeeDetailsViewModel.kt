package com.adminapp.ui.employee_details_activity

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EmployeeDetailsViewModel
@Inject constructor(
    val preference: Preference
) : ViewModel() {

    private val tag = "EmployeeDetailsViewModelActivity"

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad

    private val isUpdate = MediatorLiveData<Boolean>()
    val isUpdated: LiveData<Boolean> = isUpdate

    fun uploadFirebase( uri: Uri, activity: EmployeeDetailsActivity) {
        val fileType = getFileExtension(uri, activity)

        val reference: StorageReference = FirebaseStorage.getInstance()
            .getReference("${System.currentTimeMillis()}.$fileType ")
        reference.putFile(uri)
            .addOnSuccessListener {
                Log.d(tag, "Uploaded")
                reference.downloadUrl.addOnSuccessListener { uri ->
                    updateEmployee(preference.getUserName()!!,uri.toString())
                    preference.setUserProfilePhoto(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.d(tag, "Failed " + e.message)
            }
    }

    private fun getFileExtension(uri: Uri, activity: EmployeeDetailsActivity): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

     fun updateEmployee(name: String,profileImageUrl: String) {
        isLoad.value = true
        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .document(preference.getUserId()!!)
            .update(
                mapOf(
                    "name" to name,
                    "profileImageUrl" to profileImageUrl,
                    "updatedAt" to Date().time
                )
            ).addOnSuccessListener {
                isLoad.value = false
                isUpdate.value = true


            }.addOnFailureListener {
                isLoad.value = false

            }

    }

}