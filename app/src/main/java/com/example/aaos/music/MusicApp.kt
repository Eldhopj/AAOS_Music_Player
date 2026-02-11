package com.example.aaos.music

import android.app.Application
import com.example.aaos.music.driveside.DriveSideRepository
import com.example.aaos.music.driveside.DriveStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()

        DriveStore.appContext = applicationContext
        DriveSideRepository.init(applicationContext)

    }
}
