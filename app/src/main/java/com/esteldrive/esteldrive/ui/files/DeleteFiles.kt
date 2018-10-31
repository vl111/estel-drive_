package com.esteldrive.esteldrive.ui.files

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ListView
import com.esteldrive.esteldrive.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AlertDialog


class DeleteFiles : AppCompatActivity() {
    private val TAG: String = "DATABASEFILES"

    var listView: ListView? = null
    var country: String? = null

    var mDatabase: DatabaseReference? = null
    var storage: FirebaseStorage? = null
    var filesInfoList: MutableList<FilesModelItem?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_files)

        val extras = intent.extras
        if (extras != null) {
            country = extras.getString("loacationDB")
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)
        storage = FirebaseStorage.getInstance()


        listView = findViewById<View>(R.id.files_delete_databaseLV) as ListView

        getFiles()
    }

    private fun getFiles() {
        if (storage != null) {
            mDatabase!!.child("Files/" + country).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    filesInfoList.clear()
                    dataSnapshot.children.forEach { d ->
                        run {
                            var fKey = d.key.toString()
                            if (d.children.toList().size > 0) {
                                filesInfoList.add(FilesModelItem(fKey, true, country + "/" + fKey))
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

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        if (!filesInfoList.get(position)!!.isFolder) {
                            storage!!.getReferenceFromUrl(filesInfoList.get(position)!!.value!!).delete().addOnCompleteListener {
                                mDatabase!!.child("Files/" + country + "/" + filesInfoList.get(position)!!.title)
                                        .removeValue().addOnCompleteListener {
                                    getFiles()
                                }
                            }
                            view.setBackgroundColor(Color.parseColor("#ff0000"))
                        } else {
                            mDatabase!!.child("Files/" + country + "/" + filesInfoList.get(position)!!.title)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            dataSnapshot.children.forEach { d ->
                                                run {
                                                    if (d.children.toList().size == 0) {
                                                        storage!!.getReferenceFromUrl(d.getValue(String::class.java)!!).delete().addOnSuccessListener {
                                                            mDatabase!!.child("Files/" + country + "/" + filesInfoList.get(position)!!.title + "/" + d.key.toString())
                                                                    .setValue(null).addOnSuccessListener {

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            getFiles()
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            Log.e(TAG, "onCancelled", databaseError.toException())
                                        }
                                    })
                        }

                    }
                    DialogInterface.BUTTON_NEGATIVE -> {

                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()


        })

        val adapter = FilesArrayAdapter(this,
                R.layout.item_layout_files, filesInfoList)
        listView!!.adapter = adapter
    }
}
