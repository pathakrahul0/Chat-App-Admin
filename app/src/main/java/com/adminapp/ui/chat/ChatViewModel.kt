package com.adminapp.ui.chat

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.*
import com.adminapp.model.ChatRoom
import com.adminapp.model.Employee
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

    private var chatRooms: ArrayList<ChatRoom>? = ArrayList()
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


    private fun receiverChatRoom() {
        fireBaseFireStore.collection("employees").document(receiverId!!).get()
            .addOnSuccessListener {
                chatRooms?.clear()
                val chatRoom = ChatRoom()
                chatRoom.id = senderId
                chatRoom.chatId = senderId + receiverId
                chatRooms?.add(chatRoom)
                if (it.get("chatRoom") != null) chatRooms?.addAll(it.get("chatRoom") as Collection<ChatRoom>)
                fireBaseFireStore.collection("employees").document(receiverId!!)
                    .update("chatRoom", chatRooms)
                    .addOnSuccessListener {
                        Log.d(" ", " ")
                        checkMessageCollections()
                    }.addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
            }
    }

    private fun receiverReceiverId() {
        fireBaseFireStore.collection("employees").document(receiverId!!).get()
            .addOnSuccessListener { it ->
                chatRoomReceivers?.clear()
                if (it.get("chatRoomReceiver") != null) chatRoomReceivers?.addAll(it.get("chatRoomReceiver") as ArrayList<String>)
                chatRoomReceivers?.add(senderId!!)
                fireBaseFireStore.collection("employees").document(receiverId!!)
                    .update("chatRoomReceiver", chatRoomReceivers)
                    .addOnSuccessListener {
                        receiverChatRoom()
                    }
            }
    }

    private fun senderChatRoom(it: DocumentSnapshot) {
        chatRooms?.clear()
        val chatRoom = ChatRoom()
        chatRoom.id = receiverId
        chatRoom.chatId = senderId + receiverId
        chatRoomId = senderId + receiverId
        chatRooms?.add(chatRoom)
        if (it.get("chatRoom") != null) chatRooms?.addAll(it.get("chatRoom") as Collection<ChatRoom>)
        fireBaseFireStore.collection("employees").document(senderId!!).update("chatRoom", chatRooms)
            .addOnSuccessListener {
                receiverReceiverId()
            }.addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    private fun senderReceiverId(it: DocumentSnapshot) {
        chatRoomReceivers?.clear()
        if (it.get("chatRoomReceiver") != null) chatRoomReceivers?.addAll(it.get("chatRoomReceiver") as ArrayList<String>)
        chatRoomReceivers?.add(receiverId!!)
        fireBaseFireStore.collection("employees").document(senderId!!)
            .update("chatRoomReceiver", chatRoomReceivers)
            .addOnSuccessListener { documentReference ->
                senderChatRoom(it)
            }.addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }



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


    fun startChart(receiverId: String) {
        this.receiverId = receiverId
        chatRoomReceivers?.clear()
        chatRooms?.clear()
        fireBaseFireStore.collection("employees").document(senderId!!).get().addOnSuccessListener {
            val userData = it.toObject(Employee::class.java)
            if (userData?.chatRoomReceiver != null) {
                chatRoomReceivers?.addAll(userData.chatRoomReceiver!!)
                if (chatRoomReceivers?.size!! > 0 && chatRoomReceivers?.contains(receiverId)!!) {
                    if (userData.chatRoom != null) chatRooms?.addAll(userData.chatRoom!!)
                    if (chatRooms != null && chatRooms?.size!! > 0) {
                        for (chatPosition in 0 until chatRooms?.size!!) {
                            if (chatRooms?.get(chatPosition)?.id == receiverId) {
                                chatRoomId = chatRooms?.get(chatPosition)?.chatId!!
                                checkMessageCollections()
                            }
                        }
                    }
                } else {
                    chatRooms?.clear()
                    senderReceiverId(it)
                }
            } else {
                chatRooms?.clear()
                senderReceiverId(it)
            }
        }
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