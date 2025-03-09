package org.gabrielsantana.quicknotes.data.task

import kotlin.random.Random

actual fun getDeviceId(): String {
    //TODO: fix it
    return Random.nextInt().toString()
}