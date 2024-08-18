package com.example.myexample.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexample.data.RegisterBody
import com.example.myexample.data.User
import com.example.myexample.data.ValidateEmailBody
import com.example.myexample.repository.AuthRepository
import com.example.myexample.utilis.AuthToken
import com.example.myexample.utilis.RequestStatus
import kotlinx.coroutines.launch

class RegisterActivityViewModel(val authRepository: AuthRepository, val application: Application) :
    ViewModel() {
    private var isloading: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    private var errorMessage: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private var isUniqueEmail: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    private var user: MutableLiveData<User> = MutableLiveData()


    fun getIsLoading(): LiveData<Boolean> = isloading
    fun getErrorMessage(): LiveData<HashMap<String, String>> = errorMessage
    fun getIsUniqueEmail(): LiveData<Boolean> = isUniqueEmail
    fun getUser(): LiveData<User> = user

    fun validateEmailAddress(body: ValidateEmailBody) {
        viewModelScope.launch {
            authRepository.validateEmailAddress(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isloading.value = true
                    }
                    is RequestStatus.Success -> {
                        isloading.value = false
                        isUniqueEmail.value = it.data.isUnique
                    }
                    is RequestStatus.Error -> {
                        isloading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }

    }

    fun registerUser(body: RegisterBody) {
        viewModelScope.launch {
            authRepository.registerUser(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isloading.value = true
                    }
                    is RequestStatus.Success -> {
                        isloading.value = false
                        user.value = it.data.user
                        // save token using shared preferences so that user dont have to register every time they use the app
                        AuthToken.getInstance(application.baseContext).token = it.data.token
                    }
                    is RequestStatus.Error -> {
                        isloading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }

    }

}