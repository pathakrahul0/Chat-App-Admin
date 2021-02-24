package com.adminapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.adminapp.R
import com.adminapp.databinding.AdapterChatBinding
import com.adminapp.model.SendMessage
import com.adminapp.prefrences.Preference
import com.adminapp.utils.Constants
import com.bumptech.glide.Glide

class ChatAdapter(
    private val messageList: ArrayList<SendMessage>,
    private val activity: Activity,
    private val preference: Preference,
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_chat,
                parent,
                false
            )
        )
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        if (message.senderId.equals(preference.getUserId())) {
            if (message.contentType.equals("image")) {
                Glide.with(activity).load(message.content).thumbnail(0.1f)
                    .into(holder.rowBinding.ivSender)
                holder.rowBinding.tvUserMsgRight.visibility = View.GONE
                holder.rowBinding.ivSender.visibility = View.VISIBLE
            } else {
                holder.rowBinding.tvUserMsgRight.text = message.content
                holder.rowBinding.tvUserMsgRight.visibility = View.VISIBLE
                holder.rowBinding.ivSender.visibility = View.GONE
            }

            holder.rowBinding.llLeft.visibility = View.GONE
            holder.rowBinding.llRight.visibility = View.VISIBLE
            holder.rowBinding.tvTimeStampRight.text = Constants.getDateTime(message.timeStamp!!,"EEE, MMM d, ''yy, h:mm a")

        } else {
            if (message.contentType.equals("image")) {
                Glide.with(activity).load(message.content).thumbnail(0.1f)
                    .into(holder.rowBinding.ivReceiver)
                holder.rowBinding.tvUserMsg.visibility = View.GONE
                holder.rowBinding.ivReceiver.visibility = View.VISIBLE
            } else {
                holder.rowBinding.tvUserMsg.text = message.content
                holder.rowBinding.tvUserMsg.visibility = View.VISIBLE
                holder.rowBinding.ivReceiver.visibility = View.GONE
            }
            holder.rowBinding.llRight.visibility = View.GONE
            holder.rowBinding.llLeft.visibility = View.VISIBLE
            holder.rowBinding.tvTimeStampLeft.text = Constants.getDateTime(message.timeStamp!!,"EEE, MMM d, ''yy, h:mm a")

        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    inner class ViewHolder(var rowBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(rowBinding.root)
}