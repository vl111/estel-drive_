package com.esteldrive.esteldrive.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import com.esteldrive.esteldrive.R
import com.esteldrive.esteldrive.model.User
import com.esteldrive.esteldrive.ui.main.list_view_models.users.ModelUserItem
import com.esteldrive.esteldrive.ui.main.list_view_models.users.UsersArrayAdapter
import com.google.firebase.database.*


class UsersList : AppCompatActivity() {

    private val TAG: String = "USERSLOG"

    var listView: ListView? = null
    var searchView: SearchView? = null
    var querySearch: String = ""

    var mDatabase: DatabaseReference? = null
    var users: MutableList<User?>? = null

    var userInfo: MutableList<ModelUserItem?> = mutableListOf()

    var country: String? = null
    var delOrAdd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        val extras = intent.extras
        if (extras != null) {
            country = extras.getString("loacationDB")
            delOrAdd = extras.getString("delOrAdd")
        }
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase!!.keepSynced(true)

        searchView = findViewById(R.id.search_view_users)
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText.equals("")){
                    this.onQueryTextSubmit("")
                }
                return false
            }

            override fun onQueryTextSubmit(query1: String): Boolean {
                querySearch = query1.toLowerCase()
                makeListOfUsers()
                return false
            }

        })

        makeListOfUsers()

        listView = findViewById<View>(R.id.usersLV) as ListView

    }

    private fun makeListOfUsers() {
        users = mutableListOf()
        userInfo = mutableListOf()
        mDatabase!!.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, dataSnapshot.toString())
                dataSnapshot.children.forEach { d ->
                    run {
                        var u: User? = d.getValue(User::class.java)
                        u!!.objectId = d.key
                        if (u!!.email == null)
                            u!!.email = ""
                        if (u!!.fullName == null)
                            u!!.fullName = ""

                        var bool: Boolean? = null
                        d.child("status").children.forEach { c ->
                            if (c.key.toString().equals(country))
                                bool = c.getValue(Boolean::class.java)
                        }

                        if (bool == null)
                            bool = false
                        if (u.email!!.toLowerCase().contains(querySearch) || u.fullName!!.toLowerCase().equals(querySearch)) {
                            if (!bool!! && delOrAdd.equals("add")) {
                                users!!.add(u)
                                userInfo.add(ModelUserItem(users!!.get(users!!.size - 1)!!.photo, "Name: " +
                                        users!!.get(users!!.size - 1)!!.fullName + "\n" +
                                        "E-mail: " + users!!.get(users!!.size - 1)!!.email!!, null, null))
                            }
                            if (bool!! && delOrAdd.equals("del")) {
                                users!!.add(u)
                                userInfo.add(ModelUserItem(users!!.get(users!!.size - 1)!!.photo, "Name: " +
                                        users!!.get(users!!.size - 1)!!.fullName + "\n" +
                                        "E-mail: " + users!!.get(users!!.size - 1)!!.email!!, null, null))
                            }
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

    private fun updateListView() {
        listView!!.setOnItemClickListener({ parent, view, position, id ->

            var popup = PopupMenu(this@UsersList, view)
            popup.getMenuInflater().inflate(R.menu.popup_menu3, popup.getMenu())
            popup.setOnMenuItemClickListener({ item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.delUserItem -> {
                        Toast.makeText(this@UsersList, item.title, Toast.LENGTH_SHORT).show()
                        mDatabase!!.child("Users").child(users!!.get(position)!!.objectId)
                                .child("status").child(country!!).setValue(null)
                        var imView = view.findViewById<View>(R.id.item_icon) as ImageView
                        imView.setImageResource(R.drawable.icon_del)
                        userInfo.get(position)!!.icon = R.drawable.icon_del
                    }
                    R.id.addUserItem -> {
                        Toast.makeText(this@UsersList, item.title, Toast.LENGTH_SHORT).show()
                        mDatabase!!.child("Users").child(users!!.get(position)!!.objectId)
                                .child("status").child(country!!).setValue(true)
                        var imView = view.findViewById<View>(R.id.item_icon) as ImageView
                        imView.setImageResource(R.drawable.icon_add)
                        userInfo.get(position)!!.icon = R.drawable.icon_add
                    }
                }
                true
            })
            popup.show()
        })

        val adapter = UsersArrayAdapter(this,
                R.layout.item_layout_users, userInfo)
        listView!!.adapter = adapter
    }


    companion object {
        fun newInstance() = UsersList()
    }
}
