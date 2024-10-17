package com.digiventure.ventnote.config

import android.content.Context
import com.digiventure.ventnote.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class DriveAPI {
    companion object {
        private var instance: Drive? = null

        fun getInstance(context: Context, signInAccount: GoogleSignInAccount): Drive {
            return instance ?: synchronized(this) {
                instance ?: createDriveInstance(context, signInAccount).also { instance = it }
            }
        }

        private fun createDriveInstance(context: Context, signInAccount: GoogleSignInAccount): Drive {
            val scopes = listOf(DriveScopes.DRIVE_APPDATA)
            val credential = GoogleAccountCredential.usingOAuth2(context, scopes)
            credential.selectedAccount = signInAccount.account

            return Drive.Builder(
                NetHttpTransport(),
                GsonFactory(),
                credential
            ).apply {
                applicationName = context.getString(R.string.app_name)
            }.build()
        }
    }
}