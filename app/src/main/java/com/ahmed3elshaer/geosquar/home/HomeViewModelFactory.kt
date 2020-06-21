/*
 * *
 *  * Created by Ahmed Elshaer on 6/21/20 10:57 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/21/20 10:57 AM
 *
 */

package com.ahmed3elshaer.geosquar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmed3elshaer.geosquar.common.schedulers.BaseSchedulerProvider
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesCacheUseCase
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesRealtimeUseCase
import com.ahmed3elshaer.geosquar.home.usecases.ExploreVenuesSingleUseCase

class HomeViewModelFactory(
    private val schedulerProvider: BaseSchedulerProvider,
    private val exploreVenuesRealtimeUseCase: ExploreVenuesRealtimeUseCase,
    private val exploreVenuesSingleUseCase: ExploreVenuesSingleUseCase,
    private val exploreVenuesCacheUseCase: ExploreVenuesCacheUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            BaseSchedulerProvider::class.java,
            ExploreVenuesRealtimeUseCase::class.java,
            ExploreVenuesSingleUseCase::class.java,
            ExploreVenuesCacheUseCase::class.java
        ).newInstance(
            schedulerProvider,
            exploreVenuesRealtimeUseCase,
            exploreVenuesSingleUseCase,
            exploreVenuesCacheUseCase
        )
    }
}