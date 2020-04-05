package com.songlanyun.msgCenter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyParam {

    /** 发送人id **/
    private String senderId;

    /** 接收人id **/
    private String uid;

    /** 模板id **/
    private String tplId;

    private Date createDate=new Date();

    /** %s 的替换参数数组 **/
    private String params;
    /** 只在通知时有用：与前端协商好的消息标志 0--不处理 如1--发货信息 extra则为订单号 2--xx  **/
    private int flag=0;
    /** 扩展参数  如通知某用户处理某订单的id,某条审核信息的id等，前端会根据此**/
    private String extra="";


}
