package com.messengerkotlin.fragments.name_editor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.messengerkotlin.R
import com.messengerkotlin.core.ViewModelFactory
import com.messengerkotlin.core.enums.CommonStatus
import com.messengerkotlin.databinding.FragmentNameEditorBinding

class NameEditor : Fragment(R.layout.fragment_name_editor) {

    private var _binding: FragmentNameEditorBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentNameEditorBinding.bind(view)
        val viewModel = ViewModelProvider(this, ViewModelFactory(null)).get(NameEditorViewModel::class.java)

        viewModel.currentUserLiveData.observe(viewLifecycleOwner){ userModel ->
            binding.editName.setText(userModel.username)
            binding.editUserKey.setText(userModel.userkey?: "")
        }

        binding.acceptBtn.setOnClickListener{
            val newUsername = binding.editName.text.toString()
            val userKey = binding.editUserKey.text.toString()
            viewModel.editProfile(newUsername, userKey)
        }

        viewModel.editUserKeyStatusLiveData.observe(viewLifecycleOwner){ editUserKeyStatus ->
            if (editUserKeyStatus == CommonStatus.ALREADY_EXIST) binding.warningMsg.visibility = View.VISIBLE
            else if (editUserKeyStatus == CommonStatus.SUCCESS) back()
        }

        binding.editNameBackBtn.setOnClickListener{ back() }
    }

    private fun back(){
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}