package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/9/7.
 */

public class VersionBean {

    /**
     * results : {"msg":"检测到新版本，是否升级！","versionNum":null,"id":1,"versionName":"1.0.2","versionCode":"1.0.0","status":0,"out":1}
     * status : 0
     */

    private ResultsBean results;
    private int status;

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

    public static class ResultsBean {
        /**
         * msg : 检测到新版本，是否升级！
         * versionNum : null
         * id : 1
         * versionName : 1.0.2
         * versionCode : 1.0.0
         * status : 0
         * out : 1
         */

        private String msg;
        private int versionNum;
        private int id;
        private String versionName;
        private int versionCode;
        private int status;
        private int out;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getVersionNum() {
            return versionNum;
        }

        public void setVersionNum(int versionNum) {
            this.versionNum = versionNum;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getOut() {
            return out;
        }

        public void setOut(int out) {
            this.out = out;
        }
    }
}

/*    // json {"url":"http://192.168.205.33:8080/Hello/app_v3.0.1_Other_20150116.apk","versionCode":2,"updateMessage":"版本更新信息"}

    static final String APK_DOWNLOAD_URL = "url";
    static final String APK_UPDATE_CONTENT = "updateMessage";
    static final String APK_VERSION_CODE = "versionCode";


    static final int TYPE_NOTIFICATION = 2;

    static final int TYPE_DIALOG = 1;

    static final String TAG = "UpdateChecker";

    static final String UPDATE_URL = "https://raw.githubusercontent.com/feicien/android-auto-update/develop/extras/update.json";*/