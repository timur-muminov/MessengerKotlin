package com.messengerkotlin.fragments.registration

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FragmentRegistrationBinding
import com.messengerkotlin.firebase_repository.auth_manager.enums.AuthStatus
import com.messengerkotlin.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Registration :
    BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startbtn.setOnClickListener {
            val name = binding.usernameEdit.text.toString()
            val email = binding.emailEdit.text.toString()
            val password = binding.passwordEdit.text.toString()
            if (name.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                viewModel.signUp(name, email, password)
            } else makeShortToast("All fields are required")

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statusSharedFlow.collect { status ->
                when (status) {
                    AuthStatus.SUCCESS -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentRegistration_to_fragmentUsers)
                    AuthStatus.FAILURE -> makeShortToast("this name already exists")
                }
            }
        }
    }
}