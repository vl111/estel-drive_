package com.esteldrive.esteldrive.ui.files

import android.os.AsyncTask
import java.io.File
import java.net.URL

class DownloadFileAsyncTask(callback: (value: String, file: File, fileName: String) -> Unit,
                            value: String, file: File, fileName: String) : AsyncTask<URL, Int, Long>() {
    var callback = callback
    var value = value
    var file = file
    var fileName = fileName

    override fun doInBackground(vararg urls: URL): Long? {
        var totalSize: Long = 0

        callback(value, file, fileName)

        return totalSize
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)

    }

}