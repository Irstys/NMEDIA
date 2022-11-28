package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.AuthRepository
import javax.inject.Inject

class SignInViewModel   @Inject constructor(
    val auth: AppAuth,
    private val repository: AuthRepository
) : ViewModel() {

    private val _data = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _data

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state
    fun loginAttempt(login: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.authUser(login, password)
                _data.value = user
            } catch (e: Exception) {
                _state.postValue(FeedModelState(loginError = true))
            }
        }
    }
}