package com.mfs.resourcesmanagement.po;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SysFile implements Serializable {
    private File file;
    private SysFile parent;
    private String owner;

    /**
    * 以下数据要传往前端
    * */
    private String name;
    private String path;
    private boolean directory;
    private List<SysFile> list = new LinkedList<>();

    public SysFile(File file,SysFile parent, String owner) {
        this.file = file;
        this.parent =parent;
        this.owner = owner;
        name = file.getName();
        path = file.getAbsolutePath();
        directory = file.isDirectory();
    }

    //添加子文件
    public synchronized boolean addSubFile(SysFile file) {
        if (isDirectory()) {
            if (!list.contains(file)) {
                list.add(file);
                return true;
            } else {
                deleteFile(file.getName());
                list.add(file);
            }
        }
        return false;
    }

    //删除子文件
    public synchronized boolean deleteFile(String name) {
        if (isDirectory()) {
            Iterator<SysFile> iterator = list.iterator();
            while (iterator.hasNext()) {
                SysFile next = iterator.next();
                if (next.getName().equals(name)) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    //获取指定子文件
    public SysFile getSubFile(String name) {
        if (isDirectory()) {
            for (SysFile f : list) {
                if (f.getName().equals(name)) {
                    return f;
                }
            }
        }
        return null;
    }

    //相对路径获取指定文件
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

    //删除文件
    public synchronized boolean delete(){
        if (isDirectory()) {
            while (!list.isEmpty()){
                list.get(0).delete();
            }
            deleteFile();
        } else {
            deleteFile();
        }
        return true;
    }

    private boolean deleteFile(){
        SysFile parent = getParent();
        if (parent != null) {
            file.delete();
            return parent.list.remove(this);
        } else {
            return false;
        }
    }

    public synchronized boolean rename(String name) {
        File f = new File(this.file.getParent() + "/" + name);
        boolean b = this.file.renameTo(f);
        if (b) {
            file = f;
            this.name = name;
            this.path = f.getPath();
            return true;
        } else {
            return false;
        }
    }


    /**
    * getter，setter方法
    * */
    //返回父文件，顶层文件的父文件位null
    private SysFile getParent(){
        return parent;
    }

    //判断是否是目录
    public boolean isDirectory() {
        return directory;
    }

    //判断是否是文件
    private boolean isFile() {
        return file.isFile();
    }

    //获取名称
    public String getName() {
        return name;
    }

    public String getOwner(){
        return owner;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }


    public List<SysFile> getList() {
        return list;
    }

    public void setList(List<SysFile> list) {
        this.list = list;
    }

    public void setParent(SysFile parent) {
        this.parent = parent;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

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
        return Objects.hash(file, list);
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
