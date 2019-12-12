package com.july.log.annotation;

import com.july.log.object.DefaultContentParse;
import com.july.log.service.IService;

import java.lang.annotation.*;

/**
 * 记录用户操作日志
 * @author zqk
 * @since 2019/12/12
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SysLog {

    String name() default "";
    /**
     * @return 接口参数数组
     */
    String[] feildName() default {"id"};
    /**
     * @return 接口名称
     */
    String interName() default "";

    /**
     * @return 获取解析接口信息
     */
    Class parseclass() default DefaultContentParse.class;

    /**
     * @return 查询数据库所调用的class文件
     */
    Class serviceclass() default IService.class;


}
