package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.BigPicActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/2.
 */
class QAFragment : Fragment() {

    // view
	private var mRecycle by notNullSingleValue<RecyclerView>()
	private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
	private var mFootText by notNullSingleValue<TextView>()
	private var mFootPrg by notNullSingleValue<ProgressBar>()
	private var mToolBar by notNullSingleValue<Toolbar>()

	private var admMenu: MenuItem? = null

    // fields
	private var userId by preference("userId", "")
	private var token by preference("token", "")
	private var cacheJson by preference("cacheJson", "") // 缓存以Json格式存储本地 , 也可以存储到服务器
	private val role by preference("role", 1) // role = 2 为管理员

	private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue() // RecyclerView适配器 (HeaderView FooterView)

    // property & cacheList
	private val map = hashMapOf<CharSequence, CharSequence>() // 网络请求map
	private val list = arrayListOf<ResponseBean.QADetail>() // 返回数据集合
    private val cacheList = arrayListOf<String>() // 缓存列表, 保存createDate
    private var savedList = arrayListOf<String>() // 避免cacheList读取冲突
	private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private var maxDate by
    Delegates.vetoable(Util.getDateFormatInstance()
            .format(Date(System.currentTimeMillis() + 1000))) { _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one > two
        }

        return@vetoable true
    }
    private var minDate by Delegates.vetoable("") { _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one < two
        }

        return@vetoable true
    }

    // 图片等宽
	private val imgWidth by lazy {
		val point = Point()
		activity.windowManager.defaultDisplay.getSize(point)
		(point.x - resources.getDimension(R.dimen.dp30)) / 3
	}

    // cache
    private var cacheIndex = 0 // 读取缓存下标

    // status
	private var b = true // 加载完成提示 只显示一次
	private var over = false // 是否读取完缓存列表
	private var openCache = true // 是否开启缓存
	private var isUpdate = false

	override fun getContentLayoutId() = R.layout.fragment_parents_q

	override fun initWidget(root: View) {
		mRecycle = root.findViewById(R.id._recycler)
		mSwipe = root.findViewById(R.id._swipe)
		mToolBar = root.findViewById(R.id._toolbar)

		root.findViewById<ConstraintLayout>(R.id._parent)
				.setOnClickListener { startActivity(Intent(context, QAParent::class.java)) }

		mRecycle.layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false)
		mRecycle.adapter = AdapterK(this::itemBind,
				R.layout.item_qa_1, list).also { mAdapter = it }

		setScrollChangedListener()

		mSwipe.setOnRefreshListener {
			downData()
		}

		mToolBar.title = ""

		layoutInflater.inflate(R.layout.footview_parents_qa, mRecycle, false).run {
			mFootText = findViewById(R.id.foot_loading)
			mFootPrg = findViewById(R.id.foot_progress)
			mAdapter.footView = this
		}
	}

	override fun initData() {
		if (cacheJson.isNotEmpty()) {
			val readCache = Gson()
					.fromJson<ReadCache>(cacheJson, object : TypeToken<ReadCache>() {}.type)
			maxDate = readCache.top
			minDate = readCache.end
			cacheList.addAll(readCache.menu)
			savedList.addAll(cacheList)
		}

        logE("cacheList = $cacheList")

		map["userId"] = userId
		map["token"] = token

		upData()
	}

	private fun downData() {
		if (isUpdate) {
			if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
			return
		}
		if (!mSwipe.isRefreshing) mSwipe.isRefreshing = true
		inRequest({
			val bean = it.menuList

            bean.forEach { // ForEach 循环过滤最大 & 最小 createDate
                maxDate = it.createDate
                minDate = it.createDate
            }

			if (bean.isEmpty()) {
				showSnackBar(mRecycle, "暂时没有新的话题_(:з」∠)_")
				b = false
				mFootText.text = "—我是有底线的—"
				mFootPrg.visibility = View.GONE

				if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
				return@inRequest
			}
			mAdapter.addDataToTop(ArrayList(bean))
			mAdapter.notifyDataSetChanged()

			if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
            b = true

            var k = ""
            bean.forEach { k = "$k|${it.id}" }
            savedList.add(0, k.trim('|'))
		}, 1)
	}

	private fun upData() {
        if (!b && cacheIndex >= cacheList.size) return

		mFootPrg.visibility = View.VISIBLE
		mFootText.text = "加载中"
		isUpdate = true

		inRequest({
			val bean = it.menuList

            bean.forEach { // ForEach 循环过滤最大 & 最小 createDate
                maxDate = it.createDate
                minDate = it.createDate
            }

			if (bean.isEmpty() && over) {
				if (b) {
					mFootText.text = "—我是有底线的—"
					mFootPrg.visibility = View.GONE
					b = false
				}
                isUpdate = false
				return@inRequest
			}
			mAdapter.addData(bean)
			mAdapter.notifyDataSetChanged()

			// 改变FooterView状态
			mFootText.text = "加载更多"
			mFootPrg.visibility = View.GONE
			isUpdate = false
            b = true

			// 缓存
			if (over) {
                var k = ""
                bean.forEach { k = "$k|${it.id}" }
                savedList.add(k.trim('|'))
			}

		}, 2)
	}

    private fun inRequest(s: (ResponseBean.QABody) -> Unit, type: Int) {
        when (type) {
            1 -> {
                map["createDate"] = maxDate ?: ""
                map["endDate"] = minDate
                map["type"] = type.toString()

                logE("request pull down : createDate = $maxDate, endDate = $minDate")
                response(s, 199, map)
            }
            2 -> {
                if (cacheList.isNotEmpty() && cacheIndex < cacheList.size) {
                    val id = if (cacheList[cacheIndex].split("\\|".toRegex()).size < 3
                            && cacheIndex + 1 < cacheList.size) {
                        "${cacheList[cacheIndex++]}|${cacheList[cacheIndex++]}"
                    } else {
                        cacheList[cacheIndex++]
                    }
                    map["id"] = id

                    logE("request pull up from history")
                    response(s, "199_1", map)
                } else {
                    over = true

                    map["createDate"] = maxDate ?: ""
                    map["endDate"] = minDate
                    map["type"] = type.toString()

                    logE("request pull up to old news : newsDate = $maxDate, endDate = $minDate")
                    response(s, 199, map)
                }
            }
        }

        if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
    }

	@SuppressLint("SetTextI18n")
	private fun itemBind(holder: Adapter.Holder,
						 mutableList: MutableList<ResponseBean.QADetail>, position: Int) {
		val parent = holder.find<ConstraintLayout>(R.id.item_parent)
		val title = holder.find<TextView>(R.id.item_title)
		val reply = holder.find<TextView>(R.id.item_reply)
		val img1 = holder.find<ImageView>(R.id.item_img1)
				.also { it.setWidth() }
		val img2 = holder.find<ImageView>(R.id.item_img2)
				.also { it.setWidth() }
		val img3 = holder.find<ImageView>(R.id.item_img3)
				.also { it.setWidth() }

		val data = mutableList[position]

		parent.setOnClickListener {
			startActivityForResult(Intent(context, QAParent::class.java)
                    .putExtra("position", position)
					.putExtra("taskId", data.id.toString())
					.putExtra("commentId", data.id.toString()), 20)
		}

		title.text = data.title
		reply.text = "${data.comment_count}回答"

		if (data.imgs == null) {
			img1.visibility = View.GONE
			return
		}

		img1.visibility = if (data.imgs.isEmpty()) View.GONE else View.VISIBLE

		if (data.imgs.isNotEmpty()) {
			val split = data.imgs.split("\\|".toRegex())
			when (split.size) {
				1 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					img1.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
					}
					img2.visibility = View.GONE
				}
				2 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					Glide.with(img2).load(split[1]).apply(options).into(img2)
					img1.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
					}
					img2.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img2, split[1])
					}
					img2.visibility = View.VISIBLE
					img3.visibility = View.GONE
				}
				3 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					Glide.with(img2).load(split[1]).apply(options).into(img2)
					Glide.with(img3).load(split[2]).apply(options).into(img3)
					img1.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
					}
					img2.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img2, split[1])
					}
					img3.setOnClickListener {
						BigPicActivity.launch(activity as AppCompatActivity, img3, split[2])
					}
					img2.visibility = View.VISIBLE
					img3.visibility = View.VISIBLE
				}
			}
		}
	}

	private fun setScrollChangedListener() {
		mRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (!mRecycle.canScrollVertically(1))
						upData()
				}
			}
		})
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		saveCache()
	}

	override fun onDestroy() {
		saveCache()
		super.onDestroy()
	}

	private fun saveCache() {
        if (!openCache) return

        val map = hashMapOf<String, Any>()
        map["end"] = minDate
        map["top"] = maxDate
        map["menu"] = savedList

        cacheJson = Gson().toJson(map)
	}

	private fun View.setWidth() {
        layoutParams = layoutParams.also { it.width = imgWidth.toInt() }
    }

	private data class ReadCache(val top: String = "",
	                             val end: String = "",
	                             val menu: Array<String> = arrayOf()) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as ReadCache

			if (top != other.top) return false
			if (end != other.end) return false
			if (!Arrays.equals(menu, other.menu)) return false

			return true
		}

		override fun hashCode(): Int {
			var result = top.hashCode()
			result = 31 * result + end.hashCode()
			result = 31 * result + Arrays.hashCode(menu)
			return result
		}
	}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 20) {
            data?.let {
                val er = it.getBooleanExtra("error", false)
                if (er) {
                    val position = it.getIntExtra("position", -1)
                    if (position != -1) {
                        val element = mAdapter.bean[position].id.toString()
                        mAdapter.remove(position)

                        val temp = arrayListOf<String>()

                        savedList.forEach {
                            val split = it.split('|')
                            var filter = ""
                            if (split.size > 1) {
                                split.filterNot { it == element }.forEach { filter = "$filter|$it" }
                            } else if (split.size == 1 && split[0] != element) {
                                filter = split[0]
                            }

                            if (filter.isNotEmpty())
                                temp.add(filter.trim('|'))
                        }

                        savedList.clear()
                        savedList.addAll(temp)
                        logE("filtered temp = $temp")
                    }
                }
            }
        }
    }

	override fun onResume() {
		super.onResume()
		if (role == 2) {
			setHasOptionsMenu(true)
			(activity as AppCompatActivity).setSupportActionBar(mToolBar)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		if (role == 2) {
			inflater?.inflate(R.menu.menu_role_item, menu)
			admMenu = menu?.findItem(R.id.menu_clear)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (role == 1) return false

		when (item?.itemId) {
			R.id.menu_clear -> {
				openCache = if (openCache) {
					admMenu?.title = "打开缓存"
					cacheJson = ""
					false
				} else {
					admMenu?.title = "清除缓存并关闭"
					true
				}
			}
		}
		return true
	}
}