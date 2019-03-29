package com.example.demo.datasource;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicDataSourceRegister.class)
public @interface EnableDynamicDataSource {
    Class<?>[] exclude() default {};
}