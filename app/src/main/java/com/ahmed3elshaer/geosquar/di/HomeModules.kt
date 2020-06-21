/*
 * *
 *  * Created by Ahmed Elshaer on 6/21/20 10:10 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/21/20 10:10 AM
 *
 */

package com.ahmed3elshaer.geosquar.di

import android.content.Context
import android.content.SharedPreferences
import com.ahmed3elshaer.geosquar.common.AuthInterceptor
import com.ahmed3elshaer.geosquar.common.FourSquareApi
import com.ahmed3elshaer.geosquar.common.Repository
import com.ahmed3elshaer.geosquar.common.SharedPrefWrapper
import com.ahmed3elshaer.geosquar.common.local.VenuesDao
import com.ahmed3elshaer.geosquar.common.local.VenuesDatabase
import com.ahmed3elshaer.geosquar.common.schedulers.BaseSchedulerProvider
import com.ahmed3elshaer.geosquar.common.schedulers.SchedulerProvider
import com.ahmed3elshaer.geosquar.home.HomeViewModelFactory
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesCacheUseCase
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesRealtimeUseCase
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesSingleUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class HomeModules(private val context: Context) {



    @Singleton
    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


    @Singleton
    @Provides
    fun provideSharedPreference(): SharedPreferences =
        context.getSharedPreferences("GeoSquare", Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSharedPrefWrapper(sharedPreferences: SharedPreferences): SharedPrefWrapper {
        return SharedPrefWrapper(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideVenuesDatabase(context: Context): VenuesDatabase {
        return VenuesDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideVenuesDao(venuesDatabase: VenuesDatabase): VenuesDao {
        return venuesDatabase.moviesDao();
    }

    @Singleton
    @Provides
    fun provideFourSquareApi(okHttpClient: OkHttpClient, moshi: Moshi): FourSquareApi {
        return createWebService(okHttpClient, moshi, "https://api.foursquare.com/v2/");
    }


    @Singleton
    @Provides
    fun createOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider();

    @Singleton
    @Provides
    fun provideRepository(
        sharedPrefWrapper: SharedPrefWrapper,
        fourSquareApi: FourSquareApi,
        venuesDao: VenuesDao
    ): Repository {
        return Repository(sharedPrefWrapper, fourSquareApi, venuesDao)
    }


    @Singleton
    @Provides
    fun provideExploreVenuesCacheUseCase(repository: Repository): ExploreVenuesCacheUseCase {
        return ExploreVenuesCacheUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideExploreVenuesRealtimeUseCase(repository: Repository,baseSchedulerProvider: BaseSchedulerProvider): ExploreVenuesRealtimeUseCase {
        return ExploreVenuesRealtimeUseCase(repository,baseSchedulerProvider)
    }

    @Singleton
    @Provides
    fun provideExploreVenuesSingleUseCase(repository: Repository,baseSchedulerProvider: BaseSchedulerProvider):  ExploreVenuesSingleUseCase {
        return  ExploreVenuesSingleUseCase(repository,baseSchedulerProvider)
    }


    @Singleton
    @Provides
    inline fun <reified T> createWebService(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        url: String
    ): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(T::class.java)
    }

    @Singleton
    @Provides
    fun provideViewModelFactory(schedulerProvider: BaseSchedulerProvider,
                                exploreVenuesRealtimeUseCase: ExploreVenuesRealtimeUseCase,
                                exploreVenuesSingleUseCase: ExploreVenuesSingleUseCase,
                                exploreVenuesCacheUseCase: ExploreVenuesCacheUseCase): HomeViewModelFactory =
        HomeViewModelFactory(schedulerProvider,exploreVenuesRealtimeUseCase,exploreVenuesSingleUseCase,exploreVenuesCacheUseCase)
}