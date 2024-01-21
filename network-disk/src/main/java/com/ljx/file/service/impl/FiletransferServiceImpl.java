package com.ljx.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.file.common.RestResult;
import com.ljx.file.controller.FiletransferController;
import com.ljx.file.dto.DownloadFileDTO;
import com.ljx.file.dto.UploadFileDTO;
import com.ljx.file.mapper.FileMapper;
import com.ljx.file.mapper.UserfileMapper;
import com.ljx.file.model.File;
import com.ljx.file.model.User;
import com.ljx.file.model.UserFile;
import com.ljx.file.operation.LocalStorageOperationFactory;
import com.ljx.file.operation.domain.DownloadFile;
import com.ljx.file.operation.domain.UploadFile;
import com.ljx.file.operation.download.Downloader;
import com.ljx.file.operation.upload.Uploader;
import com.ljx.file.service.FileService;
import com.ljx.file.service.FiletransferService;
import com.ljx.file.service.UserService;
import com.ljx.file.util.DateUtil;
import com.ljx.file.util.FileUtil;
import com.ljx.file.util.PropertiesUtil;
import com.ljx.file.vo.UploadFileVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ljx
 * @Date: 2023/12/21 21:49
 */
@Service
public class FiletransferServiceImpl implements FiletransferService {
    @Autowired
    private LocalStorageOperationFactory localStorageOperationFactory;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserfileMapper userfileMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {
        Uploader uploader = null;
        UploadFile uploadFile = new UploadFile();
        BeanUtils.copyProperties(uploadFileDto,uploadFile);

        String storageType = PropertiesUtil.getProperty("file.storage-type");
        synchronized (FiletransferService.class) {
            if ("0".equals(storageType)) {
                uploader = localStorageOperationFactory.getUploader();
            }
        }
        List<UploadFile> uploadFileList = uploader.upload(request,uploadFile);
        System.out.println(uploadFileList.size());
        for(int i = 0;i < uploadFileList.size();i++) {
            uploadFile = uploadFileList.get(i);
            File file = new File();
            file.setTimeStampName(uploadFile.getTimeStampName());
            file.setIdentifier(uploadFile.getIdentifier());
            file.setStorageType(Integer.parseInt(storageType));
            if(uploadFile.getSuccess() == 1) {
                file.setFileUrl(uploadFile.getUrl());
                file.setFileSize(uploadFile.getFileSize());
                file.setPointCount(1);
                fileMapper.insert(file);
                UserFile userFile = new UserFile();
                userFile.setFileId(file.getFileId());
                userFile.setExtendName(uploadFile.getFileType());
                userFile.setFileName(uploadFile.getFileName());
                userFile.setFilePath(uploadFileDto.getFilePath());
                userFile.setDeleteFlag(0);
                userFile.setUserId(userId);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userfileMapper.insert(userFile);
            }
        }
    }

    @Override
    public void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDto) {
        UserFile userFile =userfileMapper.selectById(downloadFileDto.getUserFileId());
        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        response.setContentType("application/force-download"); // 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName); // 设置文件名

        File file = fileMapper.selectById(userFile.getFileId());
        Downloader downloader = null;
        if(file.getStorageType() == 0) {
            downloader = localStorageOperationFactory.getDownLoader();
        }
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(file.getFileUrl());
        downloadFile.setTimeStampName(file.getTimeStampName());
        downloader.download(response,downloadFile);
    }

    @Override
    public Long selectStorageSizeByUserId(Long userId) {
        return userfileMapper.selectStorageSizeByUserId(userId);
    }

    @Override
    public RestResult skipUpload(UploadFileDTO uploadFileDto, String token) {
        User loginUser = userService.getUserByToken(token);
        if(loginUser == null) {
            return RestResult.fail().message("未登录");
        }
        UploadFileVo uploadFileVo = new UploadFileVo();
        Map<String,Object> param = new HashMap<>();
        param.put("identifier",uploadFileDto.getIdentifier());
        synchronized (FiletransferController.class) {
            List<File> list = fileService.listByMap(param);
            if(list != null && !list.isEmpty()) {
                File file = list.get(0);

                UserFile userFile = new UserFile();
                userFile.setFileId(file.getFileId());
                userFile.setUserId(loginUser.getUserId());
                userFile.setFilePath(uploadFileDto.getFilePath());
                String fileName = uploadFileDto.getFilename();
                userFile.setFileName(FileUtil.getFileNameNoExtend(fileName));
                userFile.setExtendName(FileUtil.getFileExtendName(fileName));
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setDeleteFlag(0);
                LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
                System.out.println(userFile);
                wrapper.eq(UserFile::getFileName,userFile.getFileName())
                                .eq(UserFile::getFilePath,userFile.getFilePath())
                                        .eq(UserFile::getExtendName,userFile.getExtendName());
                List<UserFile> userFiles = userfileMapper.selectList(wrapper);
                if(userFiles == null || userFiles.isEmpty()) {
                    System.out.println("insert");
                    userfileMapper.insert(userFile);
                } else {
                    System.out.println("update");
                    userFile.setUserFileId(userFiles.get(0).getUserFileId());
                    userFile.setDeleteTime("");
                    userFile.setDeleteBatchNum("");
                    userfileMapper.updateById(userFile);
                }
                uploadFileVo.setSkipUpload(true);
            } else {
                uploadFileVo.setSkipUpload(false);
            }
        }
        return RestResult.success().data(uploadFileVo);
    }

}
