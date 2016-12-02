package com.ruby.preview.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jian on 2016/12/1.
 */
public class GetFileUtil {

    private static List<String> lstFile = new ArrayList<>(); //结果 List
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString();

    /**
     * @param Path        搜索目录
     * @param Extension   扩展名
     * @param IsIterative 是否进入子文件夹
     */
    private static void getFiles(String Path, String Extension, boolean IsIterative) {
        lstFile.clear();
        File[] files = new File(Path).listFiles();

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //判断扩展名
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) //忽略点文件（隐藏文件/文件夹）
                getFiles(f.getPath(), Extension, IsIterative);
        }
    }

    public static List<String> getFileList(String path, String extension, boolean isIterative) {
        getFiles(ROOT_PATH + path, extension, isIterative);
        return lstFile;
    }
}
