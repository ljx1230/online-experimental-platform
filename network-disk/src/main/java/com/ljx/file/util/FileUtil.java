package com.ljx.file.util;


/**
 * @Author: ljx
 * @Date: 2023/12/21 15:57
 */
public class FileUtil {
    public static final String[] IMG_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg"};
    public static final String[] DOC_FILE = {"doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp", "wps", "rtf", "html", "pdf"};
    public static final String[] VIDEO_FILE = {"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] MUSIC_FILE = {"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;
    public static final int SHARE_FILE = 6;
    public static final int RECYCLE_FILE = 7;

    /**
     * 判断文件是否为图片
     * @param extendName
     * @return
     */
    public static boolean isImageFile(String extendName) {
        for(int i = 0;i < IMG_FILE.length;i++) {
            if(extendName.equalsIgnoreCase(IMG_FILE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件是否为视频
     * @param extendName
     * @return
     */
    public static boolean isVideoFile(String extendName) {
        for(int i = 0;i < VIDEO_FILE.length;i++) {
            if(extendName.equalsIgnoreCase(VIDEO_FILE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件是否为文档
     * @param extendName
     * @return
     */
    public static boolean isDocFile(String extendName) {
        for(int i = 0;i < DOC_FILE.length;i++) {
            if(extendName.equalsIgnoreCase(DOC_FILE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件是否为音频
     * @param extendName
     * @return
     */
    public static boolean isMusicFile(String extendName) {
        for(int i = 0;i < MUSIC_FILE.length;i++) {
            if(extendName.equalsIgnoreCase(MUSIC_FILE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名，如果文件没有扩展名，那么返回空字符串
     * @param fileName
     * @return
     */
    public static String getFileExtendName(String fileName) {
        if(fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取不带扩展名的文件名
     * @param fileName
     * @return
     */
    public static String getFileNameNoExtend(String fileName) {
        if(fileName.lastIndexOf(".") == -1) {
            return fileName;
        }
        return fileName.substring(0,fileName.lastIndexOf("."));
    }

}
