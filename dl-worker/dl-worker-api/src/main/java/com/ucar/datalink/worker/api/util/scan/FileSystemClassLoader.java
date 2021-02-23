package com.ucar.datalink.worker.api.util.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 基于文件系统的classloader.
 * 
 * @author lubiao
 */
public class FileSystemClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemClassLoader.class);
    private String              rootDir;

    public FileSystemClassLoader(String rootDir, ClassLoader parent){
        super(parent);
        this.rootDir = rootDir;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }

    private byte[] getClassData(String className) {
        String path = classNameToPath(className);
        InputStream ins = null;
        try {
            ins = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            logger.error("ERROR ## get class data has an error", e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    logger.error("ERROR ## close inputstream has an error", e);
                }
            }
        }
        return null;
    }

    private String classNameToPath(String className) {
        return rootDir + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
    }

}
