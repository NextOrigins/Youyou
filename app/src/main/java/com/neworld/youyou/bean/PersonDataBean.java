package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/7.
 */

public class PersonDataBean {


    /**
     * tokenStatus : 1
     * menuList : {"birthday":"","updateDate":"2017-09-28 11:45:18","sdasd":"","address":"","role":1,"faceImg":"http://www.uujz.me:8082/olopicture/icon/10001201709281113431506568423525.jpg","nickName":"123有何不可","openId":"otm2auDHdAYIqoPWMJfj76TtOwgY","mobileCheck":"","sex":0,"userPwd":"","remarkName":"","userName":"15821450047","cardNo":"","token":"f361e2a6-d7bf-4a82-8922-ef23eec12cd8","active_time":"2017-09-28 11:45:18","realName":"","phone":"","userAccount":"","id":10001,"createDate":"2017-08-20 11:05:58","status":0}
     * status : 0
     */

    private int tokenStatus;
    private MenuListBean menuList;
    private int status;

    public int getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(int tokenStatus) {
        this.tokenStatus = tokenStatus;
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
         * birthday :
         * updateDate : 2017-09-28 11:45:18
         * sdasd :
         * address :
         * role : 1
         * faceImg : http://www.uujz.me:8082/olopicture/icon/10001201709281113431506568423525.jpg
         * nickName : 123有何不可
         * openId : otm2auDHdAYIqoPWMJfj76TtOwgY
         * mobileCheck :
         * sex : 0
         * userPwd :
         * remarkName :
         * userName : 15821450047
         * cardNo :
         * token : f361e2a6-d7bf-4a82-8922-ef23eec12cd8
         * active_time : 2017-09-28 11:45:18
         * realName :
         * phone :
         * userAccount :
         * id : 10001
         * createDate : 2017-08-20 11:05:58
         * status : 0
         */

        private String birthday;
        private String updateDate;
        private String sdasd;
        private String address;
        private int role;
        private String faceImg;
        private String nickName;
        private String openId;
        private String mobileCheck;
        private int sex;
        private String userPwd;
        private String remarkName;
        private String userName;
        private String cardNo;
        private String token;
        private String active_time;
        private String realName;
        private String phone;
        private String userAccount;
        private int id;
        private String createDate;
        private int status;

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getSdasd() {
            return sdasd;
        }

        public void setSdasd(String sdasd) {
            this.sdasd = sdasd;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
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

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getMobileCheck() {
            return mobileCheck;
        }

        public void setMobileCheck(String mobileCheck) {
            this.mobileCheck = mobileCheck;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getUserPwd() {
            return userPwd;
        }

        public void setUserPwd(String userPwd) {
            this.userPwd = userPwd;
        }

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getActive_time() {
            return active_time;
        }

        public void setActive_time(String active_time) {
            this.active_time = active_time;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getUserAccount() {
            return userAccount;
        }

        public void setUserAccount(String userAccount) {
            this.userAccount = userAccount;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
