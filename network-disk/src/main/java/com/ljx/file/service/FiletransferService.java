package com.ljx.file.service;

import com.ljx.file.common.RestResult;
import com.ljx.file.dto.DownloadFileDTO;
import com.ljx.file.dto.UploadFileDTO;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: ljx
 * @Date: 2023/12/21 21:47
 */
public interface FiletransferService {
    void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto,Long userId);
    void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDto);
    Long selectStorageSizeByUserId(Long userId);
    RestResult skipUpload(UploadFileDTO uploadFileDto, String token);
}
