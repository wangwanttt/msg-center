package com.songlanyun.msgCenter.handler;

import com.songlanyun.msgCenter.utils.Utils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public interface HeartbeatHandler extends WebSocketHandler {

        Map<String, Timer> timer = new HashMap<>();// 定时器

        /**
         * 通用ws方法
         *
         * @param session
         *            WebSocketSession
         * @param messageHandle
         *            消息处理器
         * @param closeHandle
         *            连接关闭处理器
         * @param endTime
         *            超时时间
         * @return Mono<Void>
         */
        default Mono<Void> handle(
                WebSocketSession session,
                Function<String, String> messageHandle,
                Supplier closeHandle,
                int endTime) {

            // 开启热源
            UnicastProcessor<WebSocketMessage> hotSource = UnicastProcessor
                    .create();
            Flux<WebSocketMessage> hotFlux = hotSource.publish()
                    .autoConnect();

            // 向client发送数据
            Mono<Void> output = session.send(hotFlux);

            // 接收处理数据
            Mono<Void> input = session.receive().doOnNext(m -> {
                String msg = m.getPayloadAsText();
//			System.out.println("收到类型：" + m.getType() + " 收到的数据："
//					+ msg);

                // 如果是client回复的PONG，则删除定时器
                if (m.getType().equals(WebSocketMessage.Type.PONG)) {
//				System.out.println("倒计时时间未到，取消关闭连接 uuid=" + msg);
                    if (timer.containsKey(msg)) {
                        timer.get(msg).cancel();
                        timer.remove(msg);
                    }
                }

                // 如果是TEXT，调用用户接口并将结果回复给client
                if (m.getType().equals(WebSocketMessage.Type.TEXT))
                    hotSource.onNext(session.textMessage(messageHandle.apply(msg)));

            }).then();

            // 开启PING。
            // 每发送一个PING，则必须等待一个PONG。如果client超时，则中断连接，并调用用户的close handle
            new Thread(() -> {
                AtomicBoolean isClose = new AtomicBoolean(false);
                while (!isClose.get()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 如果数据已经被清空，说明是第一次或client已经回复PONG，则发送下一次PING
                    if (timer.isEmpty()) {
                        String uuid = UUID.randomUUID().toString();
//					System.out.println("ping id=" + uuid);

                        // 将定时器记录下来
                        timer.put(uuid,
                                Utils.countDown(endTime, t -> {
//								System.out.println(
//										"倒计时时间到，关闭连接 uuid=" + uuid);
                                    hotSource.onComplete();
                                    timer.remove(uuid);
                                    isClose.set(true);
                                    closeHandle.get();
                                }));
                        if (isClose.get())
                            break;
                        // 发送PING
                        hotSource.onNext(
                                session.pingMessage(dbf -> {
                                    DataBuffer db = dbf
                                            .allocateBuffer(uuid.length());
                                    return db.write(uuid.getBytes());
                                }));
                    }
                }
            }).start();

            return Mono.zip(input, output).then();
        }
    }


