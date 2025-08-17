package com.digiventure.ventnote.config

import android.content.Context
import com.digiventure.ventnote.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

/**
 * Provides a helper for obtaining a Google Drive API client instance.
 *
 * This class encapsulates the logic for creating and managing a [Drive] client,
 * ensuring that only a single instance is used throughout the application (singleton pattern).
 * It handles authentication using a [GoogleSignInAccount] and scopes access to app data.
 *
 * Note: This implementation uses the Drive REST API, as the deprecated Drive Android API
 * is no longer supported (https://developers.google.com/drive/api/guides/android-api-deprecation).
 * Once the Google Drive SDK provides updated support or a new official Android API,
 * this class may be updated to utilize it for improved performance and features.
 */
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