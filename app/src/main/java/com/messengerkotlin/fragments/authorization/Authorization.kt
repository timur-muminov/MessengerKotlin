package com.messengerkotlin.fragments.authorization

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.databinding.FragmentAuthBinding

class Authorization : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAuthBinding.bind(view)
        val viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.registration.setOnClickListener {
            Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment
            ).navigate(R.id.action_fragmentAuth_to_fragmentRegistration)
        }


        binding.startbtn.setOnClickListener {
            val email: String = binding.emailEdit.text.toString()
            val password: String = binding.passwordEdit.text.toString()
            if (email.isNotEmpty() || password.isNotEmpty()) {
                viewModel.signIn(email, password)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.statusLiveData.observe(viewLifecycleOwner) { commonStatus ->
            if (commonStatus == CommonStatus.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_fragmentAuth_to_fragmentUsers)
            } else {
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}