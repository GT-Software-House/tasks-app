package org.gabrielsantana.tasks.data

import android.provider.Settings.Secure
import kotlin.random.Random

actual fun getDeviceId(): String {
    //TODO: fix it
    return Random.nextInt().toString()
}