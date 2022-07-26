package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.repository.AuthRepository
import java.io.IOException
import javax.inject.Inject

val token = Token(
    id = 0,
    token = "",
)
val authState = AuthState(
    id = 0,
    token = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val service: ApiService,
    private val repository: AuthRepository,
) : ViewModel() {

    val data: LiveData<AuthState> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0L

    fun setAuth(id: Long, token: String) {
        auth.setAuth(id, token)
    }

    fun getToken(login: String, pass: String): Boolean {
        viewModelScope.launch {
            try {
                val response = service.getToken(login, pass)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                setAuth(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Throwable) {
                println("AVM e 2 $e")
            }
        }
        return true
    }


}
