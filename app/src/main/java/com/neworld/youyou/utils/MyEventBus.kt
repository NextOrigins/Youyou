package com.neworld.youyou.utils


// 注册
inline fun <reified T : Any> T.registerStation(msg: MyEventBus.Configuration, def: Int? = null) {
    MyEventBus.INSTANCE.registerObserver<T>(msg, def)
}

// 取消注册
inline fun <reified T : Any> T.unregisterStation(def: Int? = null) {
    MyEventBus.INSTANCE.unregisterObserver<T>(def)
}

/**
 * @author by hhhh on 2018/4/19.
 */

class MyEventBus private constructor() {

    val mHandle = hashMapOf<Int, Configuration>()

    companion object {
        val INSTANCE by lazy { MyEventBus() }
    }

    fun obtain(mark: Int, obs: (Any) -> Any?) = Configuration(mark, obs)

    /**
     * 发送Event
     */
    fun postEvent(mark: Int, obj: Any = ""): Array<Any?> {
        val p1: ArrayList<Any?> = arrayListOf()
        mHandle.entries.forEach {
            it.value.mRegisters.entries.forEach {
                if (mark == it.key) {
                    p1.add(it.value.invoke(obj))
                }
            }
        }

        return p1.toTypedArray()
    }

    inline fun <reified T : Any> registerObserver(msg: Configuration, def: Int?) {
        val key = def ?: T::class.java.hashCode()
        logE("register key = $key")
        mHandle[key] = msg
    }

    inline fun <reified T : Any> unregisterObserver(def: Int?) {
        val key = def ?: T::class.java.hashCode()
        logE("unregister key = $key")
        mHandle.remove(key)
    }

    class Configuration(mark: Int, obs: (Any) -> Any?) {

        var obs: ArrayList<((Any) -> Any?)?> = arrayListOf(obs)

        val mRegisters = hashMapOf(Pair(mark, obs))

        fun addBus(mark: Int, obs: (Any) -> Any?) {
            mRegisters[mark] = obs
        }
    }
}
