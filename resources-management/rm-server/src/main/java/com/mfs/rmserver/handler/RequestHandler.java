package com.mfs.rmserver.handler;

import com.mfs.rmcommon.po.Request;
import com.mfs.rmcommon.po.Response;
import com.mfs.rmcore.po.SysFile;
import com.mfs.rmserver.service.FileService;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable{
    private static long count = 0;
    private final long id = ++ count;
    private Socket socket;
    private FileService fileService;
    private ObjectInput oi;
    private ObjectOutput oo;

    public RequestHandler(Socket socket, FileService fileService) {
        this.socket = socket;
        this.fileService = fileService;
    }

    @Override
    public void run() {
        Request request =  null;
        try {
            request = initRequest();
            Response response = null;
            //根据请求类型分发请求
            switch (request.getType()) {
                case GET: response = fileService.getDocument(request);
                    break;
                case ADD: response = fileService.addFile(request);
                    break;
                case DELETE: response = fileService.deleteFile(request);
                    break;
                case UPDATE: response = fileService.renameFile(request);
                    break;
            }

            sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }finally
        {
            try {
                if (request != null) {
                    request.close();
                }
                socket.shutdownInput();
                socket.shutdownOutput();
                if (oi != null) {
                    oi.close();
                }
                if (oo != null) {
                    oo.close();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化Request对象，主要是判断是否该Request请求中是否包含文件，如果包含则创建临时文件
     * @return Request
     * */
    private Request initRequest() {
        OutputStream os = null;
        try {
            //读取请求数据
            oi = new ObjectInputStream(socket.getInputStream());
            Request request = (Request)oi.readObject();
            //创建临时文件
            if (request.getContent() != null) {
                File file = new File("temFile" + id);
                os = new FileOutputStream(file);
                os.write(request.getContent().getBytes("utf8"));
                os.flush();
                request.setTemFile(file);
            }
            return request;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            sendResponse(new Response(500,e.getClass().getName(),null));
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 回送Response响应
     * */
    private void sendResponse(Response response) {
        try {
            oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeObject(response);
            oo.flush();
        } catch (IOException e) {
            e.printStackTrace();
            sendResponse(new Response(500,e.getClass().getName(),null));
        }
    }
}
