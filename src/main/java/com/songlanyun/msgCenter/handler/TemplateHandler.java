package com.songlanyun.msgCenter.handler;

import com.alibaba.fastjson.JSON;
import com.songlanyun.msgCenter.dao.NotifyRepository;
import com.songlanyun.msgCenter.dao.TemplateRepository;
import com.songlanyun.msgCenter.domain.Notify;
import com.songlanyun.msgCenter.domain.NotifyParam;
import com.songlanyun.msgCenter.domain.Template;
import com.songlanyun.msgCenter.domain.WebSocketSender;
import com.songlanyun.msgCenter.error.GlobalException;
import com.songlanyun.msgCenter.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class TemplateHandler {


    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;
    @Autowired
    private final TemplateRepository templateRepository;

    @Autowired
    private ConcurrentHashMap<String, WebSocketSender> senderMap;
    @Autowired
    private final NotifyRepository notifyRepository;

    public TemplateHandler(TemplateRepository templateRepository, NotifyRepository notifyRepository) {
        this.templateRepository = templateRepository;
        this.notifyRepository = notifyRepository;
    }


    /**
     * 发送消息
     **/
    public Mono<ServerResponse> sendMsg(ServerRequest request) {
        Mono<NotifyParam> msgBody = request.bodyToMono(NotifyParam.class);
        return msgBody.flatMap(msgVo -> {
            //发送消息的用户id
            String senderId = msgVo.getSenderId();
            //接收消息的用户id
            String uid = msgVo.getUid();
            //模板id
            String tplId = msgVo.getTplId();
            // 扩展参数，
            String extraStr = msgVo.getExtra();
            WebSocketSender sender = senderMap.get(uid);
            if (sender == null) {
                return R.getResp(-200, "无法找到消息发送人其已下线或不存在");
            }

            //模板参数字符串 如 ww%sbb%s 中这2个%s 的值 ，注意数量保持一致
            String[] paramArr = msgVo.getParams().split(",");
            Mono<Template> templateMono = templateRepository.findById(tplId);
            return templateMono.flatMap(tplVo -> {
                String content = tplVo.getContent();
                //得到模板中  %s 个数
                int replaceCnt = Utils.countStr(content, "%s");
                if (replaceCnt != paramArr.length) {
                    System.out.print(replaceCnt + "==" + paramArr.length);
                    return R.getResp(-300, "消息内容待替换的%s与传递过来的参数数量不匹配");
                }
                //将消息模板内容替换
                content = String.format(content, paramArr);
                //将此消息写入未读列表，
                Notify notify = new Notify();
                notify.setSenderId(msgVo.getSenderId());
                notify.setUid(msgVo.getUid());
                notify.setContent(content);
                notify.setTitle(tplVo.getTitle());
                notify.setExtra(extraStr);
                notify.setType(tplVo.getType());
                notify.setFlag(tplVo.getFlag());
                notify.setPrjId(tplVo.getPrjId());
                return notifyRepository.save(notify)
                        .flatMap(_msgVo -> {
                            Mono<List<Notify>> listOfnotify = notifyRepository.findByprjIdAndUidAndIsRead(tplVo.getPrjId(), uid, 0).collectList();
                            return listOfnotify.flatMap(listv -> {
                                //如果是通知，则发送消息内容给前端 ，如果是消息，则发送未读消息条数给前端
                                if (tplVo.getType() == 0) { //消息 ---
                                    notify.setContent(listv.size() + "");
                                }
                                sender.sendData(JSON.toJSONString(notify));
                                return R.getResp(200, "消息发送成功");

                            }).onErrorResume(e -> {
                                return R.getResp(-400, "消息保存失败");
                            });
                        }).switchIfEmpty(R.getResp(-100, "模板id不存在"));

            });
        });
    }


    /**
     * 得到某用户某项目所有未读消息
     **/
    public Mono<ServerResponse> getNoReadMsgCnt(ServerRequest request) {
        String prjId = request.pathVariable("prjId");
        String uid = request.pathVariable("uid");
        Mono<List<Notify>> listOfnotify = notifyRepository.findByprjIdAndUidAndIsRead(prjId, uid, 0).collectList();
       return listOfnotify.flatMap(list->{
            return ok().contentType(APPLICATION_JSON).body(ResponseInfo.ok(Mono.just(list.size()+"")), ResponseInfo.class);

        });
    }

    /**
     * 得到某人某项目分页列表列表
     * request 参数为 page:xx  size :xx
     **/
    public Mono<ServerResponse> getNotifyPage(ServerRequest request) {
        return request.bodyToMono(NotifyPageQuery.class).flatMap(pageQuery -> {
            return notifyListPageQuery(pageQuery);
        });

    }

    /**
     * 某项目某人分页列表的具体实现
     **/
    public Mono<ServerResponse> notifyListPageQuery(NotifyPageQuery pageQuery) {
        Query query = getQuery(pageQuery);
        String[] strArray = {"createTime"};
        pageQuery.setOrder(strArray);
        Pageable pageable = getPageable(pageQuery);
        Query with = query.with(pageable);

        query.addCriteria(Criteria.where("prjId").is(pageQuery.getPrjId()));
        query.addCriteria(Criteria.where("uid").is(pageQuery.getUid()));

        Query sumQuery=new Query();
        sumQuery.addCriteria(Criteria.where("prjId").is(pageQuery.getPrjId()));
        sumQuery.addCriteria(Criteria.where("uid").is(pageQuery.getUid()));


        Mono<Long> count = reactiveMongoTemplate.count(sumQuery, Notify.class);
        return count.flatMap(sums -> {
            long size = pageQuery.getPage() * pageQuery.getSize();
            if (sums.longValue() == size) {
                return R.getResp(-200, "无更多数据");
            }
            //获取数据
            return reactiveMongoTemplate.find(with, Notify.class).collectList()
                    .map(list -> new PageSupport(list, pageQuery.getPage(), pageQuery.getSize(), sums)).flatMap(pageSupportVo -> {
                        return ServerResponse.ok().body(ResponseInfo.pageOk(pageSupportVo), ResponseInfo.class);
                    }).switchIfEmpty(ServerResponse.ok().body(Mono.just(ResponseInfo.info(-200, "分页查询参数不能为null")), ResponseInfo.class));

        });
    }


    /**
     * 得到分页的查询条件
     **/
    protected Query getQuery(NotifyPageQuery pageQuery) {
        Query query = new Query();
        Pageable pageable = getPageable(pageQuery);
        return query.with(pageable);
    }

    /**
     * 得到分页对象
     **/
    protected Pageable getPageable(NotifyPageQuery pageQuery) {
        //排序条件
        if (ArrayUtils.isEmpty(pageQuery.getOrder())) {
            String[] order = {"_id"};
            pageQuery.setOrder(order);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, pageQuery.getOrder());
        //分页
        return PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), sort);

    }


    /**
     * 设置未读消息为已读
     **/
    public Mono<ServerResponse> setMsgRead(ServerRequest request) {
        String id = request.pathVariable("id");

        return notifyRepository.findById(id).flatMap(t -> {
            Notify notifyToSave = new Notify();
            BeanUtils.copyProperties(t, notifyToSave);
            notifyToSave.setIsRead(1);
            return ok().contentType(APPLICATION_JSON).body(ResponseInfo.ok(notifyRepository.save(notifyToSave)), Notify.class);
        });

    }


    /**
     * 新增保存
     **/
    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Void> fallback = Mono.error(new GlobalException(-200, "此项目此模板标题已存在 "));
        return request
                .bodyToMono(Template.class)
                .flatMap(prjVo -> {
                    String name = prjVo.getTitle();
                    return templateRepository.findByPrjIdAndTitle(prjVo.getPrjId(), name)
                            .flatMap(userDbRecord -> {
                                return ok().contentType(APPLICATION_JSON).body(ResponseInfo.info(-100, "此项目此模板已存在"), ResponseInfo.class);
                            }).switchIfEmpty(
                                    ok().contentType(APPLICATION_JSON).body(ResponseInfo.ok(insertTpl(prjVo), "保存成功"), ResponseInfo.class)
                            );
                });
    }

    /**
     * 插入模板
     **/
    public Mono insertTpl(Template prjVo) {
        return templateRepository.insert(prjVo);
    }


    /**
     * 按项目id 查找模板
     **/
    public Mono<ServerResponse> list(ServerRequest request) {
        String prjId = request.pathVariable("prjId");
        Mono<List<Template>> m = templateRepository.findByPrjId(prjId).collectList();
        return ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.ok(m), Template.class);
    }

    /**
     * 更新template
     **/
    public Mono<ServerResponse> update(ServerRequest request) {
        return request.bodyToMono(Template.class).flatMap(Template -> {
            return ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.ok(templateRepository.save(Template), "更新成功"), Template.class);
        });

    }


    /**
     * 得到某个模板
     **/
    public Mono<ServerResponse> getTemplateById(ServerRequest request) {
        String forexId = request.pathVariable("id");
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();
        Mono<Template> forex = templateRepository.findById(forexId);
        return ok().contentType(MediaType.APPLICATION_JSON).body(forex, Template.class)
                .switchIfEmpty(notFound);
    }

    /**
     * 删除某模板
     **/
    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        id = id.replaceAll("\"", "");
        Mono<Void> delId = templateRepository.deleteById(id);
        return ok().contentType(MediaType.APPLICATION_JSON).body(ResponseInfo.ok(delId), Template.class);

    }
}
