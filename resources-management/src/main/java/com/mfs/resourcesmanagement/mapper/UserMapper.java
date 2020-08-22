package com.mfs.resourcesmanagement.mapper;

import com.mfs.resourcesmanagement.po.User;
import org.apache.ibatis.annotations.Mapper;

import javax.jws.soap.SOAPBinding;
import java.net.UnknownServiceException;
import java.util.List;

@Mapper
public interface UserMapper {
    int add(User user);
    int delete(User user);
    int update(User user);
    User queryById(Integer id);
    List<User> query(User user);
}
