package com.songlanyun.msgCenter.error;

import com.mongodb.DuplicateKeyException;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义全局错误响应属性
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request);
    }

    private Map<String, Object> assembleError(ServerRequest request) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof GlobalException) {
            errorAttributes.put("code", ((GlobalException) error).getCode());
            errorAttributes.put("msg", error.getMessage());
            return errorAttributes;
        }
        if (error instanceof  DuplicateKeyException)
        {
            errorAttributes.put("code", ((DuplicateKeyException) error).getCode());
            errorAttributes.put("msg", "mongodb数据库访问出错:"+error.getMessage());
            return errorAttributes;
        }
        if (error instanceof ResponseStatusException){
            errorAttributes.put("code", -300);
            errorAttributes.put("msg", "后台访问地址有误:"+error.getMessage());
            return errorAttributes;
        }
        else if(error instanceof  Exception){
//            if  (error==400){
//                errorAttributes.put("code",400);
//                errorAttributes.put("msg", "前端参数传递不完整");
//            }else{
                errorAttributes.put("code",-100);
                errorAttributes.put("msg",   "后台处理出错："+error.getMessage());
           // }
            return errorAttributes;
        }
        return errorAttributes;
    }
//    @Override
//    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
//        Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
//
//        if (getError(request) instanceof GlobalException) {
//            GlobalException ex = (GlobalException) getError(request);
//            map.put("exception", ex.getClass().getSimpleName());
//            map.put("message", ex.getMessage());
//            map.put("status", ex.getStatus().value());
//            map.put("error", ex.getStatus().getReasonPhrase());
//
//            return map;
//        }
//
//        map.put("exception", "SystemException");
//        map.put("message", "System Error , Check logs!");
//        map.put("status", "500");
//        map.put("error", " System Error ");
//        return map;
//    }
}
