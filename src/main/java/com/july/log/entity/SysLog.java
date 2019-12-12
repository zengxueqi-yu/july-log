package com.july.log.entity;

import com.july.log.annotation.FieldName;
import lombok.Data;

/**
 * 日志信息类
 * @author zqk
 * @since 2019/12/12
 */
@Data
public class SysLog {

    @FieldName(name = "id")
    private Long id;
    @FieldName(name = "修改人")
    private String updateName;
    @FieldName(name = "修改时间")
    private String updateDate;
    @FieldName(name = "修改操作")
    private String updateWork;
    @FieldName(name = "修复业务类")
    private String updateObject;
    @FieldName(name = "修改内容")
    private String updateContent;
    @FieldName(name = "修改人Ip")
    private String updateIp;

}
