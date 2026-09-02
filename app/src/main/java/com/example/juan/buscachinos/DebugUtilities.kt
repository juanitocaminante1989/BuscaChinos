package com.example.juan.buscachinos

import android.util.Log

/**
 * Created by Juan on 21/06/2017.
 */
object DebugUtilities {
    private const val TAG = "DEBUG"

    @JvmOverloads
    fun writeLog(text: String, excep: Exception? = null as Exception?) {
        Log.e(TAG, text)
        if (excep != null) {
            excep.printStackTrace()
        }
    }
}
