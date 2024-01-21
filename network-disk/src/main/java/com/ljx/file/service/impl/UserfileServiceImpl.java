package com.ljx.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.file.mapper.FileMapper;
import com.ljx.file.mapper.RecoveryFileMapper;
import com.ljx.file.mapper.UserfileMapper;
import com.ljx.file.model.File;
import com.ljx.file.model.RecoveryFile;
import com.ljx.file.model.UserFile;
import com.ljx.file.service.UserfileService;
import com.ljx.file.util.DateUtil;
import com.ljx.file.vo.UserfileListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.ljx.file.constant.FileConstant.*;

/**
 * @Author: ljx
 * @Date: 2023/12/20 20:01
 */
@Slf4j
@Service
public class UserfileServiceImpl extends ServiceImpl<UserfileMapper, UserFile> implements UserfileService {
    public static Executor executors = Executors.newFixedThreadPool(20);
    @Autowired
    private UserfileMapper userfileMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private RecoveryFileMapper recoveryFileMapper;

    @Override
    public List<UserfileListVO> getUserFileByFilePath(String filePath, Long userId, Long currentPage, Long pageCount) {
        Long beginCount = (currentPage - 1) * pageCount;
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFilePath(filePath);
        return userfileMapper.userfileList(userFile, beginCount, pageCount);
    }

    @Override
    public Map<String, Object> getUserFileByType(int fileType, Long userId, Long currentPage, Long pageCount) {
        Long beginCount = (currentPage - 1) * pageCount;
        List<UserfileListVO> fileList = null;
        Long total = 0L;
        if(fileType == OTHER_TYPE) {
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(DOC_FILE));
            list.addAll(Arrays.asList(IMG_FILE));
            list.addAll(Arrays.asList(VIDEO_FILE));
            list.addAll(Arrays.asList(MUSIC_FILE));
            fileList = userfileMapper.selectFileNotInExtendNames(list,beginCount,pageCount,userId);
            total = userfileMapper.selectCountNotInExtendNames(list,beginCount,pageCount,userId);
        } else {
            List<String> list = null;
            if (fileType == IMAGE_TYPE) {
                list = Arrays.asList(IMG_FILE);
            } else if (fileType == DOC_TYPE) {
                list = Arrays.asList(DOC_FILE);
            } else if (fileType == VIDEO_TYPE) {
                list = Arrays.asList(VIDEO_FILE);
            } else if (fileType == MUSIC_TYPE) {
                list = Arrays.asList(MUSIC_FILE);
            }
            fileList = userfileMapper.selectFileByExtendName(list, beginCount, pageCount,userId);
            total = userfileMapper.selectCountByExtendName(list, beginCount, pageCount,userId);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("list",fileList);
        map.put("total",total);
        return map;
    }

    @Override
    public void deleteUserFile(Long userFileId, Long userId) {
        UserFile userFile = userfileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if(userFile.getIsDir() == 1) {
            // 删除目录
            LambdaUpdateWrapper<UserFile> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserFile::getDeleteFlag,1)
                    .set(UserFile::getDeleteBatchNum,uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getFileId,userFileId);
            userfileMapper.update(null,updateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            // 删除目录
            updateFileDeleteStateByFilePath(filePath, userFile.getDeleteBatchNum(), userId);
        } else {
            // 删除文件
            LambdaUpdateWrapper<UserFile> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserFile::getDeleteFlag,1)
                    .set(UserFile::getDeleteBatchNum,uuid)
                    .set(UserFile::getDeleteTime,DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId,userFileId);
            userfileMapper.update(null,updateWrapper);
        }
        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteBatchNum(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFileMapper.insert(recoveryFile);
    }

    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        new Thread(() -> {
            List<UserFile> fileList = selectFileTreeListLikeFilePath(filePath,userId);
            fileList.forEach((userFile -> {
                executors.execute(() -> {
                    LambdaUpdateWrapper<UserFile> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.set(UserFile::getDeleteFlag,1)
                            .set(UserFile::getDeleteBatchNum,deleteBatchNum)
                            .set(UserFile::getDeleteTime,DateUtil.getCurrentTime())
                            .eq(UserFile::getUserId,userId);
                    userfileMapper.update(null,wrapper);
                });
            }));
        }).start();
    }

    @Override
    public List<UserFile> selectFileTreeListLikeFilePath(String filePath, Long userId) {
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        //userFile.setFilePath(filePath);

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("查询文件路径：" + filePath);

        lambdaQueryWrapper.eq(UserFile::getUserId, userId).likeRight(UserFile::getFilePath, filePath);
        return userfileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFile::getUserId,userId).eq(UserFile::getIsDir,1);
        return userfileMapper.selectList(wrapper);
    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, Long userId) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
        lambdaUpdateWrapper.set(UserFile::getFilePath, newfilePath)
                .eq(UserFile::getFilePath, oldfilePath)
                .eq(UserFile::getFileName, fileName)
                .eq(UserFile::getUserId, userId);
        if (StringUtils.isNotEmpty(extendName)) {
            lambdaUpdateWrapper.eq(UserFile::getExtendName, extendName);
        } else {
            lambdaUpdateWrapper.isNull(UserFile::getExtendName);
        }
        userfileMapper.update(null, lambdaUpdateWrapper);
        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userfileMapper.updateFilepathByFilepath(oldfilePath, newfilePath, userId);
        }
    }

    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFile::getFileName,fileName)
                .eq(UserFile::getFilePath,filePath)
                .eq(UserFile::getUserId,userId)
                .eq(UserFile::getDeleteFlag,0);
        return userfileMapper.selectList(wrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userfileMapper.replaceFilePath(filePath,oldFilePath,userId);
    }
}
