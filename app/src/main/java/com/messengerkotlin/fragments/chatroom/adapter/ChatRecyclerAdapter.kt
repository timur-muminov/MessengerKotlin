package com.messengerkotlin.fragments.chatroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.messengerkotlin.R
import com.messengerkotlin.models.MessageModel

class ChatRecyclerAdapter(private var messageList: List<MessageModel>) : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {

    private val RIGHT: Int = 1
    private val LEFT: Int = 0
    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int) : ViewHolder {
        return if (viewType == RIGHT) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sent_message_item, parent, false))
        } else {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.received_message_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = messageList[position].message
    }

    override fun getItemViewType(position: Int) : Int {
        return if (messageList[position].senderId == userId) {
            RIGHT
        } else {
            LEFT
        }
    }

    override fun getItemCount() : Int {
        return messageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.message)
    }
}