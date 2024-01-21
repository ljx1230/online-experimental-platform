package com.ljx.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.file.common.RestResult;
import com.ljx.file.dto.DownloadFileDTO;
import com.ljx.file.dto.UploadFileDTO;
import com.ljx.file.mapper.StorageMapper;
import com.ljx.file.model.File;
import com.ljx.file.model.Storage;
import com.ljx.file.model.User;
import com.ljx.file.model.UserFile;
import com.ljx.file.operation.domain.UploadFile;
import com.ljx.file.service.FileService;
import com.ljx.file.service.FiletransferService;
import com.ljx.file.service.UserService;
import com.ljx.file.service.UserfileService;
import com.ljx.file.util.DateUtil;
import com.ljx.file.util.FileUtil;
import com.ljx.file.vo.UploadFileVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ljx
 * @Date: 2023/12/22 14:33
 */
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {
    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserfileService userfileService;

    @Autowired
    private FiletransferService filetransferService;

    @Autowired
    private StorageMapper storageMapper;

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @GetMapping("/uploadfile")
    public RestResult uploadFileSpeed(UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {
        return filetransferService.skipUpload(uploadFileDto,token);
    }

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @PostMapping("/uploadfile")
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if(loginUser == null) {
            return RestResult.fail().message("未登录");
        }
        filetransferService.uploadFile(request,uploadFileDto,loginUser.getUserId());
        // UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success();
    }

    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @GetMapping("/downloadfile")
    public void downLoadFile(HttpServletResponse response, DownloadFileDTO downloadFileDto) {
        filetransferService.downloadFile(response,downloadFileDto);
    }

    @Operation(summary = "获取存储信息",description = "获取存储信息",tags = {"filetransfer"})
    @GetMapping("/getstorage")
    public RestResult getStorage(@RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if(loginUser == null) {
            return RestResult.fail().message("未登录");
        }
        Long storageSize = filetransferService.selectStorageSizeByUserId(loginUser.getUserId());
        return RestResult.success().data(storageSize);
    }

    @Operation(summary = "获取用户总空间大小",description = "获取用户最大存储空间", tags = {"filetransfer"})
    @GetMapping("/getuserstorage")
    public RestResult getUserStorage(@RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if(loginUser == null) {
            return RestResult.fail().message("未登录");
        }
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Storage::getUserId,loginUser.getUserId());
        Long userMaxStorage = storageMapper.selectOne(wrapper).getStorageSize();
        return RestResult.success().data(userMaxStorage);
    }
}
