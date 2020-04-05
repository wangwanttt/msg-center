package com.songlanyun.msgCenter.utils;

import java.util.List;

import com.songlanyun.msgCenter.domain.Template;
import lombok.Data;

@Data
public class PageSupport<T> {

    public List list;
    public int pageNumber;
    public int pageSize;
    public long total;

    public <T> PageSupport(List<Template> collectList, int pageNo, int pageSize, Long totals) {
        this.pageNumber = pageNo;
        this.pageSize = pageSize;
        this.total = totals;
        this.list = collectList;
    }


}
