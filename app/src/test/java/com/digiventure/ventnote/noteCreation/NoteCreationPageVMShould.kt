package com.digiventure.ventnote.noteCreation

import com.digiventure.utils.BaseUnitTest
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.local.NoteModel
import org.mockito.kotlin.mock

class NoteCreationPageVMShould: BaseUnitTest() {
    private val repository: NoteRepository = mock()
    private val note = mock<NoteModel>()

}