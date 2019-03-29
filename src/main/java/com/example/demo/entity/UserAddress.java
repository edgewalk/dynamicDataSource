package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddress implements Serializable {

    //用户ID
    private int id;

    //用户名
    private int userId;

    //地址
    private String addr;
}