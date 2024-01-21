package com.ljx.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.file.mapper.FileMapper;
import com.ljx.file.model.File;
import com.ljx.file.service.FileService;
import org.springframework.stereotype.Service;

/**
 * @Author: ljx
 * @Date: 2023/12/20 20:01
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
}
