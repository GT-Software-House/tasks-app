package org.gabrielsantana.tasks.data

internal fun Number.convertToBoolean() = this == 1

internal fun Boolean.convertToNumber() = if (this) 1 else 0