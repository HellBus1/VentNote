package com.digiventure.ventnote.commons

import android.content.res.Resources

object StringUtil {
    /**
     * Return string from resource
     * @param id of string resource
     * */
    fun getStringFromResources(id: Int): String {
        return Resources.getSystem().getString(id)
    }
}