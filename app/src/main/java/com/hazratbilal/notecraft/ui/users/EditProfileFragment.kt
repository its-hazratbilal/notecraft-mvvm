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
import androidx.navigation.NavOptions
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentEditProfileBinding
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.utils.Constant
import com.hazratbilal.notecraft.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModels<UserViewModel>()
    private var gender: String = ""

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        attachClickListener()
        bindObservers()
    }

    private fun initViews() {
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

        gender = sharedPrefs.getString(Constant.GENDER)
        binding.fullName.setText(sharedPrefs.getString(Constant.FULL_NAME))

        if (gender.equals("male")) {
            binding.gender.setSelection(0)
        } else if (gender.equals("female")) (
            binding.gender.setSelection(1)
        ) else {
            binding.gender.setSelection(2)
        }
    }

    private fun attachClickListener() {
        binding.updateProfile.setOnClickListener {
            val fullName = binding.fullName.text.toString()

            if (fullName.trim().isEmpty()) {
                requireContext().showToast("Full name cannot be empty")
            } else {
                userViewModel.updateProfile(UserRequest(fullName, "", gender, "", ""))
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

                        sharedPrefs.putString(Constant.FULL_NAME, binding.fullName.text.toString())
                        sharedPrefs.putString(Constant.GENDER, gender)
                        requireContext().showToast("Profile updated successfully")

                        findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                            set("profileUpdated", true)
                        }
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