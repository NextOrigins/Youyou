package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_parent_qa.*
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/4.
 */
class ParentsQA : Activity() {
	
	private val userId by preference("userId", "")
	private val map = hashMapOf<CharSequence, CharSequence>()
	
	// 数据
	private val list = arrayListOf<ResponseBean.AnswerList>()
	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	
	private var b = true
	private var measured = false
	
	private var headTitle by notNullSingleValue<TextView>()
	private var headIcon by notNullSingleValue<ImageView>()
	
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()
	
	private var taskId by Delegates.notNull<String>()
	
	override fun getContentLayoutId() = R.layout.activity_parent_qa
	
	override fun initWindows() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
		}
	}
	
	override fun initWidget() {
		_recycle.run {
			layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@ParentsQA::bind, R.layout.item_answer, list).also { mAdapter = it }
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
		}
		
		_toolbar.title = ""
		setSupportActionBar(_toolbar)
		
		_star.setOnClickListener {
			hashMapOf<CharSequence, CharSequence>().run {
				put("userId", userId)
				put("taskId", taskId)
				put("type", "5")
				put("status", if (_star.isChecked) "1" else "0")
				
				doAsync {
					// TODO : 网络请求 -> 112 (收藏)
				}
			}
		}
		
		_close.setOnClickListener { finish() }
		
		_swipe.setOnRefreshListener {
			initData()
			_swipe.isRefreshing = false
		}
		
		_recycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE)
					if (!_recycle.canScrollVertically(1))
						upData()
			}
		})
		
		layoutInflater.inflate(R.layout.footview_parents_qa, _recycle, false).run {
			footText = findViewById(R.id.foot_loading)
			footPrg = findViewById(R.id.foot_progress)
			mAdapter.setFootView(this)
		}
		
		layoutInflater.inflate(R.layout.header_parent_qa, _recycle, false).run {
			headTitle = findViewById(R.id.head_title)
			headIcon = findViewById(R.id.head_img)
			mAdapter.setHeadView(this)
		}
	}
	
	override fun initData() {
		taskId = intent.getStringExtra("taskId")
		val date = intent.getStringExtra("date")
		map.run {
			put("userId", userId)
			put("taskId", "1613") // 1613
			put("createDate", date)
			NetBuild.response(this@ParentsQA::success,
					ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
	}
	
	private fun upData() {
		if (list.isEmpty()) return
		if (b) {
			footText.text = "加载中"
			footPrg.visibility = View.VISIBLE
		}
		map.run {
			put("createDate", list[list.size - 1].createDate)
			NetBuild.response({
				if (it.menuList.isEmpty() || it.menuList[it.menuList.size - 1].createDate == list[list.size - 1].createDate) {
					if (b) {
						footText.text = "没有更多数据了"
						footPrg.visibility = View.GONE
						b = false
					}
					return@response
				}
				mAdapter.addData(it.menuList)
				mAdapter.notifyDataSetChanged()
				footText.text = "加载更多"
				footPrg.visibility = View.GONE
			}, ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun bind(holder: Adapter.Holder,
	                 mutableList: MutableList<ResponseBean.AnswerList>, position: Int) {
		val name = holder.find<TextView>(R.id.item_name)
		val praise = holder.find<CheckBox>(R.id.item_praise)
		val icon = holder.find<ImageView>(R.id.item_icon)
		val content = holder.find<TextView>(R.id.item_content)
		val praises = holder.find<TextView>(R.id.item_praise_count)
		val read = holder.find<TextView>(R.id.item_read_count)
		val img = holder.find<ImageView>(R.id.item_img)
		
		val data = mutableList[position]

//		val nicai = "<p><img src=\"http://106.14.251.200:8083/ueditor_server/ueditor/jsp/upload/image/20170811/1502431918388057979.jpg\" title=\"1502431918388057979.jpg\" alt=\"1.webp.jpg\"/></p><p><br/></p><p>学区房，一个特别的名词。教育资源分布不均，家长不愿让孩子输在起跑</p><p>线上，直接让北京上海等一线城市的学区房成为炙手可热的高价抢购品。</p><p>本文以上海为例，谈一谈学区房的现状和风险，以及学区房是否具有不可替代的重要意义。</p><p><br/></p><p>目录：</p><p>-上海优质高中/初中/小学有哪些</p><p>-好的升学规划路线有哪些</p><p>-什么是学区房</p><p>-上海学区房现状如何</p><p>-学区房的政策及风险</p><p><br/></p><p>城市分割线</p><p><br/></p><p>01、上海优质高中/初中/小学有哪些</p><p><br/></p><p>聊学区房为什么要聊高中？</p><p><br/></p><p>要知道学区房是体制内产物，而体制内绕不开高考，因此采取从“高中-初中-小学”倒推逻辑来聊学区房似乎也没毛病，即普遍观念会认为好的高中会影响初中，好的初中会影响小学。</p><p><br/></p><p>至于“为什么学历不一定值钱，但学区房却非常值钱”这种世纪难题不在本文讨论范畴，知乎上就有这个问题，有兴趣的盆友可以查查。</p><p><br/></p><p>以下根据2016年清华、北大、复旦、上交的录取人数罗列的上海高中名单。</p><p><br/></p><p><img src=\"http://106.14.251.200:8083/ueditor_server/ueditor/jsp/upload/image/20170811/1502431933000007362.jpg\" title=\"1502431933000007362.jpg\" alt=\"2.webp.jpg\"/></p><p><br/></p><p>其中包括赫赫有名的四大名校和八大金刚。上海中学，一本率接近100%，16年清北录取了55人，要知道清北在上海每年共录取200出头，一个学校占比达1/4，另外复旦和上交101人。前段时间特别火的《中国诗词大会》有位非常优秀的选手姜闻页同学就是上中的学生。当然还有一位更火的选手武亦姝，也是来自四大名校之一的复旦附中。</p><p><br/></p><p>紧随上中其后的是华二，华东师范大学第二附属中学，一本率97%，非常厉害，并且华二13年在闵行建立了一所分校，分校的师资是从本部调过来的，质量也很不错，一本率88%。</p><p><br/></p><p>复旦附中和交大附中的复旦、上交录取人数也很可观，占尽了先天优势，一本率在95%左右，交大附中最近有微跌。</p><p><br/></p><p>另外，必须要提一下闵行的七宝中学，近年来势头非常猛，单纯从清北复交的录取人数来看已冲击到四大，甩其他八大几条街，也有不少人断言七宝必将改变四大的格局。</p><p><br/></p><p>这里有两所不同体制的牛校。一所是浦东的上海实验学校，另一所是虹口的上外附中。</p><p><br/></p><p>我们常说四大名校、八大金刚，还有一个我们叫他“神仙学校”，就是上外附中。虽然他的清北复交录取人数比不上四大，但我们看看他的毕业生去向就知道他的神之处。</p><p><br/></p><p>有部分毕业生被耶鲁、哥伦比亚这样的常春藤录取，还有被斯坦福、牛津、早稻田、港大这样的世界名校录取，只有百分之十几的人会参加国内高考或保送，成绩也很突出。</p><p><br/></p><p>这就是“神仙学校”，如果家长想走出国这条路的话，上外附中是不错的选择。</p><p><br/></p><p>另一所不同体制的学校是上海实验学校，采取的是独一无二的十年制，小学4年，初中3年，高中3年，比同年级少2年，学生压力和教学能力可想而知。虽然是公办，但上实小学部是面向全市招生，录取比例极低。</p><p><br/></p><p>以上为上海顶尖高中的大体情况，关于数据较靠后的同等学校，列表中存在少许遗漏。</p><p><br/></p><p>那么这些牛校的生源是从哪里来的呢？来看看上海的初中。</p><p><br/>"
//		XmlHelper.parse(StringReader(data.content))
		
		/*val getter = Html.ImageGetter {
			img.visibility = if (!TextUtils.isEmpty(it)) {
				Glide.with(img).load(it).into(img)
				View.VISIBLE
			} else
				View.GONE
			
			if (img.visibility == View.VISIBLE) img.drawable else null
		}*/
		
//		val fromHtml = Html.fromHtml(data.content, getter, null)
		content.text = data.content
		
		name.text = data.from_nickName
		praises.text = "${data.commentLike}赞"
		read.text = "${data.clickSum}万阅读"
		
		Glide.with(icon).load(data.faceImg).into(icon)
		
		praise.isChecked = data.likeCommentStatus == 0
		
		img.visibility = View.GONE

//		content.post {
//			LogUtils.E("lineCount = ${content.lineCount}, position = $position")
//			LogUtils.E("text = ${content.text}")
//			if (content.lineCount > 5) {
//
//			}
//		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun success(t: ResponseBean.AnswerBody) {
		mAdapter.addDataAndClear(t.stickNamicfoList)
		mAdapter.addData(t.menuList)
		mAdapter.notifyDataSetChanged()
		
		b = true
		footText.text = "加载更多"
		
		val data = t.result
		
		_answer_count.text = "${data.comment_count}个回答"
		_star.text = "${data.collect_count}人收藏"
		
		headTitle.text = data.title
		Glide.with(headIcon).load(data.imgs).into(headIcon)
		
		if (!measured)
			_star.post {
				val point = Point()
				val left = windowManager.defaultDisplay.getSize(point).let {
					(point.x - (_star.measuredWidth * 3)) / 6
				}
				
				_answer_count.setPadding(left, 0, left, 0)
				_star.setPadding(left, 0, left, 0)
				
				measured = true
			}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_item, menu)
		return true
	}
}