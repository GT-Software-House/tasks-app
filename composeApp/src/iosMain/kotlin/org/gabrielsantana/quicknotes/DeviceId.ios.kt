package org.gabrielsantana.quicknotes

import platform.UIKit.UIDevice

actual fun getDeviceId(): String {
    return UIDevice.currentDevice.identifierForVendor?.UUIDString!!
}