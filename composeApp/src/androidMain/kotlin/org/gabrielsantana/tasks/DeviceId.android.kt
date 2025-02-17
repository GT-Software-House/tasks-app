package org.gabrielsantana.tasks

import android.provider.Settings.Secure

actual fun getDeviceId(): String {
    return Secure.getString(TasksApp.getAppContext().contentResolver, Secure.ANDROID_ID)
}