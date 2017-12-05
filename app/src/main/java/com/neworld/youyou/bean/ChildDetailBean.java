package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/15.
 */

public class ChildDetailBean {

    /**
     * results : [{"birthday":"2005-08-11","createDate":"2017-08-11 15:39:42.981","id":864,"name":"DDs","sex":0,"updateDate":"2017-08-11 15:39:49.875","userId":10850,"userName":"13127767028"},{"birthday":"","createDate":"2017-08-15 15:58:24.715","id":894,"name":"shijingyan","sex":1,"updateDate":"2017-08-15 15:58:28.702","userId":10850,"userName":"13127767028"},{"birthday":"2013-08-15","createDate":"2017-08-15 16:07:43.776","id":898,"name":"Fgsd","sex":1,"updateDate":"2017-08-15 16:07:55.381","userId":10850,"userName":"13127767028"}]
     * status : 0
     */

    private int status;
    private List<ResultsBean> results;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * birthday : 2005-08-11
         * createDate : 2017-08-11 15:39:42.981
         * id : 864
         * name : DDs
         * sex : 0
         * updateDate : 2017-08-11 15:39:49.875
         * userId : 10850
         * userName : 13127767028
         */

        private String birthday;
        private String createDate;
        private int id;
        private String name;
        private int sex;
        private String updateDate;
        private int userId;
        private String userName;

        public int id2;

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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
