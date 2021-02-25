package com.adminapp.model

import android.os.Parcel
import android.os.Parcelable
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
    @SerializedName("chatRoom")
    @Expose
    var chatRoom: ArrayList<ChatRoom>? = null,
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
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        arrayListOf<ChatRoom>().apply {
            parcel.readArrayList(ChatRoom::class.java.classLoader)
        },
        arrayListOf<String>().apply {
            parcel.readArrayList(String::class.java.classLoader)
        },
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong()
    )

    constructor() : this(
        "",
        "",
        "",
        ArrayList(),
        ArrayList(),
        0,
        0,
        0

    )

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    companion object CREATOR : Parcelable.Creator<Employee> {
        override fun createFromParcel(parcel: Parcel): Employee {
            return Employee(parcel)
        }

        override fun newArray(size: Int): Array<Employee?> {
            return arrayOfNulls(size)
        }
    }


}
