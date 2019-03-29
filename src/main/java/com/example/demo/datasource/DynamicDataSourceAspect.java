package com.example.demo.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 切换数据源Advice
 */
@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
@Slf4j
public class DynamicDataSourceAspect {

    /**
     * @within：当前执行方法上类上持有注解xxx将被匹配
     * @annotation：用于匹配当前执行方法持有指定注解的方法,会拦截注解targetDataSource的方法，否则不拦截; 不能合并在一起写, 原因是要么注解在类上, 要么在方法上, 这样是都拦截, 然后会导致
     * 给参数targetDataSource赋值的时候,会导致覆盖两次,一个为null,一个有值,会导致被覆盖为null
     * @After("@within(targetDataSource)|| @annotation(targetDataSource)")
     */

    //拦截类上有targetDataSource注解的方法
    @Before("@within(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        doChangeDataSource(point, targetDataSource);
    }

    //拦截方法上有targetDataSource注解的方法
    @Before("@annotation(targetDataSource)")
    public void changeDataSource1(JoinPoint point, TargetDataSource targetDataSource) {
        doChangeDataSource(point, targetDataSource);
    }

    @After("@within(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        doRestoreDataSource(point, targetDataSource);
    }

    @After("@annotation(targetDataSource)")
    public void restoreDataSource1(JoinPoint point, TargetDataSource targetDataSource) {
        doRestoreDataSource(point, targetDataSource);
    }


    private void doChangeDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        if (!StringUtils.isEmpty(targetDataSource.value())) {
            log.debug("method annotation invoke, datasource changed.");
            if (point == null || point.getSignature() == null) {
                changed(targetDataSource.value(), "method not found1.");
            } else {
                changed(targetDataSource.value(), point.getSignature().toLongString());
            }
        }
    }


    private void doRestoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        if (!StringUtils.isEmpty(targetDataSource.value())) {
            log.debug("method annotation invoke, datasource restored.");
            if (point == null || point.getSignature() == null) {
                restore(targetDataSource.value(), "method not found2.");
            } else {
                restore(targetDataSource.value(), point.getSignature().toLongString());
            }
        }
    }


    /**
     * 更改数据源
     */
    private void changed(String dataSource, String signature) {
        //如果不在我们注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。
        if (!DynamicDataSourceContextHolder.containsDataSource(dataSource)) {
            log.error("数据源[{}]不存在，使用默认数据源 > {}", dataSource, signature);
        } else {
            log.debug("Use DataSource : {} > {}", dataSource, signature);
            //找到的话，那么设置到动态数据源上下文中。
            DynamicDataSourceContextHolder.setDataSourceType(dataSource);
        }
    }

    /**
     * 恢复数据源
     */
    private void restore(String dataSource, String signature) {
        log.debug("Revert DataSource : {} > {}", dataSource, signature);
        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
