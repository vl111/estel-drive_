package com.esteldrive.esteldrive.ui.manual_bottomtab

import android.os.AsyncTask
import java.net.URL

class DownloadImgAsyncTask(callback: (url: String) -> Unit,
                           url: String/*, imgView: ImageView?, file: File*/) : AsyncTask<URL, Int, Long>() {
    var callback = callback
    var url = url

    override fun doInBackground(vararg urls: URL): Long? {
        var totalSize: Long = 0

        callback(url)

        return totalSize
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)
    }

}