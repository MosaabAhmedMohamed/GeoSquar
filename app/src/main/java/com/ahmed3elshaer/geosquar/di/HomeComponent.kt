/*
 * *
 *  * Created by Ahmed Elshaer on 6/21/20 10:56 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/21/20 10:56 AM
 *
 */

package com.ahmed3elshaer.geosquar.di

import com.ahmed3elshaer.geosquar.home.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [HomeModules::class])
interface HomeComponent {
    fun poke(mainActivity: MainActivity)
}