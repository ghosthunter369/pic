package com.yupi.yupicturebackend.model.dto.user.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureUpdateRequest implements Serializable {
  
    /**  
     * id  
     */  
    private Long id;  
  
    /**  
     * 图片名称  
     */  
    private String name;  
  
    /**  
     * 简介  
     */  
    private String introduction;  
  
    /**  
     * 分类  
     */  
    private String category;  
  
    /**  
     * 标签  
     */  
    private List<String> tags;
    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewstatus;

    /**
     * 审核信息
     */
    private String reviewmessage;

    /**
     * 审核人 ID
     */
    private Long reviewerid;

    /**
     * 审核时间
     */
    private Date reviewtime;
    private static final long serialVersionUID = 1L;  
}
