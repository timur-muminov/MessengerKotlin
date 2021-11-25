package com.messengerkotlin.fragments.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.messengerkotlin.R
import com.messengerkotlin.core.ViewModelFactory
import com.messengerkotlin.databinding.FragmentProfileBinding

class Profile : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,ViewModelFactory(null)).get(ProfileViewModel::class.java)
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            viewModel.loadProfileImage(uri, requireActivity().contentResolver)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileBinding.bind(view)
        val viewModel = ViewModelProvider(this, ViewModelFactory(null)).get(ProfileViewModel::class.java)

        binding.toolbar.inflateMenu(R.menu.menu_profile)
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.edit_profile) {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_fragmentProfile_to_fragmentNameEditor)
            }
            return@setOnMenuItemClickListener true
        }

        viewModel.currentUserLiveData.observe(viewLifecycleOwner) { userModel ->
            Glide.with(this)
                .asBitmap()
                .centerCrop()
                .circleCrop()
                .load(userModel.imageurl ?: R.mipmap.ic_launcher)
                .into(binding.profileImage)
            binding.username.text = userModel.username
            binding.userKey.text = userModel.userkey ?: ""
        }

        binding.editPhoto.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}