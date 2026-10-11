package com.llh.mapper;

import com.llh.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     */
    void insert(User user);

    /**
     * 根据创建时间，统计用户数量
     */
    Integer countByMap(Map<String, LocalDateTime> map);
}
