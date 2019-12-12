package com.july.log.aspect;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.july.log.annotation.FieldName;
import com.july.log.config.WorkConfig;
import com.july.log.entity.SysLog;
import com.july.log.object.ContentParser;
import com.july.log.service.SysLogService;
import com.july.log.utils.ReflectionUtils;
import com.july.log.utils.SpringUtil;
import com.july.log.utils.ToolUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面类
 * @author zqk
 * @since 2019/12/12
 */
@Aspect
@Component
public class WebLogAspect {

    private final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    private SysLog sysLogModel = new SysLog();
    private Object oldObject;
    private Object newObject;
    private Map<String,Object> fieldValues;
    private Map<String ,Object> oldMap;

    @Resource
    private SysLogService sysLogService;

    @Before("@annotation(sysLog)")
    public void doBefore(JoinPoint joinPoint, com.july.log.annotation.SysLog sysLog){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object info=joinPoint.getArgs()[0];
        String[] feilds= sysLog.feildName();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sysLogModel.setUpdateName(SpringUtil.getName());
        sysLogModel.setUpdateIp(ToolUtil.getClientIp(request));
        sysLogModel.setUpdateDate(sdf.format(new Date()));
        String handelName = sysLog.interName();
        if("".equals(handelName)){
            sysLogModel.setUpdateObject(request.getRequestURL().toString());
        }else {
            sysLogModel.setUpdateObject(handelName);
        }
        sysLogModel.setUpdateWork(sysLog.name());
        sysLogModel.setUpdateContent("");
        if(WorkConfig.UPDATE.equals(sysLog.name())){
            for(String feild:feilds){
                fieldValues=new HashMap<>();
                Object result= ReflectionUtils.getFieldValue(info,feild);
                fieldValues.put(feild,result);
            }
            try {
                ContentParser contentParser= (ContentParser) sysLog.parseclass().newInstance();
                oldObject=contentParser.getResult(fieldValues,sysLog);
                oldMap= (Map<String, Object>) objectToMap(oldObject);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }
        }else{
            if(WorkConfig.UPDATE.equals(sysLog.name())){
                logger.error("id查询失败，无法记录日志");
            }
        }

    }

    @AfterReturning(pointcut = "@annotation(sysLog)",returning = "object")
    public void doAfterReturing(Object object, com.july.log.annotation.SysLog sysLog){
        if(WorkConfig.UPDATE.equals(sysLog.name())){
            ContentParser contentParser= null;
            try {
                contentParser = (ContentParser) sysLog.parseclass().newInstance();
                newObject=contentParser.getResult(fieldValues,sysLog);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }

            try {
                Map<String ,Object> newMap= (Map<String, Object>) objectToMap(newObject);
                StringBuilder str=new StringBuilder();
                oldMap.forEach((k,v)->{
                    Object newResult=newMap.get(k);
                    if(v!=null&&!v.equals(newResult)){
                        Field field=ReflectionUtils.getAccessibleField(newObject,k);
                        FieldName dataName=field.getAnnotation(FieldName.class);
                        if(dataName!=null){
                            str.append("【").append(dataName.name()).append("】从【")
                                    .append(v).append("】改为了【").append(newResult).append("】;\n");
                        }else {
                            str.append("【").append(field.getName()).append("】从【")
                                    .append(v).append("】改为了【").append(newResult).append("】;\n");
                        }
                    }

                });
                sysLogModel.setUpdateContent(str.toString());
            } catch (Exception e) {
                logger.error("比较异常",e);
            }
        }
        sysLogService.save(sysLogModel);
    }

    /**
     * 把对象信息解析到Map中
     * @param obj
     * @return
     * @author zqk
     * @since 2019/12/12
     */
    private  Map<?, ?> objectToMap (Object obj) {
        if (obj == null) {
            return null;
        }
        ObjectMapper mapper=new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<?, ?> mappedObject = mapper.convertValue(obj, Map.class);

        return mappedObject;
    }

}
