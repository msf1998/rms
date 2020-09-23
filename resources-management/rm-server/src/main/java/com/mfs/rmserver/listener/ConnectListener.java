package com.mfs.rmserver.listener;

import com.mfs.rmserver.handler.RequestHandler;
import com.mfs.rmserver.service.FileService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ConnectListener implements Runnable{
    private int port = 8888;
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executorService = Executors.newFixedThreadPool(20, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            });
            System.out.println("正在监听" + port + "端口");
            while (!Thread.interrupted()) {
                Socket accept = serverSocket.accept();
                //System.out.println("收到来自" + accept.getLocalAddress() + ":" + accept.getLocalPort() + "的连接请求");
                Future<?> submit = executorService.submit(new RequestHandler(accept,new FileService()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
