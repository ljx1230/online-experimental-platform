package com.ljx.file.operation;

import com.ljx.file.operation.delete.Deleter;
import com.ljx.file.operation.delete.product.LocalStorageDeleter;
import com.ljx.file.operation.download.Downloader;
import com.ljx.file.operation.download.product.LocalStorageDownLoader;
import com.ljx.file.operation.upload.Uploader;
import com.ljx.file.operation.upload.product.LocalStorageUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: ljx
 * @Date: 2023/12/21 21:44
 */
@Component
public class LocalStorageOperationFactory implements FileOperationFactory{
    @Autowired
    LocalStorageUploader localStorageUploader;
    @Autowired
    LocalStorageDownLoader localStorageDownLoader;
    @Autowired
    LocalStorageDeleter localStorageDeleter;

    @Override
    public Uploader getUploader() {
        return localStorageUploader;
    }

    @Override
    public Downloader getDownLoader() {
        return localStorageDownLoader;
    }

    @Override
    public Deleter getDeleter() {
        return localStorageDeleter;
    }
}
