/*
 * *
 *  * Created by Ahmed Elshaer on 10/26/19 4:17 AM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 10/25/19 9:48 PM
 *
 */

package com.ahmed3elshaer.geosquar.common

import com.ahmed3elshaer.geosquar.common.models.PhotosResponse
import com.ahmed3elshaer.geosquar.common.models.VenuesResponse
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface FourSquareApi {

    @GET("/v2/venues/explore")
    fun exploreVenues(
        @Query("ll") coordinates: String,
        @Query("radius") radius: Long = 1000
    ):
            Observable<VenuesResponse>

    @GET("/v2/venues/{id}/photos")
    fun getVenuePhotos(@Path("id") venueId: String): Observable<PhotosResponse>

    @GET
    fun getPhotoStream(@Url url: String): Observable<ResponseBody>
}
