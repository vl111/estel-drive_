package com.esteldrive.esteldrive.ui.manual_bottomtab

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.esteldrive.esteldrive.R
import java.io.File


class ImageAdapter(
        private val activity: Context
) : PagerAdapter() {

    override fun getCount(): Int {
        return 3
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.activity_manual2, container, false)
        val imageView = view.findViewById<ImageView>(R.id.manual_image)
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
        loadImage(imageView, position)
        container.addView(view)
        return view
    }

    @Synchronized
    private fun loadImage(imageView: ImageView, index: Int) {
        var saveDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() +
                File.separator + "Estel-drive" + File.separator + "manual"
        var folder = File(saveDir)
        if (!folder.exists())
            folder.mkdirs()
        var file = File(saveDir + File.separator + "val" + index + ".jpeg")
        if (file.exists()) {
            imageView!!.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath.toString()))
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}
