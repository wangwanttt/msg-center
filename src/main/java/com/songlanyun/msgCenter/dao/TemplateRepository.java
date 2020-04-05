package com.songlanyun.msgCenter.dao;

import com.songlanyun.msgCenter.domain.Template;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TemplateRepository extends ReactiveMongoRepository<Template, String> {
   /** 某项目id ,支付方式，支付类型  **/
   Mono<Template> findByPrjIdAndTitle(String prjId,String title);

   Flux<Template> findByPrjId(String prjId);
}
