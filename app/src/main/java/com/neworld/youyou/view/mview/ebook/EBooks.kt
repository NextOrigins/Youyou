package com.neworld.youyou.view.mview.ebook

import android.content.Intent
import android.graphics.Point
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.SpacesItemDecoration
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.fragment_my.*
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

/**
 * @author by user on 2017/12/18.
 */
class EBooks : Fragment() {

    private val map = hashMapOf<CharSequence, CharSequence>()
	private val list = arrayListOf<ResponseBean.EBookMenu>()
	private val userId by preference("userId", "")
	private val token by preference("token", "")
	private val spacing = 20F
	private var b = true
	
	private val x by lazy {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)
        (point.x - resources.getDimension(R.dimen.dp10) - spacing * 2) / 3 - resources.getDimension(R.dimen.dp10)
    }
    private val y by lazy { x * 1.420 }

    private var recycler: RecyclerView by notNullSingleValue()
    private var swipe: SwipeRefreshLayout by notNullSingleValue()
	private var group: ConstraintLayout by notNullSingleValue()
    private var mAdapter: Adapter<ResponseBean.EBookMenu> by notNullSingleValue()
	private var snackBar: Snackbar by Delegates.notNull()
    private var createDate = ""
	
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                if (!recycler.canScrollVertically(1)) {
	                if (list.isNotEmpty()) {
		                createDate = list.last().createDate
		                refreshControl = 2
		                initData()
	                } else {
		               refreshControl = 1
	                }
                }
        }
    }
	
	private var refreshControl by Delegates.observable(0) {
		_, _, new ->
		when (new) {
			0 -> {
				b = true
				createDate = ""
			}
			1 -> {
				if (b) {
					if (snackBar.isShown) {
						snackBar.setText("没有数据了")
						Handler().postDelayed(snackBar::dismiss, 700)
					}
					else
						showSnackBar(group, "没有数据了_", 700)
				}
				b = false
			}
			2 -> if (b)
				snackBar = showSnackBar(group, "正在加载更多...", 1000 * 10)
		}
	}
	
	private val obs = object : Adapter.AdapterObs<ResponseBean.EBookMenu> {
		
		override fun onBind(holder: Adapter.Holder, bean: MutableList<ResponseBean.EBookMenu>, position: Int) {
			val image = holder.find<ImageView>(R.id.item_icon)
			
			image.let {
				it.layoutParams = it.layoutParams.apply {
					width = x.toInt()
					height = y.toInt()
				}
				it.setOnClickListener {
					val intent = Intent(context, EBookDetail::class.java)
					intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					intent.putExtra("bookId", bean[position].bookId.toString())
					startActivity(intent)
				}
				Glide.with(it).load(bean[position].iconImg).into(it)
			}
		}
		
		override fun layoutId() = R.layout.item_ebook
	}

    override fun getContentLayoutId() = R.layout.activity_ebook

    override fun initWidget(root: View) = with(root) {
        recycler = findViewById(R.id._recycler)
        swipe = findViewById(R.id._swipe)
	    group = findViewById(R.id._parent)

        recycler.layoutManager = GridLayoutManager(context, 3,
                GridLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(SpacesItemDecoration(3,
                spacing, false))
        mAdapter = Adapter(obs, list)
        recycler.adapter = mAdapter
	    recycler.addOnScrollListener(scrollListener)

        swipe.setOnRefreshListener {
	        refreshControl = 0
            initData()
        }
    }

    override fun initData() = with(map) {
	    clear()
        put("userId", userId)
        put("token", token)
        put("type", "0")
        put("CreateDate", createDate)
        NetBuild.response(this@EBooks::listAddAll, ToastUtil::showToast,
                197, ResponseBean.EBookBody::class.java, this@with)
    }

    private fun listAddAll(it: ResponseBean.EBookBody) {
	    if (it.menuList.isEmpty()) {
		    refreshControl = 1
		    return
	    }
	    if (createDate == "")
            list.clear()
        list.addAll(it.menuList)
        mAdapter.notifyDataSetChanged()
	    swipe.isRefreshing = false
    }
}