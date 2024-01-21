package com.ljx.file.operation.upload.product;

import com.ljx.file.exception.NotSameFileException;
import com.ljx.file.exception.UploadException;
import com.ljx.file.operation.domain.UploadFile;
import com.ljx.file.operation.upload.Uploader;
import com.ljx.file.util.FileUtil;
import com.ljx.file.util.PathUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @Author: ljx
 * @Date: 2023/12/21 19:21
 */
@Component
public class LocalStorageUploader extends Uploader {
    public LocalStorageUploader() {

    }

    /**
     * 实现文件的上传，服务器本地存储
     *
     * @param httpServletRequest
     * @param uploadFile
     * @return
     */
    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest, UploadFile uploadFile) {
        List<UploadFile> saveUploadFileList = new ArrayList<>();
        StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
        if (!isMultiPart) {
            try {
                throw new UploadException("未上传文件");
            } catch (UploadException e) {
                throw new RuntimeException(e);
            }
        }

        String savePath = getSaveFilePath();

        Iterator<String> iter = request.getFileNames();
        while (iter.hasNext()) {
            try {
                saveUploadFileList = doUpload(request, savePath, iter, uploadFile);
            } catch (IOException e) {
                try {
                    throw new UploadException("文件未上传");
                } catch (UploadException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (NotSameFileException e) {
                throw new RuntimeException(e);
            }
        }
        return saveUploadFileList;
    }

    private List<UploadFile> doUpload(StandardMultipartHttpServletRequest request, String savePath, Iterator<String> iter, UploadFile uploadFile) throws IOException, NotSameFileException {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        MultipartFile multipartFile = request.getFile(iter.next());

        String timeStampName = uploadFile.getIdentifier();

        String originalName = multipartFile.getOriginalFilename();

        String fileName = getFileName(originalName);
        String fileType = FileUtil.getFileExtendName(originalName);

        uploadFile.setFileName(fileName);
        uploadFile.setFileType(fileType);
        uploadFile.setTimeStampName(timeStampName);

        String saveFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType;
        String tempFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType + "_tmp";
        String minFilePath = savePath + FILE_SEPARATOR + timeStampName + "_min" + "." + fileType;
        String confFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + "conf";

        String prefixPath = PathUtil.getStaticPath() + FILE_SEPARATOR;

        File file = new File(prefixPath + saveFilePath);
        File tempFile = new File(prefixPath + tempFilePath);
        File minFile = new File(prefixPath + minFilePath);
        File confFile = new File(prefixPath + confFilePath);

        uploadFile.setUrl(saveFilePath);

        if(StringUtils.isEmpty(uploadFile.getTaskId())) {
            uploadFile.setTaskId(UUID.randomUUID().toString());
        }

        // 1.打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        // 2.打开通道
        FileChannel fileChannel = raf.getChannel();
        // 3.计算偏移量
        long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
        // 4.获取分片数据
        byte[] fileData = multipartFile.getBytes();
        // 5.写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名
        boolean isComplete = checkUploadStatus(uploadFile, confFile);
        if (isComplete) {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            String md5 = DigestUtils.md5DigestAsHex(fileInputStream);
            fileInputStream.close();
            if (StringUtils.isNotBlank(md5) && !md5.equals(uploadFile.getIdentifier())) {
                throw new NotSameFileException();
            }
            tempFile.renameTo(file);
            if (FileUtil.isImageFile(uploadFile.getFileType())){
                Thumbnails.of(file).size(300, 300).toFile(minFile);
            }

            uploadFile.setSuccess(1);
            uploadFile.setMessage("上传成功");
        } else {
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未完成");
        }
        uploadFile.setFileSize(uploadFile.getTotalSize());

        saveUploadFileList.add(uploadFile);
        return saveUploadFileList;
    }
}
