package com.freework.user.config.dao;

import com.freework.common.loadon.util.DesUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyVetoException;

/**
 * @author daihongru
 */
@Configuration
@MapperScan("com.freework.user.dao")
public class DateSourceConfiguration {
    @Value("${jdbc.driver}")
    private String jdbcDriver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;
    @Value("${c3p0.maxPoolSize}")
    private int maxPoolSize;
    @Value("${c3p0.minPoolSize}")
    private int minPoolSize;
    @Value("${c3p0.initialPoolSize}")
    private int initialPoolSize;
    @Value("${c3p0.autoCommitOnClose}")
    private boolean autoCommitOnClose;
    @Value("${c3p0.checkoutTimeout}")
    private int checkoutTimeout;
    @Value("${c3p0.acquireRetryAttempts}")
    private int acquireRetryAttempts;
    @Value("${c3p0.maxIdleTime}")
    private int maxIdleTime;

    /**
     * 生成与spring-dao.xml对应的bean dataSource
     *
     * @return
     * @throws PropertyVetoException
     */
    @Bean(name = "dataSource")
    public ComboPooledDataSource createDataSource() throws PropertyVetoException {
        /**
         * 生成datasource实例
         */
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(jdbcDriver);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(DesUtil.getDecryptString(jdbcUsername));
        dataSource.setPassword(DesUtil.getDecryptString(jdbcPassword));
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setInitialPoolSize(initialPoolSize);
        dataSource.setAutoCommitOnClose(autoCommitOnClose);
        dataSource.setCheckoutTimeout(checkoutTimeout);
        dataSource.setAcquireRetryAttempts(acquireRetryAttempts);
        dataSource.setMaxIdleTime(maxIdleTime);
        return dataSource;
    }
}
