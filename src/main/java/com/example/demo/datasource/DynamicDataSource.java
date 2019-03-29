package com.example.demo.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * AbstractRoutingDataSource
 * 是实现多数据源的关键，他的作用就是动态切换数据源
 * 实质：有多少个数据源就存多少个数据源在targetDataSources(其中value为每个数据源，key表示每个数据源的名字）这个属性中，
 * 然后根据determineCurrentLookupKey（）这个方法获取当前数据源在map中的key值，
 * 然后determineTargetDataSource（）方法中动态获取当前数据源，如果当前数据源不存并且默认数据源也不存在就抛出异常。
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {


    /**
     * 代码中的determineCurrentLookupKey方法取得一个字符串，
     * 该字符串将与配置文件中的相应字符串进行匹配以定位数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        // DynamicDataSourceContextHolder代码中使用setDataSourceType
        // 设置当前的数据源，在路由类中使用getDataSourceType进行获取，
        // 交给AbstractRoutingDataSource进行注入使用
        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        log.info("数据源为:{}", dataSourceType);
        return dataSourceType;
    }

}
