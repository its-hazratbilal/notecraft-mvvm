package com.hazratbilal.notecraft.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hazratbilal.notecraft.model.UserRequest
import com.hazratbilal.notecraft.utils.showToast
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentRegistrationBinding
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.utils.Helper.Companion.isValidEmail
import com.hazratbilal.notecraft.remote.NetworkResult
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : BaseFragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModels<UserViewModel>()
    private var gender: String = "male"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        attachClickListener()
        bindObservers()
    }

    private fun initViews() {
        Glide
            .with(this)
            .load(R.drawable.logo)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_error)
            .into(binding.logoImage)

        val items = listOf("male", "female", "other")
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.gender.adapter = adapter
        binding.gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gender = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun attachClickListener() {
        binding.createAccount.setOnClickListener {
            val fullName = binding.fullName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (fullName.trim().isEmpty()) {
                requireContext().showToast("Full name cannot be empty")
            } else if (email.trim().isEmpty()) {
                requireContext().showToast("Email cannot be empty")
            } else if (!isValidEmail(email)) {
                requireContext().showToast("Email is not valid")
            } else if (password.trim().isEmpty()) {
                requireContext().showToast("Password cannot be empty")
            } else if (confirmPassword.isEmpty()) {
                requireContext().showToast("Confirm password cannot be empty")
            } else if (!password.equals(confirmPassword)) {
                requireContext().showToast("Password and confirm password do not match")
            } else {
                userViewModel.registerUser(UserRequest(fullName, email, gender, password, ""))
            }
        }

        binding.login.setOnClickListener {
            findNavController().popBackStack()
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

                        requireContext().showToast("Registration successful. Please login")
                        findNavController().popBackStack()
                    } else {
                        requireContext().showToast(it.data?.message.toString())
                    }
                }

                is NetworkResult.AuthError -> { }

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