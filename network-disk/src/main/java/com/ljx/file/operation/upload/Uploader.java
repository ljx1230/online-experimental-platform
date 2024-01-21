package com.ljx.file.operation.upload;

import com.ljx.file.exception.UploadException;
import com.ljx.file.operation.domain.UploadFile;
import com.ljx.file.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: ljx
 * @Date: 2023/12/21 18:43
 */
@Slf4j
public abstract class Uploader {
    public static final String ROOT_PATH = "upload";
    public static final String FILE_SEPARATOR = "/";
    // 文件大小限制，单位KB
    public final int maxSize = 10000000;

    public abstract List<UploadFile> upload(HttpServletRequest request,UploadFile uploadFile);

    protected String getFileName(String fileName) {
        if(!fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0,fileName.lastIndexOf("."));
    }

    protected String getTimeStampName() {
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            return "" + number.nextInt(10000) + System.currentTimeMillis();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成随机数失败");
        }
        return "" + System.currentTimeMillis();
    }

    protected String getSaveFilePath() {
        String path = ROOT_PATH;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        path = File.separator + path + FILE_SEPARATOR + formater.format(new Date());

        String staticPath = PathUtil.getStaticPath();
        File dir = new File(staticPath + path);
        if(!dir.exists()) {
            boolean success = dir.mkdirs();
            if(!success) {
                log.error("目录创建失败:" + PathUtil.getStaticPath() + path);
            }
        }
        return path;
    }

    public synchronized boolean checkUploadStatus(UploadFile param,File confFile) throws IOException {
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile,"rw");
        // 设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        // 设置偏移量
        confAccessFile.seek(param.getChunkNumber() - 1);
        // 将指定的一个字节写入文件中
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();
        //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是127
        for(int i = 0;i < completeStatusList.length;i++) {
            if (completeStatusList[i] != Byte.MAX_VALUE) {
                return false;
            }
        }
        confFile.delete();
        return true;
    }
}
