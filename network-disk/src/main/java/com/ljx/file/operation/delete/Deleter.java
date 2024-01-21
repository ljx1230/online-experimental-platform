package com.ljx.file.operation.delete;

import com.ljx.file.operation.domain.DeleteFile;

/**
 * @Author: ljx
 * @Date: 2023/12/21 20:54
 */
public abstract class Deleter {
    public abstract void delete(DeleteFile deleteFile);
}
