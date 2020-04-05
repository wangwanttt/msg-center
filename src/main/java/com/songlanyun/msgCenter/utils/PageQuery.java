package com.songlanyun.msgCenter.utils;

import lombok.Data;


@Data
public class PageQuery {
    /**
     * 页数
     */
    private Integer page;
    /**
     * 条数
     */
    private Integer size;
    /**
     * 排序字段
     */
    private String[] order;
}
