package com.taoqy;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 *
 * @author Taoqy
 * @version 1.0, 2020/10/21
 * @see [相关类/方法]
 * @since bapfopm-pfpsmas-cbfsms-service 1.0
 */
@SpringBootApplication
//@Configuration
public class SpringBootWebApplication {
//    @Autowired
//    private DruidDataSource druidDataSource;


    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class,args);
        findClassJar(STARATEGY_PATH);
        System.out.println("找到的类的数量："+eleStrategyList.size());
        for (Class clazz : eleStrategyList){
            System.out.println(clazz.getName());
        }

    }
    private static Class<?> superStrategy = String.class;//接口类class 用于过滤 可以不要

    private static List<Class<? extends String>> eleStrategyList = new ArrayList<Class<? extends String>>();

    private static ClassLoader classLoader = SpringBootWebApplication.class.getClassLoader();//默认使用的类加载器

    private static final String STARATEGY_PATH = "com.taoqy";//需要扫描的策略包名

    private static void findClassLocal(final String packName){
        URI url = null ;
        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
//            url = classLoader.getResource("/").toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }
        System.out.println(url);
        File file = new File(url);
        file.listFiles(new FileFilter() {

            public boolean accept(File chiFile) {
                if(chiFile.isDirectory()){
                    findClassLocal(packName+"."+chiFile.getName());
                }
                if(chiFile.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packName + "." + chiFile.getName().replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(chiFile);
                    if(superStrategy.isAssignableFrom(clazz)){
                        eleStrategyList.add((Class<? extends String>) clazz);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private static void findClassJar(final String packName){
        String pathName = packName.replace(".", "/");
        JarFile jarFile  = null;
        try {

            URL url = classLoader.getResource(pathName);
//            URL url2 = new URL("E:\\repository\\com\\taoqy\\springBoot-web-test\\1.0-SNAPSHOT\\springBoot-web-test-1.0-SNAPSHOT.jar");
            System.out.println(url);
            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            System.out.println("jarEntryName:"+jarEntryName);
            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    System.out.println(jarEntryName+"是目录");
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    findClassJar(prefix);
                }
                else if(jarEntry.getName().endsWith(".class")){
                    System.out.println(jarEntryName+"是类文件");
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
//                    if(superStrategy.isAssignableFrom(clazz)){
                        eleStrategyList.add((Class<? extends String>) clazz);
//                    }
                }
            }

        }

    }



}
