/*
 * *
 *  * Created by Ahmed Elshaer on 10/26/19 4:17 AM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 10/24/19 9:31 PM
 *
 */

package com.ahmed3elshaer.geosquar.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val formatter = SimpleDateFormat("YYYYMMDD", Locale.getDefault())

        val url = request.url.newBuilder()
            .addQueryParameter("client_id", "VZC2GFB3EJ35BGUXAJRLU12RGP2BFLREFOYNAQFIOT2WFTJI")
            .addQueryParameter("client_secret", "0LPQQUCUA1YZ0IPUBKNJX1DEQDGBMUXNNGPZWDIHKE0HLSPF")
            .addQueryParameter("v", formatter.format(Date()))
            .build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}
