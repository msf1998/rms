package com.mfs.resourcesmanagement.controller;

import com.mfs.resourcesmanagement.po.Result;
import com.mfs.resourcesmanagement.po.User;
import com.mfs.resourcesmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public Result login(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {
        try {
            System.out.print(user.getUsername());
            return userService.login(request,response,user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }

    @RequestMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            return userService.register(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }

    @RequestMapping("/check")
    public Result isExists(@RequestBody User user) {
        try {
            return userService.isExists(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }

}
