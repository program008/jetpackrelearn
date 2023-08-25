package com.enabot.myview

import android.app.AlertDialog
import android.content.ContentValues
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.toast
import com.enabot.myview.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date


class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var gestureLibrary: GestureLibrary
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initViews() {

        gestureLibrary = GestureLibraries.fromFile(filesDir)
        if (gestureLibrary.load()) {
            log("手势加载成功")
        } else {
            log("手势加载失败")
        }

        viewBinding.gesture.gestureColor = Color.GREEN
        viewBinding.gesture.gestureStrokeWidth = 5F
        viewBinding.gesture.addOnGesturePerformedListener { overlay, gesture ->
            if (viewBinding.checkbox.isChecked) {
                val saveDialog = layoutInflater.inflate(R.layout.dialog_save, null, false)
                val img_show = saveDialog.findViewById<ImageView>(R.id.img_show)
                val edit_name = saveDialog.findViewById<EditText>(R.id.edit_name)
                val bitmap = gesture.toBitmap(128, 128, 10, 0xffff0000.toInt())
                img_show.setImageBitmap(bitmap)
                //saveBitmap(bitmap,this@MainActivity.filesDir.path)
                saveImage29(bitmap)
                AlertDialog.Builder(this).setView(saveDialog)
                    .setPositiveButton(
                        "保存"
                    ) { dialog, which ->
                        gestureLibrary.addGesture(edit_name.text.toString(), gesture)
                        gestureLibrary.save()
                    }
                    .setNegativeButton("取消", null).show()
            } else {
                val predictions = gestureLibrary.recognize(gesture)
                val result = mutableListOf<String>()
                predictions.forEach {
                    if (it.score > 2.0) {
                        result.add("与手势【${it.name}】相似度${it.score}")
                    }
                }
                if (result.size > 0) {
                    val adapter = ArrayAdapter<Any>(
                        this@MainActivity,
                        android.R.layout.simple_dropdown_item_1line, result.toTypedArray()
                    )
                    AlertDialog.Builder(this@MainActivity).setAdapter(adapter, null)
                        .setPositiveButton("确定", null).show()
                } else {
                    toast("无法找到匹配的手势")
                }
            }
        }
    }

    fun saveBitmap(bitmap: Bitmap,path:String){
        val savePath:String
        val filePic:File
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            savePath = path
        }else{
            log("saveBitmap failure : sdcard not mounted")
            return
        }
        try {
            filePic = File(savePath)
            if (!filePic.exists()){
                filePic.parentFile?.mkdirs()
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
            fos.flush()
            fos.close()
        }catch (e:IOException){
            log("saveBitmap:${e.message}")
            return
        }
        log("saveBitmap success:${filePic.absolutePath}")
    }
    /**
     * [MediaStore.Images.Media.insertImage deprecated](https://stackoverflow.com/questions/57726896/mediastore-images-media-insertimage-deprecated/76975953#76975953)
     */
    fun saveImage(bitmap: Bitmap){
        var insertImage = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "手势${Date().time}",
            "手势识别"
        )

        if (!insertImage.isEmpty()){
            toast("图片保存成功")
            log("图片保存路径$insertImage")
        }
    }


    fun saveImage29(bitmap: Bitmap){
        val insertUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
        try {
            val outputStream = insertUri?.let { contentResolver.openOutputStream(it, "rw") }
            if (bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream)){
                toast("保存成功")
            }else{
                toast("保存失败")
            }
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }

    }
}