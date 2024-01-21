package com.ljx.file.operation.download.product;

import com.ljx.file.operation.domain.DownloadFile;
import com.ljx.file.operation.download.Downloader;
import com.ljx.file.util.PathUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:38
 */
@Component
public class LocalStorageDownLoader extends Downloader {
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        BufferedInputStream bufferedInputStream = null;
        byte[] buf = new byte[1024];
        // 设置文件路径
        File file = new File(PathUtil.getStaticPath() + downloadFile.getFileUrl());
        if(file.exists()) {
            FileInputStream fileInputStream = null;
            try{
               fileInputStream = new FileInputStream(file);
               bufferedInputStream = new BufferedInputStream(fileInputStream);
                OutputStream outputStream = httpServletResponse.getOutputStream();
                int size = bufferedInputStream.read(buf);
                while(size != -1) {
                    outputStream.write(buf,0,size);
                    size = bufferedInputStream.read(buf);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
