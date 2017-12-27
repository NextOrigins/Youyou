package com.neworld.youyou.bean

import com.google.gson.annotations.SerializedName


/**
 * @author by user on 2017/11/20.
 */
class ResponseBean {
	
	// 版本更新
	data class Version(
			val results: Results?,
			val status: Int? //0
	)
	
	data class Results(
			val msg: String?, //检测到新版本，是否升级！
			val versionNum: String?, //1
			val id: Int?, //1
			val versionName: String?, //1.0.1
			val status: Int?, //0
			val out: Int? //1
	)
	
	
	// 图书页面
	data class BooksBody(
			val tokenStatus: Int, //1
			val menuList: List<Menu>,
			val status: Int //0
	)
	
	data class Menu(
			val price: Float, //40.0
			val author: String, //路易·亚历山大（L.G.Alexander）| 朱莉娅·亚历山大（Julia Alexander）| 罗伊·金斯伯里（Roy Kingsbury）
			val iconImg: String, //http://106.14.251.200:8082/olopicture/book/5b_face.jpg
			val constPrice: Float, //40.0
			val bookName: String, //新概念英语青少版(5B)
			val bookId: String, //28
			val createDate: String //2017-08-20 11:48:43
	)
	
	// 图书详情
	data class BooksDetailBody(
			val menuList: MenuList,
			val likeStatus: Int, //1
			val collectStatus: Int, //1
			val status: Int //0
	)
	
	data class MenuList(
			val updateDate: String, //2017-08-20 11:44:43
			val author: String, //路易·亚历山大（L.G.Alexander）| 朱莉娅·亚历山大（Julia Alexander）| 罗伊·金斯伯里（Roy Kingsbury）
			val iconImg: String, //http://106.14.251.200:8082/olopicture/book/3bq_face.jpg
			val publishDate: String, //外语教学与研究出版社
			val count: Int, //230
			val constPrice: Double, //40
			val suggest: String, //content
			val title: String,
			val type: Int, //0
			val bookName: String, //新概念英语青少版(3B)
			val expressFee: Double, //10
			val userId: String, //null
			val contentImg: String, //http://106.14.251.200:8082/olopicture/book/3bq_book1.png
			val likeSum: Int, //0
			val collectSum: Int, //0
			val price: Double, //40
			val introduceImg: String?, //null  图片
			val id: Int, //24
			val createDate: String, //2017-08-20 11:44:43
			val status: Int //0
	)
	
	// 收货地址列表
	data class AddressBody(
			val menuList: List<AddressDetailList>,
			val status: Int //0
	)
	
	data class AddressDetailList(
			val consignee: String, //打的
			val address: String, //哒哒哒哒哒
			val phone: String, //13888888888
			val id: String, //127
			val userId: Int, //33866
			val status: Int //0
	)
	
	// 图书订单
	data class BooksOrderBody(
			val menuList: List<OrderMenu>,
			val status: Int //0
	)
	
	data class OrderMenu(
			val date: String, //null
			val msg: String, //null
			val updateDate: String, //2017-08-20 11:44:43
			val orderMoney: Double, //50
			val orderId: String, //2604
			val bookCount: String, //1
			val publishDate: String, //外语教学与研究出版社
			val type: Int, //0
			val title: String,
			val expressFee: Double, //10
			val contentImg: String, //http://106.14.251.200:8082/olopicture/book/3bq_book1.png
			val addressId: Int, //198
			val orderCreateDate: String, //2017-12-07 18:19:54.475
			val userMeassge: String, //null
			val likeSum: Int, //1
			val examinee_name: String, //新概念英语青少版(3B)
			val school: String, //null
			val price: Double, //40
			val outTradeNo: String, //201712071819546111
			val introduceImg: String, //null
			val id: Int, //24
			val order_type: Int, //2
			val createDate: String, //2017-08-20 11:44:43
			val author: String, //路易·亚历山大（L.G.Alexander）| 朱莉娅·亚历山大（Julia Alexander）| 罗伊·金斯伯里（Roy Kingsbury）
			val iconImg: String, //http://106.14.251.200:8082/olopicture/book/3bq_face.jpg
			val count: Int, //0
			val constPrice: Int, //40
			val suggest: String, //编辑推荐：
			val userId: String, //null
			val bookName: String, //新概念英语青少版(3B)
			val bookId: Int, //24
			val money: Int, //50
			val collectSum: Int, //1
			val phone: String, //13888888888
			val bookSum: Int, //0
			val typeId: String, //null
			val payStatus: Int, //0
			val status: Int //0
	)
	
	
	data class EBookBody(
			val tokenStatus: Int, //1
			val menuList: List<EBookMenu>,
			val status: Int //0
	)
	
	data class EBookMenu(
			val price: Double, //48.0
			val author: String,
			val iconImg: String, //http://106.14.251.200:8082/olopicture/book/you_face1.jpg
			val constPrice: Double, //48.0
			val bookName: String, //2018幼升小白皮书
			val bookId: Int, //16
			val createDate: String //2017-08-20 11:36:43
	)
	
	
	data class EBookContentBody(
			val menuList: EBookContentList,
			val typeList: List<Type>,
			val status: Int //0
	)
	
	data class EBookContentList(
			val updateDate: String, //2017-08-17 11:27:43
			val read_chapter: String, //1|2|3|4|5|6|7
			val author: String, //亚历山大L.G. Alexander|何其莘
			val iconImg: String, //http://106.14.251.200:8082/olopicture/book/1g_face.jpg
			val publishDate: String, //外语教学与研究出版社、朗文出版亚洲有限公司
			val count: Int, //0
			val constPrice: Int, //60
			val suggest: String, //编辑推荐
			val type: Int, //0
			val bookName: String, //新概念英语1
			val contentImg: String, //http://106.14.251.200:8082/olopicture/book/1g_book1.png|http://106.14.251.200:8082/olopicture/book/1g_book2.png
			val likeSum: Int, //0
			val collectSum: Int, //0
			val price: Int, //60
			val introduceImg: Any, //null
			val id: Int, //4
			val createDate: String, //2017-08-17 11:27:43
			val status: Int //0
	)
	
	data class Type(
			var isLoaded: Boolean = false,
			val imgSum: Int, //3
			val typeName: String, //升学
			val id: Int, //1
			val typeImg: String //http://106.14.251.200:8082/olopicture/icon/y1.jpg|http://106.14.251.200:8082/olopicture/icon/rdht.png|http://106.14.251.200:8082/olopicture/icon/f1.jpg
	)
}