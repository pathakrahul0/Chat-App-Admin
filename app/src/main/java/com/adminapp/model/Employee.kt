package com.adminapp.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class Employee(
    @SerializedName("id")
    @Expose
    val id: String?,
    @SerializedName("name")
    @Expose
    val name: String?,
    @SerializedName("phone")
    @Expose
    val phone: String?,
    @SerializedName("profileImageUrl")
    @Expose
    val profileImageUrl: String?,
    @SerializedName("chatRoomReceiver")
    @Expose
    var chatRoomReceiver: ArrayList<String>? = null,
    @SerializedName("createdAt")
    @Expose
    val createdAt: Long,
    @SerializedName("updatedAt")
    @Expose
    val updatedAt: Long,
    @SerializedName("timeStamp")
    @Expose
    val timeStamp: Long,
    @SerializedName("isSelected")
    @Expose
    var isSelected: Boolean,
)



