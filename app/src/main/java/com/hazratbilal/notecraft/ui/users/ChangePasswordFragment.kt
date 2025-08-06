package com.hazratbilal.notecraft.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hazratbilal.notecraft.model.UserRequest
import com.hazratbilal.notecraft.utils.showToast
import androidx.navigation.NavOptions
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentChangePasswordBinding
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModels<UserViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachClickListener()
        bindObservers()
    }

    private fun attachClickListener() {
        binding.updatePassword.setOnClickListener {
            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val confirmNewPassword = binding.comfirmNewPassword.text.toString()

            if (oldPassword.trim().isEmpty()) {
                requireContext().showToast("Old password cannot be empty")
            } else if (newPassword.trim().isEmpty()) {
                requireContext().showToast("New password cannot be empty")
            } else if (confirmNewPassword.trim().isEmpty()) {
                requireContext().showToast("Confirm new password cannot be empty")
            } else if (!newPassword.equals(confirmNewPassword)) {
                requireContext().showToast("New password and confirm new password do not match")
            } else {
                userViewModel.changePassword(UserRequest("", "", "", oldPassword, newPassword))
            }
        }

    }

    private fun bindObservers() {
        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) {
            hideLoading()
            when (it) {
                is NetworkResult.Idle -> { }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Success -> {
                    if (it.data?.success == true) {
                        requireContext().showToast("Password updated successfully")
                        findNavController().popBackStack()
                    } else {
                        requireContext().showToast(it.data?.message.toString())
                    }
                }

                is NetworkResult.AuthError -> {
                    requireContext().showToast("Token expired. Please login again.")
                    sharedPrefs.clearAll()
                    findNavController().navigate(
                        R.id.loginFragment, null, NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, inclusive = true)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                }

                is NetworkResult.Error -> {
                    requireContext().showToast(it.message ?: "Unknown error")
                }

            }

            if (it !is NetworkResult.Idle && it !is NetworkResult.Loading) {
                userViewModel.clearState()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}