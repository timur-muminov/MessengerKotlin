package com.messengerkotlin.fragments.name_editor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.databinding.FragmentNameEditorBinding
import com.messengerkotlin.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NameEditor : BaseFragment<FragmentNameEditorBinding>(FragmentNameEditorBinding::inflate) {

    private val viewModel: NameEditorViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUserStateFlow.collect { userModel ->
                binding.editName.setText(userModel.username)
                binding.editUserKey.setText(userModel.userkey ?: "")
            }
        }

        binding.acceptBtn.setOnClickListener {
            val newUsername = binding.editName.text.toString()
            val userKey = binding.editUserKey.text.toString()
            viewModel.editProfile(newUsername, userKey)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editUserKeyStatusStateFlow.collect { editUserKeyStatus ->
                if (editUserKeyStatus == CommonStatus.ALREADY_EXIST) binding.warningMsg.visibility =
                    View.VISIBLE
                else if (editUserKeyStatus == CommonStatus.SUCCESS) back()
            }
        }

        binding.editNameBackBtn.setOnClickListener { back() }
    }

    private fun back() {
        requireActivity().onBackPressed()
    }

}