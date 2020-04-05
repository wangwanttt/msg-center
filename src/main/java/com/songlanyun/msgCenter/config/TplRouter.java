package com.songlanyun.msgCenter.config;

import com.songlanyun.msgCenter.handler.TemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TplRouter {
    static final String API_BASE_URL = "/api/v1/msg/";

    @Bean
    public RouterFunction<ServerResponse> routePay(TemplateHandler tplHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET(API_BASE_URL + "list/{prjId}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), tplHandler::list)

                .andRoute(RequestPredicates.DELETE(API_BASE_URL + "delete/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        tplHandler::delete)
                .andRoute(RequestPredicates.POST(API_BASE_URL + "update")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        tplHandler::update)
                .andRoute(RequestPredicates.POST(API_BASE_URL + "save")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        tplHandler::save)
                 .andRoute(RequestPredicates.GET(API_BASE_URL + "getNoReadMsgCnt/{prjId}/{uid}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                         tplHandler::getNoReadMsgCnt)
                 .andRoute(RequestPredicates.GET(API_BASE_URL + "setMsgRead/{id}")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                         tplHandler::setMsgRead)
                .andRoute(RequestPredicates.POST(API_BASE_URL + "getPageList")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        tplHandler::getNotifyPage)
                .andRoute(RequestPredicates.POST(API_BASE_URL + "sendMsg")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        tplHandler::sendMsg);


    }
}