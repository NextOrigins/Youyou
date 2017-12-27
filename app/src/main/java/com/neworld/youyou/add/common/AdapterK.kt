package com.neworld.youyou.add.common


/**
 * @author by user on 2017/12/25.
 */
class
AdapterK<T>(bind: (Holder, MutableList<T>, Int) -> Unit, id: Int, list: ArrayList<T>)
	: Adapter<T>(list) {
	
	init {
		val obs = object : AdapterObs<T> {
			override fun onBind(holder: Holder, bean: MutableList<T>, position: Int) =
					bind.invoke(holder, bean, position)
			
			override fun layoutId() = id
		}
		super.setObs(obs)
	}
	
}