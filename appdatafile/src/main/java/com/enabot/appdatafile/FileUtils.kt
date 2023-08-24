package com.enabot.appdatafile

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

/**
 * @author liu tao
 * @date 2023/8/23 15:32
 * @description data storage
 * 1.从内部存储空间访问
 *   val file = File(context.filesDir, filename) // 访问和存储文件
 *   var files: Array<String> = context.fileList() // 查看文件列表
 *   context.getDir(dirName, Context.MODE_PRIVATE) // 创建嵌套目录
 *   创建缓存文件
 *   File.createTempFile(filename, null, context.cacheDir)
 *   val cacheFile = File(context.cacheDir, filename)
 *   移除缓存文件
 *   cacheFile.delete()
 *   context.deleteFile(cacheFileName)
 * 2.从外部存储空间访问
 *   访问持久性文件
 *   val appSpecificExternalDir = File(context.getExternalFilesDir(null), filename)
 *   创建缓存文件
 *   val externalCacheFile = File(context.externalCacheDir, filename)
 *   删除缓存文件
 *   externalCacheFile.delete()
 *   媒体文件
 *   @link [getAppSpecificAlbumStorageDir]
 *
 */
object FileUtils {
    private const val TAG = "FileUtils"

    // At the top level of your kotlin file:
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    /**
     * Checks if a volume containing external storage is available for read and write.
     */
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Checks if a volume containing external storage is available to at least read.
     */
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    /**
     * 媒体内容
     */
    fun getAppSpecificAlbumStorageDir(context: Context, albumName: String): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        return file
    }

    // 1.从内部存储读取/写入一个文件 /数据缓存
    // 2.从外部存储读取/写入一个文件 /数据缓存
    // 3.读取项目assets里的文件
    // 4.键值对存储 SharedPreferences ，DataStore ，mmkv
    // 5.room 数据库持久化存储

    /**
     * 设置偏好
     */
    fun Context.setPreferences(key: String, value: Any) {
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("value`s type not supported!")
            }
            apply()
        }
    }

    /**
     * 获取SharedPreferences
     */
    fun Context.sharedPreferences(): SharedPreferences {
        return getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    //Preferences DataStore 和 Proto DataStore
    //Preferences DataStore 使用键存储和访问数据。此实现不需要预定义的架构，也不确保类型安全。
    //Proto DataStore 将数据作为自定义数据类型的实例进行存储。此实现要求您使用协议缓冲区来定义架构，但可以确保类型安全。

    /**
     * 获取偏好数据
     */
    fun Context.getDataStore(key: String): Flow<Int> {
        val intKey = intPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[intKey] ?: 0
        }
    }

    /**
     * 设置偏好数据
     */
    suspend fun Context.incrementCounter(key: String) {
        val intKey = intPreferencesKey(key)
        dataStore.edit { settings ->
            val currentCounterValue = settings[intKey] ?: 0
            settings[intKey] = currentCounterValue + 1
        }
    }
}