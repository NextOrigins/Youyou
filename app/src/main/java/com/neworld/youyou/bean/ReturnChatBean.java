package com.neworld.youyou.bean;

/**
 * Created by tt on 2017/8/29.
 */

public class ReturnChatBean {

    /**
     * appid : wxd7cf604bab22c904
     * noncestr : XbeXoqL5wAZzMgdH
     * prepayid : wx201708291408001f6c5449f20839931141
     * sign : AF8D892FC43D18A5E04B69F21A886C5F
     * status : 0
     * timeStamp : 1503986883
     */

    private String appid;
    private String noncestr;
    private String prepayid;
    private String sign;
    private int status;
    private String timeStamp;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
