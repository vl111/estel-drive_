package com.esteldrive.esteldrive.ui.manual


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esteldrive.esteldrive.R

class ActivityManual1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.activity_manual1, container, false)
    }


    companion object {
        fun newInstance() = ActivityManual1()
    }
}
