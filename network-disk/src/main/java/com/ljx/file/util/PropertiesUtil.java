package com.ljx.file.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @Author: ljx
 * @Date: 2023/12/21 15:54
 */
public class PropertiesUtil {
    private static Environment environment;

    public static void setEnvironment(Environment environment) {
        PropertiesUtil.environment = environment;
    }

    public static String getProperty(String key) {
        return PropertiesUtil.environment.getProperty(key);
    }
}
