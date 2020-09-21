package com.mfs.rmweb.service;

import com.mfs.rmcore.dao.FileDao;
import com.mfs.rmcore.po.Result;
import com.mfs.rmcore.po.SysFile;
import com.mfs.rmcore.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@Service
public class FileService {
    //private static String root = "/usr/local/src/tomcat/apache-tomcat-8.5.57-1/webapps/rms";
    //for test
    private static String root = "E:\\test1";
    @Autowired
    FileDao fileDao;
    /**
     * 创建文件夹
     * @param request HttpServletRequest
     * @param path 父文件夹的绝对路径
     * @param name 文件夹名
     * @return Result
     * */
    public Result createDirectory(HttpServletRequest request,String path,String name){
        path = path.replace(root,"").replaceAll("\\\\","/");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user != null) {
            SysFile sysFile = fileDao.queryOne(path);
            if (sysFile != null) {
                if (sysFile.getOwner() != null && sysFile.getOwner().equals(user.getUsername())) {
                    try {
                        boolean directory = fileDao.createDirectory(path, name);
                        if (directory) return new Result(1,"创建成功",fileDao.queryMyRootDirectory(user));
                        return new Result(2,"创建失败",null);
                    } catch (FileAlreadyExistsException e) {
                        e.printStackTrace();
                        return new Result(2,"该文件夹已存在",null);
                    }
                }
                return new Result(2,"您没有此权限",null);
            }
            return new Result(2,"路径错误",null);
        }
        return new Result(3,"请先登录",null);
    }
    /**
     * 创建文档
     * @param request HttpServletRequest
     * @param path 父文件夹的绝对路径
     * @param file 文档
     * @return Result
     * */
    public Result createDocument(HttpServletRequest request, String path, MultipartFile file) throws IOException {
        path = path.replace(root,"").replaceAll("\\\\", "/");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user != null) {
            SysFile sysFile = fileDao.queryOne(path);
            if (sysFile != null) {
                if (sysFile.getOwner() != null && sysFile.getOwner().equals(user.getUsername())) {
                    boolean directory = fileDao.createDocument(path, file.getName(), file.getInputStream());
                    if (directory) return new Result(1,"创建成功",fileDao.queryMyRootDirectory(user));
                    return new Result(2,"创建失败",null);
                }
                return new Result(2,"您没有此权限",null);
            }
            return new Result(2,"路径错误",null);
        }
        return new Result(3,"请先登录",null);
    }
    /**
     * 删除文件
     * @param request HttpServletRequest
     * @param file 文件的绝对路径
     * @return Result
     * */
    public Result deleteFile(HttpServletRequest request,String file) {
        file = file.replace(root,"").replaceAll("\\\\", "/");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user != null) {
            SysFile file1 = fileDao.queryOne(file);
            if (file1 != null) {
                if (file1.getOwner() != null && !file1.getName().equals(user.getUsername())) {
                    if (file1.getOwner().equals(user.getUsername())) {
                        boolean b = fileDao.deleteFile(file);
                        if (b) return new Result(1,"删除成功",fileDao.queryMyRootDirectory(user));
                        return new Result(2,"删除失败",null);
                    }
                    return new Result(2,"您没有此权限",null);
                }
                return new Result(2,"此文件不允许删除",null);
            }
            return new Result(2,"没有此文件",null);
        }
        return new Result(3,"请先登录",null);
    }
    /**
     * 删除文件
     * @param request HttpServletRequest
     * @param file 文件的绝对路径
     * @param newName 新文件名
     * @return Result
     * */
    public Result renameFile(HttpServletRequest request,String file,String newName) {
        System.out.println(file + " " + newName);
        file = file.replace(root,"").replaceAll("\\\\", "/");
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user != null) {
            SysFile sysFile = fileDao.queryOne(file);
            if (sysFile != null) {
                if (sysFile.getOwner() != null && sysFile.getOwner().equals(user.getUsername()) && !sysFile.getName().equals(user.getUsername())) {
                    boolean b = fileDao.renameFile(file, newName);
                    if (b) {
                        return new Result(1,"重命名成功",fileDao.queryMyRootDirectory(user));
                    }
                    return new Result(2,"重命名失败",null);
                }
                return new Result(2,"您没有此权限",null);
            }
            return new Result(2,"该文件不存在",null);
        }
        return new Result(3,"请先登录",null);
    }
    /**
     * 删除文件
     * @param request HttpServletRequest
     * @return Result
     * */
    public Result listFiles(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if (user != null) {
            SysFile sysFile = fileDao.queryMyRootDirectory(user);
            if (sysFile != null) return new Result(1,"查询成功",sysFile);
            return new Result(2,"查询失败",null);
        }
        return new Result(3,"请先登录",null);
    }

}
