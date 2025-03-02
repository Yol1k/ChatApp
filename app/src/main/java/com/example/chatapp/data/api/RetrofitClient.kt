package com.example.chatapp.data.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.Navigation
import com.example.chatapp.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.nogamenolife.pro/"
    private val mainHandler = Handler(Looper.getMainLooper())

    fun create(context: Context, view: View?): AuthApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().apply {
                TokenManager.getToken(context)?.let { token ->
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()

            val response = chain.proceed(request)
            if (response.code == 401) {
                TokenManager.clearToken(context)
                if (view != null)
                    mainHandler.post {
                        Navigation.findNavController(view).popBackStack(R.id.authFragment, false)
                    }
            }
            response
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)

    }
}