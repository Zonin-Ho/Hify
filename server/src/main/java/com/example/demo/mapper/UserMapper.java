package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Update("update users set username = #{username} where id= #{id}")
    void updateUsername(String id, String username);

    @Update("update users set avatar_url = #{avatar_url} where id= #{id}")
    void updateAvatar(String id, String avatar_url);
}
