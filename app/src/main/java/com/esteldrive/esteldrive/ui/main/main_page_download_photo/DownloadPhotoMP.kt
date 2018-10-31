package com.esteldrive.esteldrive.ui.main.main_page_download_photo

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.esteldrive.esteldrive.R
import java.net.URL

class DownloadPhotoMP(iv: ImageView, strPhotoURL: String, photoBtmp: MutableList<Bitmap?>?, activity: Activity) : AsyncTask<URL, Int, Long>() {

    var iv = iv
    var strPhotoUrl = strPhotoURL
    var bitmap: Bitmap? = null
    var photoBitmap = photoBtmp
    var activity = activity

    override fun doInBackground(vararg urls: URL): Long? {
        var totalSize: Long = 0

        try {
            bitmap = BitmapFactory.decodeStream(
                    URL(strPhotoUrl).openConnection().getInputStream())
        } catch (e: Exception) {
            bitmap = null
        }

        return totalSize
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)

        if (bitmap != null) {
            iv.setImageBitmap(bitmap)
            photoBitmap!!.clear()
            photoBitmap!!.add(bitmap)
        } else {
            bitmap = BitmapFactory.decodeResource(activity!!.resources,
                    R.drawable.noimage)
            iv.setImageBitmap(bitmap)
            photoBitmap!!.clear()
            photoBitmap!!.add(bitmap)
        }

    }
}