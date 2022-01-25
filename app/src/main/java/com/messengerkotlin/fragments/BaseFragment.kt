package com.messengerkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding

open class BaseFragment<Binding : ViewBinding>(private val inflateBinding: (LayoutInflater, ViewGroup?, Boolean) -> Binding) :
    Fragment() {

    private var _binding: Binding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflateBinding(inflater, container, false).also { _binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun makeShortToast(message: String) {
        if (message.isNotEmpty()) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
            .show()
    }

    protected fun navigate(direction: NavDirections){
        view?.findNavController()?.navigate(direction)
    }
}