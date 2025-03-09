package org.gabrielsantana.quicknotes.data.task

import platform.UIKit.UIDevice

actual fun getDeviceId(): String {
    return UIDevice.currentDevice.identifierForVendor?.UUIDString!!
}