package com.mfs.rmclient.factory;

import com.mfs.rmclient.manager.ResourceManager;

import java.io.InputStream;

public class ResourceManagerBuilder {
    public static ResourceManager build(InputStream inputStream){
        return ResourceManager.getManager();
    }
}
