package classLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class MyClassLoader extends ClassLoader {
    //类存放的路径
    private String rootDir;

    public MyClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * 重写findClass方法
     */
    @Override
    public Class<?> findClass(String name) {
        byte[] data = loadClassData(name);
        // 调用父类的 defineClass 方法
        return this.defineClass(name, data, 0, data.length);
    }

    /**
     * 编写获取class文件并转换为字节码流的逻辑
     * @param name
     * @return
     */
    public byte[] loadClassData(String name) {
        try {
            name = rootDir + name.replace(".", File.separator) + ".class";
            FileInputStream is = new FileInputStream(name);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

