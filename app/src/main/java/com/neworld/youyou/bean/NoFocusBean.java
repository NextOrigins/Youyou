package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by tt on 2017/8/7.
 */

public class NoFocusBean {

    /**
     * menuList : [{"spotId":851,"typeId":1,"typeImg":"http://106.14.251.200:8082/olopicture/icon/y1.jpg","typeName":"幼升小"}]
     * notCircleList : [{"namicInfoBean":{"content":"ii","createDate":"2017-08-04 14:02:48","title":"pp"},"typeId":2,"typeImg":"http://106.14.251.200:8082/olopicture/icon/x1.jpg","typeName":"小升初"},{"namicInfoBean":{"content":"听说作业还少，而且还不补课，这个是真的吗？有知道具体情况的旁友们么？","createDate":"2017-08-01 09:40:42","title":"延安初中九年2班一半学生被四校录取？"},"typeId":3,"typeImg":"http://106.14.251.200:8082/olopicture/icon/z1.jpg","typeName":"中考"},{"namicInfoBean":{"content":"孩子享用的美食图片.....","createDate":"2017-07-24 16:33:52","title":"我的午饭美食图片"},"typeId":4,"typeImg":"http://106.14.251.200:8082/olopicture/icon/r1.jpg","typeName":"育儿"},{"typeId":5,"typeImg":"http://106.14.251.200:8082/olopicture/icon/f1.jpg","typeName":"婚后夫妻"},{"typeId":6,"typeImg":"http://106.14.251.200:8082/olopicture/icon/p1.jpg","typeName":"婆媳关系"},{"namicInfoBean":{"content":"吃吃喝喝开心一夏","createDate":"2017-08-04 17:36:35","title":"消暑一下"},"typeId":7,"typeImg":"http://106.14.251.200:8082/olopicture/icon/m1.jpg","typeName":"美食厨房"},{"typeId":8,"typeImg":"http://106.14.251.200:8082/olopicture/icon/n1.jpg","typeName":"旅行随拍"},{"typeId":9,"typeImg":"http://106.14.251.200:8082/olopicture/icon/s1.jpg","typeName":"创意手工"},{"typeId":10,"typeImg":"http://106.14.251.200:8082/olopicture/icon/q1.jpg","typeName":"七嘴八舌"}]
     * status : 0
     */

    private int status;
    private List<MenuListBean> menuList;
    private List<NotCircleListBean> notCircleList;

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

    public List<NotCircleListBean> getNotCircleList() {
        return notCircleList;
    }

    public void setNotCircleList(List<NotCircleListBean> notCircleList) {
        this.notCircleList = notCircleList;
    }

    public static class MenuListBean {
        /**
         * spotId : 851
         * typeId : 1
         * typeImg : http://106.14.251.200:8082/olopicture/icon/y1.jpg
         * typeName : 幼升小
         */

        private int spotId;
        private int typeId;
        private String typeImg;
        private String typeName;

        public int getSpotId() {
            return spotId;
        }

        public void setSpotId(int spotId) {
            this.spotId = spotId;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getTypeImg() {
            return typeImg;
        }

        public void setTypeImg(String typeImg) {
            this.typeImg = typeImg;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }

    public static class NotCircleListBean {
        /**
         * namicInfoBean : {"content":"ii","createDate":"2017-08-04 14:02:48","title":"pp"}
         * typeId : 2
         * typeImg : http://106.14.251.200:8082/olopicture/icon/x1.jpg
         * typeName : 小升初
         */

        private NamicInfoBeanBean namicInfoBean;
        private int typeId;
        private String typeImg;
        private String typeName;

        public NamicInfoBeanBean getNamicInfoBean() {
            return namicInfoBean;
        }

        public void setNamicInfoBean(NamicInfoBeanBean namicInfoBean) {
            this.namicInfoBean = namicInfoBean;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getTypeImg() {
            return typeImg;
        }

        public void setTypeImg(String typeImg) {
            this.typeImg = typeImg;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public static class NamicInfoBeanBean {
            /**
             * content : ii
             * createDate : 2017-08-04 14:02:48
             * title : pp
             */

            private String content;
            private String createDate;
            private String title;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
