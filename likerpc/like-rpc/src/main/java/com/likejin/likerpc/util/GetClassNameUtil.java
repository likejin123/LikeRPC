package com.likejin.likerpc.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 15:33
 * @Description
 */
public class GetClassNameUtil {


    /*
     * @Description 根据传入的包名和传入的接口名，获取指定包下的实现接口的全类名
     * @param packageName
     * @param interfaceName
     * @return String
     **/
    public static String getClassImpl(String packageName,String interfaceName){
        List<Class<?>> classes = getClasses(packageName);
        String classImplName = null;
        for(Class clazz :classes){
            Class[] interfaces = clazz.getInterfaces();
            for(Class clazz1 : interfaces){
                if(clazz1.getSimpleName().equals(interfaceName)){
                    classImplName = clazz.getName();
                }
            }
        }
        return classImplName;
    }


    /*
     * @Description 根据包名获取包下的所有类
     * @param packageName
     * @return List<Class<?>>
     **/
    private static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            List<String> resourceList = new ArrayList<>();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                resourceList.add(resource.getFile());
            }

            for (String resource : resourceList) {
                File file = new File(resource);
                findClassesInDirectory(packageName, file, classes);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return classes;
    }


    private static void findClassesInDirectory(String packageName, File directory, List<Class<?>> classes)
            throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackageName = packageName + '.' + file.getName();
                findClassesInDirectory(newPackageName, file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
    }
}
