package com.example.chatapp.data.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.nogamenolife.pro"
    private var retrofit: Retrofit? = null

    fun <T> create(context: Context, service: Class<T>): T {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(createOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(service)
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createAuthInterceptor(context))
            .build()
    }

    private fun createLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun createAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder().apply {
                TokenManager.getToken(context)?.let { token ->
                    addHeader("Authorization", "Bearer $token")
                }
                addHeader("Accept", "application/json")
                addHeader("Content-Type", "application/json")
            }.build()

            val response = chain.proceed(request)

            if (response.code == 401) {
                TokenManager.clearToken(context)
                // Ошибка будет обработана в вызывающем коде
            }
            response
        }
    }
}