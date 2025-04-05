package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.yupicturebackend.model.dto.user.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.model.dto.user.picture.PictureUploadRequest;
import com.yupi.yupicturebackend.model.dto.user.picture.PictureVO;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 张子涵
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-04-05 12:45:34
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
}
