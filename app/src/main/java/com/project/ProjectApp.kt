package com.project

import android.app.Application
import com.project.di.applicationModules
import com.project.domain.di.domainModules
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ProjectApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@ProjectApp)
            modules(
                applicationModules +
                        domainModules,
            )
        }
    }
}
