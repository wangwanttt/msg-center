package com.songlanyun.msgCenter.dao;

import com.songlanyun.msgCenter.domain.Notify;
import com.songlanyun.msgCenter.domain.Template;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NotifyRepository extends ReactiveMongoRepository<Notify, String> {
   /** 某项目id ,支付方式，支付类型  **/
   Mono<Template> findByPrjIdAndTitle(String prjId, String title);

   /** 得到某用户某项目未读消息条数  **/
   Flux<Notify> findByprjIdAndUidAndIsRead(String prjId,String uid,int read);

  Long countByprjIdAndUidAndIsRead(String prjId,String uid,int read);

}
