package com.anhdxy.picturegallery.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.anhdxy.picturegallery.R
import com.anhdxy.picturegallery.adapter.ChildPicturesAdapter
import com.anhdxy.picturegallery.adapter.ParentDirecAdapter
import com.anhdxy.picturegallery.adapter.SelectedPictureAdapter
import com.anhdxy.picturegallery.bean.ParentDirecBean
import com.anhdxy.picturegallery.util.PictureUtils
import com.anhdxy.picturegallery.util.RecycleItemTouchHelper
import com.anhdxy.picturegallery.util.toast
import kotlinx.android.synthetic.main.app_activity_multi_select_picture.*

/**
 * 选择多张图片
 * Created by Andrnhd on 2018/3/9.
 */
class MultiSelectPictureActivity : AppCompatActivity() {
    private lateinit var imagesMap: HashMap<String, ArrayList<String>>
    private lateinit var parentAdapter: ParentDirecAdapter
    private lateinit var parentDatas: ArrayList<ParentDirecBean>
    private lateinit var selectedPictureDatas: ArrayList<String>
    private lateinit var selectedPictureAdapter: SelectedPictureAdapter

    private var MAX_SELECTED_SIZE = 9

    companion object {
        /**
         * 跳转选择图片界面（请求码为0x102）
         * 返回类型为ArrayList<String>
         * @param max_selected_size 最大可选择数量，默认为9
         */
        fun openActivity(activity: AppCompatActivity, max_selected_size: Int = 9) {
            val intent = Intent(activity, MultiSelectPictureActivity::class.java)
            intent.putExtra("max_selected_size", max_selected_size)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_multi_select_picture)
        if (intent != null) {
            MAX_SELECTED_SIZE = intent.getIntExtra("max_selected_size", 9)
        }
        PictureUtils.getInstance(this).getImages().subscribe {
            if (it.isEmpty()) {
                recycler_view.visibility = View.GONE
                view_stub.inflate()
            }
            imagesMap = it
            initParentAdapter()
            initSelectedPictureAdapter()
            initListener()
        }
    }

    private fun initSelectedPictureAdapter() {
        selectedPictureDatas = arrayListOf()
        selectedPictureAdapter = SelectedPictureAdapter(this, selectedPictureDatas)
        recycler_view_selected.apply {
            layoutManager = LinearLayoutManager(this@MultiSelectPictureActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = selectedPictureAdapter
        }
        selectedPictureAdapter.setOnItemClickListener { ShowPicturesActivity.openActivity(this, selectedPictureDatas, it) }
        val callback = RecycleItemTouchHelper({
            selectedPictureDatas.removeAt(it)
            selectedPictureAdapter.refresh(selectedPictureDatas).notifyItemRemoved(it)
            selectedPictureAdapter.notifyItemRangeChanged(it, selectedPictureAdapter.itemCount)
            if (selectedPictureDatas.isEmpty()) {
                recycler_view_selected.visibility = View.GONE
                tv_finish.text = "完成"
            }
        })
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recycler_view_selected)
    }

    private fun initListener() {
        float_action_btn_add.setOnClickListener {
            if (MAX_SELECTED_SIZE == selectedPictureDatas.size) {
                toast("您最多只能选择${MAX_SELECTED_SIZE}张图片")
                return@setOnClickListener
            }
            PictureUtils.getInstance(this@MultiSelectPictureActivity).takeCamera()
        }
        tv_back.setOnClickListener { back() }
        tv_finish.setOnClickListener {
            if (selectedPictureDatas.isEmpty()) {
                toast("请选择您需要的图片")
            } else {
                val data = Intent()
                data.putExtra(RESULT_DATA, selectedPictureDatas)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
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
            layoutManager = LinearLayoutManager(this@MultiSelectPictureActivity)
            setHasFixedSize(true)
            adapter = parentAdapter
        }
        parentAdapter.setOnItemClickListener { showChildPictures(parentDatas[it].parentName) }
    }

    private fun showChildPictures(key: String) {
        tv_back.text = key
        val adapter = ChildPicturesAdapter(this, imagesMap[key]!!)
        recycler_view.layoutManager = GridLayoutManager(this, 3)
        recycler_view.adapter = adapter
        adapter.setOnItemAddListener { addSelectedPictureToList(imagesMap[key]!![it]) }
        adapter.setOnItemClickListener { ShowPicturesActivity.openActivity(this@MultiSelectPictureActivity, imagesMap[key]!!, it) }
    }

    private fun addSelectedPictureToList(path: String) {
        if (recycler_view_selected.visibility == View.GONE) {
            recycler_view_selected.visibility = View.VISIBLE
        }
        if (MAX_SELECTED_SIZE == selectedPictureDatas.size) {
            toast("您最多只能选择${MAX_SELECTED_SIZE}张图片")
            return
        }
        if (selectedPictureDatas.contains(path)) {
            toast("请不要添加重复的图片")
            return
        }
        selectedPictureDatas.add(0, path)
        selectedPictureAdapter.refresh(selectedPictureDatas)
        selectedPictureAdapter.notifyItemInserted(0)
        selectedPictureAdapter.notifyItemRangeChanged(0, selectedPictureAdapter.itemCount)
        recycler_view_selected.scrollToPosition(0)
        tv_finish.text = "完成(已选择 ${selectedPictureDatas.size} 张)"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val onCameraResult = PictureUtils.getInstance(this).onCameraResult(requestCode, resultCode)
        if (onCameraResult.isNotEmpty()) {
            addSelectedPictureToList(onCameraResult)
        }
        super.onActivityResult(requestCode, resultCode, data)
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
}