package com.hazratbilal.notecraft.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hazratbilal.notecraft.MainActivity
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentLoginBinding
import com.hazratbilal.notecraft.model.UserRequest
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.utils.Helper.Companion.isValidEmail
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.utils.Constant
import com.hazratbilal.notecraft.utils.SharedPrefs
import com.hazratbilal.notecraft.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModels<UserViewModel>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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
    }

    private fun attachClickListener() {
        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                requireContext().showToast("Email and Password cannot be empty")
            } else if (!isValidEmail(email)) {
                requireContext().showToast("Email is not valid")
            } else {
                userViewModel.loginUser(UserRequest("", email, "", password, ""))
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
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

                        sharedPrefs.putString(Constant.FULL_NAME, it.data.user.full_name)
                        sharedPrefs.putString(Constant.EMAIL, it.data.user.email)
                        sharedPrefs.putString(Constant.GENDER, it.data.user.gender)
                        sharedPrefs.putString(Constant.TOKEN, it.data.user.token)

                        (requireActivity() as? MainActivity)?.updateDrawer()

                        requireContext().showToast("Login successful")
                        findNavController().navigate(
                            R.id.notesFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                        )
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