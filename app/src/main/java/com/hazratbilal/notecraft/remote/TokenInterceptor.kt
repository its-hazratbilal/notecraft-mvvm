package com.hazratbilal.notecraft.remote

import com.hazratbilal.notecraft.utils.Constant
import com.hazratbilal.notecraft.utils.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class TokenInterceptor @Inject constructor(): Interceptor {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.header("Require-Auth") != null) {
            val newRequest = request.newBuilder()
                .removeHeader("Require-Auth")
                .addHeader("Authorization", sharedPrefs.getString(Constant.TOKEN))
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}