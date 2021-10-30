package com.messengerkotlin.fragments.chatroom

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.messengerkotlin.R
import com.messengerkotlin.core.ViewModelFactory
import com.messengerkotlin.databinding.FragmentChatroomBinding
import com.messengerkotlin.fragments.chatroom.adapter.ChatRecyclerAdapter

class Chatroom : Fragment(R.layout.fragment_chatroom) {

    private var _binding: FragmentChatroomBinding? = null
    private val binding get() = _binding!!

    private lateinit var chat: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentChatroomBinding.bind(view)
        val receiverId = arguments?.getString("id")
        val viewModel = ViewModelProvider(
            this,
            ViewModelFactory(receiverId!!)
        ).get(ChatroomViewModel::class.java)

        viewModel.otherUserLiveData.observe(viewLifecycleOwner) { otherUserModel ->
            binding.appbarName.text = otherUserModel.username
            Glide.with(this)
                .load(otherUserModel.imageurl ?: R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.profileImage)
        }


        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        linearLayoutManager.stackFromEnd = true
        chat.layoutManager = linearLayoutManager
        chat.setHasFixedSize(true)
        viewModel.messagesLiveData.observe(viewLifecycleOwner) { messages ->
            chat.adapter = ChatRecyclerAdapter(messages)
        }

        binding.btnSend.setOnClickListener {
            val message = binding.sendMessageEdit.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.sendMessageEdit.setText("")
            } else {
                Toast.makeText(requireContext(), "You can not send empty message", Toast.LENGTH_SHORT).show();
            }
        }

        binding.backBtn.setOnClickListener{ requireActivity().onBackPressed()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
