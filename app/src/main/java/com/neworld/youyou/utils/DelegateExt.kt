package com.neworld.youyou.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.preference.Preference
import com.neworld.youyou.dialog.DialogUtils
import com.neworld.youyou.manager.MyApplication
import com.umeng.socialize.utils.DeviceConfig.context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author by user on 2017/12/5.
 */

/**
 * SharedPreference 自动存取
 */
@JvmOverloads
fun <T : Any> preference(name: String, default: T, context: Context = MyApplication.sContext):
        ReadWriteProperty<Any?, T> = Preference(context, name, default)

/**
 * NotNull初始化, 只能赋值1次
 */
fun <T> notNullSingleValue():
        ReadWriteProperty<Any?, T> = NotNullInitialized()

/**
 * dialog
 */
fun displayDialog(context: Context, content: String, enter: () -> Unit = { },
                  positive: String = "确定", cancel: () -> Unit = { }, negative: String = "取消") {
    DialogUtils.showDialog(context, content, positive, negative,
            object : DialogUtils.onDiaLogBtnListener {
                override fun onNegativeListener(dialog: DialogInterface?) {
                    cancel.invoke()
                    dialog?.dismiss()
                }

                override fun onPositiveListener(dialog: DialogInterface?) {
                    enter.invoke()
                    dialog?.dismiss()
                }
            })
}

/**
 * toast
 */
fun showToast(text: String) {
    ToastUtil.showToast(text)
}

/**
 * Log.e
 */
fun logE(text: String) {
    LogUtils.E(text)
}

/**
 * Net
 */
inline fun <reified T> response(noinline s: (T) -> Unit, url: Any, map: Map<CharSequence, CharSequence>,
                                noinline f: (String) -> Unit = ::showToast) {
    val clazz = T::class.java
    if (url is Int || url is String) {
        NetBuild.response(s, f, url.toString(), clazz, map)
    } else {
        throw IllegalArgumentException("This type is not be request")
    }
}

/**
 * Thread
 */
fun uiThread(action: () -> Unit) {
    Util.uiThread(action)
}

private class NotNullInitialized<T> : ReadWriteProperty<Any?, T> {
    private var value: T? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("not initialized")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = if (this.value == null) value
        else throw IllegalStateException("already initialized")
    }
}

private class Preference<T>(val context: Context, val name: String, val default: T)
    : ReadWriteProperty<Any?, T> {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <T> findPreference(name: String, def: T): T = with(prefs) {
        val res: Any = when (def) {
            is String -> getString(name, def)
            is Int -> getInt(name, def)
            is Boolean -> getBoolean(name, def)
            is Float -> getFloat(name, def)
            is Long -> getLong(name, def)
            else -> throw IllegalArgumentException("This type can not be find into Preference")
        }
        res as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun <V> putPreference(name: String, value: V) = with(prefs.edit()) {
        when (value) {
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            is Long -> putLong(name, value)
            else -> throw IllegalArgumentException("This type can not be saved into Preference")
        }.apply()
    }
}