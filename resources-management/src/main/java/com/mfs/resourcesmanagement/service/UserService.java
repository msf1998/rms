package com.mfs.resourcesmanagement.service;

import com.mfs.resourcesmanagement.dao.FileDao;
import com.mfs.resourcesmanagement.mapper.UserMapper;
import com.mfs.resourcesmanagement.po.Result;
import com.mfs.resourcesmanagement.po.User;
import com.mfs.resourcesmanagement.utill.CryptUtil;
import com.mfs.resourcesmanagement.utill.SaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    FileDao fileDao;

    //登录
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result login(HttpServletRequest request, HttpServletResponse response, User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username != null && password != null && user.isValidate()) {
            List<User> list = userMapper.query(user.setPassword(null));
            if (!list.isEmpty()) {
                user = list.get(0);
                if (user.getPassword().equals(CryptUtil.getMessageDigestByMD5(password + "" + user.getSalt()))) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user",user);
                    Cookie cookie = new Cookie("JSESSIONID",session.getId());
                    response.addCookie(cookie);
                    return new Result(1,"登录成功",null);
                }
                return new Result(2,"密码错误",null);
            }
            return new Result(2,"用户不存在",null);
        }
        return new Result(2,"用户名或密码不合法",null);
    }
    //注册
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result register(User user) throws FileAlreadyExistsException {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username != null && password != null && user.isValidate()) {
            List<User> list = userMapper.query(user.setPassword(null));
            if (list.isEmpty()) {
                String s = SaltGenerator.generatorSalt();
                user.setPassword(CryptUtil.getMessageDigestByMD5(password + "" + s)).setSalt(s).setCreateTime(new Date());
                int add = userMapper.add(user);
                if (add == 1) {
                    boolean directory = fileDao.createDirectory("/", username);
                    if (directory) {
                        return new Result(1,"注册成功",null);
                    }
                    throw new FileAlreadyExistsException("创建文件失败");
                }
                return new Result(2,"注册失败",null);
            }
            return new Result(2,"该用户名已被注册",null);
        }
        return new Result(2,"用户名或密码不合法",null);
    }
    //检查存在性
    //@Transactional(isolation = Isolation.READ_COMMITTED)
    public Result isExists(User user) {
        List<User> list = userMapper.query(user);
        if (list.isEmpty()) {
            return new Result(1,"",null);
        }
        return new Result(2,"用户已名存在",null);
    }
}
