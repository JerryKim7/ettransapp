package ru.ettransapp.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import ru.ettransapp.data.network.ApiService
import javax.inject.Singleton
import kotlin.jvm.JvmStatic
import okhttp3.OkHttpClient
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

private const val BASE_URL = "https://ettrans.ru/api/"
// Persistent CookieManager to retain session cookies across requests
private val cookieManager = CookieManager().apply {
    setCookiePolicy(CookiePolicy.ACCEPT_ALL)
}

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @JvmStatic
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d("NetworkModule", "Request ${request.method} ${request.url}")
                request.headers.names().forEach { name ->
                    request.headers.values(name).forEach { value ->
                        Log.d("NetworkModule", "Header $name: $value")
                    }
                }
                val response = chain.proceed(request)
                Log.d("NetworkModule", "Response ${response.code} for ${response.request.url}")
                response
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideApiService(client: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}
