package winflex.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 注解扫描工具类
 * @author lixiaohui
 * @date 2016年11月13日 下午11:52:23
 */
public class Annotations {
    
    private static final String CLASS_FILE_SUFFIX = "class";
    
    private static final FileFilter CLASS_FILE_FILTER = new FileFilter() {
        
        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(".class") || file.isDirectory();
        }
    };
    
    /**
     * 在packageName包系 下搜索所有被注解 annotationClass 标记的类
     * @param packageName 包名
     * @param annotationClass 注解class
     * @return 所有找到的Class
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Set<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', '/'));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if ("file".equals(url.getProtocol())) { 
                // 目录结构的class
                findClassesFromDirectory(new File(URLDecoder.decode(url.getFile(), "UTF-8"))
                    , packageName, classes, annotationClass);
            } else if ("jar".equals(url.getProtocol())) { 
                JarFile file = ((JarURLConnection) url.openConnection()).getJarFile();
                findClassesFromJar(file, packageName, classes, annotationClass);
            }
        }
        return classes;
    }
    
    /**
     * 查找指定类的所有被注解 annotationClass 标记的Method
     * @param clazz 目标class
     * @param annotationClass 注解class
     * @return 所有找到的方法
     */
    public static List<Method> findAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            Annotation anno = method.getAnnotation(annotationClass);
            if (anno != null) {
                methods.add(method);
            }
        }
        return methods; 
    }
    
    /**
     * 从jar包中扫描类
     * JarEntry:每个文件夹, 文件都是个JarEntry, 因此无需递归就能遍历JarFile
     * @param file
     * @param packageName
     * @param container
     * @param annotationClass
     * @throws ClassNotFoundException 
     */
    private static void findClassesFromJar(JarFile file, String packageName, Set<Class<?>> container, Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        Enumeration<JarEntry> jarEntries = file.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry entry = jarEntries.nextElement();
            if (entry.getName().endsWith(".class")) { // 是class文件
                String className = entry.getName().substring(0, entry.getName().lastIndexOf(".")).replace('/', '.');
                Class<?> clazz = Class.forName(className);
                if (clazz.getAnnotation(annotationClass) != null) { // 被annotationClass注解标记
                    container.add(clazz);
                }
            }
        }
    }
    
    private static void findClassesFromDirectory(File file, String packageName, Set<Class<?>> container, Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        for (File f : file.listFiles(CLASS_FILE_FILTER)) {
            if (f.isDirectory()) {
                findClassesFromDirectory(f, packageName.isEmpty() ? f.getName() : packageName + "." + f.getName(), container, annotationClass);
            } else {
                String className = noSuffix(f.getName(), CLASS_FILE_SUFFIX);
                Class<?> klass = Class.forName(packageName.isEmpty() ? className : packageName + "." + className);
                if (klass.getAnnotation(annotationClass) != null) {
                    container.add(klass);
                }
            }
        }
    }
    
    private static String noSuffix(String filename, String suffix) {
        return filename.substring(0, filename.length() - suffix.length() - 1);
    }
}