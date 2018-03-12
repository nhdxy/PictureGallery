package com.anhdxy.picturegallery.util

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.Toast

/**
 * Created by Andrnhd on 2018/3/10.
 */
private var toast: Toast? = null

fun Any.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    var activity: Activity? = null
    if (this is Activity) {
        activity = this
    } else if (this is Fragment) {
        activity = this.activity
    }
    if (activity == null) {
        return
    } else {
        if (toast == null) {
            toast = Toast.makeText(activity, message, duration)
        }
        toast!!.apply {
            setText(message)
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
    }
}