package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by asus on 2017/9/29.
 */

public class AddressBean {


    /**
     * menuList : [{"consignee":"测试","address":"测试题目","phone":"13345678904","id":32,"userId":21442,"status":1},{"consignee":"测试","address":"测试题目","phone":"13345678904","id":33,"userId":21442,"status":1},{"consignee":"测试","address":"测试题目","phone":"13345678904","id":34,"userId":21442,"status":1},{"consignee":"测试","address":"测试题目","phone":"13345678904","id":35,"userId":21442,"status":1},{"consignee":"测试","address":"测试题目","phone":"13345678904","id":36,"userId":21442,"status":0}]
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
         * consignee : 测试
         * address : 测试题目
         * phone : 1334567890432
         * id :
         * userId : 21442
         * status : 1
         */

        private String consignee;
        private String address;
        private String phone;
        private int id;
        private int userId;
        private int status;

        public String getConsignee() {
            return consignee;
        }

        public void setConsignee(String consignee) {
            this.consignee = consignee;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
