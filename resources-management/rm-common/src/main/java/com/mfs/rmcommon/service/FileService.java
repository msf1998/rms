package com.mfs.rmcommon.service;

import com.mfs.rmcommon.po.Request;
import com.mfs.rmcommon.po.Response;

import java.io.IOException;

public interface FileService {

    Response createDirectory(Request request);

    Response createDocument(Request request);

    Response deleteFile(Request request);

    Response renameFile(Request request);

    Response listFiles(Request request);
}
