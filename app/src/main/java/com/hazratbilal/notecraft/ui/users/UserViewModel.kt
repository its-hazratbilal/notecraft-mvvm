package com.hazratbilal.notecraft.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hazratbilal.notecraft.model.UserRequest
import com.hazratbilal.notecraft.model.UserResponse
import com.hazratbilal.notecraft.remote.NetworkResult
import com.hazratbilal.notecraft.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData: LiveData<NetworkResult<UserResponse>> get() = _userResponseLiveData

    fun clearState() {
        _userResponseLiveData.value = NetworkResult.Idle()
    }

    fun registerUser(userRequest: UserRequest) {
        viewModelScope.launch {
            _userResponseLiveData.value = NetworkResult.Loading()
            val result = userRepository.registerUser(userRequest)
            _userResponseLiveData.value = result
        }
    }

    fun loginUser(userRequest: UserRequest) {
        viewModelScope.launch {
            _userResponseLiveData.value = NetworkResult.Loading()
            val result = userRepository.loginUser(userRequest)
            _userResponseLiveData.value = result
        }
    }

    fun updateProfile(userRequest: UserRequest) {
        viewModelScope.launch {
            _userResponseLiveData.value = NetworkResult.Loading()
            val result = userRepository.updateProfile(userRequest)
            _userResponseLiveData.value = result
        }
    }

    fun changePassword(userRequest: UserRequest) {
        viewModelScope.launch {
            _userResponseLiveData.value = NetworkResult.Loading()
            val result = userRepository.changePassword(userRequest)
            _userResponseLiveData.value = result
        }
    }
	
}
