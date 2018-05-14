package com.neworld.youyou.utils


// 注册
fun <T : Any> T.registerStation(msg: MyEventBus.Configuration) {
    MyEventBus.INSTANCE.registerObserver(msg, this.hashCode())
}

// 取消注册
fun <T : Any> T.unregisterStation() {
    MyEventBus.INSTANCE.unregisterObserver(this.hashCode())
}

/**
 * @author by hhhh on 2018/4/19.
 */

class MyEventBus private constructor() {

    private val mHandle = hashMapOf<Int, Configuration>()

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

    fun registerObserver(msg: Configuration, key: Int) {
        mHandle[key] = msg
    }

    fun unregisterObserver(key: Int) {
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
