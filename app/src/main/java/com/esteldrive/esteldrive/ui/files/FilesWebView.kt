package com.esteldrive.esteldrive.ui.files

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.WebView
import com.esteldrive.esteldrive.R
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Intent


class FilesWebView : AppCompatActivity() {

    var url: String? = null
    var locationStorage: String? = null
    var name: String? = null
    var locationDB: String? = null

    var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files_web_view)

        storage = FirebaseStorage.getInstance()

        val extras = intent.extras
        if (extras != null) {
            url = extras.getString("url")
            locationStorage = extras.getString("locationInStorage")
            name = extras.getString("name")
            locationDB = extras.getString("locationInDB")
        }

        var pdfViewer = findViewById<View>(R.id.pdfView) as PDFView
        pdfViewer.visibility = View.GONE

        var mWebView = findViewById<View>(R.id.filesWV) as WebView
        mWebView.visibility = View.GONE
        mWebView.settings.setJavaScriptEnabled(true)
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.displayZoomControls = false
        mWebView.settings.useWideViewPort = true
        mWebView.settings.setSupportZoom(true)
        mWebView.setInitialScale(25)

        val fab = findViewById<View>(R.id.fab_share_file)
        fab.setOnClickListener { view ->
            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = url
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing URL Estel drive")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share url"))
        }

        var savePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() +
                "/" + "Estel-drive" + "/" + locationDB

        try {
            val prefs = getSharedPreferences("files", MODE_PRIVATE)
            val extention = prefs.getString(savePath + "/" + name, "")
            var file = File(savePath, name + extention)
            if (file.exists()) {
                if (!extention.contains("pdf")) {
                    mWebView.visibility = View.VISIBLE
                    val uri = Uri.fromFile(file)
                    mWebView.loadUrl(uri.toString())
                } else {
                    pdfViewer.visibility = View.VISIBLE
                    pdfViewer.fromFile(file).load()
                }
            } else if (isOnline()) {
                storage!!.getReferenceFromUrl(url!!)
                        .metadata.addOnSuccessListener({ md ->
                    var folder = File(savePath)
                    if (!folder.exists())
                        folder.mkdirs()

                    var format = md.contentType
                    format = format.substring(format.lastIndexOf("/") + 1)
                    var fileName = name +
                            "." + format
                    file = File(savePath, fileName)
                    storage!!.getReferenceFromUrl(url!!)
                            .getFile(file).addOnSuccessListener {
                        file.createNewFile()
                        addFileToSharedPreferences(savePath + "/" + name,
                                "." + format)

                        if (format.contains("pdf")) {
                            pdfViewer.visibility = View.VISIBLE
                            pdfViewer.fromFile(file).load()
                        } else {
                            mWebView.visibility = View.VISIBLE
                            mWebView.loadUrl(Uri.fromFile(file).toString())
                        }
                    }
                })
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addFileToSharedPreferences(name: String, extention: String) {
        val editor: SharedPreferences.Editor = getSharedPreferences("files", AppCompatActivity.MODE_PRIVATE).edit()
        editor.putString(name, extention)
        editor.apply()
    }

    private fun isOnline(): Boolean {
        var cm: ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.getActiveNetworkInfo() == null)
            return false
        var netInfo: NetworkInfo = cm.getActiveNetworkInfo()
        return netInfo != null && netInfo.isConnectedOrConnecting()
    }
}
