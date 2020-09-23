package com.mfs.rmclient;

import com.mfs.rmcommon.po.Request;
import com.mfs.rmcommon.po.RequestType;
import com.mfs.rmcommon.po.Response;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;


public class RmClientApplication {

    public static void main(String[] args) {
        for (int i = 0; i < 1; i ++) {
            new Thread(new TestTask(i)).start();
        }
    }

    static class TestTask implements Runnable {
        private int id;
        public TestTask(int id) {
            this.id = id;
        }
        @Override
        public void run() {


        }
    }

}
