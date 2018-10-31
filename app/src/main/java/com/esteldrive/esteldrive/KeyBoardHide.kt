package com.esteldrive.esteldrive

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView

class KeyBoardHide {
    public fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager =
                activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public fun setupUI(view: View, activity: Activity) {
        if (view !is EditText && view !is ScrollView) {
            view.setOnClickListener{
                hideSoftKeyboard(activity);
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView, activity)
            }
        }
    }
}