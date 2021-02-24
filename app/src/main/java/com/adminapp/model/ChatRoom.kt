package com.adminapp.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChatRoom {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("chatId")
    @Expose
    var chatId: String? = null
}