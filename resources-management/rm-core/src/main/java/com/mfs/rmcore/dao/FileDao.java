package com.mfs.rmcore.dao;

import com.mfs.rmcore.po.SysFile;
import com.mfs.rmcore.po.User;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;

@Repository
public class FileDao {
    /** 所管理的文件空间，因为该项目的布置在了一个负载均衡的服务器的环境中所以有三个空间*/
    //private static String[] path = new String[]{"/usr/local/src/tomcat/apache-tomcat-8.5.57-1/webapps/rms","/usr/local/src/tomcat/apache-tomcat-8.5.57-2/webapps/rms","/usr/local/src/tomcat/apache-tomcat-8.5.57-3/webapps/rms"};
    //for test
    private static String[] path = new String[]{"E:/test1","E:/test2","E:/test3"};
    /** 文件空间缓存*/
    private static final SysFile[] sysFileCache = new SysFile[path.length];
    /**
     * 初始化文件空间缓存区
     * */
    static {
        initCache();
    }
    /**
     * 初始化文件空间缓冲区,与initSysFile配合使用，除非非常理解本项目，不建议使用
     *
     * */
    private static void initCache() {
        if (sysFileCache[0] == null) {
            synchronized (FileDao.class) {
                if (sysFileCache[0] == null) {
                    try {
                        int i = 0;
                        for (String path : FileDao.path) {
                            File file = new File(path);
                            if (file.exists()) {
                                sysFileCache[i] = new SysFile(file,null,null);
                                if (file.isDirectory()) {
                                    for (File f : file.listFiles()) {
                                        sysFileCache[i].addSubFile(initSysFile(f.getAbsolutePath(),sysFileCache[i],f.getName()));
                                    }
                                }
                            } else {
                                throw new FileNotFoundException("初始化缓存区失败");
                            }
                            i ++;
                        }
                        return;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        }
        return;
    }
    /**
     * 初始化文件空间缓冲区,与initCache配合使用，除非非常理解本项目，不建议使用
     * @param path 文件路径
     * @param parent 父文件
     * @param owner 文件所有者
     * @return SysFile 创建的SysFile文件对象
     * */
    private static SysFile initSysFile(String path,SysFile parent,String owner) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            SysFile sysFile = new SysFile(file,parent,owner);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    sysFile.addSubFile(initSysFile(f.getAbsolutePath(),sysFile,owner));
                }
            }
            return sysFile;
        } else {
            throw new FileNotFoundException("初始化缓存区失败");
        }
    }

    /**
     * 获取用户的根目录（与用户名相同，在管理的文件空间下的第一级目录），仅从一个缓存中获取就可以，因为三个缓存在管理文件空间下的目录结构都是一样的
     * @param user 用户对象
     * @return SysFile 用户的跟文件对象，找不到则返回null
     * */
    public SysFile queryMyRootDirectory(User user) {
        return queryMyRootDirectory(sysFileCache[0],user);
    }
    private SysFile queryMyRootDirectory(SysFile sysFile,User user) {
        return sysFile.getSubFile(user.getUsername());
    }

    /**
     * 获取指定路径的文件
     * @param path 要查找的文件的路径
     * @return SysFile 查找出的文件对象，找不到则返回null
     * */
    public SysFile queryOne(String path) {
        return queryOne(sysFileCache[0],path);
    }
    private SysFile queryOne(SysFile sysFile, String path) {
        SysFile file = sysFile.getFile(path);
        return file;
    }

    /**
     * 创建文件夹
     * @param path 文件路径（相对路径，相对文件空间的路径）
     * @param fileName 文件夹名
     * @return true 或者 false
     * */
    public boolean createDirectory(String path, String fileName) throws FileAlreadyExistsException {
        SysFile[] sysFile = sysFileCache;
        for (int i = 0; i < sysFile.length; i ++) {
            boolean directory = createDirectory(sysFile[i], path, fileName);
            if (!directory) {
                System.out.println(FileDao.class.getName() + "创建第" + (i + 1) + "文件夹失败");
                return false;
            }
        }
        return true;
    }
    private boolean createDirectory(SysFile sysFile, String path, String fileName) throws FileAlreadyExistsException {
        SysFile file = sysFile.getFile(path);
        if (file == null) return false;
        File newFile = new File(sysFile.getPath() + path + "/" + fileName);
        if (!newFile.exists()) {
            newFile.mkdir();
        } else {
            throw new FileAlreadyExistsException("磁盘中:该文件已存在");
        }
        file.addSubFile(new SysFile(newFile,file,file.getOwner()));
        return true;
    }

    /**
     * 创建文档
     * @param path 父文件的路径（相对路径，相对文件空间的路径）
     * @param name 文档名
     * @param inputStream 输入流
     * @return true 或者 false
     * */
    public boolean createDocument(String path, String name, InputStream inputStream) {
        SysFile[] sysFile = sysFileCache;
        for (int i = 0; i < sysFile.length; i ++) {
            boolean document = createDocument(sysFile[i], path, name, inputStream);
            if (!document) {
                System.out.println(FileDao.class.getName() + "创建第" + (i + 1) + "文档失败");
                return false;
            }
        }
        return true;
    }
    private boolean createDocument(SysFile sysFile, String path, String name, InputStream is) {
        SysFile root = sysFile.getFile(path);
        return root.createDocument(name,is);
    }

    /**
     * 删除文件
     * @param path 文件的路径（相对路径，相对文件空间的路径）
     * @return true 或者 false
     * */
    public boolean deleteFile(String path) {
        SysFile[] sysFile = sysFileCache;
        for (int i = 0; i < sysFile.length; i ++) {
            boolean b = deleteFile(sysFile[i], path);
            if (!b) {
                System.out.println(FileDao.class.getName() + "删除第" + (i + 1) + "文档失败");
                return false;
            }
        }
        return true;
    }
    private boolean deleteFile(SysFile sysFile, String path) {
        SysFile file = sysFile.getFile(path);
        return file.delete();
    }

    /**
     * 重命名文件
     * @param path 文件的路径（相对路径，相对文件空间的路径）
     * @param newName 新的文件名
     * @return true 或者 false
     * */
    public boolean renameFile(String path, String newName) {
        SysFile[] sysFile = sysFileCache;
        for (int i = 0; i < sysFile.length; i ++) {
            boolean b = renameFile(sysFile[i], path, newName);
            if (!b) {
                System.out.println(FileDao.class.getName() + "删除第" + (i + 1) + "文档失败");
                return false;
            }
        }
        return true;
    }
    private boolean renameFile(SysFile sysFile, String path, String newName) {
        SysFile file = sysFile.getFile(path);
        return file.rename(newName);
    }

    public static void main(String[] args) throws FileAlreadyExistsException {
        FileDao fileDao = new FileDao();
        SysFile[] sysFile = sysFileCache;
        for (SysFile f : sysFile) {
            System.out.println(f);
        }
        boolean res = fileDao.deleteFile("/mfs/hello");
        System.out.println(res);

        for (SysFile f : sysFile) {
            System.out.println(f);
        }

    }
}
