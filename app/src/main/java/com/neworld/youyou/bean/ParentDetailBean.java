package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/28.
 */

public class ParentDetailBean {

    /**
     * menuList : {"comment_count":40,"imgs":"10854201708281229471503894587980.jpeg|10854201708281229481503894588160.jpeg","like":1,"faceImg":"http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg","nickName":"晴天","sex":1,"typeName":"育儿","title":"\u201c爸妈没什么钱，不配有我这么好的儿子！\u201d","from_uid":10854,"content":"一个10岁的孩子：学习成绩优秀，奥数、围棋、轮滑各种业余爱好也出类拔萃。可他却吐槽自己爸妈没钱，还说爸妈不配有他这么好的儿子。以下是原贴截图，我是看得五味杂陈！你呢？","collect_count":1,"taskId":1381,"createDate":"2017-08-28 12:29:45","status":0,"transmit_count":0}
     * likeStatus : 1
     * collectStatus : 0
     * status : 0
     */

    private MenuListBean menuList;
    private int likeStatus;
    private int collectStatus;
    private int status;

    public MenuListBean getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuListBean menuList) {
        this.menuList = menuList;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public int getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(int collectStatus) {
        this.collectStatus = collectStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class MenuListBean {
        /**
         * comment_count : 40
         * imgs : 10854201708281229471503894587980.jpeg|10854201708281229481503894588160.jpeg
         * like : 1
         * faceImg : http://106.14.251.200:8082/olopicture/icon/10854201708171209101502942950792.jpg
         * nickName : 晴天
         * sex : 1
         * typeName : 育儿
         * title : “爸妈没什么钱，不配有我这么好的儿子！”
         * from_uid : 10854
         * content : 一个10岁的孩子：学习成绩优秀，奥数、围棋、轮滑各种业余爱好也出类拔萃。可他却吐槽自己爸妈没钱，还说爸妈不配有他这么好的儿子。以下是原贴截图，我是看得五味杂陈！你呢？
         * collect_count : 1
         * taskId : 1381
         * createDate : 2017-08-28 12:29:45
         * status : 0
         * transmit_count : 0
         */

        private int comment_count;
        private String imgs;
        private int like;
        private String faceImg;
        private String nickName;
        private int sex;
        private String typeName;
        private String title;
        private int from_uid;
        private String content;
        private int collect_count;
        private int taskId;
        private String createDate;
        private int status;
        private int transmit_count;

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
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

        public String getFaceImg() {
            return faceImg;
        }

        public void setFaceImg(String faceImg) {
            this.faceImg = faceImg;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getFrom_uid() {
            return from_uid;
        }

        public void setFrom_uid(int from_uid) {
            this.from_uid = from_uid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getCollect_count() {
            return collect_count;
        }

        public void setCollect_count(int collect_count) {
            this.collect_count = collect_count;
        }

        public int getTaskId() {
            return taskId;
        }

        public void setTaskId(int taskId) {
            this.taskId = taskId;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getTransmit_count() {
            return transmit_count;
        }

        public void setTransmit_count(int transmit_count) {
            this.transmit_count = transmit_count;
        }
    }
}
