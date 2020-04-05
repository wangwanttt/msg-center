package com.songlanyun.msgCenter.utils;

import lombok.Data;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Data
@ToString
public class ResponseInfo<T> implements Serializable {
    String msg;
    Integer code;
    Object data;
    private static final long serialVersionUID = -7551908500227408235L;

    public static <T> Mono<ResponseInfo<T>> ok (Mono<T> monoBody) {
        return responseBodyCreate(monoBody,200,"sucess");
    }

    public static <T> Mono<ResponseInfo<T>> ok (Mono<T> monoBody, String msg) {
        return responseBodyCreate(monoBody,200,msg);
    }

    public static <T> Mono<ResponseInfo<T>> list (Flux<T> monoBody, String msg) {
        return responseBodyCreate(monoBody,200,msg);
    }


    public static <T> Mono<ResponseInfo2<T>> info(int code, String msg){
        ResponseInfo2  responseInfo2 = new ResponseInfo2();
        Mono<ResponseInfo2> monoCntEmp = Mono.justOrEmpty(responseInfo2);
        return monoCntEmp.map(monoVo-> {
            final ResponseInfo2<T> responseInfo = new ResponseInfo2<>();
            responseInfo.setCode(code);
            responseInfo.setMsg(msg);
            return responseInfo;
        });
    }


    public static <T> Mono<ResponseInfo<T>> ok (Mono<T> monoBody, int code, String msg) {
        return responseBodyCreate(monoBody,code,msg);
    }

    public static <T> Mono<ResponseInfo<PageSupport>> pageOk (PageSupport pageSupport) {
        return responseBodyCreate(Mono.just(pageSupport),200,"得到数据成功");
    }

    public static <T> Mono<ResponseInfo<T>> failed (Mono<T> monoBody) {
        return responseBodyCreate(monoBody,200,null);
    }

    public static <T> Mono<ResponseInfo<T>> failed (Mono<T> monoBody, String msg) {
        return responseBodyCreate(monoBody,200,msg);
    }

    public static <T> Mono<ResponseInfo<T>> failed (Mono<T> monoBody, int code, String msg) {
        return responseBodyCreate(monoBody,code,msg);
    }

    private static <T> Mono<ResponseInfo<T>> responseBodyCreate(Mono<T> monoData, int code, String msg) {
        return monoData.map(data-> {
            final ResponseInfo<T> responseInfo = new ResponseInfo<>();
            responseInfo.setCode(code);
            responseInfo.setData(data);
            responseInfo.setMsg(msg);
            return responseInfo;
        });
    }

    private static <T> Mono<ResponseInfo<T>> responseBodyCreate(Flux<T> fluxData, int code, String msg) {
            final ResponseInfo<T> responseInfo = new ResponseInfo<>();
            responseInfo.setCode(code);
            responseInfo.setData(fluxData.collectList());
            responseInfo.setMsg(msg);
            return Mono.just(responseInfo);

    }

}