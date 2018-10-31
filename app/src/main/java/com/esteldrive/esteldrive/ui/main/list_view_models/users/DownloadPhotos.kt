package com.esteldrive.esteldrive.ui.main.list_view_models.users


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.esteldrive.esteldrive.R
import java.net.URL

class DownloadPhotos(iv: ImageView, strPhotoURL: String, mui: ModelUserItem, context: Context) : AsyncTask<URL, Int, Long>() {

    var iv = iv
    var strPhotoUrl = strPhotoURL
    var bitmap: Bitmap? = null
    var mui = mui
    var context = context

    override fun doInBackground(vararg urls: URL): Long? {
        var totalSize: Long = 0

        try {
            bitmap = BitmapFactory.decodeStream(
                    URL(strPhotoUrl).openConnection().getInputStream())
        } catch (e: Exception) {
                bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.noimage);

        }



        return totalSize
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)

        if (bitmap != null) {
            iv.setImageBitmap(bitmap)
            mui.filePhoto = bitmap
        }

    }
}

