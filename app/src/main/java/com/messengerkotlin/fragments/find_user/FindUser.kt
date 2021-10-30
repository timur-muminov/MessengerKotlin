package com.messengerkotlin.fragments.find_user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FindUserBinding

class FindUser : Fragment(R.layout.find_user) {

    private var _binding: FindUserBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FindUserBinding.bind(view)
        val viewModel = ViewModelProvider(this).get(FindUserViewModel::class.java)

        binding.findUserBtn.setOnClickListener {
            val userKey: String = binding.findUserKey.text.toString()
            viewModel.findUser(userKey)
        }

        viewModel.findUserStatusLiveData.observe(viewLifecycleOwner) { userKey ->
            if (userKey != null) {
                val bundle = Bundle()
                bundle.putString("id", userKey)
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_fragmentFindUser_to_fragmentChatroom, bundle)
            } else {
                binding.warningMsg.visibility = View.VISIBLE
            }
        }

        binding.findUserBackBtn.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}