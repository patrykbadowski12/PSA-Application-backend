package net.respekto.psawebapi;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ServiceRepository extends ReactiveMongoRepository<ServiceDbModel, String> {

    @Override
    <S extends ServiceDbModel> Mono<S> save(S s);

    @Override
    Flux<ServiceDbModel> findAll();

    Flux<ServiceDbModel> findByWho(String who);

    @Override
    Mono<Void> delete(ServiceDbModel serviceDbModel);

    @Override
    Mono<ServiceDbModel> findById(String s);

}