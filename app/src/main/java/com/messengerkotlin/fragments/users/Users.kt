package com.messengerkotlin.fragments.users

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.messengerkotlin.R
import com.messengerkotlin.core.ViewModelFactory
import com.messengerkotlin.databinding.FragmentUsersBinding
import com.messengerkotlin.fragments.users.adapters.UsersAdapter

class Users : Fragment(R.layout.fragment_users) {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentUsersBinding.bind(view)
        val viewModel =
            ViewModelProvider(this, ViewModelFactory(null)).get(UsersViewModel::class.java)

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

        viewModel.otherUsersLiveData.observe(viewLifecycleOwner) { it?.let { adapter.submitList(it) } }

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

        viewModel.currentUserLiveData.observe(viewLifecycleOwner) { userModel ->
            binding.username.text = userModel.username
            Glide.with(requireContext())
                .load(userModel.imageurl ?: R.mipmap.ic_launcher)
                .centerCrop()
                .circleCrop()
                .into(binding.profileImage)
        }
    }

    private fun navigateTo(action: Int, bundle: Bundle? = null) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action, bundle)
    }

    private fun navigateTo(action: NavDirections) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}