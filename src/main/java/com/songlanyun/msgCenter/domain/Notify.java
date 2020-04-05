package com.songlanyun.msgCenter.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by ww.
 * Date: 2020/3/26
 * Time: 9:43
 * <p>
 * 消息通知
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notify {

    @Id
    private String id;

    private  String prjId;
    /**
     * 消息通知的用户ID
     */
    private String uid;

    /**
     * 是否已读 0--未 1--已读
     */
    private int isRead=0;

    /** %s 的替换参数数组 **/
    private String params;
    /** 只在通知时有用：与前端协商好的消息标志 0--不处理 如1--订单信息 extra则为订单号 2--xx  **/
    private int flag=0;
    /** 扩展参数  如通知某用户处理某订单的id,某条审核信息的id等，前端会根据此**/
    private String extra;

    /**
     * 消息通知标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息通知时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    /**
     * 消息通知类型 0---
     */
    private Integer type;

    /**
     * 消息通知备注
     */
    private String remark;

    /**
     * 消息通知人，默认0为系统
     */
    private String senderId = "0";
}
