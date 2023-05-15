package com.grace.frame.util;

import java.io.Serializable;

public class StdUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ryid;// 人员id
    private String xm;// 姓名
    private String xmpy;// 姓名拼音
    private String xb;// 性别
    private String yxzjlx;// 有效证件类型
    private String yxzjhm;// 有效证件号码
    private Integer nl;// 年龄
    private String lxdh;// 联系电话
    private String sjhm;// 手机号码
    private DataMap allInfoDM;// 考生所有信息

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRyid() {
        return ryid;
    }

    public void setRyid(String ryid) {
        this.ryid = ryid;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getXmpy() {
        return xmpy;
    }

    public void setXmpy(String xmpy) {
        this.xmpy = xmpy;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getYxzjlx() {
        return yxzjlx;
    }

    public void setYxzjlx(String yxzjlx) {
        this.yxzjlx = yxzjlx;
    }

    public String getYxzjhm() {
        return yxzjhm;
    }

    public void setYxzjhm(String yxzjhm) {
        this.yxzjhm = yxzjhm;
    }

    public Integer getNl() {
        return nl;
    }

    public void setNl(Integer nl) {
        this.nl = nl;
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getSjhm() {
        return sjhm;
    }

    public void setSjhm(String sjhm) {
        this.sjhm = sjhm;
    }

    public DataMap getAllInfoDM() {
        return allInfoDM;
    }

    public void setAllInfoDM(DataMap allInfoDM) {
        this.allInfoDM = allInfoDM;
    }

    @Override
    public String toString() {
        return "StdUser{" +
                "ryid='" + ryid + '\'' +
                ", xm='" + xm + '\'' +
                ", xmpy='" + xmpy + '\'' +
                ", xb='" + xb + '\'' +
                ", yxzjlx='" + yxzjlx + '\'' +
                ", yxzjhm='" + yxzjhm + '\'' +
                ", nl=" + nl +
                ", lxdh='" + lxdh + '\'' +
                ", sjhm='" + sjhm + '\'' +
                ", allInfoDM=" + allInfoDM +
                '}';
    }

}
