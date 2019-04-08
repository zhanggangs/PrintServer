package com.yihong.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @ClassName ReadConfig
 * @Author ZhangGang
 * @Date 2019/1/24 16:41
 **/
public class ReadConfig {

    public static String getConfig(String configName) {

        Properties prop = new Properties();
        try {
            //InputStream in = Object.class.getResourceAsStream("/config.properties");
            // 读取系统外配置文件 (即Jar包外文件)
            // --- 外部工程引用该Jar包时需要在工程下创建config目录存放配置文件
            String filePath = System.getProperty("user.dir") + "/config/config.properties";
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            prop.load(in);
            return prop.getProperty(configName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
