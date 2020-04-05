package com.songlanyun.msgCenter.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@Log4j2
public class KeepAliveHandler implements HeartbeatHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        return handle(session, msg -> "他" + msg, () -> {
            System.out.println("关闭了");
            return null;
        }, 5000);
    }
}

