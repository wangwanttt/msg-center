/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.songlanyun.msgCenter.utils;



import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public R() {
		put("code", 200);
		put("msg", "success");
	}


	public static Mono<ServerResponse>  getResp(int code, String msg){
		return   ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
				ResponseInfo.info(code, msg), ResponseInfo.class);

	}


	public static R error() {
		return error(500, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(500, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}
	public static R error(StatusMsgEnum statu) {
		R r = new R();
		r.put("code", statu.getStatus());
		r.put("msg", statu.getMsg());
		return r;
	}
	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

//
//	public static Mono<ServerResponse> ok(int code, String msg) {
//		return ServerResponse.ok().contentType(APPLICATION_JSON).body(ResponseInfo.info(-100, msg, ResponseInfo.class));
//	}




	public static R ok(StatusMsgEnum statu) {
		R r = new R();
		r.put("code",statu.getStatus());
		r.put("msg", statu.getMsg());
		return r;
	}


	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}

enum StatusMsgEnum {
	SUCCESS(200,"操作成功！"),
	FAIL(300,"操作失败！"),
	ORDER_PAY_NOSUPPORT(300,"暂不支持此支付方式"),
	ORDER_PAY_SUCESS(310,"支付成功"),
	ORDER_PAY_FAIL(320,"支付失败"),
	ORDER_PAY_WAITING(310,"支付等待"),
	ORDER_NOEXIST(300,"订单信息不存在");

	private Integer status;

	private String msg;

	StatusMsgEnum(Integer status, String msg){
		this.status = status;
		this.msg =msg;
	}

	public Integer getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}
}
