package ru.ettransapp.data.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import okhttp3.JavaNetCookieJar

object RetrofitClient {
    private const val BASE_URL = "https://ettrans.ru/api/"

    // interceptor для логирования тела запросов/ответов
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // единый, персистентный CookieManager для хранения куки
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val httpClient = OkHttpClient.Builder()
        // подключаем JavaNetCookieJar с нашим менеджером
        .cookieJar(JavaNetCookieJar(cookieManager))
        // логируем тело HTTP
        .addInterceptor(logging)
        // отлавливаем и логируем куки до и после каждого запроса
        .addInterceptor { chain ->
            val request = chain.request()
            val uri = request.url.toUri()
            val stored = cookieManager.cookieStore.get(uri)
                .joinToString { "${it.name}=${it.value}" }
            Log.d("RetrofitClient", "🟢 Cookies before request to ${request.url}: $stored")

            val response = chain.proceed(request)

            val setCookies = response.headers("Set-Cookie")
            if (setCookies.isNotEmpty()) {
                Log.d("RetrofitClient", "🔵 Set-Cookie headers from ${request.url}: $setCookies")
            }
            response
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()
        .create(ApiService::class.java)
}