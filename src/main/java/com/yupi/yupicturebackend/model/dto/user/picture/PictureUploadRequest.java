package com.yupi.yupicturebackend.model.dto.user.picture;

import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.model.entity.Picture;
import com.yupi.yupicturebackend.model.vo.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureUploadRequest implements Serializable {
  
    /**  
     * 图片 id（用于修改）  
     */  
    private Long id;  
  
    private static final long serialVersionUID = 1L;  
}
