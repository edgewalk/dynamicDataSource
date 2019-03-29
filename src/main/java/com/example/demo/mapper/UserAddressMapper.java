package com.example.demo.mapper;

import com.example.demo.entity.UserAddress;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description //TODO
 * Author jianghui
 * Date  2019/3/12 17:40
 * Version 1.0
 */
@Mapper
public interface UserAddressMapper {
    //可以在类上写sql,也可以在配置文件中书写
    @Select("select * from user_address")
    List<UserAddress> findAll();

    @Insert("insert into user_address (id,user_id,addr) value(#{id},#{userId},#{addr})")
    void save(UserAddress userAddress);
}
