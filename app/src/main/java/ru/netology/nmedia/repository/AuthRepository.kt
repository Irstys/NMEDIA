package ru.netology.nmedia.repository


import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class AuthRepository {

    suspend fun authUser(login: String, password: String): User {
        try {
            val response = PostsApi.retrofitService.updateUser(login, password)
            if (!response.isSuccessful) {
                println("authorized")
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw Exception()
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun registrationUser(login: String, password: String, name: String): User {
        try {
            val response = PostsApi.retrofitService.registrationUser(login, password, name)
            if (!response.isSuccessful) {
                println("register")
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw Exception()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}