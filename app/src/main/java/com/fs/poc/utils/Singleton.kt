package com.fs.poc.utils

import okhttp3.Cookie
import java.net.CookieManager

class Singleton {
    companion object {
        var instance: Singleton = Singleton()
        var cookieManager: CookieManager = CookieManager()

        fun getToken(): String? {
            if (Singleton.cookieManager?.cookieStore.cookies.size > 0) {
                val cookies = Singleton.cookieManager?.cookieStore.cookies
                for (cookie in cookies)
                    if (cookie.name.equals("XSRF-TOKEN"))
                        return cookie.value
            }
            return null
        }
    }

}