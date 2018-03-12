package com.anhdxy.picturegallery.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.anhdxy.picturegallery.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.app_activity_show_pictures.*

/**
 * 显示大图
 * Created by Andrnhd on 2018/3/10.
 */
class ShowPicturesActivity : AppCompatActivity() {
    private lateinit var paths: ArrayList<String>
    private var position = 0
    private lateinit var imageViews: ArrayList<ImageView>

    companion object {
        /**
         * 跳转显示大图界面
         *
         * @param paths 图片路径集合
         * @param position 当前显示图片的索引
         */
        fun openActivity(activity: AppCompatActivity, paths: ArrayList<String>, position: Int) {
            val intent = Intent(activity, ShowPicturesActivity::class.java)
            intent.putExtra("paths", paths)
            intent.putExtra("position", position)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_show_pictures)
        if (intent != null) {
            paths = intent.getStringArrayListExtra("paths")
            position = intent.getIntExtra("position", 0)
            tv_back.text = "${position + 1} / ${paths.size}"
            imageViews = arrayListOf()
            for (i in 0 until paths.size) {
                val imageView = PhotoView(this@ShowPicturesActivity)
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                if (i == position) {
                    Glide.with(this@ShowPicturesActivity).load(paths[i]).apply(RequestOptions().fitCenter()).into(imageView)
                }
                imageViews.add(imageView)
            }
            view_page.adapter = object : PagerAdapter() {
                override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
                override fun getCount(): Int = imageViews.size
                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    container.addView(imageViews[position])
                    return imageViews[position]
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(imageViews[position])
                }
            }
            view_page.currentItem = position
            view_page.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    Glide.with(this@ShowPicturesActivity).load(paths[position]).apply(RequestOptions().fitCenter()).into(imageViews[position])
                    tv_back.text = "${position + 1} / ${paths.size}"
                }
            })
            tv_back.setOnClickListener { finish() }
        }
    }
}