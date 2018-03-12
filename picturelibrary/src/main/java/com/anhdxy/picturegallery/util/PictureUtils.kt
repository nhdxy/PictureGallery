package com.anhdxy.picturegallery.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.anhdxy.picturegallery.activity.REQUEST_CODE
import com.anhdxy.picturegallery.activity.RESULT_DATA
import com.tbruyelle.rxpermissions.RxPermissions
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 * Created by Andrnhd on 2018/3/9.
 */
class PictureUtils private constructor(context: Activity) {
    private lateinit var directoryMap: HashMap<String, ArrayList<String>>
    private val EXTERNAL_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val IMAGE_SELECTION = MediaStore.Images.Media.MIME_TYPE + " =? or " + MediaStore.Images.Media.MIME_TYPE + " =?"
    private val IMAGE_SELECTION_ARGS = arrayOf("image/jpeg", "image/png")
    private var mContext: Activity = context

    companion object {
        fun getInstance(context: Activity): PictureUtils {
            return PictureUtils(context)
        }
    }

    fun getImages(): Observable<HashMap<String, ArrayList<String>>> {
        return RxPermissions
                .getInstance(mContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .map<HashMap<String, ArrayList<String>>> {
                    if (it) {
                        directoryMap = HashMap()
                        val resolver = mContext.contentResolver
                        val cursor = resolver.query(EXTERNAL_IMAGE_URI, null, IMAGE_SELECTION, IMAGE_SELECTION_ARGS, MediaStore.Images.Media.DATE_MODIFIED + " desc")
                                ?: return@map hashMapOf<String, ArrayList<String>>()
                        while (cursor.moveToNext()) {
                            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                            val parentFileName = File(path).parentFile.name
                            if (!directoryMap.containsKey(parentFileName)) {
                                directoryMap[parentFileName] = arrayListOf(path)
                            } else {
                                var list = directoryMap[parentFileName]
                                if (list == null) {
                                    list = arrayListOf(path)
                                } else {
                                    list.add(path)
                                }
                                directoryMap[parentFileName] = list
                            }
                        }
                        cursor.close()
                        directoryMap

                    } else {
                        mContext.toast("您取消了权限，相册相机功能无法使用")
                        return@map hashMapOf<String, ArrayList<String>>()
                    }
                }

    }

    fun takeCamera() {
        val image = File(mAppImagesDir(), "cameraPicture.jpg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(image))
        mContext.startActivityForResult(intent, 0x001)
    }

    fun onCameraResult(requestCode: Int, resultCode: Int): String {
        if (requestCode == 0x001 && resultCode == -1) {
            val decodeSampleBitmapFromFile = decodeSampleBitmapFromFile(File(mAppImagesDir(), "cameraPicture.jpg").absolutePath)
            val bitmapDegree = getBitmapDegree(File(mAppImagesDir(), "cameraPicture.jpg").absolutePath)
            val bitmapByDegree = rotateBitmapByDegree(decodeSampleBitmapFromFile, bitmapDegree)
            val newFile = File(mAppImagesDir(), "${System.currentTimeMillis()}.jpg")
            val fos = FileOutputStream(newFile)
            bitmapByDegree.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, getUri(newFile))
            mContext.sendBroadcast(localIntent)
            return newFile.absolutePath
        } else if (requestCode == 0x001 && resultCode == 0) {
            mContext.toast("您取消了拍照")
            return ""
        }
        return ""
    }

    private fun mAppImagesDir(): File {
        val path = "${Environment.getExternalStorageDirectory()}/${mContext.packageName}/Images"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    private fun getUri(image: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(mContext, "com.anhdxy.picturegallery.fileprovider", image)
        } else {
            return Uri.fromFile(image)
        }
    }

    private fun decodeSampleBitmapFromFile(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = 2
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun getBitmapDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    private fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        if (degree == 0) {
            return bm
        }
        var returnBm: Bitmap? = null
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    /**
     * 获取返回结果
     */
    fun obtainResults(requestCode: Int, resultCode: Int, data: Intent?): ArrayList<String> {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            return data.getStringArrayListExtra(RESULT_DATA)
        }
        return arrayListOf()
    }

    /*private fun decodeSampleBitmapFromFile(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        val srcWidth = options.outWidth
        val srcHeight = options.outHeight
        if (srcWidth > reqWidth || srcHeight > reqHeight) {
            val widthRatio = Math.round(srcWidth.toFloat() / reqWidth.toFloat())
            val heightRatio = Math.round(srcHeight.toFloat() / reqHeight.toFloat())
            sampleSize = if (widthRatio > heightRatio) widthRatio else heightRatio
        }
        return sampleSize
    }*/

    fun startPhotoZoom(path: String) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(getUri(File(path)), "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 600)
        intent.putExtra("outputY", 600)
        //这边必须使用的Uri.fromFile而不是FileProvider
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCropPath()))
        intent.putExtra("return-data", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)//取消人脸识别功能
        mContext.startActivityForResult(intent, 0x020)
    }

    fun onClipResult(requestCode: Int, resultCode: Int): String {
        if (requestCode == 0x020 && resultCode == -1) {
            return getCropPath().absolutePath
        } else if (requestCode == 0x020 && resultCode == 0) {
            mContext.toast("您取消了裁剪")
            return ""
        } else {
            return ""
        }
    }

    private fun getCropPath(): File {
        return File(mAppImagesDir(), "small.jpg")
    }
}