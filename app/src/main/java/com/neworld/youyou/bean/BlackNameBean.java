package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/9.
 */

public class BlackNameBean {

    /**
     * menuList : [{"sdasd":null,"taruId":10856,"faceImg":"http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg","nickName":"Zeldar","sex":1},{"sdasd":"给予嗯出错的没出息的哭的你","taruId":10848,"faceImg":"http://106.14.251.200:8082/olopicture/icon/10848201708071143501502077430959.jpg","nickName":"彬彬囹圄汇源..","sex":1}]
     * status : 0
     */

    private int status;
    private List<MenuListBean> menuList;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MenuListBean> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuListBean> menuList) {
        this.menuList = menuList;
    }

    public static class MenuListBean {
        /**
         * sdasd : null
         * taruId : 10856
         * faceImg : http://106.14.251.200:8082/olopicture/icon/10856201708010924581501550698895.jpg
         * nickName : Zeldar
         * sex : 1
         */

        private Object sdasd;
        private int taruId;
        private String faceImg;
        private String nickName;
        private int sex;

        public Object getSdasd() {
            return sdasd;
        }

        public void setSdasd(Object sdasd) {
            this.sdasd = sdasd;
        }

        public int getTaruId() {
            return taruId;
        }

        public void setTaruId(int taruId) {
            this.taruId = taruId;
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
    }
}
