package com.songlanyun.msgCenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.songlanyun.msgCenter.dao.TemplateRepository;
import com.songlanyun.msgCenter.domain.NotifyParam;
import com.songlanyun.msgCenter.domain.Template;
import com.songlanyun.msgCenter.domain.WebSocketSender;
import com.songlanyun.msgCenter.utils.InputStreamCollector;
import com.songlanyun.msgCenter.utils.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@RestController()
@RequestMapping("/msg")
@EnableScheduling
@Slf4j
public class MsgController {

    @Autowired
    private final TemplateRepository templateRepository;


    @Autowired
    private ConcurrentHashMap<String, WebSocketSender> senderMap;


    public MsgController(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }


    /**
     * 得到模板 by id
     **/
    public Mono<ServerResponse> getTemplateById(ServerRequest request) {
        String forexId = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Mono<Template> forex = templateRepository.findById(forexId);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(forex, Template.class)
                .switchIfEmpty(ServerResponse.ok().body(Mono.just(ResponseInfo.info(-200, "没有此消息模板")), ResponseInfo.class));
    }

    /**
     * 发送消息
     *
     * @return
     */
    @PostMapping("/sendMsg")
    public Mono<ServerResponse> getCon(ServerWebExchange exchange) {
        Flux<DataBuffer> body = exchange.getRequest().getBody();
        return body.collect(InputStreamCollector::new, (t, dataBuffer) -> t.collectInputStream(dataBuffer.asInputStream()))
                .flatMap(inputStream -> {
                    /**
                     * 读取通知参数
                     */
                    JSONObject jsonObject;
                    NotifyParam param = new NotifyParam();
                   // try {
                        String strXML = "vvvvvvvvvvvv";//InputStreamCollector.getStringFromStream(inputStream.getInputStream());

                      //  jsonObject = JSONObject.parseObject(strXML);
                    //}
                      //  catch (IOException e) {
                        //    throw new GlobalException(-300, "传递的参数解析错误");
                     //   }
                    String uid = "aaa";//jsonObject.get("uid").toString();
                    //模板id
                    String tplId ="bbb";// jsonObject.get("tplId").toString();
//                        String uid = jsonObject.get("uid").toString();
//                        //模板id
//                        String tplId = jsonObject.get("tplId").toString();
//                        //模板参数字符串
                    String[] paramArr = {"aa","bb"};//jsonObject.getString("params").split(",");
                        Mono<Template> templateMono = templateRepository.findById(tplId);
                        return templateMono.flatMap(tplVo -> {
                            String content = tplVo.getContent();
                            //得到模板中  %s 个数
//                            int replaceCnt = TimeUtils.countStr(content,"%s");
//                            if (replaceCnt==paramArr.length ){
//                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
//                                        ResponseInfo.info(-300, "消息内容待替换的%s与传递过来的参数数量不匹配"), ResponseInfo.class);
//                            }
                            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                                    ResponseInfo.info(-300, "消息内容待替换的%s与传递过来的参数数量不匹配"), ResponseInfo.class);

                        }).switchIfEmpty(
                                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.info(-300, "消息内容待替换的%s与传递过来的参数数量不匹配"), ResponseInfo.class)
                        );

                });
    }


//        return body.collect(InputStreamCollector::new, (t, dataBuffer) -> t.collectInputStream(dataBuffer.asInputStream()))
//                .flatMap(inputStream -> {
//                    strXML = FileUtil.getStringFromStream(inputStream.getInputStream());
//             Object cc=inputStream;
//            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
//                    ResponseInfo.info(200,"更新成功"), ResponseInfo.class);
//        });
    //   MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
//        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
//        String[] paramStr = request.getURI().getQuery().split("=");
//        if (!paramStr[0].equals("params")) {
//            throw new GlobalException(-300, "参数名称为params");
//        }
//        if (paramStr.length < 2) {
//            throw new GlobalException(-300, "参数格式不正确，格式为：xx,xx,xx分别为：userid,模板id,模板参数1，模板参数2 ...");
//        }
//        String splitStr = paramStr[1]; //参数
//        //模板参数字符串
//        String[] paramArr = splitStr.split(",");
//        //userid
//        String id = paramArr[0].toString();
//        //模板id
//        String tplId = paramArr[1].toString();
//        Mono<Template> templateMono = templateRepository.findById(tplId);
//        return templateMono.flatMap(tplVo -> {
//            String content=tplVo.getContent();
//            //得到模板中  %s 个数
//            int replaceCnt=content.split("%s").length - 1; // -1 是最后一个为%s 时长度为空
//            //如果 传递过来的参数个数与模板中的个数不一样，则提示参数匹配错误
//            if (replaceCnt!=paramArr.length -2 ){  //前2个已经是 userid,模板id 所以-1
//                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.info(-300, "传递"), ResponseInfo.class);
//            }
//            content=String.format(content,)
//
//            WebSocketSender sender = senderMap.get(id);
//            if (sender != null) {
//                sender.sendData(tplId);
//                return String.format("Message '%s' sent to connection: %s.", data, id);
//            } else {
//                senderMap.remove(id);
//                return String.format("Connection of id '%s' doesn't exist", id);
//            }
//
//
//
//                    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.info(200, "消息已发送"), ResponseInfo.class);
//                }
//        ).switchIfEmpty(ServerResponse.ok().body(Mono.just(
//                ResponseInfo.info(-310, "没有此消息模板")), ResponseInfo.class));
//


//
//            return data.flatMap(formData -> {
//                Map<String, String> params = formData.toSingleValueMap();
//                if (MapUtils.isEmpty(params)) {
//                    throw new GlobalException(-300,"发送消息不能为空");
//                }
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.info(200, "消息已发送"), ResponseInfo.class)
//                .switchIfEmpty(ServerResponse.ok().body(Mono.just(ResponseInfo.info(-200, "没有此消息模板")), ResponseInfo.class));
//
//         WebSocketSender  sender = senderMap.get(id);
//        if (sender != null) {
//            sender.sendData(data);
//            return String.format("Message '%s' sent to connection: %s.", data, id);
//        } else {
//            return String.format("Connection of id '%s' doesn't exist", id);
//        }

    //})
    //}


//
//    /** 每晚24点清除无用的session **/
//    @Scheduled(cron="*/10 * * * * ?")
//    private void  closeDisconnectSession(){
//         doClear();
//    }

    @Async  //标注使用
    public void doClear() {
        for (WebSocketSender sender : senderMap.values()) {
            WebSocketSession session = sender.getSession();
            ;
//            session
//                    .send(Mono.just(session.textMessage("pong心跳")))
//                    .doOnError(ClosedChannelException.class, t -> connectionClosed(sender))
//                    .doOnError(AbortedException.class, t -> connectionClosed(sender))
//                    .onErrorResume(ClosedChannelException.class, t -> {
//                        System.out.print("fcunimab");
//                       return Mono.empty();
//                    })
//                    .onErrorResume(AbortedException.class, t -> {
//                        System.out.print("fcunimab");
//                        return Mono.empty();
//                    })
//                    .subscribe();
        }
    }

    private void connectionClosed(WebSocketSender sender) {
        sender.getSession().close();
        System.out.print("------" + sender.getUid() + "-----");
        senderMap.remove(sender.getUid());
    }

}