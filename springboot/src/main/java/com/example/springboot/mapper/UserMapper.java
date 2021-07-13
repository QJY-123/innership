package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    /*@Result是结果映射列表
    property是User类的属性名，colomn是数据库表的字段名
     */
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    //插入用户
    @Insert("INSERT INTO user(name, age) VALUES (#{name}, #{age})")
    void save(User user);

    //根据年龄查询用户
    @Select("SELECT * FROM user WHERE age = #{age}")
    List<User> select(int age);

    //根据年龄查询用户
    @Select("SELECT * FROM user ")
    List<User> findAll();

    //根据id删除用户
    @Delete("delete from user where id= #{id} ")
    void delete(int id);

    //根据id查找用户
    @Select("select * from user where id= #{id} ")
    User get(int id);

    //更新用户信息
    @Update("update user set name=#{name} where id=#{id} ")
    int update(User user);
}



