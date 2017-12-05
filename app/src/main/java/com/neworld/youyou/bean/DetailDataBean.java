package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/7/31.
 */

public class DetailDataBean {

    /**
     * menuList : {"sdasd":"给予嗯出错的没出息的哭的你","address":null,"faceImg":"http://106.14.251.200:8082/olopicture/icon/10848201707311631091501489869064.jpg","nickName":"彬彬囹圄汇源..","sex":1,"remarkName":"","id":10848}
     * os : 1
     * count : 1
     * status : 0
     */

    private MenuListBean menuList;
    private int os;
    private int count;
    private int status;

    public MenuListBean getMenuList() {
        return menuList;
    }

    public void setMenuList(MenuListBean menuList) {
        this.menuList = menuList;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class MenuListBean {
        /**
         * sdasd : 给予嗯出错的没出息的哭的你
         * address : null
         * faceImg : http://106.14.251.200:8082/olopicture/icon/10848201707311631091501489869064.jpg
         * nickName : 彬彬囹圄汇源..
         * sex : 1
         * remarkName :
         * id : 10848
         */

        private String sdasd;
        private Object address;
        private String faceImg;
        private String nickName;
        private int sex;
        private String remarkName;
        private int id;

        public String getSdasd() {
            return sdasd;
        }

        public void setSdasd(String sdasd) {
            this.sdasd = sdasd;
        }

        public Object getAddress() {
            return address;
        }

        public void setAddress(Object address) {
            this.address = address;
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

        public String getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(String remarkName) {
            this.remarkName = remarkName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
