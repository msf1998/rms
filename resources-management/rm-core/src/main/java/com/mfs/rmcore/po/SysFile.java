package com.mfs.rmcore.po;

import java.io.*;
import java.io.File;
import java.util.*;

public class SysFile implements Serializable {
    /** File对象*/
    private volatile File file;
    /** 该文件的父文件*/
    private volatile SysFile parent;

    /**
    * 以下数据要传往前端，所以配备了相应的getter,setter方法
    * */
    /** 文件名*/
    private volatile String name;
    /** 该文件的所有者*/
    private volatile String owner;
    /** 文件路径，绝对路径*/
    private volatile String path;
    /** 是否是文件夹*/
    private volatile boolean directory;
    /** 子文件*/
    private volatile Set<SysFile> list = new HashSet<>();

    /**
     * getter setter
     * */
    public synchronized String getName() {
        return name;
    }

    public synchronized String getOwner(){
        return owner;
    }

    public synchronized String getPath() {
        return path;
    }
    public SysFile parent(){
        return parent;
    }

    public synchronized boolean isDirectory() {
        return directory;
    }

    public synchronized Set<SysFile> getList() {
        return list;
    }

    public synchronized void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public synchronized void setList(Set<SysFile> list) {
        this.list = list;
    }

    public synchronized void setParent(SysFile parent) {
        this.parent = parent;
    }

    public synchronized void setOwner(String owner) {
        this.owner = owner;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized void setPath(String path) {
        this.path = path;
    }

    /**
     * 唯一公共构造方法
     * */
    public SysFile(File file,SysFile parent, String owner) {
        this.file = file;
        this.parent =parent;
        this.owner = owner;
        name = file.getName();
        path = file.getAbsolutePath();
        directory = file.isDirectory();
    }

    /**
     * 为满足特定需求而建立的私有构造方法，如非特别了解该项目慎用
     * */
    private SysFile(String path,String name) {
        this.name = name;
        this.path = path;
    }

    public synchronized boolean createDocument(String name, InputStream is) {
        File newFile = new File( path + "/" + name);
        if (!newFile.exists()) {
            try {
                if (!newFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(newFile);
            byte[] bytes = new byte[1024];
            int n = -1;
            while ((n = is.read(bytes)) > 0) {
                os.write(bytes,0,n);
            }
            return addSubFile(new SysFile(newFile,this,owner));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 向父文件中添加子文件。
     * 如果父文件为文档则直接返回false，如果父文件为文件夹则添加该文件
     * @param file,要添加的文件对象
     * @return 添加成功返回true，添加失败返回false
     * */
    public synchronized boolean addSubFile(SysFile file) {
        if (isDirectory()) {
            if (!list.contains(file)) {
                return list.add(file);
            } else {
                return list.remove(file) && list.add(file);
            }
        }
        return false;
    }

    /**
     * 删除指定文件下的某个子文件
     * @param name,要删除的子文件名
     * @return true 或者 false
     * */
    public synchronized boolean deleteFile(String name) {
        if (isDirectory()) {
            return list.remove(new SysFile(path,name));
        }
        return false;
    }

    /**
     * 获取该文件的指定子文件
     * @param name, 文件名
     * @return SysFile对象,如果该文件为文档或者没有相应的子文件则返回null
     * */
    public SysFile getSubFile(String name) {
        if (isDirectory()) {
            for (SysFile f : list) {
                if (f.getName().equals(name)) {
                    return f;
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 或者去指定路径中的文件
     * @param path，相对该目录的相对路径，以'/'开头
     * @return SysFile 或者 null
     * */
    public SysFile getFile(String path) {
        return getFile(path.split("/"),1);
    }
    private SysFile getFile(String[] strings,int i) {
        if (i >= strings.length) return this;
        if (isDirectory()) {
            for (SysFile f : list) {
                if (f.getName().equals(strings[i])) {
                    return f.getFile(strings,i + 1);
                }
            }
        }
        return null;
    }

    /**
     * 删除该文件
     * @return true 或者 false
     * */
    public synchronized boolean delete(){
        if (isDirectory()) {  //文件夹
            Iterator<SysFile> iterator = list.iterator();
            while (iterator.hasNext()) {
                SysFile next = iterator.next();
                if (next.delete()) {
                    iterator.remove();
                } else {
                    return false;
                }
            }
            return deleteFile();
        } else {  //文档
            return deleteFile();
        }
    }

    private boolean deleteFile(){
        if (parent != null) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 重命名当前文件
     * @param name,新名字
     * @return true 或者 false
     * */
    public synchronized boolean rename(String name) {
        File f = new File(this.file.getParent() + "/" + name);

        if (this.file.renameTo(f)) {
            file = f;
            this.name = name;
            this.path = f.getPath();
            return true;
        } else {
            return false;
        }
    }


    /**
     * 重写方法
     * */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SysFile)) return false;
        SysFile f = (SysFile) o;
        if (getName().equals(f.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

    private static StringBuffer blank = new StringBuffer();
    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(blank.toString() +"name:" + getName() + " owner:" + getOwner() + "\n");
        blank.append("  ");
        for (SysFile f : list) {
            sb.append(f.toString());
        }
        blank.deleteCharAt(blank.length() - 1);
        blank.deleteCharAt(blank.length() - 1);
        return sb.toString();
    }
}
