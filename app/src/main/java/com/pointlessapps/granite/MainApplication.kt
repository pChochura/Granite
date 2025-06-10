package com.pointlessapps.granite

import android.app.Application
import android.content.Context
import com.pointlessapps.granite.di.applicationModules
import com.pointlessapps.granite.domain.di.domainModules
import com.pointlessapps.granite.local.datasource.di.localModules
import com.pointlessapps.granite.supabase.datasource.di.supabaseModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.fileProperties

class MainApplication : Application() {

    private fun KoinApplication.koinConfiguration(context: Context) {
        fileProperties()
        androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
        androidContext(context)
        modules(applicationModules + domainModules + supabaseModules + localModules)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin { koinConfiguration(applicationContext) }
    }
}
