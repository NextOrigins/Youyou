package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/16.
 */

public class SubjectOrder {

    /**
     * list : {"id":96,"subject_address":"考试地址待定","subject_count":20,"subject_date":"2017.11.04","subject_money":40,"type_name":"一年级"}
     * menuList : {"activityTitle":"读写语文阅读","address":"考场地址待定","admissionUrl":"准考证下载暂时未开通，请敬请关注！","apply_count":0,"biginDate":"2017-08-01","ccaaUrl":"http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp","collect_count":2,"comment_count":0,"comment_img":"http://106.14.251.200:8082/olopicture/activity/icon/dxywyd123.jpg","count":200,"createDate":"2017-09-05 16:53:14","endDate":"2017-10-19","examDate":"2017.11.04","exam_schedule":"新贝分校领取","get_address":"考办下发","grade":"","icon":"","id":23,"like":1,"money":"40","queryResultUrl":"成绩查询暂时未开通，请敬请关注！","referral":"活动目","status":0,"subject_type":"96|97|98|99|100|101","title":"上海市\u201c读写新天地杯\u201d小学生语文现场阅读活动","type":2,"updateDate":"2017-08-1716:53:14"}
     * orderId : 1981
     * status : 0
     */

    private ListBean list;
    private MenuListBean menuList;
    private int orderId;
    private int status;

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public MenuListBean getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuListBean menuList) {
        this.menuList = menuList;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class ListBean {
        /**
         * id : 96
         * subject_address : 考试地址待定
         * subject_count : 20
         * subject_date : 2017.11.04
         * subject_money : 40.0
         * type_name : 一年级
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

    public static class MenuListBean {
        /**
         * activityTitle : 读写语文阅读
         * address : 考场地址待定
         * admissionUrl : 准考证下载暂时未开通，请敬请关注！
         * apply_count : 0
         * biginDate : 2017-08-01
         * ccaaUrl : http://192.168.1.123:8080/neworld/admin/jsp/neworld/Pre_exam.jsp
         * collect_count : 2
         * comment_count : 0
         * comment_img : http://106.14.251.200:8082/olopicture/activity/icon/dxywyd123.jpg
         * count : 200
         * createDate : 2017-09-05 16:53:14
         * endDate : 2017-10-19
         * examDate : 2017.11.04
         * exam_schedule : 新贝分校领取
         * get_address : 考办下发
         * grade :
         * icon :
         * id : 23
         * like : 1
         * money : 40
         * queryResultUrl : 成绩查询暂时未开通，请敬请关注！
         * referral : 活动目
         * status : 0
         * subject_type : 96|97|98|99|100|101
         * title : 上海市“读写新天地杯”小学生语文现场阅读活动
         * type : 2
         * updateDate : 2017-08-1716:53:14
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
        private int type;
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

        public int getType() {
            return type;
        }

        public void setType(int type) {
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
