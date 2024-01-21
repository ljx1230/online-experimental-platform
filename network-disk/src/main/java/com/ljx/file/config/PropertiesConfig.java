package com.ljx.file.config;

import com.ljx.file.util.PropertiesUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * @Author: ljx
 * @Date: 2023/12/21 15:53
 */
@Configuration
public class PropertiesConfig {
    @Autowired
    private Environment environment;

    @PostConstruct
    public void setEnvironment() {
        PropertiesUtil.setEnvironment(environment);
    }
}
