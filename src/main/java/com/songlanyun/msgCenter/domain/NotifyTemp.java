package com.songlanyun.msgCenter.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ww.
 * Date: 2020/3/26
 * Time: 10:41
 * <p>
 * 消息模板表
 */
@Data

public class NotifyTemp implements Serializable {

    /** 项目类型  **/
    private String prjId;
    private String id;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板内容,站位内容使用  %s  占位
     */
    private String content;

    /**
     * 用途：消息类型 0---消息 1---通知
     */
    private Integer type;

    /** 备注  **/
    private  String remark;

    /**
     * 创建人ID
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    /**
     * 是否启用
     */
    private Boolean isUse = true;


}
