package com.mfs.rmclient.manager;

import com.mfs.rmcommon.po.Request;
import com.mfs.rmcommon.po.RequestType;
import com.mfs.rmcommon.po.Response;

import java.io.*;
import java.net.Socket;

public class ResourceManager {
    private long id = 0;
    private static int port = 8888;
    private static String host = "127.0.0.1";
    private static ResourceManager manager;
    private ResourceManager() {
    }
    public static ResourceManager getManager() {
        if (manager == null) {
            synchronized (ResourceManager.class) {
                if (manager == null) {
                    manager = new ResourceManager();
                }
            }
        }
        return manager;
    }

    public boolean createDirectory(String path,String name) {
        Response response = request(new Request().setPath(path).setName(name));
        return response.getCode() == 200 ? true : false;
    }

    public boolean addDocument(String path,String name,InputStream inputStream) {
        BufferedReader br = null;
        try {
             br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            Response request = request(new Request().setPath(path).setName(name).setContent(sb.toString()));
            return request.getCode() == 200 ? true : false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }

    }

    public boolean deleteFile(String path) {
        Response request = request(new Request().setPath(path));
        return request.getCode() == 200 ? true : false;
    }

    public boolean renameFile(String path,String name) {
        Response request = request(new Request().setPath(path).setName(name));
        return request.getCode() == 200 ? true : false;
    }

    public InputStream getDocumentAsInputStream(String path) {
        Response response = request(new Request().setPath(path));
        return response.inputStream();
    }

    private Response request(Request request) {
        Socket socket = null;
        ObjectOutput oo = null;
        ObjectInput oi = null;
        try {
            socket = new Socket(host,port);
            oo = new ObjectOutputStream(socket.getOutputStream());
            oo.writeObject(request);
            oo.flush();

            oi = new ObjectInputStream(socket.getInputStream());
            Response response = (Response)oi.readObject();
            return initResponse(response);
        } catch (IOException | ClassNotFoundException e) {
            return null;
        } finally {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
                oo.close();
                oi.close();
                socket.close();
            } catch (IOException e) {
            }

        }
    }

    private Response initResponse(Response response) {
        id ++;
        OutputStream os = null;
        try {
            //创建临时文件
            if (response.getContent() != null) {
                File file = new File("temFile" + id);
                os = new FileOutputStream(file);
                os.write(response.getContent().getBytes("utf8"));
                os.flush();
                response.setTemFile(file);
                response.setContent(null);
            }
            return response;
        } catch (IOException e) {
            return response;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
