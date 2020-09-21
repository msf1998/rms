package com.mfs.rmcore.mapper;

import com.mfs.rmcore.po.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int add(User user);
    int delete(User user);
    int update(User user);
    User queryById(Integer id);
    List<User> query(User user);
}
