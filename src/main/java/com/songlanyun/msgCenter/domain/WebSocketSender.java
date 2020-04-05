package com.songlanyun.msgCenter.domain;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

public class WebSocketSender {
    public WebSocketSession getSession() {
        return session;
    }

    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;

    public String getUid() {
        return uid;
    }

    private String uid;
    public WebSocketSender(String _uid,WebSocketSession session, FluxSink<WebSocketMessage> sink) {
        this.session = session;
        this.sink = sink;
        this.uid =_uid;
    }


    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }
}
