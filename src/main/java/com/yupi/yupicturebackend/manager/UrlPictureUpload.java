package com.yupi.yupicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void vaildPicture(Object inputSource) {
        String fileUrl = inputSource.toString();
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");

        try {
            // 1. 验证 URL 格式
            new URL(fileUrl); // 验证是否是合法的 URL
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }

        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");

        // 3. 发送 HEAD 请求以验证文件是否存在
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long TWO_MB = 2 * 1024 * 1024L; // 限制文件大小为 2MB
                    ThrowUtils.throwIf(contentLength > TWO_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        String fileName = (String) inputSource;
        return FileUtil.getName(fileName);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws IOException {
        String fileName = (String) inputSource;
        HttpUtil.downloadFile(fileName, file);
    }

    public static void main(String[] args) {
        String url = "https://th.bing.com/th/id/OIP.2SQA-lzW_2BUbSOvBpWixAHaEJ?w=325&h=182&c=7&r=0&o=5&dpr=1.3&pid=1.7";
        HttpResponse response = HttpUtil.createRequest(Method.GET, url).execute();

        // 检查是否返回 301/302 重定向
        int statusCode = response.getStatus();
        System.out.println("HTTP 状态码：" + statusCode);

        if (statusCode == 301 || statusCode == 302) {
            // 获取重定向后的 URL
            String newUrl = response.header("Location");
            System.out.println("重定向到：" + newUrl);

            // 重新请求新 URL
            response = HttpUtil.createRequest(Method.GET, newUrl).execute();
        }

        // 获取最终的 Content-Type
        String contentType = response.header("Content-Type");
        System.out.println("最终 Content-Type：" + contentType);
    }
}
