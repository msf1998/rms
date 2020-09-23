package com.mfs.rmcommon.po;

import java.io.*;

public class Response implements Serializable {
    /** 200成功，500异常*/
    private int code;
    /** 描述*/
    private String describe;

    private String content;

    private File temFile;


    public Response() {
    }

    public Response(int code, String describe, String content) {
        this.code = code;
        this.describe = describe;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public String getDescribe() {
        return describe;
    }

    public Response setDescribe(String describe) {
        this.describe = describe;
        return this;
    }

    public InputStream inputStream() {
        try {
            return new FileInputStream(temFile);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public String getContent() {
        return content;
    }

    public Response setContent(String content) {
        this.content = content;
        return this;
    }

    public Response setTemFile(File temFile) {
        this.temFile = temFile;
        return this;
    }

    public OutputStream outputStream() {
        try {
            return new FileOutputStream(temFile);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public void close() {
        if (temFile != null) {
            temFile.delete();
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", describe='" + describe + '\'' +
                ", content='" + content + '\'' +
                ", temFile=" + temFile +
                '}';
    }

}
