package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/16.
 */

public class ChildBean {

    /**
     * babyList : [{"babyId":1015,"createDate":"2017-08-16 14:58:52.796","districtName":"普陀区","grade_class":"小班入园","id":110,"intake_time":"2005- 9","schoolName":"实验幼儿园实验幼儿园","type":0,"updateDate":"2017-08-16 14:58:52.796"},{"babyId":1015,"createDate":"2017-08-16 14:58:52.796","districtName":"普陀区","grade_class":"10班","id":111,"intake_time":"2005- 9","schoolName":"金洲小学","type":1,"updateDate":"2017-08-16 14:58:52.796"},{"babyId":1015,"createDate":"2017-08-16 14:58:52.796","districtName":"普陀区","grade_class":"101班","id":112,"intake_time":"2005- 9","schoolName":"玉华中学","type":2,"updateDate":"2017-08-16 14:58:52.796"}]
     * results : {"birthday":"2013-08-16","createDate":"2017-08-16 15:23:46.401","id":1015,"name":"1234","sex":1,"updateDate":"2017-08-16 15:23:50.155","userId":10850}
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
         * birthday : 2013-08-16
         * createDate : 2017-08-16 15:23:46.401
         * id : 1015
         * name : 1234
         * sex : 1
         * updateDate : 2017-08-16 15:23:50.155
         * userId : 10850
         */

        private String birthday;
        private String createDate;
        private int id;
        private String name;
        private int sex;
        private String updateDate;
        private int userId;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    public static class BabyListBean {
        /**
         * babyId : 1015
         * createDate : 2017-08-16 14:58:52.796
         * districtName : 普陀区
         * grade_class : 小班入园
         * id : 110
         * intake_time : 2005- 9
         * schoolName : 实验幼儿园实验幼儿园
         * type : 0
         * updateDate : 2017-08-16 14:58:52.796
         */

        private int babyId;
        private String createDate;
        private String districtName;
        private String grade_class;
        private int id;
        private String intake_time;
        private String schoolName;
        private int type;
        private String updateDate;

        public int getBabyId() {
            return babyId;
        }

        public void setBabyId(int babyId) {
            this.babyId = babyId;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public String getGrade_class() {
            return grade_class;
        }

        public void setGrade_class(String grade_class) {
            this.grade_class = grade_class;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIntake_time() {
            return intake_time;
        }

        public void setIntake_time(String intake_time) {
            this.intake_time = intake_time;
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

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
    }
}
