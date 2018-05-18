package com.neworld.youyou.utils

import android.text.TextUtils
import android.util.Base64

import com.google.gson.Gson
import com.neworld.youyou.manager.NetManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author by user on 2017/11/13.
 */

object NetBuild {

    /**
     * @param value     post值
     * @param url       url尾数
     * @param beanClass bean类
     * @param <T>       类型
     * @return 解析后的Bean
    </T> */
    fun <T> response(value: String, url: Int, beanClass: Class<T>, obs: ResponseObs<T>) {
        Thread {
            val content = getResponse(value, url)
            if (!TextUtils.isEmpty(content)) {
                val t = GsonUtil.parseJsonToBean(content, beanClass)
                Util.uiThread { obs.onSuccess(t) }

            } else
                Util.uiThread { obs.onFailed() }
        }.start()
    }

    // 接收map
    @JvmStatic
    fun <T> response(obs: ResponseObs<T>, url: Int, beanClass: Class<T>, map: Map<out CharSequence, CharSequence>) {
        doAsync {
            val content = getResponse(map, url)
            if (!TextUtils.isEmpty(content)) {
                val t = GsonUtil.parseJsonToBean(content, beanClass)
                uiThread {
                    if (t == null) {
                        obs.onFailed("数据错误, 请到用户反馈处反馈此问题. 谢谢")

                    } else obs.onSuccess(t)
                }
            } else
                uiThread { obs.onFailed() }
        }
    }

    // 接收map
    @JvmStatic
    fun <T> response(success: (T) -> Unit, failed: (String) -> Unit,
                     url: Int, beanClass: Class<T>, map: Map<out CharSequence, CharSequence>)
		    = response(success, failed, url.toString(), beanClass, map)

    fun <T> response(success: (T) -> Unit, failed: (String) -> Unit,
                     url: String, beanClass: Class<T>, body: String) {
        doAsync {
            val content = getResponse(body, url)
            if (!TextUtils.isEmpty(content)) {
                val t = GsonUtil.parseJsonToBean(content, beanClass)
                uiThread {
                    if (t == null) {
                        failed.invoke("出错了! 请到用户反馈处反馈此问题! ")
                    } else {
                        success.invoke(t)
                    }
                }
            } else {
                uiThread { failed.invoke("未知错误! 错误代码[001]请到用户反馈处反馈") }
            }
        }
    }
    
	@JvmStatic
    fun <T> response(success: (T) -> Unit, failed: (String) -> Unit,
                     url: String, beanClass: Class<T>, map: Map<out CharSequence, CharSequence>) {
	    doAsync {
		    val content = getResponse(map, url)
		    if (!TextUtils.isEmpty(content)) {
			    val t = GsonUtil.parseJsonToBean(content, beanClass)
			    uiThread {
				    if (t == null) {
					    failed("数据错误, 请到用户反馈处反馈此问题. 谢谢")
					
				    } else success(t)
			    }
			
		    } else uiThread { failed("网络错误, 请稍后重试") }
	    }
    }

	// 解析Json格式 <{"userId":"userID"}>
    @JvmStatic
    fun getResponse(body: String, url: Int): String? = getResponse(body, url.toString())

    fun getResponse(body: String, url: String): String? {
        val base64 = Base64.encodeToString(body.toByteArray(), Base64.DEFAULT)
        val replace = base64.replace("\n", "")
        return NetManager.getInstance().getContent(replace, url)
    }

    // 解析map
    @JvmStatic
    fun getResponse(map: Map<out CharSequence, CharSequence>, url: Int): String {
<<<<<<< HEAD
        return getResponse(map, url.toString())
=======
        val json = Gson().toJson(map)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)
        val replace = base64.replace("\n", "")
        return NetManager.getInstance().getContent(replace, url.toString())
>>>>>>> parent of 8d52dad... 17_12_19
    }

    // 解析map
    @JvmStatic
    fun getResponse(map: Map<out CharSequence, CharSequence>, url: String): String {
        val json = Gson().toJson(map)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.DEFAULT)
        val replace = base64.replace("\n", "")
        val content = NetManager.getInstance().getContent(replace, url)
        return if (!TextUtils.isEmpty(content)) content else "null"
    }

    interface ResponseObs<in T> {
        fun onSuccess(t: T)

        fun onFailed(error: String = "网络错误, 请稍后重试")
    }
}
