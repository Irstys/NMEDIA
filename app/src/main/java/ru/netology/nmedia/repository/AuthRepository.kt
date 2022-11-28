package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.User

interface AuthRepository {

    suspend fun authUser(login: String, pass: String): User

    suspend fun registrationUser(login: String, password: String, name: String): User

}