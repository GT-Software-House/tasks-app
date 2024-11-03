package org.gabrielsantana.tasks

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform