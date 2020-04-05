package com.songlanyun.msgCenter.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 城市实体类
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {

    /** 项目类型  **/
    private String prjId;
    @Id
    private String id;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 模板内容,站位内容使用  %s  占位
     */
    private String content;

    /** 消息备注  **/
    private String remark;

    /**
     * 用途：消息类型 0--通知 1--消息
     */
    private Integer type;

    private  Integer flag;

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
