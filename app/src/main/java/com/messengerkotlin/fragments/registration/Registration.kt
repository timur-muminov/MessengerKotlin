package com.messengerkotlin.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.core.ViewModelFactory
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.databinding.FragmentRegistrationBinding

class Registration : Fragment(R.layout.fragment_registration){

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegistrationBinding.bind(view)
        val viewModel = ViewModelProvider(this, ViewModelFactory(null)).get(RegistrationViewModel::class.java)

        binding.startbtn.setOnClickListener{
            val name = binding.usernameEdit.text.toString()
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            if (name.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                viewModel.signUp(name, email, password)
            } else {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.statusLiveData.observe(viewLifecycleOwner, { status ->
            if (status == CommonStatus.SUCCESS) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentRegistration_to_fragmentUsers)
            } else if (status == CommonStatus.ALREADY_EXIST){
                Toast.makeText(requireContext(), "this name already exists", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}