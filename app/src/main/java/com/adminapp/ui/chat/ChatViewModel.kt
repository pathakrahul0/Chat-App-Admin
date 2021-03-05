package com.adminapp.ui.chat

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.adminapp.model.SendMessage
import com.adminapp.prefrences.Preference
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ChatViewModel
@Inject constructor(
    preference: Preference
) : ViewModel() {

    private var chatRoomSenders: ArrayList<String>? = ArrayList()
    private var chatRoomReceivers: ArrayList<String>? = ArrayList()
    private var fireBaseFireStore = FirebaseFirestore.getInstance()
    private var _chatList = MediatorLiveData<ArrayList<SendMessage>>()
    var chatList: LiveData<ArrayList<SendMessage>> = _chatList
    private var chatDataList = ArrayList<SendMessage>()
    private var senderId = preference.getUserId()
    private val TAG = "ChatViewModel"
    private var textChatValue = MutableLiveData<String>()
    var textChatValueChange: LiveData<String> = textChatValue
    var chatRoomId: String? = null
    var receiverId: String? = null


    fun sendMsg(textMsg: String, contentType: String) {
        val sendMsg = SendMessage(
            content = textMsg,
            receiverId = receiverId,
            senderId = senderId,
            contentType = contentType,
            timeStamp = Date().time,
            status = false
        )
        val database = fireBaseFireStore
            .collection(chatRoomId!!)
        val node = database.document()

        node.set(sendMsg)
            .addOnCompleteListener {
                Log.d(TAG, "Send")
                updateChat(node.id)
                updateReviverTime()
                updateSenderTime()
            }
        textChatValue.value = ""
    }


    private fun receiverReceiverId() {
        chatRoomReceivers?.add(chatRoomId!!)
        fireBaseFireStore
            .collection("employees")
            .document(receiverId!!)
            .update(
                mapOf(
                    "chatRoomReceiver" to chatRoomSenders
                )
            )
            .addOnSuccessListener {
            }.addOnFailureListener {

            }
    }

    private fun senderReceiverId() {
        chatRoomId = senderId + receiverId
        chatRoomSenders?.add(chatRoomId!!)
        fireBaseFireStore
            .collection("employees")
            .document(senderId!!)
            .update(
                mapOf(
                    "chatRoomReceiver" to chatRoomSenders
                )
            )
            .addOnSuccessListener { _ ->
                receiverReceiverId()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getReceivers() {
        chatRoomReceivers?.clear()
        fireBaseFireStore
            .collection("employees")
            .document(receiverId!!)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("chatRoomReceiver") != null)
                    chatRoomReceivers = it.data?.get("chatRoomReceiver") as ArrayList<String>
                getSenders()
            }
    }

    private fun getSenders() {
        chatRoomSenders?.clear()
        fireBaseFireStore
            .collection("employees")
            .document(senderId!!)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("chatRoomReceiver") != null)
                    chatRoomSenders = it.data?.get("chatRoomReceiver") as ArrayList<String>
                getChatRoomId()
            }
    }

    private fun getChatRoomId() {

        if (chatRoomSenders?.size!! > 0) {
            for (chatSender in chatRoomSenders!!) {
                if (chatRoomReceivers?.contains(chatSender)!!) {
                    if (chatSender.endsWith(receiverId!!))
                        chatRoomId = chatSender
                    else  chatRoomId = chatSender
                    checkMessageCollections()
                    break
                }
            }
            if (chatRoomId.isNullOrEmpty())
                senderReceiverId()
        } else
            senderReceiverId()
    }

    fun startChart(receiverId: String) {
        this.receiverId = receiverId
        getReceivers()
    }

    private fun checkMessageCollections() {
        fireBaseFireStore
            .collection(chatRoomId!!)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, it1 ->
                if (it1 != null) {
                    Log.w(TAG, "Listen failed.", it1)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val messageData = snapshot.toObjects(SendMessage::class.java)
                    chatDataList.clear()
                    chatDataList.addAll(messageData)
                    _chatList.value = chatDataList
                }
            }
    }



    fun uploadFirebase(uri: Uri, activity: ChatActivity) {
        val fileType = getFileExtension(uri, activity)

        val reference: StorageReference = FirebaseStorage.getInstance()
            .getReference("${System.currentTimeMillis()}.$fileType ")
        reference.putFile(uri)
            .addOnSuccessListener {
                Log.d(TAG, "Uploaded")
                reference.downloadUrl.addOnSuccessListener { uri ->
                    if (
                        fileType.equals("jpg") ||
                        fileType.equals("png") ||
                        fileType.equals("jpeg") ||
                        fileType.equals("gif")
                    ) {
                        sendMsg(uri.toString(), "image")

                    } else if (fileType.equals("pdf")) {
                        sendMsg(uri.toString(), "pdf")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Failed " + e.message)
            }
    }

    private fun getFileExtension(uri: Uri, activity: ChatActivity): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

    private fun updateChat(id: String) {

        val node = FirebaseFirestore
            .getInstance().collection(chatRoomId!!)
            .document(id)
        node.update(
            mapOf(
                "status" to true,
                "timeStamp" to Date().time,
            )
        ).addOnSuccessListener {

        }.addOnFailureListener {
        }

    }

    private fun updateReviverTime() {
        FirebaseFirestore
            .getInstance().collection("employees")
            .document(receiverId!!).update(
                mapOf(
                    "timeStamp" to Date().time,
                )
            ).addOnSuccessListener {
                Log.d("Update ", "Success")

            }
            .addOnFailureListener {
                Log.d("Update ", "Failure")

            }

    }

    private fun updateSenderTime() {
        FirebaseFirestore
            .getInstance().collection("employees")
            .document(senderId!!).update(
                mapOf(
                    "timeStamp" to Date().time,
                )
            ).addOnSuccessListener {
                Log.d("Update ", "Success")

            }
            .addOnFailureListener {
                Log.d("Update ", "Failure")

            }

    }


}