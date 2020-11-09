package com.taoqy;

import org.junit.Test;

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
 * @version 1.0, 2020/11/4
 * @see [相关类/方法]
 * @since bapfopm-pfpsmas-cbfsms-service 1.0
 */
public class ScanPacketTest {
    @Test
    public void searchClass() throws IOException, ClassNotFoundException {
        String basePack = "org.junit";
        //通过当前线程得到类加载器从而得到URL的枚举
        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(basePack.replace(".", "/"));
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();//得到的结果大概是：jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit
            String protocol = url.getProtocol();//大概是jar
            if ("jar".equalsIgnoreCase(protocol)) {
                //转换为JarURLConnection
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                if (connection != null) {
                    JarFile jarFile = connection.getJarFile();
                    if (jarFile != null) {
                        //得到该jar文件下面的类实体
                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                        while (jarEntryEnumeration.hasMoreElements()) {
                            /*entry的结果大概是这样：
                                    org/
                                    org/junit/
                                    org/junit/rules/
                                    org/junit/runners/*/
                            JarEntry entry = jarEntryEnumeration.nextElement();
                            String jarEntryName = entry.getName();
                            //这里我们需要过滤不是class文件和不在basePack包名下的类
                            if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/", ".").startsWith(basePack)) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                                Class cls = Class.forName(className);
                                System.out.println(cls);
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void findScanLocalPacket(){
        findClassLocal(STARATEGY_PATH);
    }


    private Class<?> superStrategy = String.class;//接口类class 用于过滤 可以不要

    private List<Class<? extends String>> eleStrategyList = new ArrayList<Class<? extends String>>();

    private ClassLoader classLoader = ScanPacketTest.class.getClassLoader();//默认使用的类加载器

    private static final String STARATEGY_PATH = "com.taoqy";//需要扫描的策略包名

    private void findClassLocal(final String packName){
        URI url = null ;
        try {
            url = classLoader.getResource(packName.replace(".", "/")).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

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
}
