package com.example.demo.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源注册
 */
@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {


    // 如配置文件中未指定数据源类型，使用该默认值
    private static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";
    //定义默认常量
    private static final String DRIVER_CLASS_NAME = "driver-class-name";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    // 默认数据源
    private DataSource defaultDataSource;
    // 其他数据源
    private Map<String, DataSource> customDataSources = new HashMap();

    @Override
    public void setEnvironment(Environment env) {
        log.info("DynamicDataSourceRegister.setEnvironment()");
        //加载默认数据源
        initDefaultDataSource(env);
        //加载自定义数据源
        initCustomDataSources(env);
    }

    /**
     * 加载自定义数据源
     *
     * @param env
     */
    private void initCustomDataSources(Environment env) {
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        String DatasourcePrefixs = Binder.get(env).bind("custom.datasource.names", Bindable.of(String.class)).get();
        if (!StringUtils.isEmpty(DatasourcePrefixs)) {
            for (String dsPrefix : DatasourcePrefixs.split(",")) {
                Map<String, Object> configMap = Binder.get(env).bind("custom.datasource." + dsPrefix, Bindable.mapOf(String.class, Object.class)).get();
                //为每个datasource 设置连接池参数
                configMap.putAll(bindConnectionPollParams(env));
                DataSource dataSource = buildDataSource(configMap);
                customDataSources.put(dsPrefix, dataSource);
            }
        }
    }

    private Map<String, Object> bindConnectionPollParams(Environment env) {
        Map<String, Object> configMap = Binder.get(env).bind("spring.datasource", Bindable.mapOf(String.class, Object.class)).get();
        // 排除已经设置的属性
        configMap.remove(TYPE);
        configMap.remove(DRIVER_CLASS_NAME);
        configMap.remove(URL);
        configMap.remove(NAME);
        configMap.remove(USERNAME);
        configMap.remove(PASSWORD);
        return configMap;
    }


    /**
     * 加载默认数据源
     *
     * @param env
     */
    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        Map<String, Object> configMap = Binder.get(env).bind("spring.datasource", Bindable.mapOf(String.class, Object.class)).get();
        this.defaultDataSource = buildDataSource(configMap);
    }

    /**
     * 创建datasource.
     */
    private DataSource buildDataSource(Map<String, Object> configMap) {
        Object type = configMap.get(TYPE);
        if (type == null) {
            type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
        }
        try {
            return DruidDataSourceFactory.createDataSource(configMap);
        } catch (Exception e) {
            log.error("build datasource error: ", e);
        }
        return null;
    }

    /**
     * 启动时,注册bean到IOC容器
     *
     * @param annotationMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        log.info("DynamicDataSourceRegister.registerBeanDefinitions()");
        HashMap<String, DataSource> targetDataSources = new HashMap<String, DataSource>();
        String dataSource = "master";

        // 将主数据源添加到更多数据源中
        targetDataSources.put(dataSource, defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add(dataSource);
        // 添加更多数据源
        targetDataSources.putAll(customDataSources);
        DynamicDataSourceContextHolder.dataSourceIds.addAll(customDataSources.keySet());

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);

        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        //添加属性：AbstractRoutingDataSource.defaultTargetDataSource
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        registry.registerBeanDefinition(dataSource, beanDefinition);
    }
}
