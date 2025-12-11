package com.bootcamp67.ms_account.service;

import com.bootcamp67.ms_account.dto.AccountDTO;
import com.bootcamp67.ms_account.dto.AccountRequest;
import com.bootcamp67.ms_account.dto.TransactionRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountService {
  Flux<AccountDTO> findAll();
  Mono<AccountDTO> findById(String id);
  Mono<AccountDTO> create(AccountRequest accountRequest);
  Mono<AccountDTO> update(String id, AccountRequest accountRequest);
  Mono<Void> delete(String id);

  Mono<AccountDTO> deposit(String accountId, TransactionRequest transactionRequest);
  Mono<AccountDTO> withdraw(String accountId, TransactionRequest transactionRequest);
  Flux<TransactionRequest> getTransactions(String accountId);
  Mono<BigDecimal> getBalance(String accountId);
}
