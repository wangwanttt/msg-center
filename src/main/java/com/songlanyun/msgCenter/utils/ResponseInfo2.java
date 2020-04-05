package com.songlanyun.msgCenter.utils;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ResponseInfo2<T> implements Serializable {
    String msg;
    Integer code;
    private static final long serialVersionUID = -2551908500227408235L;

}