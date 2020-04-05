package com.songlanyun.msgCenter.handler;

import com.alibaba.fastjson.JSON;
import com.songlanyun.msgCenter.annotation.WebSocketMapping;
import com.songlanyun.msgCenter.dao.TemplateRepository;
import com.songlanyun.msgCenter.domain.WebSocketSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门处理消息中心的handler
 **/
@Component
@WebSocketMapping("/notify")
@Slf4j
public class NotifyMsgHandler implements WebSocketHandler {

    @Autowired
    private ConcurrentHashMap<String, WebSocketSender> senderMap;
    @Autowired
    private final TemplateRepository templateRepository;

    public NotifyMsgHandler(TemplateRepository templateRepository ) {
        this.templateRepository = templateRepository;

    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        Map<String, String> queryMap = getQueryMap(handshakeInfo.getUri().getQuery());
        String id = queryMap.getOrDefault("uid", "defaultId");
        // 收到消息
        Mono<Void> input = session.receive().map(WebSocketMessage::getPayloadAsText)
                .map(msg -> id + ": " + msg).doOnNext(message -> {
                    log.info(message);
                }).then().doFinally(sig -> {
                    log.info("客户端连接断开 sig: [{}], [{}]", sig.name(), id);
                    session.close();
                    senderMap.remove(id);  // remove the stored session id
                });
        // store session and FluxSink to WebSocketSender
        Mono<Void> output = session.send(Flux.create(
                sink -> {
                    senderMap.put(id, new WebSocketSender(id, session, sink));
                }
        ));

        return Mono.zip(input, output).then(Mono.fromRunnable(() -> {
            senderMap.remove(id);
        }));
    }

    private Map<String, String> getQueryMap(String queryStr) {
        Map<String, String> queryMap = new HashMap<>();
        if (!StringUtils.isEmpty(queryStr)) {
            String[] queryParam = queryStr.split("&");
            Arrays.stream(queryParam).forEach(s -> {
                String[] kv = s.split("=", 2);
                String value = kv.length == 2 ? kv[1] : "";
                queryMap.put(kv[0], value);
            });
        }
        return queryMap;
    }


}