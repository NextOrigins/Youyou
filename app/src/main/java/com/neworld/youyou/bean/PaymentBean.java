package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/16.
 */

public class PaymentBean {

    /**
     * menuList : {"date":null,"msg":null,"comment_count":0,"queryResultUrl":"成绩查询暂时未开通，请敬请关注！","updateDate":"2017-07-28 16:53:14","type_name":"五年级","orderId":2189,"endDate":"2017-10-19","comment_img":"http://106.14.251.200:8082/olopicture/activity/icon/dxywyd123.jpg","examDate":"2017年11月4日","icon":"","type":"2","title":"上海市\u201c读写新天地杯\u201d小学生语文现场阅读活动","subject_address":"武夷路238号新长宁教育培训中心\r\n（待定，具体以准考证为准）","addressId":null,"activityTitle":"读写语文阅读","examinee_name":null,"school":null,"outTradeNo":null,"exam_schedule":"新贝分校领取","id":23,"createDate":"2017-09-05 16:53:14","subject_date":"2017年11月4日","address":"武夷路238号新长宁教育培训中心\r\n（待定，具体以准考证为准）","subject_type":"96|97|98|99|100|101","like":2,"count":0,"admissionUrl":"准考证下载暂时未开通，请敬请关注！","get_address":"考办下发","userId":20655,"collect_count":3,"bookId":1,"apply_count":240,"referral":"上海市\u201c读写新天地杯\u201d小学生现场语文系列活动分为阅读和写作两项，上半年是写作活动，下半年是阅读活动。\r\n\r\n活动目的：\r\n\u201c读写新天地杯\u201d阅读活动根据课程标准，紧贴现行教材，并参照《上海市中小学生学业质量绿色指标》，约请小学语文界专家、各区语文学科带头教师及骨干教师，担任1-5年级的命题及评卷工作。为体现活动的公开、公平和公正，全部命题由小学语文界权威专家审核。主要形式为百分之五十为课内阅读理解综合训练题，百分之五十为课外阅读综合拓展训练题。\r\n\r\n参加对象：1\u20145年级小学生。\r\n\r\n活动奖项：按年级分组出题评选，分别设一、二、三等奖和鼓励奖，由主办单位《读写新天地》编委会向获奖学生颁发证书，获一﹑二、三等奖学生老师也将颁发优秀指导奖证书。","ccaaUrl":"http://106.14.251.200:8082/neworld/admin/jsp/neworld/Pre_exam.jsp","money":"40","phone":null,"grade":"","biginDate":"2017-08-01","typeId":100,"payStatus":0,"status":0}
     * results : {"updateDate":null,"author":"新贝","iconImg":"http://106.14.251.200:8082/olopicture/icon/10855201708202328131503242893268.jpg","publishDate":null,"constPrice":null,"suggest":null,"title":null,"type":null,"bookName":"小升初白皮书","expressFee":10,"userId":null,"contentImg":null,"likeSum":null,"collectSum":null,"price":150,"id":1,"createDate":null,"status":1}
     * status : 0
     */

    private MenuListBean menuList;
    private ResultsBean results;
    private int status;

    public MenuListBean getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuListBean menuList) {
        this.menuList = menuList;
    }

    public ResultsBean getResults() {
        return results;
    }

    public void setResults(ResultsBean results) {
        this.results = results;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class MenuListBean {
        /**
         * date : null
         * msg : null
         * comment_count : 0
         * queryResultUrl : 成绩查询暂时未开通，请敬请关注！
         * updateDate : 2017-07-28 16:53:14
         * type_name : 五年级
         * orderId : 2189
         * endDate : 2017-10-19
         * comment_img : http://106.14.251.200:8082/olopicture/activity/icon/dxywyd123.jpg
         * examDate : 2017年11月4日
         * icon :
         * type : 2
         * title : 上海市“读写新天地杯”小学生语文现场阅读活动
         * subject_address : 武夷路238号新长宁教育培训中心
         （待定，具体以准考证为准）
         * addressId : null
         * activityTitle : 读写语文阅读
         * examinee_name : null
         * school : null
         * outTradeNo : null
         * exam_schedule : 新贝分校领取
         * id : 23
         * createDate : 2017-09-05 16:53:14
         * subject_date : 2017年11月4日
         * address : 武夷路238号新长宁教育培训中心
         （待定，具体以准考证为准）
         * subject_type : 96|97|98|99|100|101
         * like : 2
         * count : 0
         * admissionUrl : 准考证下载暂时未开通，请敬请关注！
         * get_address : 考办下发
         * userId : 20655
         * collect_count : 3
         * bookId : 1
         * apply_count : 240
         * referral : 上海市“读写新天地杯”小学生现场语文系列活动分为阅读和写作两项，上半年是写作活动，下半年是阅读活动。

         活动目的：
         “读写新天地杯”阅读活动根据课程标准，紧贴现行教材，并参照《上海市中小学生学业质量绿色指标》，约请小学语文界专家、各区语文学科带头教师及骨干教师，担任1-5年级的命题及评卷工作。为体现活动的公开、公平和公正，全部命题由小学语文界权威专家审核。主要形式为百分之五十为课内阅读理解综合训练题，百分之五十为课外阅读综合拓展训练题。

         参加对象：1—5年级小学生。

         活动奖项：按年级分组出题评选，分别设一、二、三等奖和鼓励奖，由主办单位《读写新天地》编委会向获奖学生颁发证书，获一﹑二、三等奖学生老师也将颁发优秀指导奖证书。
         * ccaaUrl : http://106.14.251.200:8082/neworld/admin/jsp/neworld/Pre_exam.jsp
         * money : 40
         * phone : null
         * grade :
         * biginDate : 2017-08-01
         * typeId : 100
         * payStatus : 0
         * status : 0
         */

        private Object date;
        private Object msg;
        private int comment_count;
        private String queryResultUrl;
        private String updateDate;
        private String type_name;
        private int orderId;
        private String endDate;
        private String comment_img;
        private String examDate;
        private String icon;
        private String type;
        private String title;
        private String subject_address;
        private Object addressId;
        private String activityTitle;
        private Object examinee_name;
        private Object school;
        private Object outTradeNo;
        private String exam_schedule;
        private int id;
        private String createDate;
        private String subject_date;
        private String address;
        private String subject_type;
        private int like;
        private int count;
        private String admissionUrl;
        private String get_address;
        private int userId;
        private int collect_count;
        private int bookId;
        private int apply_count;
        private String referral;
        private String ccaaUrl;
        private double money;
        private Object phone;
        private String grade;
        private String biginDate;
        private int typeId;
        private int payStatus;
        private int status;

        public Object getDate() {
            return date;
        }

        public void setDate(Object date) {
            this.date = date;
        }

        public Object getMsg() {
            return msg;
        }

        public void setMsg(Object msg) {
            this.msg = msg;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public String getQueryResultUrl() {
            return queryResultUrl;
        }

        public void setQueryResultUrl(String queryResultUrl) {
            this.queryResultUrl = queryResultUrl;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getComment_img() {
            return comment_img;
        }

        public void setComment_img(String comment_img) {
            this.comment_img = comment_img;
        }

        public String getExamDate() {
            return examDate;
        }

        public void setExamDate(String examDate) {
            this.examDate = examDate;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubject_address() {
            return subject_address;
        }

        public void setSubject_address(String subject_address) {
            this.subject_address = subject_address;
        }

        public Object getAddressId() {
            return addressId;
        }

        public void setAddressId(Object addressId) {
            this.addressId = addressId;
        }

        public String getActivityTitle() {
            return activityTitle;
        }

        public void setActivityTitle(String activityTitle) {
            this.activityTitle = activityTitle;
        }

        public Object getExaminee_name() {
            return examinee_name;
        }

        public void setExaminee_name(Object examinee_name) {
            this.examinee_name = examinee_name;
        }

        public Object getSchool() {
            return school;
        }

        public void setSchool(Object school) {
            this.school = school;
        }

        public Object getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(Object outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public String getExam_schedule() {
            return exam_schedule;
        }

        public void setExam_schedule(String exam_schedule) {
            this.exam_schedule = exam_schedule;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getSubject_date() {
            return subject_date;
        }

        public void setSubject_date(String subject_date) {
            this.subject_date = subject_date;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSubject_type() {
            return subject_type;
        }

        public void setSubject_type(String subject_type) {
            this.subject_type = subject_type;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getAdmissionUrl() {
            return admissionUrl;
        }

        public void setAdmissionUrl(String admissionUrl) {
            this.admissionUrl = admissionUrl;
        }

        public String getGet_address() {
            return get_address;
        }

        public void setGet_address(String get_address) {
            this.get_address = get_address;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getCollect_count() {
            return collect_count;
        }

        public void setCollect_count(int collect_count) {
            this.collect_count = collect_count;
        }

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public int getApply_count() {
            return apply_count;
        }

        public void setApply_count(int apply_count) {
            this.apply_count = apply_count;
        }

        public String getReferral() {
            return referral;
        }

        public void setReferral(String referral) {
            this.referral = referral;
        }

        public String getCcaaUrl() {
            return ccaaUrl;
        }

        public void setCcaaUrl(String ccaaUrl) {
            this.ccaaUrl = ccaaUrl;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money;
        }

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getBiginDate() {
            return biginDate;
        }

        public void setBiginDate(String biginDate) {
            this.biginDate = biginDate;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class ResultsBean {
        /**
         * updateDate : null
         * author : 新贝
         * iconImg : http://106.14.251.200:8082/olopicture/icon/10855201708202328131503242893268.jpg
         * publishDate : null
         * constPrice : null
         * suggest : null
         * title : null
         * type : null
         * bookName : 小升初白皮书
         * expressFee : 10
         * userId : null
         * contentImg : null
         * likeSum : null
         * collectSum : null
         * price : 150.0
         * id : 1
         * createDate : null
         * status : 1
         */

        private Object updateDate;
        private String author;
        private String iconImg;
        private Object publishDate;
        private Object constPrice;
        private Object suggest;
        private Object title;
        private Object type;
        private String bookName;
        private int expressFee;
        private Object userId;
        private Object contentImg;
        private Object likeSum;
        private Object collectSum;
        private double price;
        private int id;
        private Object createDate;
        private int status;

        public Object getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(Object updateDate) {
            this.updateDate = updateDate;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getIconImg() {
            return iconImg;
        }

        public void setIconImg(String iconImg) {
            this.iconImg = iconImg;
        }

        public Object getPublishDate() {
            return publishDate;
        }

        public void setPublishDate(Object publishDate) {
            this.publishDate = publishDate;
        }

        public Object getConstPrice() {
            return constPrice;
        }

        public void setConstPrice(Object constPrice) {
            this.constPrice = constPrice;
        }

        public Object getSuggest() {
            return suggest;
        }

        public void setSuggest(Object suggest) {
            this.suggest = suggest;
        }

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public int getExpressFee() {
            return expressFee;
        }

        public void setExpressFee(int expressFee) {
            this.expressFee = expressFee;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public Object getContentImg() {
            return contentImg;
        }

        public void setContentImg(Object contentImg) {
            this.contentImg = contentImg;
        }

        public Object getLikeSum() {
            return likeSum;
        }

        public void setLikeSum(Object likeSum) {
            this.likeSum = likeSum;
        }

        public Object getCollectSum() {
            return collectSum;
        }

        public void setCollectSum(Object collectSum) {
            this.collectSum = collectSum;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Object createDate) {
            this.createDate = createDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
