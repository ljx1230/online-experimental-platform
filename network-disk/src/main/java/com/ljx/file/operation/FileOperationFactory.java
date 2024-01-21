package com.ljx.file.operation;

import com.ljx.file.operation.delete.Deleter;
import com.ljx.file.operation.download.Downloader;
import com.ljx.file.operation.upload.Uploader;

/**
 * @Author: ljx
 * @Date: 2023/12/21 21:43
 */
public interface FileOperationFactory {
    Uploader getUploader();
    Downloader getDownLoader();
    Deleter getDeleter();
}
