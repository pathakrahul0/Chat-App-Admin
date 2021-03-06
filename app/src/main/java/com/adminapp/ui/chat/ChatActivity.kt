package com.adminapp.ui.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.adminapp.adapter.ChatAdapter
import com.adminapp.databinding.ActivityChatBinding
import com.adminapp.model.SendMessage
import com.adminapp.prefrences.Preference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    @Inject
    lateinit var preference: Preference

    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var chatAdapter: ChatAdapter? = null
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding
    private var receiverId: String? = null
    private var chatList: ArrayList<SendMessage> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        receiverId = intent.getStringExtra("receiverId")


        chatAdapter = ChatAdapter(chatList, this, preference)
        binding.rvChat.adapter = chatAdapter
        binding.rvChat.layoutManager?.scrollToPosition(chatList.size)
        binding.rvChat.scrollToPosition(chatList.size)
        binding.rvChat.smoothScrollToPosition(chatList.size)


        viewModel.startChart(receiverId!!)
        viewModel.chatList.observe({ lifecycle }) {
            chatList.clear()
            chatList.addAll(it)
            chatAdapter?.notifyDataSetChanged()

            binding.rvChat.layoutManager?.scrollToPosition(chatList.size)
            binding.rvChat.scrollToPosition(chatList.size)
            binding.rvChat.smoothScrollToPosition(chatList.size)

        }

        binding.sendText.setOnClickListener {
            if (binding.etMsg.text.toString().isNotEmpty()) {
                viewModel.sendMsg(binding.etMsg.text.toString(), "text")
            } else Toast.makeText(this, "Can't send empty message", Toast.LENGTH_LONG).show()
        }
        viewModel.textChatValueChange.observe({lifecycle}){
            binding.etMsg.setText(it)
        }

        binding.etMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 0) {
                    binding.sendText.visibility = View.VISIBLE
                    binding.sendFile.visibility = View.GONE
                } else {
                    binding.sendText.visibility = View.GONE
                    binding.sendFile.visibility = View.VISIBLE

                }

            }

            override fun afterTextChanged(s: Editable?) {}

        })


        binding.sendFile.setOnClickListener {
            if (!hasPermissions(*PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryIntent.type = "*/*"
                startActivityForResult(galleryIntent, 1)
            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                uri?.let { viewModel.uploadFirebase(it, this) }
            }
        }
    }

    private fun hasPermissions(vararg permissions: String?): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


}