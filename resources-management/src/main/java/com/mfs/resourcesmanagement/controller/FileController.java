package com.mfs.resourcesmanagement.controller;

import com.mfs.resourcesmanagement.po.File;
import com.mfs.resourcesmanagement.po.Result;
import com.mfs.resourcesmanagement.po.SysFile;
import com.mfs.resourcesmanagement.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    FileService fileService;

    @RequestMapping("/create/directory")
    public Result createDirectory(HttpServletRequest request,@RequestBody File file){
        try {
            return fileService.createDirectory(request,file.getPath(),file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }
    @RequestMapping("/create/document")
    public Result createDocument(HttpServletRequest request, String path, MultipartFile file){
        try {
            return fileService.createDocument(request,path,file);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }
    @RequestMapping("/delete")
    public Result deleteFile(HttpServletRequest request, @RequestBody File file){
        try {
             return fileService.deleteFile(request, file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }
    @RequestMapping("/rename")
    public Result renameFile(HttpServletRequest request,@RequestBody File file){
        try {
            return fileService.renameFile(request,file.getPath(),file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }
    @RequestMapping("/list")
    public Result listFiles(HttpServletRequest request){
        try {
            Result result = fileService.listFiles(request);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(4,"服务器异常",null);
        }
    }
}
