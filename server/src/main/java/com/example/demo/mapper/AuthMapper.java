package com.example.demo.mapper;

import com.example.demo.entity.LoginLog;
import com.example.demo.entity.User;
import com.example.demo.vo.LoginLogVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthMapper {
    @Select("select * from users where id = #{id}")
    User getUserById(String id);

    @Select("select * from users where email = #{email}")
    User getUserByEmail(String email);

    @Insert("insert into users (username, email, password_hash) values (#{email}, #{email}, #{passwordHash})")
    void insertUser(User user);

    @Insert("insert into login_logs (user_id, ip_address, user_agent) VALUES (#{userId},#{ipAddress},#{userAgent})")
    int saveLoginLog(LoginLog loginLog);

    @Select("select ip_address, user_agent, login_at from login_logs where user_id = #{userId}")
    List<LoginLogVO> getIpAddress(String userId);
}
