package com.digiventure.ventnote.feature.note_detail.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.NoteModel


class NoteDetailPageMockVM: ViewModel(), NoteDetailPageBaseVM {
    override val loader: MutableLiveData<Boolean> = MutableLiveData(false)
    override var noteDetail: MutableLiveData<Result<NoteModel>> = MutableLiveData()

    override var titleText: MutableState<String> = mutableStateOf("This is sample title text")
    override var descriptionText: MutableState<String> = mutableStateOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium odio maximus tellus pellentesque, a dignissim massa commodo.\n")
    override var isEditing: MutableState<Boolean> = mutableStateOf(false)

    init {
        // Initialize with mock data
        val mockNote = NoteModel(
            id = 0,
            title = "This is sample title text",
            note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec dignissim, sem sit amet consectetur ornare, lorem orci vulputate tortor, scelerisque vulputate elit nulla sed lacus. Praesent aliquet dui vitae elit tincidunt, non commodo dui semper. Etiam faucibus tempus dui et sagittis. Donec non magna tempus, lobortis leo et, vestibulum risus. Duis tincidunt est ante, ac venenatis ex lacinia sit amet. Praesent eu sem a velit feugiat condimentum. Donec mollis blandit tellus. Aliquam dignissim nulla at lacus consequat, vitae fermentum nunc vehicula. Cras vulputate dui mauris, vitae ultricies eros consectetur ac. Mauris ac velit nec quam finibus mollis id sed purus. Pellentesque blandit vehicula augue, at lobortis est tincidunt ut. Proin nec consequat neque.\n" +
                    "\n" +
                    "Praesent luctus risus nisl. Phasellus justo nunc, cursus ac ligula nec, pellentesque viverra elit. Sed a sem libero. Sed eleifend posuere leo et tincidunt. In quis ultrices diam. Fusce vitae tempor arcu, at iaculis neque. Phasellus nec ex et dolor elementum condimentum. Sed pretium suscipit lacinia. Vivamus tristique tellus urna, at pharetra massa tincidunt tempus. Nulla aliquet erat ligula, eget commodo erat congue ut. Nulla facilisi. Praesent erat arcu, cursus eget felis at, egestas tincidunt ex. Nulla hendrerit maximus neque, commodo tincidunt lacus faucibus at. Praesent elementum ut erat ac consequat.\n" +
                    "\n" +
                    "Suspendisse ac porta mi, eu congue lectus. Nulla mollis efficitur sagittis. In et nunc in ante tincidunt porttitor. Aliquam accumsan nibh nunc, eu mollis nunc tempus laoreet. Etiam placerat maximus bibendum. Duis volutpat tortor at orci lacinia, vitae ullamcorper sem cursus. Proin et tellus ac nunc tristique condimentum. Sed non maximus turpis, malesuada sodales augue. In pellentesque, nulla eu blandit ultricies, nisl ipsum malesuada nulla, eget dignissim erat odio a lectus.\n" +
                    "\n" +
                    "Suspendisse tempor sapien vel massa viverra, at faucibus ipsum tincidunt. Vestibulum euismod, dolor eu finibus tincidunt, nulla ante lobortis magna, vel fringilla felis velit et nisi. Suspendisse aliquet lacus dolor, id auctor felis dapibus eget. Donec interdum lectus vitae dolor pharetra placerat. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Cras sagittis elit ac dapibus volutpat. Integer in nunc in eros semper pharetra. Morbi rhoncus, turpis ac congue bibendum, massa massa efficitur quam, quis accumsan justo dolor congue diam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum ipsum elit, dictum in laoreet eu, bibendum a sem. Curabitur efficitur mollis imperdiet. Suspendisse maximus, diam id tincidunt porta, risus mauris volutpat urna, ac aliquet ante eros non odio. Phasellus at hendrerit nibh, ut imperdiet neque.\n" +
                    "\n" +
                    "Curabitur diam turpis, pretium sed velit a, placerat laoreet tellus. Fusce vehicula, diam iaculis cursus blandit, justo justo bibendum magna, nec rutrum magna urna quis mauris. Nullam sed justo eros. Nullam ac volutpat turpis. Vivamus rutrum maximus maximus. Sed vehicula sem suscipit nibh posuere pharetra. Vestibulum non velit quis velit semper imperdiet eget ut lorem. Mauris pulvinar ex lectus, sed pharetra nulla placerat sed. Suspendisse in felis eleifend, euismod sem nec, rutrum lectus. Phasellus non lectus cursus, mattis purus in, aliquam lorem. Curabitur sagittis facilisis finibus. Duis dolor neque, tristique id feugiat eget, eleifend sit amet magna. Sed pharetra fermentum diam quis dignissim. Etiam sagittis vel diam at placerat. Aenean tempor nisl eget nunc tempus malesuada. Aliquam rhoncus, arcu nec convallis luctus, nibh sem suscipit ante, sit amet imperdiet nisi neque non velit."
        )
        noteDetail.value = Result.success(mockNote)
    }

    override suspend fun getNoteDetail(id: Int) {
        // Mock implementation - data is already set in init
    }

    override suspend fun updateNote(note: NoteModel): Result<Boolean> = Result.success(true)
    override suspend fun deleteNoteList(vararg notes: NoteModel): Result<Boolean> = Result.success(true)
}