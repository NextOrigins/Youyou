package com.neworld.youyou.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tt on 2017/7/20.
 */

public class ParentBean {
    /**
     * menuList : [{"collectStatus":1,"collect_count":0,"comment_count":19,"content":"一个10岁的孩子：学习成绩优秀，奥数、围棋、轮滑各种业余爱好也出类拔萃。可他却吐槽自己爸妈没钱，还说爸妈不配有他这么好的儿子。以下是原贴截图，我是看得五味杂陈！你呢？","createDate":"2017-08-28 12:29:45","faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","from_uid":10854,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10854201708281229471503894587980.jpeg|http://106.14.251.200:8082/olopicture/contextImg/10854201708281229481503894588160.jpeg","like":0,"likeStatus":1,"nickName":"晴天","remarkName":"晴天","sex":1,"status":0,"taskId":1381,"title":"\u201c爸妈没什么钱，不配有我这么好的儿子！\u201d","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":0,"content":"明明是尽职尽责的父母，却因为交流不畅而在心理上失去了孩子的信任。家庭教育的本质，不是教化，是交流与理解。中国父母最大的问题是\u201c大主角\u201d意识太强，三观极正。你不想听他说\u201c混账话\u201d，可能就永远听不到他说真话。孩子不会因为你少说一句就变坏。重要的是交流与理解。各位觉得呢？","createDate":"2017-08-28 11:46:41","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708281146411503892001987.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1379,"title":"很多父母，就是输在\u201c三观太正\u201d上","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":0,"content":"天才少女12岁参加高考，超出一本线135分！从小爱看书，爱思考，高二就趁着业余时间学了一些大学基础课程。对未来暂无规划，想多读书。准备博士毕业以后出来工作。对于这种天才少女，你有什么想说的吗？","createDate":"2017-08-28 11:25:25","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708281125271503890727712.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1378,"title":"13岁小姑娘高考620分！6岁上小学，7岁上初中\u2026","transmit_count":0,"typeName":"育儿"},{"collectStatus":0,"collect_count":1,"comment_count":0,"content":"做人没有梦想跟咸鱼有什么区别","createDate":"2017-08-28 09:37:09","faceImg":"http://106.14.251.200:8082/olopicture/icon/10850201708211057191503284239747.jpg","from_uid":10850,"like":0,"likeStatus":0,"nickName":"咸鱼","remarkName":"咸鱼","sex":1,"status":0,"taskId":1377,"title":"咸鱼","transmit_count":0,"typeName":"小升初"},{"collectStatus":0,"collect_count":1,"comment_count":1,"content":"对此，郎朗的回应是\u201c谢谢爸爸！逼我练琴\u201d，周杰伦也坦言自己学琴是被逼的。孩子被逼学习的过程可能是残酷的，但残酷的背后是父母培养孩子特长远见和决心。作为家长你会怎么选呢？","createDate":"2017-08-26 15:51:28","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708261551291503733889150.jpeg","like":0,"likeStatus":0,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1375,"title":"逼孩子学习和放任孩子，哪个更残酷？","transmit_count":0,"typeName":"育儿"},{"collectStatus":0,"collect_count":1,"comment_count":1,"content":"郭敬明的成功是建立在谎言之上的，他抄袭，拜金，而他的读者又恰恰是心智不成熟的初高中生。为了让孩子树立正确的金钱观，不要让他们在郭敬明的书里去寻找慰藉，带他们去亲身了解这个世界吧。","createDate":"2017-08-26 15:31:23","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708261531241503732684137.jpeg","like":0,"likeStatus":0,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1374,"title":"不要让孩子通过郭敬明的书来了解这个世界","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":1,"content":"一个会奥数轮滑围棋，班里成绩第一的孩子，竟称\u201c爸妈不配拥有他这么好的儿子\u201d！听到爸妈想生二胎，说\u201c这么穷，坑了一个还想再坑一个孩子吗\u201d。为了出国，他努力学英语，为了长高而拼命锻炼，为了锻炼大脑而学围棋。有人称他是\u201c冷血鸡娃\u201d，有人称他\u201c将来大有前途\u201d，你的看法呢？","createDate":"2017-08-25 14:39:28","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708251439291503643169044.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1369,"title":"10岁孩说\u201c爸妈不配拥有我这么好的孩子\u201d","transmit_count":0,"typeName":"育儿"},{"collectStatus":0,"collect_count":1,"comment_count":8,"content":"当父母恨不得把全世界所有的好东西和所有的爱都给孩子时，却忘了告诉孩子一件事：生活的艰辛，是难以想象的。孩子们倒是心安理得享受着一切，根本不知道知足，不知道感恩，不知道体贴，不知道生活的不易，相反，还滋生了很多虚荣、懒惰、不学无术的坏毛病。所以，给孩子再好的教育，都不如让他亲自去感受一下，成人世界的不容易。关于这点，相信作为家长的你，也是很有感悟的吧！欢迎留言和我们分享。","createDate":"2017-08-24 13:48:31","faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","from_uid":10854,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10854201708241348321503553712746.jpeg","like":1,"likeStatus":0,"nickName":"晴天","remarkName":"晴天","sex":1,"status":0,"taskId":1365,"title":"最悲哀的教育，就是把普通家庭的孩子，养成了富二代","transmit_count":2,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":0,"content":"1、\u201c层次低\u201d的父母缺乏学习意识；2、\u201c层次低\u201d的父母忽视榜样的作用；3、\u201c层次低\u201d的父母只会要求孩子。你会在孩子面前玩手机吗？","createDate":"2017-08-23 18:03:24","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708231803251503482605104.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1335,"title":"为什么\u201c层次低\u201d的父母更喜欢玩手机？","transmit_count":0,"typeName":"育儿"},{"collectStatus":0,"collect_count":1,"comment_count":0,"content":"做人没有梦想跟咸鱼有什么区别","createDate":"2017-08-23 17:00:07","faceImg":"http://106.14.251.200:8082/olopicture/icon/10850201708211057191503284239747.jpg","from_uid":10850,"like":0,"likeStatus":0,"nickName":"咸鱼","remarkName":"咸鱼","sex":1,"status":0,"taskId":1334,"title":"咸鱼","transmit_count":0,"typeName":"小升初"},{"collectStatus":1,"collect_count":0,"comment_count":0,"content":"初一：适应节奏，做好积累；初二：加强积累，提炼学习方法；初三：体系化学科，提升深度。你们还有什么别的方法分享吗？","createDate":"2017-08-23 12:03:02","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708231203041503460984435.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1329,"title":"如何考重点高中？初中三年这样学！再不看来不及了！","transmit_count":0,"typeName":"中考"},{"collectStatus":0,"collect_count":2,"comment_count":6,"content":"对于每个家庭来说，情况不同，大家生不生，二胎不二胎都是各有各纠结的地方，但是请别一直站在原地纠结，做和不做，踏出了，用心了，都会收获。如果纠结后选择了接受一个新生命，那就坚信：没有女人会后悔成为孩子的妈妈。因为无论一个还是两个，对孩子和我们是独一无二应该珍惜的经历，不是吗？","createDate":"2017-08-22 15:40:41","faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","from_uid":10854,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10854201708221540421503387642278.jpeg","like":1,"likeStatus":0,"nickName":"晴天","remarkName":"晴天","sex":1,"status":0,"taskId":1327,"title":"要不要二胎？不生，就别后悔，生了，别怕辛苦","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":1,"comment_count":3,"content":"爸爸不陪伴孩子，孩子当然就不和爸爸亲，而这也不过是最表面的现象，真正的本质影响是，一个缺席的爸爸身后会产生一个问题孩子。心理学家也多次指出：父亲对孩子的健康发展有着非常重要的影响，有些作用甚至是母亲无法替代的。你身边有没有一些原来不陪孩子的爸爸，在某些机缘巧合或是顿悟之后，终于觉察到，世间所有的美好，都不抵孩子微微一笑，于是，努力变成了孩子心目中不可替代的好爸爸的感人事迹呢？","createDate":"2017-08-22 14:29:21","faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","from_uid":10854,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10854201708221429231503383363330.jpeg","like":2,"likeStatus":1,"nickName":"晴天","remarkName":"晴天","sex":1,"status":0,"taskId":1326,"title":"那些不陪孩子的爸爸，后来都怎么样了？","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":0,"content":"1、特别重视孩子和谁在一起；2、孩子的书单里，必备\u201c非虚构图书\u201d；3、不要放任看电视，玩电脑；4、饮食节制，规律健身；5、培养孩子控制言辞，控制情绪；6、少抱怨，多乐观；7、端正态度，把金钱当朋友；8、不依靠，不依赖。你有培养孩子养成这些习惯吗？","createDate":"2017-08-22 14:22:31","faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","from_uid":10856,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10856201708221422321503382952610.jpeg","like":0,"likeStatus":1,"nickName":"Zeldar","remarkName":"Zeldar","sex":1,"status":0,"taskId":1324,"title":"5年跟踪调查410个\u201c富裕\u201dvs\u201c普通\u201d家庭，发现培养优秀孩子的8个秘密","transmit_count":0,"typeName":"育儿"},{"collectStatus":1,"collect_count":0,"comment_count":1,"content":"距离开学不到两周了宝贝我好舍不得你喔\u2026\u2026才怪咧！因为，终于！终于！终于！麻麻要解放了呀~简单总结下六条收心大法：家长应该在开学前两周给孩子收心，帮助孩子调整好心态和生物钟，合理安排孩子的生活、学习和娱乐时间，让孩子逐渐适应校园作息。","createDate":"2017-08-21 11:55:27","faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","from_uid":10854,"imgs":"http://106.14.251.200:8082/olopicture/contextImg/10854201708211155281503287728310.jpeg","like":0,"likeStatus":1,"nickName":"晴天","remarkName":"晴天","sex":1,"status":0,"taskId":1305,"title":"暑假余额不足2周：聪明妈妈这样帮孩子收心，受益一学期！","transmit_count":0,"typeName":"育儿"}]
     * status : 0
     */

    private int status;
    private int tokenStatus;
    private List<MenuListBean> menuList;
    public List<MenuListBean> stickNamicfoList;

    public int getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(int tokenStatus) {
        this.tokenStatus = tokenStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MenuListBean> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuListBean> menuList) {
        this.menuList = menuList;
    }

    public static class MenuListBean {
        /**
         * collectStatus : 1
         * collect_count : 0
         * comment_count : 19
         * content : 一个10岁的孩子：学习成绩优秀，奥数、围棋、轮滑各种业余爱好也出类拔萃。可他却吐槽自己爸妈没钱，还说爸妈不配有他这么好的儿子。以下是原贴截图，我是看得五味杂陈！你呢？
         * createDate : 2017-08-28 12:29:45
         * faceImg : http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg
         * from_uid : 10854
         * imgs : http://106.14.251.200:8082/olopicture/contextImg/10854201708281229471503894587980.jpeg|http://106.14.251.200:8082/olopicture/contextImg/10854201708281229481503894588160.jpeg
         * like : 0
         * likeStatus : 1
         * nickName : 晴天
         * remarkName : 晴天
         * sex : 1
         * status : 0
         * taskId : 1381
         * title : “爸妈没什么钱，不配有我这么好的儿子！”
         * transmit_count : 0
         * typeName : 育儿
         * sort : 1
         * voideImg : null  (img)
         */

        private int collectStatus;
        private int collect_count;
        private int comment_count;
        private String content;
        private String createDate;
        private String faceImg;
        private String from_uid;
        private String imgs;
        private int like;
        private int likeStatus;
        private String nickName;
        private String remarkName;
        private int sex;
        private int status;
        private String taskId;
        private String title;
        private int transmit_count;
        private String typeName;
        public boolean isVisible;

        public int sort;
        public String voideImg;
        public int maskStatus;
        public int stickStatus;

        public int getCollectStatus() {
            return collectStatus;
        }

        public void setCollectStatus(int collectStatus) {
            this.collectStatus = collectStatus;
        }

        public int getCollect_count() {
            return collect_count;
        }

        public void setCollect_count(int collect_count) {
            this.collect_count = collect_count;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getFaceImg() {
            return faceImg;
        }

        public void setFaceImg(String faceImg) {
            this.faceImg = faceImg;
        }

        public String getFrom_uid() {
            return from_uid;
        }

        public void setFrom_uid(String from_uid) {
            this.from_uid = from_uid;
        }

        public String getImgs() {
            return imgs;
        }

        public void setImgs(String imgs) {
            this.imgs = imgs;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(int likeStatus) {
            this.likeStatus = likeStatus;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getTransmit_count() {
            return transmit_count;
        }

        public void setTransmit_count(int transmit_count) {
            this.transmit_count = transmit_count;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}
