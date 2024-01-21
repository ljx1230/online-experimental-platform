package com.ljx.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljx.file.model.UserFile;
import com.ljx.file.vo.UserfileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: ljx
 * @Date: 2023/12/19 16:45
 */
public interface UserfileMapper extends BaseMapper<UserFile> {
    List<UserfileListVO> userfileList(UserFile userfile, Long beginCount, Long pageCount);
    List<UserfileListVO> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    List<UserfileListVO> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    void updateFilepathByFilepath(@Param("oldfilePath") String oldfilePath,@Param("newfilePath") String newfilePath, @Param("userId")Long userId);
    void replaceFilePath(@Param("filePath") String filePath,@Param("oldfilePath") String oldfilePath, @Param("userId")Long userId);
    Long selectStorageSizeByUserId(Long userId);

}
