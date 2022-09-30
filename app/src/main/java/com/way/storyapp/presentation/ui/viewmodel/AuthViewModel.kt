package com.way.storyapp.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.way.storyapp.data.Repository
import com.way.storyapp.data.local.model.DataStoreRepository
import com.way.storyapp.data.local.model.UserModel
import com.way.storyapp.data.remote.model.auth.LoginResponse
import com.way.storyapp.data.remote.model.auth.PostResponse
import com.way.storyapp.data.remote.model.auth.UserRegisterData
import com.way.storyapp.presentation.ui.utils.Resource
import com.way.storyapp.presentation.ui.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository,
    val app: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(app) {
    private var _postRegister: MutableLiveData<Resource<PostResponse>> = MutableLiveData()
    val postRegister: LiveData<Resource<PostResponse>> = _postRegister

    private var _postLogin: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val postLogin: LiveData<Resource<LoginResponse>> = _postLogin

    val readAccount: LiveData<UserModel> = dataStoreRepository.readAccount().asLiveData()

    fun logout() = viewModelScope.launch {
        dataStoreRepository.logout()
    }

    fun postRegister(userRegisterData: UserRegisterData) = viewModelScope.launch {
        postRegisterSafeCall(userRegisterData)
    }

    fun postLogin(userRegisterData: UserRegisterData) = viewModelScope.launch {
        postLoginSafeCall(userRegisterData)
    }

    fun saveAccount(userModel: UserModel) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.login()
        dataStoreRepository.saveAccount(userModel)
    }

    private suspend fun postLoginSafeCall(userRegisterData: UserRegisterData) {
        _postLogin.value = Resource.Loading()
        if (isNetworkAvailable(app)) {
            try {
                val response = repository.remoteDataSource.login(userRegisterData)
                _postLogin.value = handlePostLoginResponse(response)
            } catch (e: Exception) {
                _postLogin.value = Resource.Error(e.message.toString())
            }
        } else {
            _postLogin.value = Resource.Error("No internet connection")
        }
    }

    private suspend fun postRegisterSafeCall(userRegisterData: UserRegisterData) {
        _postRegister.value = Resource.Loading()
        if (isNetworkAvailable(app)) {
            try {
                val response = repository.remoteDataSource.register(userRegisterData)
                _postRegister.value = handlePostDataResponse(response)
            } catch (e: IOException) {
                _postRegister.value = Resource.Error(e.message.toString())
            }
        } else {
            _postRegister.value = Resource.Error("No internet connection")
        }
    }

    private fun handlePostDataResponse(response: Response<PostResponse>?): Resource<PostResponse> {
        if (response != null) {
            when {
                response.isSuccessful -> {
                    val data = response.body()
                    return if (data != null) {
                        Resource.Success(data)
                    } else {
                        Resource.Error(response.errorBody().toString())
                    }
                }
                !response.isSuccessful -> {
                    val messageErr =
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            PostResponse::class.java
                        )
                    return Resource.Error(messageErr.message)
                }
                else -> return Resource.Error(response.errorBody().toString())
            }
        } else return Resource.Error("Response is null")
    }

    private fun handlePostLoginResponse(response: Response<LoginResponse>?): Resource<LoginResponse> {
        if (response != null) {
            when {
                response.isSuccessful -> {
                    val data = response.body()
                    return if (data != null) {
                        Resource.Success(data)
                    } else {
                        Resource.Error(response.errorBody().toString())
                    }
                }
                !response.isSuccessful -> {
                    val messageErr =
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            PostResponse::class.java
                        )
                    return Resource.Error(messageErr.message)
                }
                else -> return Resource.Error(response.errorBody().toString())
            }
        } else return Resource.Error("Response is null")
    }
}