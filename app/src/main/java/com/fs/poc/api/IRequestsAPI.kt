package com.fs.poc.api

import android.media.session.MediaSession
import com.fs.poc.data.model.Ticket
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface IRequestsAPI {
    @Multipart
    @POST("/api/authentication")
    fun login(
        @Header("X-XSRF-TOKEN") token: String,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("remember-me") rememberMe: RequestBody,
        @Part("submit") submit: RequestBody
    ): Observable<CommonResponse>

    @POST("/")
    fun getCookies(): Observable<CommonResponse>

    @POST("/api/tickets")
    fun addTicket(
        @Header("X-XSRF-TOKEN") token: String,
        @Body ticket: Ticket
    ): Observable<CommonResponse>
}