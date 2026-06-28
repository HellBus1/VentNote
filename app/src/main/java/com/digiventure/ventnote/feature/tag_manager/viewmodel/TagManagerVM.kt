package com.digiventure.ventnote.feature.tag_manager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digiventure.ventnote.data.persistence.TagModel
import com.digiventure.ventnote.data.persistence.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TagManagerVM @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel(), TagManagerBaseVM {

    override val loader = MutableLiveData<Boolean>(false)

    private val _tagList = MutableLiveData<Result<List<TagModel>>>()
    override val tagList: LiveData<Result<List<TagModel>>> = _tagList

    override fun observeTags() {
        viewModelScope.launch {
            loader.postValue(true)
            tagRepository.getAllTags()
                .onEach { loader.postValue(false) }
                .collectLatest { result ->
                    _tagList.postValue(result)
                }
        }
    }

    override suspend fun insertTag(tag: TagModel): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val existingTagsResult = tagRepository.getAllTags().first()
            val existingTags = existingTagsResult.getOrDefault(emptyList())
            val nameExists = existingTags.any { it.name.equals(tag.name.trim(), ignoreCase = true) }
            if (nameExists) {
                return@withContext Result.failure(Exception("Tag name already exists"))
            }
            tagRepository.insertTag(tag).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTag(tag: TagModel): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val existingTagsResult = tagRepository.getAllTags().first()
            val existingTags = existingTagsResult.getOrDefault(emptyList())
            val nameExists = existingTags.any { 
                it.name.equals(tag.name.trim(), ignoreCase = true) && it.id != tag.id 
            }
            if (nameExists) {
                return@withContext Result.failure(Exception("Tag name already exists"))
            }
            tagRepository.updateTag(tag).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tag: TagModel): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            tagRepository.deleteTag(tag).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
