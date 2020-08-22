package com.mfs.resourcesmanagement.po;

import java.util.Date;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private Date createTime;

    public User(Integer id, String username, String password, String salt, Date createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.createTime = createTime;
    }
    public User(){}

    public boolean isValidate(){
        if (username != null) {
          int n = username.length();
          for (int i = 0; i < n; i ++) {
              char ch = username.charAt(i);
              if (!(Character.isDigit(ch) || Character.isLetter(ch))) {
                  return false;
              }
          }
        }
        return true;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public User setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
