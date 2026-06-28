package com.digiventure.ventnote.feature.tag_manager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digiventure.ventnote.data.persistence.TagModel

class TagManagerMockVM : ViewModel(), TagManagerBaseVM {
    override val loader = MutableLiveData<Boolean>(false)

    private val _tagList = MutableLiveData<Result<List<TagModel>>>(
        Result.success(
            listOf(
                TagModel(1, "Work", "#EF5350"),
                TagModel(2, "Personal", "#42A5F5"),
                TagModel(3, "Ideas", "#66BB6A")
            )
        )
    )
    override val tagList: LiveData<Result<List<TagModel>>> = _tagList

    override fun observeTags() {}

    override suspend fun insertTag(tag: TagModel): Result<Long> = Result.success(1L)
    override suspend fun updateTag(tag: TagModel): Result<Boolean> = Result.success(true)
    override suspend fun deleteTag(tag: TagModel): Result<Boolean> = Result.success(true)
}
