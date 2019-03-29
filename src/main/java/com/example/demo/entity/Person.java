package com.example.demo.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Description //TODO
 * Author jianghui
 * Date  2019/3/9 15:48
 * Version 1.0
 */
@Data
public class Person {
    @NotNull(message = "值不能为空")
    private String name;
    @Range(min = 1, max = 100, message = "年龄需要在1和100之间")
    private int age;
    private Long pid;
    //定义2种分组
    public interface Get {
    }

    public interface Save {
    }
}
