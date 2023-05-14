package com.digiventure.ventnote

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.google_api.DatabaseFiles
import com.digiventure.ventnote.data.google_api.GoogleAPIService
import org.junit.Before
import org.mockito.Mockito.mock

class GoogleAPIServiceShould: BaseUnitTest() {
    private val databaseFiles: DatabaseFiles = mock()

    private lateinit var service: GoogleAPIService

    @Before
    fun setup() {
        service = GoogleAPIService(databaseFiles)
    }
}