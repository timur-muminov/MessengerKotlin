package com.messengerkotlin.fragments.chatroom

import android.os.Bundle
import android.view.View
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FragmentChatroomBinding
import com.messengerkotlin.fragments.BaseFragment
import com.messengerkotlin.fragments.chatroom.adapter.ChatRecyclerAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class Chatroom : BaseFragment<FragmentChatroomBinding>(FragmentChatroomBinding::inflate) {

    private val viewModel: ChatroomViewModel by viewModel { parametersOf(arguments?.getString("receiverId")) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.otherUserStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                it?.let { otherUserModel ->
                    binding.appbarName.text = otherUserModel.username
                    Glide.with(requireContext())
                        .load(otherUserModel.imageurl ?: R.mipmap.ic_launcher)
                        .circleCrop()
                        .into(binding.profileImage)
                }
            }
        }

        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        binding.chatRecycler.layoutManager = linearLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messagesSharedFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect { messages ->
                    binding.chatRecycler.adapter = ChatRecyclerAdapter(messages)
            }
        }

        binding.btnSend.setOnClickListener {
            val message = binding.sendMessageEdit.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.sendMessageEdit.setText("")
            }
        }

        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
}
