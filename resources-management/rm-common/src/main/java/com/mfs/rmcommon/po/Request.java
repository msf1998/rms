package com.mfs.rmcommon.po;

import java.io.*;

public class Request implements Serializable {
    /** 请求的类型*/
    private RequestType type;
    /** 路径，相对用户根目录的相对路径*/
    private String path;
    /** 文件名*/
    private String name;

    private String content;

    /** 上传文件时使用*/
    private File temFile;

    public Request() {
    }

    public Request(RequestType type, String path, String name, String content) {
        this.type = type;
        this.path = path;
        this.name = name;
        this.content = content;
    }

    public RequestType getType() {
        return type;
    }

    public Request setType(RequestType type) {
        this.type = type;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Request setPath(String path) {
        this.path = path;
        return this;
    }

    public String getName() {
        return name;
    }

    public Request setName(String name) {
        this.name = name;
        return this;
    }

    public InputStream inputStream() {
        try {
            return new FileInputStream(temFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Request setTemFile(File temFile) {
        this.temFile = temFile;
        return this;
    }

    public OutputStream outputStream() {
        try {
            return new FileOutputStream(temFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getContent() {
        return content;
    }

    public Request setContent(String content) {
        this.content = content;
        return this;
    }

    public void close() {
        if (temFile != null) {
            temFile.delete();
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", temFile=" + temFile +
                '}';
    }

}
