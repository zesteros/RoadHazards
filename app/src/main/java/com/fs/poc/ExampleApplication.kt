package com.fs.poc

import android.app.Application
import com.here.see.LiveSenseEngine

/**
 * This app class is used for initializing live sense engine. It needs application context to
 * initialize the components.
 */
class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Get the live sense engine instance
        val liveSenseEngine = LiveSenseEngine.getInstance()
        // Initialise the Live Sense Engine.
        liveSenseEngine.initialize(this)
    }
}