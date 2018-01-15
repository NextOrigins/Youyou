package com.neworld.youyou.view.mview.parents

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.ImageView
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.logE
import kotlinx.android.synthetic.main.activity_answers.*
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.E

/**
 * @author by user on 2018/1/15.
 */
class Answers : Activity() {
	
	val html = "<p><img src=\"http://106.14.251.200:8083/ueditor_server/ueditor/jsp/upload/image/20170811/1502431918388057979.jpg\" title=\"1502431918388057979.jpg\" alt=\"1.webp.jpg\"/></p>\n" +
			"<p><br/></p>\n" +
			"<p>学区房，一个特别的名词。教育资源分布不均，家长不愿让孩子输在起跑</p>\n" +
			"<p>线上，直接让北京上海等一线城市的学区房成为炙手可热的高价抢购品。</p>\n" +
			"<p>本文以上海为例，谈一谈学区房的现状和风险，以及学区房是否具有不可替代的重要意义。</p>\n" +
			"<p><br/></p>\n" +
			"<p>目录：</p>\n" +
			"<p>-上海优质高中/初中/小学有哪些</p>\n" +
			"<p>-好的升学规划路线有哪些</p>\n" +
			"<p>-什么是学区房</p>\n" +
			"<p>-上海学区房现状如何</p>\n" +
			"<p>-学区房的政策及风险</p>\n" +
			"<p><br/></p>\n" +
			"<p>城市分割线</p>\n" +
			"<p><br/></p>\n" +
			"<p>01、上海优质高中/初中/小学有哪些</p>\n" +
			"<p><br/></p>\n" +
			"<p>聊学区房为什么要聊高中？</p>\n" +
			"<p><br/></p>\n" +
			"<p>要知道学区房是体制内产物，而体制内绕不开高考，因此采取从“高中-初中-小学”倒推逻辑来聊学区房似乎也没毛病，即普遍观念会认为好的高中会影响初中，好的初中会影响小学。</p>\n" +
			"<p><br/></p>\n" +
			"<p>至于“为什么学历不一定值钱，但学区房却非常值钱”这种世纪难题不在本文讨论范畴，知乎上就有这个问题，有兴趣的盆友可以查查。</p>\n" +
			"<p><br/></p>\n" +
			"<p>以下根据2016年清华、北大、复旦、上交的录取人数罗列的上海高中名单。</p>\n" +
			"<p><br/></p>\n" +
			"<p><img src=\"http://106.14.251.200:8083/ueditor_server/ueditor/jsp/upload/image/20170811/1502431933000007362.jpg\" title=\"1502431933000007362.jpg\" alt=\"2.webp.jpg\"/></p>\n" +
			"<p><br/></p>\n" +
			"<p>其中包括赫赫有名的四大名校和八大金刚。上海中学，一本率接近100%，16年清北录取了55人，要知道清北在上海每年共录取200出头，一个学校占比达1/4，另外复旦和上交101人。前段时间特别火的《中国诗词大会》有位非常优秀的选手姜闻页同学就是上中的学生。当然还有一位更火的选手武亦姝，也是来自四大名校之一的复旦附中。</p>\n" +
			"<p><br/></p>\n" +
			"<p>紧随上中其后的是华二，华东师范大学第二附属中学，一本率97%，非常厉害，并且华二13年在闵行建立了一所分校，分校的师资是从本部调过来的，质量也很不错，一本率88%。</p>\n" +
			"<p><br/></p>\n" +
			"<p>复旦附中和交大附中的复旦、上交录取人数也很可观，占尽了先天优势，一本率在95%左右，交大附中最近有微跌。</p>\n" +
			"<p><br/></p>\n" +
			"<p>另外，必须要提一下闵行的七宝中学，近年来势头非常猛，单纯从清北复交的录取人数来看已冲击到四大，甩其他八大几条街，也有不少人断言七宝必将改变四大的格局。</p>\n" +
			"<p><br/></p>\n" +
			"<p>这里有两所不同体制的牛校。一所是浦东的上海实验学校，另一所是虹口的上外附中。</p>\n" +
			"<p><br/></p>\n" +
			"<p>我们常说四大名校、八大金刚，还有一个我们叫他“神仙学校”，就是上外附中。虽然他的清北复交录取人数比不上四大，但我们看看他的毕业生去向就知道他的神之处。</p>\n" +
			"<p><br/></p>\n" +
			"<p>有部分毕业生被耶鲁、哥伦比亚这样的常春藤录取，还有被斯坦福、牛津、早稻田、港大这样的世界名校录取，只有百分之十几的人会参加国内高考或保送，成绩也很突出。</p>\n" +
			"<p><br/></p>\n" +
			"<p>这就是“神仙学校”，如果家长想走出国这条路的话，上外附中是不错的选择。</p>\n" +
			"<p><br/></p>\n" +
			"<p>另一所不同体制的学校是上海实验学校，采取的是独一无二的十年制，小学4年，初中3年，高中3年，比同年级少2年，学生压力和教学能力可想而知。虽然是公办，但上实小学部是面向全市招生，录取比例极低。</p>\n" +
			"<p><br/></p>\n" +
			"<p>以上为上海顶尖高中的大体情况，关于数据较靠后的同等学校，列表中存在少许遗漏。</p>\n" +
			"<p><br/></p>\n" +
			"<p>那么这些牛校的生源是从哪里来的呢？来看看上海的初中。</p>\n" +
			"<p><br/></p>"
	
	private var drawable: Drawable? = null
	
	override fun getContentLayoutId()
			= R.layout.activity_answers
	
	override fun initWidget() {
		_close.setOnClickListener { finish() }
		val imageView = ImageView(baseContext)
		
		val textView = TextView(this)
		textView.textSize = 16F
		textView.setTextColor(ContextCompat.getColor(this, R.color.black))
		
		val getter = Html.ImageGetter {
			val url = URL(it)
			logE("url : $it")
			val httpURLConnection = url.openConnection() as HttpURLConnection
			httpURLConnection.requestMethod = "GET"
			httpURLConnection.connectTimeout = 3000
			val inputStream = httpURLConnection.inputStream
			val bitmap = BitmapFactory.decodeStream(inputStream)
			drawable = BitmapDrawable(bitmap)
//				drawable = Drawable.createFromStream(inputStream, "icon")
			runOnUiThread { imageView.setImageDrawable(drawable) }
			
			drawable
		}
		
		val fromHtml = Html.fromHtml(html, getter, null)
		textView.text = fromHtml
		textView.append("呵呵")
//		val bitmap = BitmapFactory.decodeStream(assets.open("test.png"))
//		val imageSpan = ImageSpan(this, bitmap)
//		val sbs = SpannableString("icon")
//		sbs.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//		textView.text = sbs
//		textView.append("iAUDIO求带后期维护第九好玩的缷IQ的")
		
		_verticalParent.addView(textView)
		_verticalParent.addView(imageView)
	}
}