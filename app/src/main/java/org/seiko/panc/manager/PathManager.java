package org.seiko.panc.manager;

import android.text.TextUtils;

import org.seiko.panc.App;

import java.util.Locale;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class PathManager {
    /* 基本路径 */
    private static final String basePath = App.getInstance().getBasePath();
    /* 插件 */
    public static final String sitePath = basePath + "siteD/";
    /* 缓存 */
    public static final String cachePath = basePath + "cache/";
    /* 下载 */
    public static final String downloadPath = basePath + "download/";
    /* 插件html缓存 */
    public static final String htmlPath = cachePath + "html/";

    public static String getSitePath(String source) {
        if (TextUtils.isEmpty(source)) {
            return null;
        }
        if (!source.endsWith(".sited")) {
            source = source + ".sited";
        }
        return sitePath + source;
    }

    public static String getBookPath(String source, String title) {
        return downloadPath + Fo(source) + "/" + Fo(title) + "/";
    }

    public static String getSectionPath(String source, String title, String name) {
        return downloadPath + Fo(source) + "/" + Fo(title) + "/" + Fo(name) + "/";
    }

    /* 下载路径：章节的每个图片文件 */
    public static String getSectionImgPath(String path, int index) {
        return path + getFilename(index + 1);
    }

    //图片保存名称
    private static String getFilename(int index) {
        return String.format(Locale.US, "%03d.jpg", index);
    }

    //ComicBean
    public static String getBookBean(String source, String title) {
        return getBookPath(source, title) + "Comic.json";
    }

    //DownBean
    public static String getSectionBean(String source, String title, String name) {
        return getSectionPath(source, title, name) + "Section.json";
    }

    //去除多余的斜杠
    private static String Fo(String name) {return name.replaceAll("/", "_");}
}
