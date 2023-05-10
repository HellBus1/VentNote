package com.digiventure.ventnote.data.google_api

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive


class GoogleApiHelper constructor(
    private val credential: GoogleAccountCredential
) {
    var driveInstance: Drive? = null
        get() {
            if (field == null) {
                synchronized(Drive::class.java) {

                    field = Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        GsonFactory(),
                        credential
                    )
                        .setApplicationName("VentNote")
                        .build()
                }
            }
            return field
        }
        private set
}