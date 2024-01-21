package com.ljx.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ljx.file.common.RestResult;
import com.ljx.file.dto.*;
import com.ljx.file.model.File;
import com.ljx.file.model.User;
import com.ljx.file.model.UserFile;
import com.ljx.file.service.FileService;
import com.ljx.file.service.UserService;
import com.ljx.file.service.UserfileService;
import com.ljx.file.util.DateUtil;
import com.ljx.file.vo.TreeNodeVo;
import com.ljx.file.vo.UserfileListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author: ljx
 * @Date: 2023/12/21 13:34
 */
@Tag(name = "file", description = "该接口为文件接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserfileService userfileService;

    @Operation(summary = "创建文件", description = "目录(文件夹)的创建", tags = {"file"})
    @PostMapping("/createfile")
    public RestResult<String> creatFile(@RequestBody CreateFileDTO createFileDto, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token); // 获取已经登录用户的信息
        if (loginUser == null) {
            return RestResult.fail().message("用户认证失败,请先登录");
        }
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFile::getFileName, createFileDto.getFileName())
                .eq(UserFile::getFilePath, createFileDto.getFilePath())
                .eq(UserFile::getUserId, loginUser.getUserId());
        List<UserFile> userFiles = userfileService.list(wrapper);
        if (!userFiles.isEmpty()) {
            return RestResult.fail().message("文件名不能重复重复");
        }
        UserFile userFile = new UserFile();
        BeanUtils.copyProperties(createFileDto, userFile);
        userFile.setUserId(loginUser.getUserId());
        userFile.setIsDir(1); // 网盘中只能创建目录
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userfileService.save(userFile);
        return RestResult.success();
    }

    @Operation(summary = "获取文件列表", description = "用来做前台文件列表展示", tags = {"file"})
    @GetMapping(value = "/getfilelist")
    public RestResult<Map<String, Object>> getUserfileList(UserfileListDTO userfileListDTO, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            return RestResult.fail().message("用户认证失败,请先登录");
        }
        List<UserfileListVO> fileList = userfileService.getUserFileByFilePath(userfileListDTO.getFilePath(), loginUser.getUserId(), userfileListDTO.getCurrentPage(), userfileListDTO.getPageCount());
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();

        userFileLambdaQueryWrapper
                .eq(UserFile::getUserId, loginUser.getUserId())
                .eq(UserFile::getFilePath, userfileListDTO.getFilePath())
                .eq(UserFile::getDeleteFlag, 0);

        int total = userfileService.count(userFileLambdaQueryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", fileList);

        return RestResult.success().data(map);
    }

    @Operation(summary = "通过文件类型选择文件", description = "该接口可以实现文件格式分类查看", tags = {"file"})
    @GetMapping(value = "/selectfilebyfiletype")
    public RestResult<List<Map<String, Object>>> selectFileByFileType(int fileType, Long currentPage, Long pageCount, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            return RestResult.fail().message("用户认证失败,请先登录");
        }
        Map<String, Object> data = userfileService.getUserFileByType(fileType, loginUser.getUserId(), currentPage, pageCount);
        return RestResult.success().data(data);
    }

    @Operation(summary = "删除文件", description = "可以删除文件或者目录", tags = {"file"})
    @PostMapping("/deletefile")
    public RestResult deleteFile(@RequestBody DeleteFileDTO deleteFileDto, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            RestResult.fail().message("用户认证失败,请先登录");
        }
        userfileService.deleteUserFile(deleteFileDto.getUserFileId(), loginUser.getUserId());
        return RestResult.success();
    }

    @Operation(summary = "批量删除文件", description = "批量删除文件", tags = {"file"})
    @PostMapping("/batchdeletefile")
    public RestResult deleteBatchByIds(@RequestBody BatchDeleteFileDTO batchDeleteFileDTO, @RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            RestResult.fail().message("用户认证失败,请先登录");
        }
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDTO.getFiles(), UserFile.class);
        userFiles.forEach((userFile -> userfileService.deleteUserFile(userFile.getUserFileId(), loginUser.getUserId())));
        return RestResult.success().message("批量删除文件成功");
    }

    @Operation(summary = "获取文件树", description = "文件移动的时候需要用到该接口，用来展示目录树", tags = {"file"})
    @GetMapping("/getfiletree")
    public RestResult<TreeNodeVo> getFileTree(@RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            RestResult.fail().message("用户认证失败,请先登录");
        }
        UserFile userFile = new UserFile();
        Long userId = loginUser.getUserId();
        userFile.setUserId(userId);
        List<UserFile> filePathList = userfileService.selectFilePathTreeByUserId(userId);
        TreeNodeVo resultTreeNode = new TreeNodeVo();
        resultTreeNode.setLabel("/");
        for (int i = 0; i < filePathList.size(); i++) {
            String filePath = filePathList.get(i).getFilePath() + filePathList.get(i).getFileName() + "/";
            Queue<String> queue = new LinkedList<>();
            String[] strs = filePath.split("/");
            for (int j = 0; j < strs.length; j++) {
                if (!"".equals(strs[j]) && strs[j] != null) {
                    queue.add(strs[j]);
                }
            }
            if (queue.size() == 0) {
                continue;
            }
            resultTreeNode = insertTreeNode(resultTreeNode, "/", queue);
        }
        return RestResult.success().data(resultTreeNode);
    }

    @Operation(summary = "文件移动", description = "可以移动文件或者目录", tags = { "file" })
    @PostMapping("/movefile")
    public RestResult<String> moveFile(@RequestBody MoveFileDTO moveFileDto, @RequestHeader("token") String token) {
        User sessionUser = userService.getUserByToken(token);
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();

        userfileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName, sessionUser.getUserId());
        return RestResult.success();

    }

    @Operation(summary = "批量移动文件", description = "可以同时选择移动多个文件或者目录", tags = { "file" })
    @PostMapping("/batchmovefile")
    public RestResult<String> batchMoveFile(@RequestBody BatchMoveFileDTO batchMoveFileDto,
                                            @RequestHeader("token") String token) {

        User sessionUser = userService.getUserByToken(token);
        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();
        List<UserFile> userFiles = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : userFiles) {
            userfileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(),
                    userFile.getExtendName(), sessionUser.getUserId());
        }

        return RestResult.success().data("批量移动文件成功");

    }

    private TreeNodeVo insertTreeNode(TreeNodeVo treeNode, String filePath, Queue<String> nodeNameQueue) {

        List<TreeNodeVo> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null) {
            return treeNode;
        }

        Map<String, String> map = new HashMap<>();
        filePath = filePath + currentNodeName + "/";
        map.put("filePath", filePath);

        if (!isExistPath(childrenTreeNodes, currentNodeName)) {  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNodeVo resultTreeNode = new TreeNodeVo();


            resultTreeNode.setAttributes(map);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            // resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        } else {  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNodeVo childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())) {
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }

            }
        } else {
            treeNode.setChildren(childrenTreeNodes);
        }
        return treeNode;
    }

    private boolean isExistPath(List<TreeNodeVo> childrenTreeNodes, String path) {
        boolean isExistPath = false;
        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {
                if (path.equals(childrenTreeNodes.get(i).getLabel())) {
                    isExistPath = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExistPath;
    }

    @Operation(tags = {"file"},summary = "文件重命名",description = "文件重命名")
    @PostMapping("/renamefile")
    public RestResult renameFile(@RequestBody RenameFileDTO renameFileDTO,@RequestHeader("token") String token) {
        User loginUser = userService.getUserByToken(token);
        if (loginUser == null) {
            RestResult.fail().message("用户认证失败,请先登录");
        }
        UserFile userFile = userfileService.getById(renameFileDTO.getUserFileId());
        List<UserFile> userFileList = userfileService.selectUserFileByNameAndPath(renameFileDTO.getFileName(), userFile.getFilePath(), loginUser.getUserId());
        if(userFileList != null && !userFileList.isEmpty()) {
            return RestResult.fail().message("同名文件已存在");
        }
        if(1 == userFile.getIsDir()) {
            // 修改的文件名是文件夹
            LambdaUpdateWrapper<UserFile> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserFile::getFileName,renameFileDTO.getFileName())
                    .set(UserFile::getUploadTime,DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId,renameFileDTO.getUserFileId());
            userfileService.update(updateWrapper);
            userfileService.replaceUserFilePath(userFile.getFilePath() + renameFileDTO.getFileName() + "/",
                    userFile.getFilePath() + userFile.getFileName() + "/", loginUser.getUserId());
        }
        // 非目录
        // File file = fileService.getById(userFile.getFileId());
        LambdaUpdateWrapper<UserFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserFile::getFileName,renameFileDTO.getFileName())
                .set(UserFile::getUploadTime,DateUtil.getCurrentTime())
                .eq(UserFile::getUserFileId,renameFileDTO.getUserFileId());
        userfileService.update(updateWrapper);
        return RestResult.success();
    }

}
