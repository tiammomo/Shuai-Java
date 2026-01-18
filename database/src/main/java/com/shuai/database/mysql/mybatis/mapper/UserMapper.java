package com.shuai.database.mysql.mybatis.mapper;

import com.shuai.database.mysql.mybatis.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户 Mapper 接口（注解方式）
 */
public interface UserMapper {

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM users ORDER BY id")
    List<User> findAll();

    /**
     * 根据 ID 查询用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 插入用户（返回自增 ID）
     */
    @Insert("INSERT INTO users (username, email, password, status) VALUES (#{username}, #{email}, #{password}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户
     */
    @Update("UPDATE users SET email = #{email}, status = #{status} WHERE id = #{id}")
    int update(User user);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM users")
    int count();

    /**
     * 条件查询
     */
    @Select("SELECT * FROM users WHERE status = #{status} ORDER BY id")
    List<User> findByStatus(@Param("status") Integer status);
}
