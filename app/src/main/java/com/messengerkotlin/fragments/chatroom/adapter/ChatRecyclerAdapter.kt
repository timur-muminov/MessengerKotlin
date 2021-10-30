package com.messengerkotlin.fragments.chatroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.messengerkotlin.R
import com.messengerkotlin.models.MessageModel

class ChatRecyclerAdapter(private var messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder>() {

    private val right: Int = 1
    private val left: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int) : ViewHolder {
        if (viewType == right) {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sent_message_item, parent, false))
        } else {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.received_message_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = messageList[position].message
    }


    override fun getItemViewType(position: Int) : Int {
        val user = FirebaseAuth.getInstance().currentUser
        return if (messageList[position].senderId == user?.uid) {
            right;
        } else {
            left;
        }
    }

    override fun getItemCount() : Int {
        return messageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.message)
    }
}