package com.example.aaos.music.driveside


import android.content.Context
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DriveSideRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _isLhd = MutableStateFlow(true)
    val isLhd: StateFlow<Boolean> = _isLhd.asStateFlow()

    // Optional: persist in DataStore
    fun init(context: Context) {
        scope.launch {
            val persisted = DriveStore.read(context) ?: true
            _isLhd.value = persisted
        }
    }

    fun setLhd(value: Boolean) {
        _isLhd.value = value
        scope.launch { DriveStore.write(value) }
    }
}
