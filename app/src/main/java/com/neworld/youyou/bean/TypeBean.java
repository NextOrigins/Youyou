package com.neworld.youyou.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by tt on 2017/8/18.
 */

public class TypeBean  {

    /**
     * menuList : [{"typeName":"幼升小","id":1,"typeImg":"http://106.14.251.200:8082/olopicture/icon/y1.jpg"},{"typeName":"小升初","id":2,"typeImg":"http://106.14.251.200:8082/olopicture/icon/x1.jpg"},{"typeName":"中考","id":3,"typeImg":"http://106.14.251.200:8082/olopicture/icon/z1.jpg"},{"typeName":"育儿","id":4,"typeImg":"http://106.14.251.200:8082/olopicture/icon/r1.jpg"},{"typeName":"婚后夫妻","id":5,"typeImg":"http://106.14.251.200:8082/olopicture/icon/f1.jpg"},{"typeName":"婆媳关系","id":6,"typeImg":"http://106.14.251.200:8082/olopicture/icon/p1.jpg"},{"typeName":"美食厨房","id":7,"typeImg":"http://106.14.251.200:8082/olopicture/icon/m1.jpg"},{"typeName":"旅行随拍","id":8,"typeImg":"http://106.14.251.200:8082/olopicture/icon/n1.jpg"},{"typeName":"创意手工","id":9,"typeImg":"http://106.14.251.200:8082/olopicture/icon/s1.jpg"},{"typeName":"七嘴八舌","id":10,"typeImg":"http://106.14.251.200:8082/olopicture/icon/q1.jpg"}]
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

    public static class MenuListBean implements IPickerViewData{
        /**
         * typeName : 幼升小
         * id : 1
         * typeImg : http://106.14.251.200:8082/olopicture/icon/y1.jpg
         */

        private String typeName;
        private String id;
        private String typeImg;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTypeImg() {
            return typeImg;
        }

        public void setTypeImg(String typeImg) {
            this.typeImg = typeImg;
        }

        @Override
        public String getPickerViewText() {
            return typeName;
        }
    }
}
