package com.bootcamp67.ms_account.respository;

import com.bootcamp67.ms_account.entity.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveMongoRepository<Account,String> {
  Flux<Account> findByOwnerCustomerId(String ownerCustomerId);
  Flux<Account> findByAccountTypeAndOwnerCustomerId(String accountType, String ownerCustomerId);
}
