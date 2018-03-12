package com.anhdxy.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.anhdxy.picturegallery.activity.MultiSelectPictureActivity
import com.anhdxy.picturegallery.activity.SingleSelectPictureActivity
import com.anhdxy.picturegallery.util.PictureUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_open_single.setOnClickListener {
            SingleSelectPictureActivity.openActivity(this)
        }
        btn_open_multi.setOnClickListener {
            MultiSelectPictureActivity.openActivity(this, 6)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val obtainResults = PictureUtils.getInstance(this).obtainResults(requestCode, resultCode, data)
        if (obtainResults.isNotEmpty()) {
            iv_picture.setImageBitmap(BitmapFactory.decodeFile(obtainResults[0]))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
