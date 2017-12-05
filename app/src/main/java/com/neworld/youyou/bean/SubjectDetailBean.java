package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/11.
 */

public class SubjectDetailBean {


    /**
     * collectStatus : 1
     * likeStatus : 1
     * list : [{"id":35,"subject_address":"待定，具体以准考证为准","subject_count":80,"subject_date":"2017年3月12日","subject_money":240,"type_name":"二年级"},{"id":36,"subject_address":"待定，具体以准考证为准","subject_count":80,"subject_date":"2017年3月12日","subject_money":240,"type_name":"三年级"},{"id":37,"subject_address":"待定，具体以准考证为准","subject_count":80,"subject_date":"2017年3月12日","subject_money":240,"type_name":"四年级"},{"id":38,"subject_address":"待定，具体以准考证为准","subject_count":80,"subject_date":"2017年3月12日","subject_money":240,"type_name":"五年级"}]
     * menuList : {"activityTitle":"优赛杯综合","address":"待定，具体以准考证为准","admissionUrl":"准考证下载暂时未开通，请敬请关注！","apply_count":341,"biginDate":"2017-01-22","ccaaUrl":"http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp","collect_count":0,"comment_count":0,"comment_img":"http://106.14.251.200:8082/olopicture/activity/icon/ysb123.jpg","count":0,"createDate":"2017-09-05 16:53:14","endDate":"2017-02-24","examDate":"2017年3月12日","exam_schedule":"考办下发","get_address":"考办下发","grade":"","icon":"","id":11,"like":0,"money":"240","queryResultUrl":"成绩查询暂时未开通，请敬请关注！","referral":"活动生","status":0,"subject_type":"35|36|37|38","title":"\u201c优赛杯\u201d上海市小学生综合素质测试","type":"1|2|3","updateDate":"2017-01-29 16:53:14"}
     * status : 0
     * userAdd : {}
     */

    private int collectStatus;
    private int likeStatus;
    private MenuListBean menuList;
    private int status;
    private UserAddBean userAdd;
    private List<ListBean> list;

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

    public UserAddBean getUserAdd() {
        return userAdd;
    }

    public void setUserAdd(UserAddBean userAdd) {
        this.userAdd = userAdd;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class MenuListBean {
        /**
         * activityTitle : 优赛杯综合
         * address : 待定，具体以准考证为准
         * admissionUrl : 准考证下载暂时未开通，请敬请关注！
         * apply_count : 341
         * biginDate : 2017-01-22
         * ccaaUrl : http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp
         * collect_count : 0
         * comment_count : 0
         * comment_img : http://106.14.251.200:8082/olopicture/activity/icon/ysb123.jpg
         * count : 0
         * createDate : 2017-09-05 16:53:14
         * endDate : 2017-02-24
         * examDate : 2017年3月12日
         * exam_schedule : 考办下发
         * get_address : 考办下发
         * grade :
         * icon :
         * id : 11
         * like : 0
         * money : 240
         * queryResultUrl : 成绩查询暂时未开通，请敬请关注！
         * referral : 活动生
         * status : 0
         * subject_type : 35|36|37|38
         * title : “优赛杯”上海市小学生综合素质测试
         * type : 1|2|3
         * updateDate : 2017-01-29 16:53:14
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

    public static class UserAddBean {
    }

    public static class ListBean {
        /**
         * id : 35
         * subject_address : 待定，具体以准考证为准
         * subject_count : 80
         * subject_date : 2017年3月12日
         * subject_money : 240.0
         * type_name : 二年级
         */

        private int id;
        private String subject_address;
        private int subject_count;
        private String subject_date;
        private double subject_money;
        private String type_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSubject_address() {
            return subject_address;
        }

        public void setSubject_address(String subject_address) {
            this.subject_address = subject_address;
        }

        public int getSubject_count() {
            return subject_count;
        }

        public void setSubject_count(int subject_count) {
            this.subject_count = subject_count;
        }

        public String getSubject_date() {
            return subject_date;
        }

        public void setSubject_date(String subject_date) {
            this.subject_date = subject_date;
        }

        public double getSubject_money() {
            return subject_money;
        }

        public void setSubject_money(double subject_money) {
            this.subject_money = subject_money;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }
    }
}
