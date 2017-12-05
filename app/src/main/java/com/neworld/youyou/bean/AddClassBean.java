package com.neworld.youyou.bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by tt on 2017/8/16.
 */

public class AddClassBean {

    /**
     * menuList : [{"class_name":"一班","id":1,"school_name":"1","type":"1"},{"class_name":"二班","id":2,"school_name":"1","type":"1"},{"class_name":"三班","id":3,"school_name":"1","type":"1"},{"class_name":"四班","id":4,"school_name":"1","type":"1"},{"class_name":"五班","id":5,"school_name":"1","type":"1"},{"class_name":"六班","id":6,"school_name":"1","type":"1"},{"class_name":"七班","id":7,"school_name":"1","type":"1"},{"class_name":"八班","id":8,"school_name":"1","type":"1"},{"class_name":"添加"}]
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

    public static class MenuListBean implements IPickerViewData {
        /**
         * class_name : 一班
         * id : 1
         * school_name : 1
         * type : 1
         */

        private String class_name;
        private int id;
        private String school_name;
        private String type;

        public String getClass_name() {
            return class_name;
        }

        public void setClass_name(String class_name) {
            this.class_name = class_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String getPickerViewText() {
            return class_name;
        }
    }
}
