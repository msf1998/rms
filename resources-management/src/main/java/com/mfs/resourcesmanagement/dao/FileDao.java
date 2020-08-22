package com.mfs.resourcesmanagement.dao;

import com.mfs.resourcesmanagement.po.SysFile;
import com.mfs.resourcesmanagement.po.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.List;

@Repository
public class FileDao {
    private static String[] path = new String[]{"/usr/local/src/tomcat/apache-tomcat-8.5.57-1/webapps/rms","/usr/local/src/tomcat/apache-tomcat-8.5.57-2/webapps/rms","/usr/local/src/tomcat/apache-tomcat-8.5.57-3/webapps/rms"};
    //for test
    //private static String[] path = new String[]{"E:/test1","E:/test2","E:/test3"};
    private static SysFile[] sysFileCache = new SysFile[path.length];


    //从缓存获取系统目录，如果缓存尚未建立，则创建缓存
    public SysFile[] getSysFile() {
        if (sysFileCache[0] == null) {
            synchronized (this) {
                if (sysFileCache[0] == null) {
                    try {
                        int i = 0;
                        for (String path : FileDao.path) {
                            File file = new File(path);
                            if (file.exists()) {
                                sysFileCache[i] = new SysFile(file,null,null);
                                if (file.isDirectory()) {
                                    for (File f : file.listFiles()) {
                                        sysFileCache[i].addSubFile(initCache(f.getAbsolutePath(),sysFileCache[i],f.getName()));
                                    }
                                }
                            } else {
                                throw new FileNotFoundException("初始化缓存区失败");
                            }
                            i ++;
                        }
                        return sysFileCache;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    return sysFileCache;
                }
            }
        }
        return sysFileCache;
    }

    //递归创建系统目录缓存
    public SysFile initCache(String path,SysFile parent,String owner) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            SysFile sysFile = new SysFile(file,parent,owner);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    sysFile.addSubFile(initCache(f.getAbsolutePath(),sysFile,owner));
                }
            }
            return sysFile;
        } else {
            throw new FileNotFoundException("初始化缓存区失败");
        }
    }

    //获取从缓存用户的根目录
    public SysFile queryMyRootDirectory(User user) {
        return queryMyRootDirectory(getSysFile()[0],user);
    }
    private SysFile queryMyRootDirectory(SysFile sysFile,User user) {
        return sysFile.getSubFile(user.getUsername());
    }

    //查找指定路径的目录或文件
    public SysFile queryOne(String path) {
        return queryOne(getSysFile()[0],path);
    }
    private SysFile queryOne(SysFile sysFile, String path) {
        SysFile file = sysFile.getFile(path);
        return file;
    }

    //创建文件夹
    public boolean createDirectory(String path, String fileName) throws FileAlreadyExistsException {
        SysFile[] sysFile = getSysFile();
        for (int i = 0; i < sysFile.length; i ++) {
            boolean directory = createDirectory(sysFile[i], path, fileName);
            if (!directory) {
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

    //添加文件，用户上传上来的文件
    public boolean createDocument(String path, MultipartFile file) {
        SysFile[] sysFile = getSysFile();
        for (int i = 0; i < sysFile.length; i ++) {
            boolean document = createDocument(sysFile[i], path, file);
            if (!document) {
                return false;
            }
        }
        return true;
    }
    private boolean createDocument(SysFile sysFile, String path, MultipartFile file) {
        SysFile root = sysFile.getFile(path);
        File newFile = new File(sysFile.getPath() + path + "/" + file.getOriginalFilename());
        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InputStream is = file.getInputStream();
            FileOutputStream os = new FileOutputStream(newFile);
            byte[] bytes = new byte[1024];
            int n = -1;
            while ((n = is.read(bytes)) > 0) {
                os.write(bytes,0,n);
            }
            is.close();
            os.close();
            root.deleteFile(file.getName());
            root.addSubFile(new SysFile(newFile,root,root.getOwner()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //删除文件|文件夹
    public boolean deleteFile(String path) {
        SysFile[] sysFile = getSysFile();
        for (int i = 0; i < sysFile.length; i ++) {
            boolean b = deleteFile(sysFile[i], path);
            if (!b) {
                return false;
            }
        }
        return true;
    }
    private boolean deleteFile(SysFile sysFile, String path) {
        SysFile file = sysFile.getFile(path);
        return file.delete();
    }

    //重命名文件或者文件夹
    public boolean renameFile(String path, String newName) {
        SysFile[] sysFile = getSysFile();
        for (int i = 0; i < sysFile.length; i ++) {
            boolean b = renameFile(sysFile[i], path, newName);
            if (!b) {
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
        SysFile[] sysFile = fileDao.getSysFile();
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
