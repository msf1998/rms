package com.mfs.rmserver;

import com.mfs.rmserver.listener.ConnectListener;
import com.mfs.rmserver.main.ListenerManager;

//@SpringBootApplication
//@ComponentScan(basePackages = {"com.mfs.rmcore","com.mfs.rmserver"})
//@MapperScan(basePackages = {"com.mfs.rmcore.dao","com.mfs.rmcore.mapper"})
public class RmServerApplication {

    public static void main(String[] args) {
        ListenerManager.beginListen();
        //SpringApplication.run(RmServerApplication.class, args);
    }

}
