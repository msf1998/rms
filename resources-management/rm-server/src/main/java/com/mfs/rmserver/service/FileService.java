package com.mfs.rmserver.service;

import com.mfs.rmcommon.po.Request;
import com.mfs.rmcommon.po.Response;
import com.mfs.rmcore.dao.FileDao;
import com.mfs.rmcore.po.SysFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

@Service
public class FileService {
    @Autowired
    private FileDao fileDao;
    /**
     * 获取指定文件
     * @param request 请求对象，至少要保证request.path存在
     * @return Response
     * */
    public Response getDocument(Request request) {
        SysFile sysFile = fileDao.queryOne(request.getPath());
        if (sysFile == null) {
            return new Response(500,"错误的资源地址",null);
        }
        if (sysFile.isDirectory()) {
            return new Response(500,"不可获取的资源",null);
        } else {
            try {
                StringBuffer sb = new StringBuffer();
                BufferedReader br = new BufferedReader(new FileReader(sysFile.getPath()));
                String s = null;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                br.close();
                return new Response(200,"成功",sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new Response(500,"错误的资源地址",null);
            } catch (IOException e) {
                e.printStackTrace();
                return new Response(500,"IOException",null);
            }
        }
    }
    /**
     * 删除指定文件
     * @param request 请求对象，至少要保证request.path存在
     * @return Response
     * */
    public Response deleteFile(Request request) {
        boolean b = fileDao.deleteFile(request.getPath());
        if (b) {
            return new Response(200,"删除成功",null);
        } else {
            return new Response(500,"删除失败",null);
        }
    }
    /**
     * 删除指定文件
     * @param request 请求对象，至少要保证request.path、request.name存在
     * @return Response
     * */
    public Response renameFile(Request request) {
        boolean b = fileDao.renameFile(request.getPath(), request.getName());
        if (b) {
            return new Response(200,"重命名成功",null);
        } else {
            return new Response(500,"重命名失败",null);
        }
    }
    /**
     *
     * */
    public Response addFile(Request request) {
        InputStream inputStream = request.inputStream();
        if (inputStream == null) { //创建文件夹
            try {
                boolean res = fileDao.createDirectory(request.getPath(), request.getName());
                if (res) {
                    return new Response(500,"创建文件夹成功",null);
                } else {
                    return new Response(500,"创建文件夹失败",null);
                }
            } catch (FileAlreadyExistsException e) {
                return new Response(500,"文件夹已存在",null);
            }
        } else { //添加文档
            boolean res = fileDao.createDocument(request.getPath(), request.getName(), request.inputStream());
            if (res) {
                return new Response(500,"添加文档成功",null);
            } else {
                return new Response(500,"添加文档失败",null);
            }
        }
    }
}
