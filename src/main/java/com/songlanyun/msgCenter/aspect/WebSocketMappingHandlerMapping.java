package com.songlanyun.msgCenter.aspect;

import com.songlanyun.msgCenter.annotation.WebSocketMapping;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * http://www.codebelief.com/article/2019/05/webflux-analysis-on-websocket-request-processing/
 * 创建一个专门的 HandlerMapping 类来处理 WebSocketMapping 注解，自动完成 handler 的注册
 */
public class WebSocketMappingHandlerMapping extends SimpleUrlHandlerMapping {

    private Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

    /**
     *initApplicationContext() 方法是 Spring 中 ApplicationObjectSupport 类的方法，用于自定义类的初始化行为
     * @throws BeansException
     */
    @Override
    public void initApplicationContext() throws BeansException {
        Map<String, Object> beanMap = obtainApplicationContext()
                .getBeansWithAnnotation(WebSocketMapping.class);
        beanMap.values().forEach(bean -> {
            if (!(bean instanceof WebSocketHandler)) {
                throw new RuntimeException(
                        String.format("Controller [%s] doesn't implement WebSocketHandler interface.",
                                bean.getClass().getName()));
            }
            WebSocketMapping annotation = AnnotationUtils.getAnnotation(
                    bean.getClass(), WebSocketMapping.class);
            handlerMap.put(Objects.requireNonNull(annotation).value(),
                    (WebSocketHandler) bean);
        });
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }

}