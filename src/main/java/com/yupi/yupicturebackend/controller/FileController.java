package com.yupi.yupicturebackend.controller;

import cn.hutool.http.server.HttpServerResponse;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.common.BaseResponse;
import com.yupi.yupicturebackend.common.ResultUtils;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.constant.UserConstant;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    CosManager cosManager;
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile file) throws IOException {

        String key;
        File tempFile = null;
        try {
            key = file.getOriginalFilename();
            String path = String.format("/test/%s", key);
             tempFile = File.createTempFile(path,null);
            file.transferTo(tempFile);
            cosManager.putObject(path, tempFile);
        } finally {
            if(tempFile != null){
                tempFile.delete();
            }
        }
        return ResultUtils.success(key);
    }

    /**
     * 下载文件，严格按照以下路径
     * @param filePath /test/76526e75ab27e11615f0ea015c7f9b6d.png
     * @param response
     * @throws IOException
     */
    @PostMapping("/test/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void download(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream objectContent = null;
        try {
            COSObject cosObject = cosManager.getObject(filePath);
             objectContent = cosObject.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(objectContent);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition"," attachment;filename=" + filePath);
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("下载文件失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载文件失败");
        }finally {
            if(objectContent != null){
                objectContent.close();
            }
        }
    }
}
