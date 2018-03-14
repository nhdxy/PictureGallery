package com.anhdxy.picturegallery.util

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.anhdxy.picturegallery.activity.MultiSelectPictureActivity
import com.anhdxy.picturegallery.activity.REQUEST_CODE
import com.anhdxy.picturegallery.activity.RESULT_DATA
import com.anhdxy.picturegallery.activity.SingleSelectPictureActivity
import java.lang.ref.WeakReference

/**
 * Created by Andrnhd on 2018/3/14.
 */
class Anhdxy {
    private var mContext: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment?>? = null
    private var isCrop: Boolean = true
    private var selectType: SelectType = SelectType.MULTI
    private var max_selected_size = 9

    private constructor(activity: Activity) : this(activity, null)
    private constructor(fragment: Fragment) : this(fragment.activity, fragment)

    private constructor(activity: Activity, fragment: Fragment?) {
        mContext = WeakReference(activity)
        if (fragment != null) {
            mFragment = WeakReference(fragment)
        }
    }

    companion object {
        fun from(activity: Activity): Anhdxy {
            return Anhdxy(activity)
        }

        fun from(fragment: Fragment): Anhdxy {
            return Anhdxy(fragment)
        }

        fun obtainResult(requestCode: Int, resultCode: Int, data: Intent?): ArrayList<String> {
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                return data.getStringArrayListExtra(RESULT_DATA)
            }
            return arrayListOf()
        }
    }

    fun setCrop(isCrop: Boolean): Anhdxy {
        this.isCrop = isCrop
        return this
    }

    fun setSelectType(selectType: SelectType): Anhdxy {
        this.selectType = selectType
        return this
    }

    fun setMaxSelectedSize(max_selected_size: Int): Anhdxy {
        this.max_selected_size = max_selected_size
        return this
    }

    fun choose() {
        if (selectType == SelectType.SINGLE) {
            val intent = Intent(getActivity(), SingleSelectPictureActivity::class.java)
            intent.putExtra("isCrop", isCrop)
            if (getFragment() == null) {
                getActivity().startActivityForResult(intent, REQUEST_CODE)
            } else {
                getFragment()!!.startActivityForResult(intent, REQUEST_CODE)
            }
        } else {
            val intent = Intent(getActivity(), MultiSelectPictureActivity::class.java)
            intent.putExtra("max_selected_size", max_selected_size)
            if (getFragment() == null) {
                getActivity().startActivityForResult(intent, REQUEST_CODE)
            } else {
                getFragment()!!.startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    private fun getActivity(): Activity {
        if (mContext == null) {
            throw NullPointerException("you should take function from first")
        }
        return mContext!!.get()!!
    }

    private fun getFragment(): Fragment? {
        if (mFragment == null) {
            return null
        }
        return mFragment!!.get()
    }
}