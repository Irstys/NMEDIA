package ru.netology.nmedia.repository

import android.util.Log
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


class PostRepositoryImpl : PostRepository {


    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        try {
            PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }


                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }


    }


    override fun likedAsync(id: Long, likedByMe: Boolean, callback: PostRepository.Callback<Post>) {
        try {
            PostsApi.retrofitService.likeById(id, likedByMe).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    } else {
                        callback.onSuccess(response.body() ?: run {
                            callback.onError(
                                RuntimeException(
                                    response.message() + response.code().toString()
                                )
                            )
                            return
                        })
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }


    }


    override fun shareById(id: Long) {
        //TODO("Not yet implemented")
        Log.e("PostRepositoryImpl", "Share is not yet implemented")
    }
/*override fun shareById(id: Long) {
        val request: Request = Request.Builder()
            .get()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }*/

    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        try {
            PostsApi.retrofitService.removeById(id)
                .enqueue(object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return

                        } else {
                            callback.onSuccess(response.body() ?: run {
                                callback.onError(
                                    RuntimeException(
                                        response.message() + response.code().toString()
                                    )
                                )
                                return
                            })
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        callback.onError(RuntimeException(t))
                    }
                })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }


    }


    override fun saveAsync(
        post: Post,
        callback: PostRepository.Callback<Post>
    ) {
        try {
            PostsApi.retrofitService.save(post)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        callback.onSuccess(response.body() ?: run {
                            callback.onError(
                                RuntimeException(
                                    response.message() + response.code().toString()
                                )
                            )
                            return
                        })
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        callback.onError(RuntimeException(t))
                    }
                })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }


    }

}
