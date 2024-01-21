package com.ljx.file.operation.download;

import com.ljx.file.operation.domain.DownloadFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:25
 */
public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile);
}
