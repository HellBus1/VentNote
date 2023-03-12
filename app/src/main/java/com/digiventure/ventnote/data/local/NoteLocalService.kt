package com.digiventure.ventnote.data.local

import android.content.res.Resources
import androidx.compose.ui.res.stringResource
import com.digiventure.ventnote.R
import com.digiventure.ventnote.commons.StringUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteLocalService @Inject constructor(
    private val dao: NoteDAO
) {
    suspend fun getNoteList(): Flow<Result<List<NoteModel>>> =
        dao.getNotes().map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException(StringUtil.getStringFromResources(R.string.note_list_error))))
        }

    suspend fun deleteNoteList(vararg notes: NoteModel): Flow<Result<Boolean>> =
        flow {
            val result = (dao.deleteNotes(*notes) == notes.size)
            emit(Result.success(result))
        }.catch {
            emit(Result.failure(RuntimeException(StringUtil.getStringFromResources(R.string.delete_note_error))))
        }

    suspend fun getNoteDetail(id: Int): Flow<Result<NoteModel>> =
        dao.getNoteDetail(id).map {
            Result.success(it)
        }.catch {
            emit(Result.failure(RuntimeException(StringUtil.getStringFromResources(R.string.note_detail_error))))
        }
}