package com.netease.nemo.openApi.dto.nim;

public class AppInfoDto {

    private  Long appid;

    private String appintro;

    private String appkey;

    private String appname;

    private String appsecret;

    private Integer apptype;

    private Long entid;

    private int enableflag;

    private String bits;


    public Long getAppid() {
        return appid;
    }

    public void setAppid(Long appid) {
        this.appid = appid;
    }

    public String getAppintro() {
        return appintro;
    }

    public void setAppintro(String appintro) {
        this.appintro = appintro;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public Integer getApptype() {
        return apptype;
    }

    public void setApptype(Integer apptype) {
        this.apptype = apptype;
    }

    public Long getEntid() {
        return entid;
    }

    public void setEntid(Long entid) {
        this.entid = entid;
    }

    public int getEnableflag() {
        return enableflag;
    }

    public void setEnableflag(int enableflag) {
        this.enableflag = enableflag;
    }

    public String getBits() {
        return bits;
    }

    public void setBits(String bits) {
        this.bits = bits;
    }
}
