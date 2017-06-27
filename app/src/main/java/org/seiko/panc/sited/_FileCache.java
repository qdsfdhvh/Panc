package org.seiko.panc.sited;

import android.text.TextUtils;

import org.seiko.panc.manager.PathManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Seiko on 2017/6/22/022. Y
 */

class _FileCache {

    private static File getFile(String url) {
        String key_md5 = Util.md5(url);
        String path = key_md5.substring(0, 2);  //取md5前两位
        File dir2 = new File(PathManager.htmlPath, path); //生成当前html的路径
        return new File(dir2, key_md5);
    }

    static void save(String key, String html){
        File file = getFile(key);
        try {
            File parentFile =  file.getParentFile();
            boolean isCreate = parentFile.exists();
            while (!isCreate) {
                isCreate = parentFile.mkdirs();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(html);
            bw.flush();
            bw.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    static YhPair<String, Boolean> get(String key, YhNode node) {
        File file = getFile(key);
        if (file.exists()) {
            try {
                String html = toString(file);
                Long saveTime = file.lastModified();
                Long nowTime = new Date().getTime();
                Boolean isOutTime;
                switch (node.getCache()) {
                    case 0:
                        isOutTime = true;
                        break;
                    case 1:
                        isOutTime = false;
                        break;
                    default:
                        isOutTime = ((nowTime - saveTime) / 1000) > node.getCache();
                }
                return YhPair.create(html, isOutTime && TextUtils.isEmpty(html));
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return YhPair.create("", true);
    }

    private static String toString(File is) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(is));
        return doToString(in);
    }

    private static String doToString(BufferedReader in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null){
            buffer.append(line).append("\r\n");
        }
        return buffer.toString();
    }
}
