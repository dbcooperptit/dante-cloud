/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Project Name: eurynome-cloud
 * Module Name: eurynome-cloud-component-data
 * File Name: DynamicDataSourceConfiguration.java
 * Author: gengwei.zheng
 * Date: 2020/5/22 下午12:23
 * LastModified: 2020/5/22 下午12:23
 */

package cn.herodotus.eurynome.data.configuration;

import cn.herodotus.eurynome.data.datasource.DynamicRoutingDataSource;
import cn.herodotus.eurynome.data.datasource.properties.DataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * <p>Project: eurynome-cloud </p>
 * <p>File: DynamicDataSourceConfiguration </p>
 *
 * <p>Description: 动态数据源配置 </p>
 *
 * @author : gengwei.zheng
 * @date : 2020/5/22 12:23
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "cn.herodotus.eurynome.data.datasource.aop"
})
public class DynamicDataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "herodotus.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "herodotus.datasource.dynamic")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    @ConditionalOnBean({HikariConfig.class, DataSourceProperties.class})
    public DataSource dataSource(DataSourceProperties dataSourceProperties, HikariConfig hikariConfig) {
        return new DynamicRoutingDataSource(dataSourceProperties, hikariConfig);
    }
}
