package com.hazratbilal.notecraft

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: BaseApplication? = null

        @get:Synchronized
        val appContext: BaseApplication?
            get() {
                if (instance == null) {
                    instance = BaseApplication()
                }
                return instance
            }
    }
}