package com.semba.androidrealtimesamples.Shared

import android.app.Application
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class MyApplication: Application() {

    private val notificationModule: Module = module {
        single { NotificationManager() }
    }

    private fun initInjection() = loadKoinModules(
        notificationModule
    )

    override fun onCreate() {
        super.onCreate()
        initInjection()
    }
}