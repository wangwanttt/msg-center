package com.songlanyun.msgCenter.error;

public class GlobalException extends  RuntimeException {
    private int code = -1;
    private String msg = "";

    public GlobalException(String msg) {
        super(msg);
        this.msg = msg;
    }
    public GlobalException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public int getCode()
    {
        return this.code;
    }
}
