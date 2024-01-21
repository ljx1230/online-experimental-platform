package com.ljx.file.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;


/**
 * @Author: ljx
 * @Date: 2023/12/21 16:10
 */
@Slf4j
public class PathUtil {
    public static String getFilePath() {
        String path = "upload";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        path = File.separator + path + File.separator + formatter.format(new Date());

        String staticPath = PathUtil.getStaticPath();
        File dir = new File(staticPath + path);
        if(!dir.exists()) {
            boolean success = dir.mkdirs();
            if(!success) {
                log.error("目录创建失败:" + PathUtil.getStaticPath() + path);
            }
        }
        return path;
    }

    public static String getStaticPath() {
        String localStoragePath = PropertiesUtil.getProperty("file.local-storage-path");
        if(StringUtils.isNotEmpty(localStoragePath)) {
            return localStoragePath;
        } else {
            String projectRootAbsolutePath = getProjectRootPath();
            int index = projectRootAbsolutePath.indexOf("file:");
            if(index != -1) {
                projectRootAbsolutePath = projectRootAbsolutePath.substring(0,index);
            }
            return projectRootAbsolutePath + "static" + File.separator;
        }
    }

    public static String getProjectRootPath() {
        String absolutePath = "";
        try {
            String url = ResourceUtils.getURL("classpath:").getPath();
            absolutePath = urlDecode(new File(url).getAbsolutePath() + File.separator);
        } catch (FileNotFoundException e) {
            log.error("获取项目路径失败");
            e.printStackTrace();
        }
        return absolutePath;
    }

    public static String urlDecode(String url) {
        String decodeUrl = null;
        try {
            decodeUrl = URLDecoder.decode(url,"utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("路径解码失败");
            e.printStackTrace();
        }
        return decodeUrl;
    }
}
