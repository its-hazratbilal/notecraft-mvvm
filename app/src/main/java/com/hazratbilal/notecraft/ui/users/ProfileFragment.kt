package com.hazratbilal.notecraft.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hazratbilal.notecraft.MainActivity
import com.hazratbilal.notecraft.R
import com.hazratbilal.notecraft.databinding.FragmentProfileBinding
import com.hazratbilal.notecraft.ui.BaseFragment
import com.hazratbilal.notecraft.utils.Constant
import com.hazratbilal.notecraft.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadInfo()
        attachClickListener()
        attachListener()
    }

    private fun loadInfo() {
        binding.fullName.text = sharedPrefs.getString(Constant.FULL_NAME)
        binding.gender.text = sharedPrefs.getString(Constant.GENDER)
    }

    private fun attachClickListener() {
        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
    }

    private fun attachListener() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        val profileUpdated = savedStateHandle?.get<Boolean>("profileUpdated")
        if (profileUpdated == true) {
            savedStateHandle.remove<Boolean>("profileUpdated")
            loadInfo()
            (requireActivity() as? MainActivity)?.updateDrawer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}