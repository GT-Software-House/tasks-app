package org.gabrielsantana.tasks.auth.login

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

fun startFirebaseAuth(serverId: String) {
    GoogleAuthProvider.create(GoogleAuthCredentials(serverId))
}