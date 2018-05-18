package com.neworld.youyou.bean


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
    data class BooksBean(
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
    data class BooksDetailBean(
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
    data class AddressBean(
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
    data class BooksOrderBean(
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
            val typeImg: String //http://106.14.251.200:8082/olopicture/icon/y1.jpg
    )


    data class QABody(
            val tokenStatus: Int, //2
            val menuList: List<QADetail>?,
            val status: Int,  //0
            val xinStatus: Int // 1
    )

    data class QADetail(
            val comment_count: Int, //5
            val imgs: String?, //http://www.uujz.me:8082/olopicture/contextImg/10854201712251233111514176391396_10.jpg
            val like: Int, //2
            val voideImg: String,
            val ip_address: String,
            val sort: Int, //1
            val type: Int, //10
            val title: String, //孩子越优秀，离我们就越远
            val from_uid: Int, //10854
            val content: String, //我们父母
            val collect_count: Int, //2
            val stickStatus: Int, //1
            val id: Int, //1843
            val createDate: String, //2017-12-31 18:29:26
            val status: Int, //0
            val transmit_count: Int, //0
            val collectStatus: Int //1
    )

    data class AnswerBody(
            val result: Result?,
            val menuList: List<AnswerList>?,
            val stickNamicfoList: List<AnswerList>?,
            val status: Int //0
    )

    data class AnswerList(
            val from_userId: Int, //27390
            val commentImg: String?, //null
            val clickSum: Int, //0
            val faceImg: String, //http://106.14.251.200:8082/olopicture/icon/a222.jpg
            val parent_cid: Int, //0
            val commentId: Int, //1424
            val likeCommentStatus: Int, //1
            val commentLike: Int, //0
            val content: String, //虹口杨浦是可以买到电梯房的，学区不必前面某些区差
            val taskId: Int, //1613
            val createDate: String, //2017-10-23 15:32:27
            val from_nickName: String, //游客947087
            val attachedContent: String?, //content
            val comment_count: Int, // 评论数
            val transmit_count: Int // 分享数
    )

    data class Result(
            val comment_count: Int, //10  回答
            val imgs: String, //10854201710231522131508743333410.jpeg
            val like: Int, //1
            val voideImg: Any, //null
            val ip_address: String,
            val sort: Int, //1
            val type: Int, //1
            val title: String, //预算800万想在上海买套学区房
            val from_uid: Int, //10854
            val content: String, //这个预算要是放在上海？
            val collect_count: Int, //2  收藏
            val stickStatus: Int, //1
            val id: Int, //1613
            val createDate: String, //2017-10-23 15:22:12
            val status: Int, //0
            val transmit_count: Int, //0
            val collectStatus: Int // 0
    )

    data class AnswersDetailBody(
            val menuList: List<AnswersDetailList>,
            val commentBean: CommentBean,
            val userbean: Userbean,
            val status: Int //0
    )

    data class CommentBean(
            val imgUrl: String?,
            val clickSum: Int, //333
            val parent_cid: Int, //0
            val dynamic_id: Int, //1613
            val attachedContent: String, //The new version
            val likeCommentStatus: Int, //0
            val commentLike: Int, //1
            val type: Int, //5
            val from_uid: Int, //10851
            val content: String, //<p>T
            val commentCount: Int, //22
            val commentImg: String, //http://106.14.251.200:8082/olopicture/answers/10851201801271802431517047363693.jpg
            val stickStatus: Int, //1
            val to_uid: String, //10854
            val id: Int, //2575
            val createDate: String, //2018-01-27 18:03:04
            val status: Int //0
    )

    data class Userbean(
            val birthday: String, //1982-03-20
            val updateDate: String, //2018-01-24 15:14:26
            val sdasd: Any, //null
            val address: Any, //null
            val role: Int, //2
            val dynamic_status: Int, //1
            val faceImg: String, //http://www.uujz.me:8082/olopicture/icon/33866201711301733481512034428443.jpg
            val nickName: String, //Test
            val openId: Any, //null
            val mobileCheck: Any, //null
            val sex: Int, //0
            val userPwd: Any, //null
            val remarkName: Any, //null
            val userName: String, //null
            val cardNo: Any, //null
            val token: String, //d344ea45-1f2f-4426-8170-c406178c8feb
            val active_time: String, //2018-01-24 15:14:25
            val realName: Any, //null
            val phone: Any, //null
            val userAccount: String, //jy7xau6f
            val model: Int, //2
            val id: Int, //33866
            val createDate: String, //2017-11-15 09:58:32
            val status: Int //0
    )

    data class AnswersDetailList(
            val from_userId: Int, //10851
            val clickSum: Int, //0
            val faceImg: String, //http://www.uujz.me:8082/olopicture/icon/10851201711241404241511503464915.jpg
            val parent_cid: Int, //0
            var likeCommentStatus: Int, //0
            var commentLike: Int, //2
            val content: String, //0000000
            val from_nickName: String, //彩虹
            val remarkContent: String?, //null
            val remarkName: String?, //晴天
            val commentImg: String,
            val to_uid: String, //10854
            val commentId: String, //2717
            val taskId: Int, //2575
            val createDate: String //2018-01-31 11:04:08
    )

    data class BooksOrderModel(
            val UserAddressList: UserAddressList?,
            val menuList: BooksOrderMenu,
            val status: Int //0
    )

    data class BooksOrderMenu(
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
            val userMeassge: String?, //null
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
            val money: Double, //50
            val collectSum: Int, //1
            val phone: String, //13888888888
            val bookSum: Int, //0
            val typeId: String, //null
            val payStatus: Int, //0
            val status: Int, //0
            val orderCount: String
    )

    data class UserAddressList(
            val consignee: String, //啊啊啊
            val address: String, //奥Iu确定
            val phone: String, //13888888888
            val id: Int, //198
            val userId: Int, //33866
            val status: Int //1
    )

    data class QACollectBody(
            val menuList: List<QAMenu>,
            val status: Int //0
    )

    data class QAMenu(
            val comment_count: Int, //24
            val imgs: String,
            val like: Int, //0
            val voideImg: Any, //null
            val ip_address: Any, //null
            val sort: Int, //1
            val type: Any, //null
            val title: String, //如何用10个以内汉字写一篇微小说？
            val from_uid: Int, //10002
            val content: Any, //null
            val collect_count: Int, //1
            val stickStatus: Int, //1
            val newDate: String, //2018-02-07 18:26:05
            val id: Int, //1923
            val createDate: String, //2018-02-06 17:26:53
            val status: Int, //0
            val transmit_count: Int //0
    )

    data class HotTextBody(
            val tokenStatus: Int,
            val menuList: List<HotTextModel>,
            val status: Int
    )

    data class HotTextModel(
            val imgs: String,
            val faceImg: String,
            val nickName: String,
            val attachedContent: String,
            val id: Int,
            val source: String,
            val title: String,
            val createDate: String,
            val collectStatus: Int
    )

    data class HotTitleBody(
            val menuList: List<HotTitleModel>,
            val status: Int
    )

    data class HotTitleModel(
            val typeName: String,
            val id: Int,
            val typeImg: String
    )


    data class QTitleBody(
            val menuList: List<QTitleModel>,
            val status: Int
    )

    data class QTitleModel(
            val typeName: String,
            val typeId: Int,
            val id: Int,
            val typeImg: String
    )

    data class DynamicBody(
            val menuList: List<DynamicModel>,
            val userList: UserList,
            val status: Int
    )

    data class DynamicModel(
            val imgs: String?,
            val answersId: Int,
            val parent_cid: Int,
            val remarkName: String?,
            val commentContent: String,
            val title: String,
            val anStatus: Int,
            val content: String,
            val commentImg: String,
            val remarkContent: String?,
            val commentId: Int,
            val taskId: Int,
            val createDate: String
    )

    data class UserList(
            val birthday: String,
            val updateDate: String,
            val sdasd: Any,
            val address: Any,
            val role: Int,
            val dynamic_status: Int,
            val faceImg: String,
            val nickName: String,
            val openId: Any,
            val mobileCheck: Any,
            val sex: Int,
            val userPwd: Any,
            val remarkName: Any,
            val userName: String,
            val cardNo: Any,
            val token: String,
            val active_time: String,
            val realName: Any,
            val phone: Any,
            val userAccount: String,
            val model: Int,
            val id: Int,
            val createDate: String,
            val status: Int
    )
}