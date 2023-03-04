package com.digiventure.ventnote.feature.notes.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.digiventure.ventnote.data.NoteRepository
import com.digiventure.ventnote.data.NoteModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotesPageViewModel @Inject constructor(
    private val repository: NoteRepository
): ViewModel() {
    val noteList: LiveData<Result<List<NoteModel>>> = liveData {
        emitSource(repository.getNoteList()
            .onEach {}
            .asLiveData())
    }

    // Toggle search field
    val isSearching = mutableStateOf(false)
    fun toggleIsSearching() {
        isSearching.value = !isSearching.value
    }

    // Searchfield value
    val searchedTitleText = mutableStateOf("")
    fun setSearchedTitleText(title: String) {
        searchedTitleText.value = title
    }
}