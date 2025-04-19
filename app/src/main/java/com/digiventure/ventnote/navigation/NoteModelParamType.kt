package com.digiventure.ventnote.navigation

import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import com.digiventure.ventnote.data.persistence.NoteModel
import com.google.gson.Gson

class NoteModelParamType : NavType<NoteModel>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): NoteModel? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, NoteModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key) as? NoteModel
        }
    }

    override fun parseValue(value: String): NoteModel {
        return Gson().fromJson(value, NoteModel::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: NoteModel) {
        bundle.putParcelable(key, value)
    }
}