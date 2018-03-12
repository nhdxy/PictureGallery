package com.anhdxy.picturegallery.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.anhdxy.picturegallery.R
import com.anhdxy.picturegallery.adapter.ChildPicturesAdapter
import com.anhdxy.picturegallery.adapter.ParentDirecAdapter
import com.anhdxy.picturegallery.bean.ParentDirecBean
import com.anhdxy.picturegallery.util.PictureUtils
import com.anhdxy.picturegallery.util.toast
import kotlinx.android.synthetic.main.app_activity_single_select_picture.*

/**
 * 选择单张图片
 * Created by Andrnhd on 2018/3/12.
 */
const val REQUEST_CODE = 0x102
const val RESULT_DATA = "filePaths"

class SingleSelectPictureActivity : AppCompatActivity() {
    private lateinit var imagesMap: HashMap<String, ArrayList<String>>
    private lateinit var parentAdapter: ParentDirecAdapter
    private lateinit var parentDatas: ArrayList<ParentDirecBean>
    private var currentPath: String?=null

    companion object {
        /**
         * 跳转选择图片界面（请求码为0x102）
         * 返回类型为ArrayList<String>
         */
        fun openActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, SingleSelectPictureActivity::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_single_select_picture)
        PictureUtils.getInstance(this).getImages().subscribe {
            if (it.isEmpty()) {
                recycler_view.visibility = View.GONE
                view_stub.inflate()
            }
            imagesMap = it
            initParentAdapter()
            initListener()
        }
    }

    private fun initParentAdapter() {
        tv_back.text = "选择图片"
        parentDatas = arrayListOf()
        imagesMap.keys.forEach {
            parentDatas.add(ParentDirecBean(imagesMap[it]!![0], it, imagesMap[it]!!.size))
        }
        parentAdapter = ParentDirecAdapter(this, parentDatas)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@SingleSelectPictureActivity)
            setHasFixedSize(true)
            adapter = parentAdapter
        }
        parentAdapter.setOnItemClickListener { showChildPictures(parentDatas[it].parentName) }
    }

    private fun showChildPictures(key: String) {
        tv_back.text = key
        val adapter = ChildPicturesAdapter(this, imagesMap[key]!!, true)
        recycler_view.layoutManager = GridLayoutManager(this, 3)
        recycler_view.adapter = adapter
        adapter.setOnItemClickListener {
            currentPath = imagesMap[key]!![it]
            PictureUtils.getInstance(this).startPhotoZoom(currentPath!!)
        }
    }

    private fun initListener() {
        float_action_btn_add.setOnClickListener {
            PictureUtils.getInstance(this).takeCamera()
        }
        tv_back.setOnClickListener { back() }
    }

    private fun back() {
        val adapter = recycler_view.adapter
        if (adapter is ParentDirecAdapter) {
            toast("您取消了选择图片")
            finish()
        } else if (adapter is ChildPicturesAdapter) {
            initParentAdapter()
        }
    }

    override fun onBackPressed() {
        back()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        PictureUtils.getInstance(this).apply {
            val onCameraResult = onCameraResult(requestCode, resultCode)
            if (onCameraResult.isNotEmpty()) {
                currentPath = onCameraResult
                startPhotoZoom(onCameraResult)
            }
            if (currentPath != null) {
                val onClipResult = onClipResult(requestCode, resultCode, currentPath!!)
                if (onClipResult.isNotEmpty()) {
                    val intent = Intent()
                    intent.putExtra(RESULT_DATA, arrayListOf(onClipResult))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}