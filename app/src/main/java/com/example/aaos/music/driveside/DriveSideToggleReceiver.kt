package com.example.aaos.music.driveside

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DriveSideToggleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DriveSideBroadcastContract.ACTION_SET_LHD) {
            val isLhd = intent.getBooleanExtra(DriveSideBroadcastContract.EXTRA_IS_LHD, true)
            // Update singleton or repository backing your ViewModel state
            DriveSideRepository.setLhd(isLhd)
        }
    }
}
