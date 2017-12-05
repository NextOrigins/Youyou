package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/30.
 */

public class SubjectWebBean {

    /**
     * collectStatus : 1
     * likeStatus : 0
     * menuList : {"activityTitle":"现代信息杯","address":"待定，具体以准考证为准","admissionUrl":"准考证下载暂时未开通，请敬请关注！","apply_count":584,"biginDate":"2017-10-01","ccaaUrl":"http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp","collect_count":2,"comment_count":0,"comment_img":"http://106.14.251.200:8082/olopicture/activity/icon/xxb123.jpg","count":0,"createDate":"2017-09-05 16:53:14","endDate":"2017-12-05","examDate":"三、四24日","exam_schedule":"新贝分校领取","get_address":"新贝分校领取","grade":"","icon":"","id":13,"like":1,"money":"60","queryResultUrl":"成绩查询暂时未开通，请敬请关注！","referral":"活动目","status":0,"subject_type":"45|46|47|48|49|50","title":"\u201c信息杯\u201d英语听力综合评估活动","type":"1","updateDate":"2017-08-28 16:53:14"}
     * status : 0
     */

    private int collectStatus;
    private int likeStatus;
    private MenuListBean menuList;
    private int status;

    public int getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(int collectStatus) {
        this.collectStatus = collectStatus;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public MenuListBean getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuListBean menuList) {
        this.menuList = menuList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class MenuListBean {
        /**
         * activityTitle : 现代信息杯
         * address : 待定，具体以准考证为准
         * admissionUrl : 准考证下载暂时未开通，请敬请关注！
         * apply_count : 584
         * biginDate : 2017-10-01
         * ccaaUrl : http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp
         * collect_count : 2
         * comment_count : 0
         * comment_img : http://106.14.251.200:8082/olopicture/activity/icon/xxb123.jpg
         * count : 0
         * createDate : 2017-09-05 16:53:14
         * endDate : 2017-12-05
         * examDate : 三、四24日
         * exam_schedule : 新贝分校领取
         * get_address : 新贝分校领取
         * grade :
         * icon :
         * id : 13
         * like : 1
         * money : 60
         * queryResultUrl : 成绩查询暂时未开通，请敬请关注！
         * referral : 活动目
         * status : 0
         * subject_type : 45|46|47|48|49|50
         * title : “信息杯”英语听力综合评估活动
         * type : 1
         * updateDate : 2017-08-28 16:53:14
         */

        private String activityTitle;
        private String address;
        private String admissionUrl;
        private int apply_count;
        private String biginDate;
        private String ccaaUrl;
        private int collect_count;
        private int comment_count;
        private String comment_img;
        private int count;
        private String createDate;
        private String endDate;
        private String examDate;
        private String exam_schedule;
        private String get_address;
        private String grade;
        private String icon;
        private int id;
        private int like;
        private String money;
        private String queryResultUrl;
        private String referral;
        private int status;
        private String subject_type;
        private String title;
        private String type;
        private String updateDate;

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAdmissionUrl() {
            return admissionUrl;
        }

        public void setAdmissionUrl(String admissionUrl) {
            this.admissionUrl = admissionUrl;
        }

        public int getApply_count() {
            return apply_count;
        }

        public void setApply_count(int apply_count) {
            this.apply_count = apply_count;
        }

        public String getBiginDate() {
            return biginDate;
        }

        public void setBiginDate(String biginDate) {
            this.biginDate = biginDate;
        }

        public String getCcaaUrl() {
            return ccaaUrl;
        }

        public void setCcaaUrl(String ccaaUrl) {
            this.ccaaUrl = ccaaUrl;
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

        public String getComment_img() {
            return comment_img;
        }

        public void setComment_img(String comment_img) {
            this.comment_img = comment_img;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getExamDate() {
            return examDate;
        }

        public void setExamDate(String examDate) {
            this.examDate = examDate;
        }

        public String getExam_schedule() {
            return exam_schedule;
        }

        public void setExam_schedule(String exam_schedule) {
            this.exam_schedule = exam_schedule;
        }

        public String getGet_address() {
            return get_address;
        }

        public void setGet_address(String get_address) {
            this.get_address = get_address;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getQueryResultUrl() {
            return queryResultUrl;
        }

        public void setQueryResultUrl(String queryResultUrl) {
            this.queryResultUrl = queryResultUrl;
        }

        public String getReferral() {
            return referral;
        }

        public void setReferral(String referral) {
            this.referral = referral;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getSubject_type() {
            return subject_type;
        }

        public void setSubject_type(String subject_type) {
            this.subject_type = subject_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
    }
}
