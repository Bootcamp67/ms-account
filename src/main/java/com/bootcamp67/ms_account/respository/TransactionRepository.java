package com.bootcamp67.ms_account.respository;

import com.bootcamp67.ms_account.entity.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
  Flux<Transaction> findByAccountIdOrderByTimestampDesc(String accountId);
  Flux<Transaction> findByAccountIdAndTimestampBetween(String accountId, OffsetDateTime from, OffsetDateTime to);
}
