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

    private val tag = "EmployeeDetailsViewModelActiity"

    private val isLoad = MediatorLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoad

    fun uploadFirebase(name: String, uri: Uri, activity: EmployeeDetailsActivity) {
        val fileType = getFileExtension(uri, activity)

        val reference: StorageReference = FirebaseStorage.getInstance()
            .getReference("${System.currentTimeMillis()}.$fileType ")
        reference.putFile(uri)
            .addOnSuccessListener {
                Log.d(tag, "Uploaded")
                reference.downloadUrl.addOnSuccessListener { uri ->
                    updateEmployee(uri.toString())
                    preference.setUserProfilePhoto(uri.toString())
                    preference.setUserName(name)
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

    private fun updateEmployee(profileImageUrl: String) {
        isLoad.value = true
        FirebaseFirestore
            .getInstance()
            .collection("employees")
            .document(preference.getUserId()!!)
            .update(
                mapOf(
                    "profileImageUrl" to profileImageUrl,
                    "updatedAt" to Date().time
                )
            ).addOnSuccessListener {
                isLoad.value = false

            }.addOnFailureListener {
                isLoad.value = false

            }

    }

}