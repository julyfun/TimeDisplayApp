package com.example.timedisplayapp

import android.os.SystemClock
import java.util.Locale
import java.io.Serializable
import android.util.Log
import androidx.compose.ui.graphics.Color

fun randomColor(): Color {
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color(red, green, blue)
}

data class Task(
    var name: String,
    var timeMsSpentBefore: Long = 0L,
    var activateTimeMs: Long = 0L,
    var isActive: Boolean = false,
    var backGroundColor: Color = Color.Black,
    var activeTick: Long = 0L,
) : Serializable {
    constructor(name: String) : this(name = name, backGroundColor = randomColor())

    fun formatTime(): String {
        val timeMsSpent = if (isActive) {
            timeMsSpentBefore + (SystemClock.elapsedRealtime() - activateTimeMs)
        } else {
            timeMsSpentBefore
        }
        val timeSSpent = timeMsSpent / 1000
        val hours = timeSSpent / 3600
        val minutes = (timeSSpent % 3600) / 60
        val seconds = (timeSSpent % 60)
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun try_activate() {
        Log.d("TaskManager", "$name Activate")
        if (!isActive) {
            activateTimeMs = SystemClock.elapsedRealtime()
            isActive = true
        }
    }

    fun try_deactivate() {
        Log.d("TaskManager", "$name Deactivate")
        if (isActive) {
            timeMsSpentBefore += SystemClock.elapsedRealtime() - activateTimeMs
            isActive = false
        }
    }

}

