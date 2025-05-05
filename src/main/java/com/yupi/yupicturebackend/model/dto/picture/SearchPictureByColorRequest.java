package com.yupi.yupicturebackend.model.dto.picture;

import com.yupi.yupicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SearchPictureByColorRequest implements  Serializable {
  

    String picColor;
    String spaceId;


    private static final long serialVersionUID = 1L;  
}
