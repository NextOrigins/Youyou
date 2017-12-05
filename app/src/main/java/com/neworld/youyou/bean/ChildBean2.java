package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/16.
 */

public class ChildBean2 {


    /**
     * babyList : [{"babyId":1494,"updateDate":"2017-09-19 14:50:33.007","grade_class":"小班入园","districtName":"长宁区","intake_time":"2003- 9","id":475,"schoolName":"愚园路第一幼儿园（凯欣分园）","type":0,"createDate":"2017-09-19 14:50:33.007"},{"babyId":1494,"updateDate":"2017-09-19 14:50:33.007","grade_class":"小班入园","districtName":"浦东新区","intake_time":"2004- 9","id":487,"schoolName":"东方幼儿园东方梅园部","type":0,"createDate":"2017-09-19 14:50:33.007"}]
     * results : {"birthday":"2017-09-26","imgs":"http://106.14.251.200:8082/olopicture/baby/20655201709261622251506414145905.jpg","updateDate":"2017-09-26 16:22:25.915","sex":0,"cardID":null,"name":"你是","id":1494,"userId":20655,"createDate":"2017-09-22 14:18:59.569"}
     * status : 0
     */

    private ResultsBean results;
    private int status;
    private List<BabyListBean> babyList;

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

    public List<BabyListBean> getBabyList() {
        return babyList;
    }

    public void setBabyList(List<BabyListBean> babyList) {
        this.babyList = babyList;
    }

    public static class ResultsBean {
        /**
         * birthday : 2017-09-26
         * imgs : http://106.14.251.200:8082/olopicture/baby/20655201709261622251506414145905.jpg
         * updateDate : 2017-09-26 16:22:25.915
         * sex : 0
         * cardID : null
         * name : 你是
         * id : 1494
         * userId : 20655
         * createDate : 2017-09-22 14:18:59.569
         */

        private String birthday;
        private String imgs;
        private String updateDate;
        private int sex;
        private String cardID;
        private String name;
        private int id;
        private int userId;
        private String createDate;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getImgs() {
            return imgs;
        }

        public void setImgs(String imgs) {
            this.imgs = imgs;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getCardID() {
            return cardID;
        }

        public void setCardID(String cardID) {
            this.cardID = cardID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
    }

    public static class BabyListBean {
        /**
         * babyId : 1494
         * updateDate : 2017-09-19 14:50:33.007
         * grade_class : 小班入园
         * districtName : 长宁区
         * intake_time : 2003- 9
         * id : 475
         * schoolName : 愚园路第一幼儿园（凯欣分园）
         * type : 0
         * createDate : 2017-09-19 14:50:33.007
         */

        private int babyId;
        private String updateDate;
        private String grade_class;
        private String districtName;
        private String intake_time;
        private int id;
        private String schoolName;
        private int type;
        private String createDate;

        public int getBabyId() {
            return babyId;
        }

        public void setBabyId(int babyId) {
            this.babyId = babyId;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getGrade_class() {
            return grade_class;
        }

        public void setGrade_class(String grade_class) {
            this.grade_class = grade_class;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public String getIntake_time() {
            return intake_time;
        }

        public void setIntake_time(String intake_time) {
            this.intake_time = intake_time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
    }
}
