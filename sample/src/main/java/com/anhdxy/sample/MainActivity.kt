package com.anhdxy.sample

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.anhdxy.picturegallery.activity.MultiSelectPictureActivity
import com.anhdxy.picturegallery.activity.SingleSelectPictureActivity
import com.anhdxy.picturegallery.util.Anhdxy
import com.anhdxy.picturegallery.util.PictureUtils
import com.anhdxy.picturegallery.util.SelectType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_open_single.setOnClickListener {
            Anhdxy
                    .from(this)
                    .setCrop(false)
                    .setSelectType(SelectType.SINGLE)
                    .choose()
        }
        btn_open_multi.setOnClickListener {
            Anhdxy
                    .from(this)
                    .setMaxSelectedSize(9)
                    .setSelectType(SelectType.MULTI)
                    .choose()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val obtainResults = Anhdxy.obtainResult(requestCode, resultCode, data)
        if (obtainResults.isNotEmpty()) {
            iv_picture.setImageBitmap(BitmapFactory.decodeFile(obtainResults[0]))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
