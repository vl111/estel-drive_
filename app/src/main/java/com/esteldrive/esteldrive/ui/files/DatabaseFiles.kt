package com.esteldrive.esteldrive.ui.files

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.esteldrive.esteldrive.R
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*


class DatabaseFiles : AppCompatActivity() {
    private val TAG: String = "DATABASEFILES"
    private val PICKFILE_RESULT_CODE = 1
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1

    var loacationDB: String? = null
    var isAdmin: Boolean? = null

    var listView: ListView? = null
    var fam: FloatingActionMenu? = null

    var mDatabase: DatabaseReference? = null
    var storage: FirebaseStorage? = null
    var filesInfoList: MutableList<FilesModelItem?> = mutableListOf()

    var folderName = ""

    private var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_files)

        mAuth = FirebaseAuth.getInstance()
        user = mAuth!!.currentUser

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()

        val extras = intent.extras
        if (extras != null) {
            loacationDB = extras.getString("loacationDB", "")
            isAdmin = extras.getBoolean("isAdmin", false)
        }

        listView = findViewById<View>(R.id.files_databaseLV) as ListView

        fam = findViewById(R.id.fam_filesDB)
        fam!!.isIconAnimated = true
        var fabAdd: FloatingActionButton = findViewById(R.id.add_fab)
        fabAdd.setOnClickListener { v ->
            fam!!.close(true)

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL)
            } else {
                addFileToDB()
            }

        }
        var fabDel: FloatingActionButton = findViewById(R.id.del_fab)
        fabDel.setOnClickListener { v ->
            fam!!.close(true)

            var intent = Intent(this, DeleteFiles::class.java)
            intent.putExtra("loacationDB", loacationDB)
            startActivity(intent)
        }
        var fabFolder: FloatingActionButton = findViewById(R.id.add_folder_fab)
        fabFolder.setOnClickListener { v ->
            fam!!.close(true)

            addFolderToDB()
        }
        var fabDownloadAll: FloatingActionButton = findViewById(R.id.download_all_files_fab)
        fabDownloadAll.setOnClickListener { v ->
            fam!!.close(true)

            downloadAllFiles(loacationDB!!)
            getFiles()
        }

        if (!isAdmin!!) {
            fabAdd.isEnabled = false
            fabDel.isEnabled = false
            fabFolder.isEnabled = false
        }

        listView!!.setOnTouchListener { v, me ->
            fam!!.close(true)
            false
        }

        getFiles()
    }

    private fun addFileToDB() {

        var intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("file/*")
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICKFILE_RESULT_CODE)
    }

    private fun addFolderToDB() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Folder name")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT /*or InputType.TYPE_TEXT_VARIATION_PASSWORD*/
        input.setPadding(30, 30, 30, 30)
        builder.setView(input)

        builder.setPositiveButton("OK", { dialog, which ->
            if (!input.text.equals("")) {
                folderName = input.text.toString()
                addFileToDB()
            } else
                dialog.cancel()

        })
        builder.setNegativeButton("Cancel", { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun getFiles() {
        if (storage != null) {
            mDatabase!!.child("Files/" + loacationDB).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    filesInfoList.clear()
                    dataSnapshot.children.forEach { d ->
                        run {
                            var fKey = d.key.toString()
                            if (d.children.toList().size > 0) {
                                filesInfoList.add(FilesModelItem(fKey, true, loacationDB + "/" + fKey))
                            } else {
                                var fValue = d.getValue(String::class.java)
                                filesInfoList.add(FilesModelItem(fKey, false, fValue))
                            }
                        }
                    }
                    updateListView()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun updateListView() {
        listView!!.setOnItemClickListener({ parent, view, position, id ->
            openFile(position)
        })

        listView!!.setOnItemLongClickListener { parent, view, position, l ->
            val popup = PopupMenu(this, view)
            popup.getMenuInflater().inflate(R.menu.popup_menu_files, popup.getMenu())
            if (filesInfoList.get(position)!!.isFolder) {
                popup.menu.findItem(R.id.rename_file).setEnabled(false)
                popup.menu.findItem(R.id.download_file).setEnabled(false)
            }
            if (!isAdmin!!)
                popup.menu.findItem(R.id.rename_file).setEnabled(false)
            popup.setOnMenuItemClickListener({ item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.open_file -> {
                        openFile(position)
                    }
                    R.id.download_file -> {
                        var saveDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS).toString() +
                                File.separator + "Estel-drive" + File.separator + loacationDB
                        var folder = File(saveDir)
                        if (!folder.exists())
                            folder.mkdirs()
                        storage!!.getReferenceFromUrl(filesInfoList.get(position)!!.value.toString())
                                .metadata.addOnSuccessListener { md ->

                            var format = md.contentType
                            format = format.substring(format.lastIndexOf("/") + 1)
                            var fileName = filesInfoList.get(position)!!.title +
                                    "." + format
                            var file = File(saveDir, fileName)
                            DownloadFileAsyncTask({ value, _file, fileName ->
                                var file = _file
                                storage!!.getReferenceFromUrl(value)
                                        .getFile(file).addOnSuccessListener {
                                    file.createNewFile()
                                    var snackbar = Snackbar
                                            .make(fam!!, fileName, Snackbar.LENGTH_SHORT)
                                    snackbar.show()
                                    addFileToSharedPreferences(saveDir + "/" + filesInfoList.get(position)!!.title,
                                            "." + format)
                                }
                            }, filesInfoList.get(position)!!.value.toString(), file, fileName).execute()

                        }

                    }
                    R.id.rename_file -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("File name")
                        val input = EditText(this)
                        input.inputType = InputType.TYPE_CLASS_TEXT /*or InputType.TYPE_TEXT_VARIATION_PASSWORD*/
                        input.setPadding(30, 30, 30, 30)
                        builder.setView(input)

                        builder.setPositiveButton("OK", { dialog, which ->
                            if (!input.text.equals("")) {
                                mDatabase!!.child("Files/" + loacationDB + "/" + filesInfoList.get(position)!!.title)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                Log.i(TAG, dataSnapshot.value.toString())

                                                mDatabase!!.child("Files/" + loacationDB + "/" + input.text.toString())
                                                        .setValue(dataSnapshot.value.toString()).addOnSuccessListener {
                                                    mDatabase!!.child("Files/" + loacationDB + "/" + filesInfoList.get(position)!!.title)
                                                            .removeValue().addOnSuccessListener {
                                                        getFiles()
                                                    }
                                                }
                                            }

                                            override fun onCancelled(p0: DatabaseError?) {
                                            }
                                        })
                            } else
                                dialog.cancel()
                        })
                        builder.setNegativeButton("Cancel", { dialog, which ->
                            dialog.cancel()
                        })
                        builder.show()
                    }
                }
                true
            })
            popup.show()
            true
        }

        val adapter = FilesArrayAdapter(this,
                R.layout.item_layout_files, filesInfoList)
        listView!!.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("File name")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setPadding(30, 30, 30, 30)
            builder.setView(input)

            builder.setPositiveButton("OK", { dialog, which ->
                if (!input.text.equals("")) {
                    var country = this.loacationDB!!.substring(0, 2)
                    var uri = data!!.data
                    var fileName = input.text
                    var fn = folderName + "/"
                    folderName = ""
                    storage!!.getReference(country + "/" + genRandName(30) + fileName).putFile(uri).addOnSuccessListener { d ->
                        mDatabase!!.child("Files/" + this.loacationDB + "/" + fn + fileName).setValue(d.downloadUrl.toString()).addOnCompleteListener({
                            getFiles()
                        })
                    }
                } else
                    dialog.cancel()

            })
            builder.setNegativeButton("Cancel", { dialog, which ->
                dialog.cancel()
            })
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    addFileToDB()

                } else {

                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()

                }
                return
            }

            else -> {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getFiles()
    }

    private fun openFile(position: Int) {
        if (filesInfoList.get(position)!!.isFolder) {
            var intent = Intent(this, DatabaseFiles::class.java)
            intent.putExtra("loacationDB", filesInfoList.get(position)!!.value)
            intent.putExtra("isAdmin", isAdmin)
            startActivity(intent)
        } else {
            var intent = Intent(this, FilesWebView::class.java)
            intent.putExtra("url", filesInfoList.get(position)!!.value.toString())
            intent.putExtra("locationInStorage", loacationDB!!.substring(0, 2))
            intent.putExtra("locationInDB", loacationDB)
            intent.putExtra("name", filesInfoList.get(position)!!.title)

            startActivity(intent)

        }
    }

    private fun genRandName(_size: Int): String {
        var size = _size
        if (size < 14)
            size = 14
        val st = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val rand = Random()
        var result = ""
        for (i in 0..(size - 1)) {
            var r = rand.nextInt(st.length)
            var k = st.substring(r, r + 1)
            result = result + k
        }
        return result
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun downloadAllFiles(country: String) {
        if (storage != null) {
            mDatabase!!.child("Files/" + country).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { d ->
                        run {
                            var fKey = d.key.toString()
                            if (d.children.toList().size > 0) {
                                downloadAllFiles(country + "/" + fKey)
                            } else {

                                var saveDir = Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS).toString() +
                                        File.separator + "Estel-drive" + File.separator + country
                                var folder = File(saveDir)
                                if (!folder.exists())
                                    folder.mkdirs()
                                storage!!.getReferenceFromUrl(d.getValue(String::class.java))
                                        .metadata.addOnSuccessListener { md ->

                                    var format = md.contentType
                                    format = format.substring(format.lastIndexOf("/") + 1)
                                    var fullName = fKey +
                                            "." + format
                                    var file = File(saveDir, fullName)
                                    DownloadFileAsyncTask({ value, _file, fileName ->
                                        var file = _file
                                        storage!!.getReferenceFromUrl(value)
                                                .getFile(file).addOnSuccessListener {
                                            file.createNewFile()
                                            addFileToSharedPreferences(saveDir + "/" + fKey, "." + format)
                                        }
                                    }, d.getValue(String::class.java), file, fullName).execute()

                                }

                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
        }
    }

    private fun addFileToSharedPreferences(name: String, extention: String) {
        val editor: SharedPreferences.Editor = getSharedPreferences("files", AppCompatActivity.MODE_PRIVATE).edit()
        editor.putString(name, extention)
        editor.apply()
    }
}
