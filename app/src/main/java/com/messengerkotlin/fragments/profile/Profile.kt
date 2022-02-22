package com.messengerkotlin.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FragmentProfileBinding
import com.messengerkotlin.fragments.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Profile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var launcher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                viewModel.loadProfileImage(uri, requireActivity().contentResolver)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.inflateMenu(R.menu.menu_profile)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_profile) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_fragmentProfile_to_fragmentNameEditor)
            }
            return@setOnMenuItemClickListener true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUserStateFlow.collect {
                it?.let { userModel ->
                    Glide.with(requireContext())
                        .asBitmap()
                        .centerCrop()
                        .circleCrop()
                        .load(userModel.imageurl ?: R.mipmap.ic_launcher_round)
                        .into(binding.profileImage)
                    binding.username.text = userModel.username
                    binding.userKey.text = userModel.userkey ?: ""
                }
            }
        }

        binding.editPhoto.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
}