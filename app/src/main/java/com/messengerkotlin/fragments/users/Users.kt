package com.messengerkotlin.fragments.users

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.messengerkotlin.R
import com.messengerkotlin.databinding.FragmentUsersBinding
import com.messengerkotlin.fragments.BaseFragment
import com.messengerkotlin.fragments.users.adapters.UsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Users : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    private val viewModel: UsersViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.toolbar.inflateMenu(R.menu.menu_users)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    navigateTo(action = UsersDirections.actionFragmentUsersToFragmentAuth())
                }
                R.id.profile -> navigateTo(action = R.id.action_fragmentUsers_to_fragmentProfile)
                R.id.findUser -> navigateTo(action = R.id.action_fragmentUsers_to_fragmentFindUser)
            }
            return@setOnMenuItemClickListener true
        }

        val adapter = UsersAdapter { userId ->
            val bundle = Bundle()
            bundle.putString("receiverId", userId)
            navigateTo(action = R.id.action_fragmentUsers_to_fragmentChatroom, bundle = bundle)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.otherUsersStateFlow.collect { it?.let { adapter.submitList(ArrayList(it)) } }
        }

        binding.userRecycler.adapter = adapter
        binding.userRecycler.setHasFixedSize(true)
        binding.userRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.userRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUserStateFlow.collect {
                it?.let { userModel ->
                    binding.username.text = userModel.username
                    Glide.with(requireContext())
                        .load(userModel.imageurl ?: R.mipmap.ic_launcher)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.profileImage)
                }
            }
        }
    }

    private fun navigateTo(action: Int, bundle: Bundle? = null) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action, bundle)
    }

    private fun navigateTo(action: NavDirections) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action)
    }
}