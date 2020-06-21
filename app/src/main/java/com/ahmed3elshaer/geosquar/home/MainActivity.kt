/*
 * *
 *  * Created by Ahmed Elshaer on 10/26/19 4:17 AM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 10/26/19 3:37 AM
 *
 */

package com.ahmed3elshaer.geosquar.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder
import com.ahmed3elshaer.geosquar.R
import com.ahmed3elshaer.geosquar.common.Event
import com.ahmed3elshaer.geosquar.common.SharedPrefWrapper
import com.ahmed3elshaer.geosquar.common.extensions.changeMode
import com.ahmed3elshaer.geosquar.common.extensions.hide
import com.ahmed3elshaer.geosquar.common.extensions.isNetworkAvailable
import com.ahmed3elshaer.geosquar.common.extensions.isRealtime
import com.ahmed3elshaer.geosquar.common.extensions.show
import com.ahmed3elshaer.geosquar.common.location.RxLocationExt
import com.ahmed3elshaer.geosquar.common.models.Venue
import com.ahmed3elshaer.geosquar.di.HomeComponent
import com.ahmed3elshaer.geosquar.di.HomeModules
import com.google.android.material.snackbar.Snackbar
import dagger.internal.DaggerCollections
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.stream.DoubleStream.builder
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory
    @Inject
    lateinit var sharedPrefWrapper: SharedPrefWrapper

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)
    }

    private val rxLocation: RxLocationExt = RxLocationExt()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var adapter: VenuesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
      /*  DaggerHomeComponent.builder()
            .magicModule(HomeModules(this))
            .build()
            .poke(this)*/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.viewState.observe(this, Observer {
            render(it)
        })
        getLocation()
        initVenuesList()
        initModeChange()
    }
    private fun render(event: Event<HomeViewState>) {
        event.getContentIfNotHandled()?.apply {
            renderLoading(isLoading)
            error?.let {
                error.printStackTrace()
                renderError(error)
                return
            }
            venues?.let {
                if (it.isEmpty())
                    renderEmptyState()
                else
                    renderVenues(it)
            }
        }
    }

    private fun initModeChange() {
        switch_mode.isChecked = isRealtime(sharedPrefWrapper)
        switch_mode.setOnCheckedChangeListener { _, isChecked ->
            changeMode(isChecked,sharedPrefWrapper)
            getLocation()
        }
    }

    private fun initVenuesList() {
        adapter = VenuesAdapter()
        recycler_venues.itemAnimator = DefaultItemAnimator()
        recycler_venues.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recycler_venues.adapter = adapter
    }

    private fun renderVenues(venues: List<Venue>) {
        Log.d("newList", venues.toString())
        text_state.hide()
        recycler_venues.show()
        adapter.updateList(venues)
    }

    private fun renderEmptyState() {
        recycler_venues.hide()
        text_state.show()
        text_state.text = getString(R.string.empty)
    }

    private fun getLocation() {
        if (isNetworkAvailable()) {
            rxLocation.stopLocationUpdates()
            compositeDisposable.add(
                    rxLocation.locations(this, isRealtime(sharedPrefWrapper))
                            .subscribe({ location ->
                                viewModel.exploreVenues(location, isRealtime(sharedPrefWrapper))
                            },
                                    { error: Throwable ->
                                        renderError(error)
                                    })
            )
        } else {
            showMessage("Requesting Offline cache")
            viewModel.checkForCachedVenues()
        }
    }

    private fun renderError(error: Throwable) {
        error.printStackTrace()
        recycler_venues.hide()
        text_state.show()
        text_state.text = getString(R.string.error)
        showMessage(error.message)
    }

    private fun showMessage(message: String?) {
        message?.let {
            val view = findViewById<View>(android.R.id.content) ?: return
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    private fun renderLoading(shouldLoad: Boolean) {
        if (shouldLoad)
            progress_loading.show()
        else
            progress_loading.hide()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        rxLocation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        rxLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
