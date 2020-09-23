package com.mfs.rmserver.main;

import com.mfs.rmserver.listener.ConnectListener;

public class ListenerManager {
    static Thread thread;
    public static void beginListen() {
        thread = new Thread(new ConnectListener());
    }
    public static void stopListen() {
        thread.interrupt();
    }
}
