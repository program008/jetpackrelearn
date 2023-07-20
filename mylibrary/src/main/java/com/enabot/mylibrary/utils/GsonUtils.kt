package com.enabot.mylibrary.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.internal.Primitives
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/04/05
 * desc  : utils about gson
 */
object GsonUtils {

    private val mGsonDefault: Gson by lazy {
        createGson()
    }

    val gson4LogUtils: Gson
        get() = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    private fun createGson(): Gson {
        return GsonBuilder().serializeNulls().disableHtmlEscaping().create()
    }

    /**
     * @param task task
     * json 序列化
     */
    fun toJson(task: Any): String {
        return mGsonDefault.toJson(task)
    }

    /**
     * @param task task
     * json 序列化String
     */
    fun toJson(task: JsonElement): String {
        return mGsonDefault.toJson(task)
    }

    /**
     * @param task task
     * json 序列化JsonElement
     */
    fun toJsonTree(task: Any): JsonElement {
        return mGsonDefault.toJsonTree(task)
    }

    /**
     * @param task JsonElement
     * @param type Class
     * json 解析
     */
    fun <T> fromJson(task: JsonElement, type: Class<T>): T {
        return mGsonDefault.fromJson(task, type)
    }

    /**
     * @param json String
     * @param type Class
     * json 解析
     */
    fun <T> fromJson(json: String, type: Class<T>): T {
        return mGsonDefault.fromJson(json, type)
    }

    /**
     * @param json String
     * @param type Type
     * json 解析
     */
    fun <T> fromJson(json: String, type: Type): T {
        return mGsonDefault.fromJson(json, type)
    }

    /**
     * @param jsonElement JsonElement
     * @param type Type
     * json 解析
     */
    fun <T> fromJson(jsonElement: JsonElement, type: Type): T {
        return mGsonDefault.fromJson(jsonElement, type)
    }

    /**
     * @param jsonElement JsonElement
     * @return T
     * json 解析 自动推导
     */
    inline fun <reified T> fromJsonByReturnType(jsonElement: JsonElement): T {
        val classOfT = T::class.java
        val type = object : TypeToken<T>() {}.type
        val result: Any = fromJson(jsonElement, type)
        return try {
            Primitives.wrap(classOfT).cast(result) as T
        } catch (e: Exception) {
            result as T
        }
    }

    /**
     * @param json String
     * @return T
     * json 解析 自动推导
     */
    inline fun <reified T> fromJsonByReturnType(json: String): T {
        val classOfT = T::class.java
        val type = object : TypeToken<T>() {}.type
        val result: Any = fromJson(json, type)
        return try {
            Primitives.wrap(classOfT).cast(result) as T
        } catch (e: Exception) {
            result as T
        }
    }

}