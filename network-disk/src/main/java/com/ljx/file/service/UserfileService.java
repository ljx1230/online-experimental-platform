package com.ljx.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.file.model.UserFile;
import com.ljx.file.vo.UserfileListVO;

import java.util.List;
import java.util.Map;

/**
 * @Author: ljx
 * @Date: 2023/12/20 19:58
 */
public interface UserfileService extends IService<UserFile> {
    List<UserfileListVO> getUserFileByFilePath(String filePath,Long userId,Long currentPage,Long pageCount);
    Map<String,Object> getUserFileByType(int fileType,Long userId,Long currentPage,Long pageCount);
    void deleteUserFile(Long userFileId, Long userId);
    List<UserFile> selectFileTreeListLikeFilePath(String filePath, Long userId);

    List<UserFile> selectFilePathTreeByUserId(Long userId);

    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, Long userId);

    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);

}
