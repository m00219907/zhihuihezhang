package com.zhihuihezhang.zj.jiaxing.net;


public class BaseBean {
    /**
     * 请求结果状态码
     * 1：成功
     * 0：失败
     */
    protected String success;
    /**
     * 成功或错误描述
     */
    protected String des;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
