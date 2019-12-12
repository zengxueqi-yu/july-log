package com.july.log.service;

import com.july.log.entity.SysLog;

/**
 * 日志接口类
 * @author zqk
 * @since 2019/12/12
 */
public interface SysLogService {

    /**
     * 保存日志信息
     * @param sysLog
     * @author zqk
     * @since 2019/12/12
     */
    void save(SysLog sysLog);

}
