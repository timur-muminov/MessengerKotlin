package com.messengerkotlin.fragments.find_user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FindUserBinding
import com.messengerkotlin.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindUser : BaseFragment<FindUserBinding>(FindUserBinding::inflate) {

    private val viewModel: FindUserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.findUserBtn.setOnClickListener {
            val userKey: String = binding.findUserKey.text.toString()
            viewModel.findUser(userKey)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.findUserCallbackSharedFlow.collect { receiverId ->
                if (receiverId != null) {
                    val bundle = Bundle()
                    bundle.putString("receiverId", receiverId)
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_fragmentFindUser_to_fragmentChatroom, bundle)
                } else {
                    binding.warningMsg.visibility = View.VISIBLE
                }
            }
        }

        binding.findUserBackBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
}