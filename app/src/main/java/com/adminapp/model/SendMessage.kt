package com.adminapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class SendMessage(
    @SerializedName("content")
    @Expose
    var content: String?,

    @SerializedName("receiverId")
    @Expose
    var receiverId: String?,

    @SerializedName("senderId")
    @Expose
    var senderId: String?,

    @SerializedName("contentType")
    @Expose
    var contentType: String?,

    @SerializedName("timeStamp")
    @Expose
    var timeStamp: Long?,

    @SerializedName("status")
    @Expose
    var status: Boolean?
){
    constructor(): this(
        "",
        "",
        "",
        "",
        0,
        false,
    )
}
