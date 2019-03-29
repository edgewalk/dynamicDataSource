package com.example.demo.service;

import com.example.demo.DemoApplicationTests;
import com.example.demo.entity.UserAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class UserServiceTest extends DemoApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void getUser() {
        List<UserAddress> user = userService.getUser();
        log.info("user:{}", user);
    }

    @Test
    public void save() {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(10001);
        userAddress.setAddr("test");
        userService.save(userAddress);
        userService.save1(userAddress);
    }
}