package com.july.log.object;

import com.july.log.annotation.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * 解析接口数据
 * @author zqk
 * @since 2019/12/12
 */
public interface ContentParser {

    final static Logger logger = LoggerFactory.getLogger(ContentParser.class);

    /**
     * 获取信息返回查询出的对象
     * @param fieldValues
     * @param sysLog
     * @return
     */
    Object getResult(Map<String, Object> fieldValues, SysLog sysLog);

}

