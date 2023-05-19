package com.digiventure.ventnote.data.google_api

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

class GoogleAPIHelperLayer {
    fun getGoogleAPIHelperInstance(credential: GoogleAccountCredential): GoogleApiHelper {
        return GoogleApiHelper(credential)
    }
}