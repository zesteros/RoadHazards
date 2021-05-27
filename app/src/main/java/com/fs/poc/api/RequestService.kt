package com.fs.poc.api

import com.fs.poc.utils.Singleton
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

class RequestService {
    companion object Client {

        fun getAPI(): IRequestsAPI? {
            val gson = GsonBuilder().create()

            // Interceptor Logg

            // Interceptor Logg
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            // COOKIE MANAGER

            // COOKIE MANAGER

            var cookieManager = CookieManager()
            if (Singleton.cookieManager != null) cookieManager =
                Singleton.cookieManager
            else Singleton.cookieManager = cookieManager
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)


            // HTTP CLIENT - config logg and cookie manager
            // HTTP CLIENT - config logg and cookie manager
            val httpClient = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieManager)) // .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.MINUTES)
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build()
            //
            //
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("http://34.71.187.174:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread())
                )
                .build()
            val client = retrofit.create(
                IRequestsAPI::class.java
            )
            Singleton.cookieManager = cookieManager
            return client
        }
    }


}