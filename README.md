# msg-center
#### webflux-websocket消息中心，区别于传统的websocket，具有响应快，吞吐量大的优点
一、使用流程
1、前端用是用户登陆或取存储的userId 连接到服务器 --用户id 不能为token
   url: this.appConfig.WEBSOCKET_URL+"notify?uid=用户id-" +uid,
   
2、在微中台配置消息模板

3、 接收各项目的http 调用来发送消息

二、相应接口
1、得到此用户某项目的分页所有消息(已+未读）post  
http://localhost:10030/api/v1/msg/getPageList  
参数为：  
` java{  
	"page":1,  
	"size":5,  
	"prjId":"5e686cf4a2cad5688a313352", ---项目id  
	"uid":"用户id-0"  ---- 用户id  
} `  
返回值：  
{  
    "msg": "得到数据成功",  
    "code": 200,  
    "data": {  
        "list": [
            {
                "id": "5e8840a615e4a528ca027000",
                "prjId": "5e686cf4a2cad5688a313352",
                "uid": "用户id-0",
                "isRead": 0,
                "params": null,
                "flag": 3,
                "extra": "",
                "title": "titlewwww",
                "content": "aabbaaacccbbbddcc",
                "createTime": "2020-04-04T08:09:02.683+0000",
                "type": null,
                "remark": null,
                "senderId": "1g1"
            } 
        ],
        "pageNumber": 1,
        "pageSize": 5,
        "total": 7
    }
}  

2、得到未读消息条数   rest-get 方式  
http://localhost:10030/api/v1/msg/getNoReadMsgCnt/5e686cf4a2cad5688a313352/id-0    
参数为：5e686cf4a2cad5688a313352/id-0   项目id/用户id  

3、设置消息为已读   rest-get 方式  
http://localhost:10030/api/v1/msg/setMsgRead/5e885e0549eeba076d221934  
参数为:消息id  

4、发送消息  
http://localhost:10030/api/v1/msg/sendMsg   [post]  
参数为：  
{
"uid":"接收消息的用户id-0",
"senderId":"发送消息的用户id0",
"tplId":"5e830116e8e8d60223589615模板id",
"params":"aaa,bbb,cc 模板内容中%s的替换值字符串"
}



