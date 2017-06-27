package org.seiko.panc.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class FileUtil {

    //=========================================
    /** Gson读取 */
    @Nullable
    public static <T> T get(String file, Class<T> cls) {
        String json = FileUtil.readTextFromSDcard(file);
        if (json != null) {
            return new Gson().fromJson(json, cls);
        }
        return null;
    }

    @Nullable
    public static <T> T get(String file, Type type) {
        String json = FileUtil.readTextFromSDcard(file);
        if (json != null) {
            return new Gson().fromJson(json, type);
        }
        return null;
    }

    /** Gson保存 */
    public static <T> boolean save(String path, T model) {
        String json = new Gson().toJson(model);
        if (json != null) {
            FileUtil.saveText2Sdcard(path, json);
            return true;
        }
        return false;
    }

    //=========================================
    public static  Observable<List<File>> loadFile(String path) {
        File file = new File(path);
        File[] strs = new File[] {};
        if (file.exists()) {
            strs = file.listFiles();
        }
        return Observable.fromArray(strs)
                .sorted(new FileComparator())
                .toList().toObservable();
    }

    /* 时间排序 */
    private static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1;// 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

    public static String readTextFromSDcard(String str) {
        return readTextFromSDcard(new File(str));
    }

    @Nullable
    public static String readTextFromSDcard(File file) {
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            int availableLength = fileInputStream.available();
            byte[] buffer = new byte[availableLength];
            fileInputStream.read(buffer);
            fileInputStream.close();

            return new String(buffer, "UTF-8");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveText2Sdcard(String fileName, String text) {
        File file = new File(fileName);
        File parentFile =  file.getParentFile();
        boolean isCreate = parentFile.exists();
        while (!isCreate) {
            isCreate = parentFile.mkdirs();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(text);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            file.delete();
            return false;
        }
        return true;
    }

    public static boolean saveText2Sdcard(String fileName, InputStream byteStream) {
        File file = new File(fileName);
        File parentFile =  file.getParentFile();
        boolean isCreate = parentFile.exists();
        while (!isCreate) {
            isCreate = parentFile.mkdirs();
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            int length;
            byte[] buffer = new byte[1024];
            while ((length = byteStream.read(buffer)) != -1){
                out.write(buffer, 0, length);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(out, byteStream);
        }
        return false;
    }

    private static void closeStream(Closeable... stream) {
        for (Closeable closeable : stream) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //==========================================
    /** 删除 */
    public static void deleteFile(String file) {deleteFile(new File(file));}

    private static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (File file1:files) { // 遍历目录下所有的文件
                    deleteFile(file1); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
            if (file.getParentFile().listFiles().length == 0) {
                file.getParentFile().delete();
            }
        }
    }

    //==========================================
    @NonNull
    public static String toString(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        return doToString(in);
    }

    @NonNull
    private static String doToString(BufferedReader in) throws IOException{
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null){
            buffer.append(line);
        }
        return buffer.toString();
    }
}
