package com.ljx.file.operation.delete.product;

import com.ljx.file.operation.delete.Deleter;
import com.ljx.file.operation.domain.DeleteFile;
import com.ljx.file.util.FileUtil;
import com.ljx.file.util.PathUtil;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:55
 */
@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        File file = new File(PathUtil.getStaticPath() + deleteFile.getFileUrl());
        if(file.exists()) {
            file.delete();
        }
        if(FileUtil.isImageFile(FileUtil.getFileExtendName(deleteFile.getFileUrl()))) {
            File minFile = new File(PathUtil.getStaticPath() + deleteFile.getFileUrl().replace(deleteFile.getTimeStampName(), deleteFile.getTimeStampName() + "_min"));
            if (minFile.exists()) {
                minFile.delete();
            }
        }
    }
}
