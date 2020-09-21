package com.mfs.rmcore.po;

import java.io.Serializable;

public class Result implements Serializable {
    private int code;   //1 成功,2、业务失败，4、服务器异常，3、登录问题
    private String describe;
    private Object obj;

    public Result(int code, String describe, Object obj) {
        this.code = code;
        this.describe = describe;
        this.obj = obj;
    }

    public Result() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", describe='" + describe + '\'' +
                ", obj=" + obj +
                '}';
    }
}
