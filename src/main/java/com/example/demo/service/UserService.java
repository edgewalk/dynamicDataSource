package com.example.demo.service;

import com.example.demo.entity.UserAddress;
import com.example.demo.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description //TODO
 * Author jianghui
 * Date  2019/3/12 16:31
 * Version 1.0
 */
@Service
@Transactional
//@TargetDataSource("db1")
public class UserService {

    @Autowired
    private UserAddressMapper mapper;


    public List<UserAddress> getUser() {
        return mapper.findAll();
    }

    public void save(UserAddress userAddress) {
        mapper.save(userAddress);
        //throw  new RuntimeException("error");
    }

    public void save1(UserAddress userAddress) {
        mapper.save(userAddress);
        //throw  new RuntimeException("error");
    }
}
