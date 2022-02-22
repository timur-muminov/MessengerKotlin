package com.messengerkotlin.fragments.authorization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FragmentAuthBinding
import com.messengerkotlin.firebase_repository.auth_manager.enums.AuthStatus
import com.messengerkotlin.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Authorization : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
            } else makeShortToast("All fields are required")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statusSharedFlow.collect { authStatus ->
                when(authStatus) {
                    AuthStatus.SUCCESS -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentAuth_to_fragmentUsers)
                    AuthStatus.FAILURE -> makeShortToast("Authentication failed")
                }
            }
        }
    }
}