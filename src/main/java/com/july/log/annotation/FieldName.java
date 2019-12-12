package com.july.log.annotation;

import java.lang.annotation.*;

/**
 * 获取字段名称
 * @author zqk
 * @since 2019/12/12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface FieldName {

    /**
     * 字段名称
     * @return
     */
    String name() default "";

}
