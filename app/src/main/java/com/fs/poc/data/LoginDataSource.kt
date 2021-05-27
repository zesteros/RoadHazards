package com.fs.poc.data

import com.fs.poc.api.CommonResponse
import com.fs.poc.api.LoginListener
import com.fs.poc.api.RequestService
import com.fs.poc.data.model.LoggedInUser
import com.fs.poc.utils.Singleton
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import io.reactivex.observers.DisposableObserver as DisposableObserver1

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String, loginListener: LoginListener) {
        try {
            val disposableObserver = object : DisposableObserver1<CommonResponse>() {
                override fun onComplete() {}
                override fun onNext(value: CommonResponse?) {
                    authenticate(username, password, loginListener)
                }

                override fun onError(e: Throwable?) {
                    authenticate(username, password, loginListener)
                }
            }
            RequestService.getAPI()?.getCookies()?.subscribeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(disposableObserver)

        } catch (e: Throwable) {
            e.printStackTrace()

        }
    }

    private fun authenticate(
        username: String,
        password: String,
        loginListener: LoginListener
    ) {
        val disposableObserver = object : DisposableObserver1<CommonResponse>() {
            override fun onComplete() {}
            override fun onNext(value: CommonResponse?) {
                if (value?.status == 200)
                    loginListener.onLoginSuccess()
                else loginListener.onLoginFailed()

            }

            override fun onError(e: Throwable?) {
                if (e is HttpException) {
                    if (e.code().equals(401)) {
                        loginListener.onLoginFailed()
                        return
                    }
                }
                loginListener.onLoginSuccess()
            }
        }
        val token = Singleton.getToken()
        if (token != null) {
            RequestService.getAPI()?.login(
                token,
                RequestBody.create(MediaType.parse("text/plain"), username),
                RequestBody.create(MediaType.parse("text/plain"), password),
                RequestBody.create(MediaType.parse("text/plain"), "true"),
                RequestBody.create(MediaType.parse("text/plain"), "Login")
            )?.subscribeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(disposableObserver)
        }

    }


    fun logout() {
        // TODO: revoke authentication
    }
}