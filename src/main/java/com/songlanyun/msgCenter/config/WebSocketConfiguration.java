package com.songlanyun.msgCenter.config;

import com.songlanyun.msgCenter.aspect.WebSocketMappingHandlerMapping;
import com.songlanyun.msgCenter.domain.WebSocketSender;
import com.songlanyun.msgCenter.handler.KeepAliveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping webSocketMapping() {
        return new WebSocketMappingHandlerMapping();
    }

    @Bean
    public ConcurrentHashMap<String, WebSocketSender> senderMap() {
        return new ConcurrentHashMap<String, WebSocketSender>();
    }


    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
//    @Bean
//    public HandlerMapping webSocketMapping() {
//        return new SimpleUrlHandlerMapping() {{
//            setOrder(Ordered.HIGHEST_PRECEDENCE);
//            setUrlMap(new HashMap<String, WebSocketHandler>() {{
//                put("/keepAlive", keepAliveHandler);
//            }});
//        }};
//    }
//
//    @Bean
//    public WebSocketHandlerAdapter handlerAdapter() {
//        return new WebSocketHandlerAdapter();
//    }
//
//    @Autowired
//    private KeepAliveHandler keepAliveHandler;

//}