package com.mfs.rmweb.serviceImpl;

import com.mfs.rmcore.dao.FileDao;
import com.mfs.rmcore.mapper.UserMapper;
import com.mfs.rmcore.po.Result;
import com.mfs.rmcore.po.User;
import com.mfs.rmcore.utill.CryptUtil;
import com.mfs.rmcore.utill.SaltGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    FileDao fileDao;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    //登录
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Result login(HttpServletRequest request, HttpServletResponse response, User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username != null && password != null && user.isValidate()) {
            List<User> list = new ArrayList<>();
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            if (redisTemplate.hasKey(username)) {
                list.add(new User((Map)valueOperations.get(username)));
            } else {
                list = userMapper.query(user.setPassword(null));
            }
            if (!list.isEmpty()) {
                user = list.get(0);
                if (user.getPassword().equals(CryptUtil.getMessageDigestByMD5(password + "" + user.getSalt()))) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user",user);
                    Cookie cookie = new Cookie("JSESSIONID",session.getId());
                    response.addCookie(cookie);
                    valueOperations.setIfAbsent(username,user);
                    redisTemplate.expire(user.getUsername(),1, TimeUnit.DAYS);
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
                        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                        valueOperations.set(user.getUsername(),user);
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
