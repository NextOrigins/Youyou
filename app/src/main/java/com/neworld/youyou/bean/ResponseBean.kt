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


    data class QABody(
            val tokenStatus: Int, //2
            val menuList: List<QADetail>,
            val dynamincStatus: Int, //1
            val status: Int //0
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
            val content: String, //我们父母那代家长，在和孩子分别时会有那种生离死别的哀伤，是因为他们那个时代的人，其时代特征是没有自己的人生。可我们不一样啊，我们退休后可能第一任务是和老伴一起全世界到处浪，哪有时间为娃不在身边伤春悲秋？不论娃在世界哪个角落，买张机票就去了啊！呆久了还嫌烦呢！所以，孩子和父母隔得远，这个问题到我们这辈应该就不是问题了，反而脓包娃在身边歪歪腻腻，时不时想啃个老，拜托带个孙啥的，可能搞得我们还火大呢！总之，成长是个跟孩子逐渐分离的过程，每个家长都必须理谨记这一点！
            val collect_count: Int, //2
            val stickStatus: Int, //1
            val id: Int, //1843
            val createDate: String, //2017-12-31 18:29:26
            val status: Int, //0
            val transmit_count: Int, //0
            val collectStatus: Int //1
    )

    data class AnswerBody(
            val result: Result,
            val menuList: List<AnswerList>,
            val stickNamicfoList: List<AnswerList>,
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
            val attachedContent: String? //content
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
            val status: Int //0
    )

    data class AnswersDetailList(
            val from_userId: Int, //28677
            val clickSum: Int, //0
            val faceImg: String, //http://106.14.251.200:8082/olopicture/icon/a111.jpg
            val parent_cid: Int, //1853
            var likeCommentStatus: Int, //1
            var commentLike: Int, //0
            val content: String, //孩子成长过程中，父母的陪伴很重要
            val from_nickName: String, //游客421523
            val commentCount: Int, //8
            val commentImg: Any, //null
            val commmentTwo: List<CommmentTwo>,
            val commentId: Int, //1468
            val taskId: Int, //1624
            val createDate: String //2017-10-27 08:50:21
    )

    data class CommmentTwo(
            val from_userId: Int, //28677
            val commentImg: Any, //null
            val clickSum: Int, //0
            val faceImg: String, //http://106.14.251.200:8082/olopicture/icon/a111.jpg
            val parent_cid: Int, //1853
            val commentId: Int, //1468
            val commentLike: Int, //0
            val content: String, //孩子成长过程中，父母的陪伴很重要
            val taskId: Int, //1624
            val createDate: String, //2017-10-27 08:50:21
            val from_nickName: String //游客421523
    )
}