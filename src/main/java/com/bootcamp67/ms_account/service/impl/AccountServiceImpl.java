package com.bootcamp67.ms_account.service.impl;

import com.bootcamp67.ms_account.dto.AccountDTO;
import com.bootcamp67.ms_account.dto.AccountRequest;
import com.bootcamp67.ms_account.dto.TransactionRequest;
import com.bootcamp67.ms_account.entity.Account;
import com.bootcamp67.ms_account.entity.Transaction;
import com.bootcamp67.ms_account.exception.BusinessException;
import com.bootcamp67.ms_account.exception.NotFoundException;
import com.bootcamp67.ms_account.respository.AccountRepository;
import com.bootcamp67.ms_account.respository.TransactionRepository;
import com.bootcamp67.ms_account.service.AccountService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final WebClient msCustomerWebClient;
  
  private static final String CUSTOMER_CB="customerCB";

  @Override
  public Flux<AccountDTO> findAll() {
    return accountRepository.findAll().map(this::toDto);
  }

  @Override
  public Mono<AccountDTO> findById(String id) {
    return accountRepository.findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
        .map(this::toDto);
  }

  @Override
  @CircuitBreaker(name = CUSTOMER_CB,fallbackMethod = "customerFallbackCreate")
  public Mono<AccountDTO> create(AccountRequest accountRequest) {
    return msCustomerWebClient.get()
        .uri("/api/v1/customer/{id}",accountRequest.getOwnerCustomerId())
        .retrieve()
        .bodyToMono(Object.class)
        .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
        .then(validateAndCreateAccount(accountRequest));
  }

  @Override
  public Mono<AccountDTO> update(String id, AccountRequest accountRequest) {
    return accountRepository.findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
        .flatMap(account -> {
          account.setAccountType(accountRequest.getAccountType());
          account.setCoOwners(accountRequest.getCoOwners());
          account.setMonthlyMovementLimit(accountRequest.getMonthlyMovementLimit());
          account.setFixedTermDay(accountRequest.getFixedTermDay());
          return accountRepository.save(account);
        })
        .map(this::toDto);
  }

  @Override
  public Mono<Void> delete(String id) {
    return accountRepository.deleteById(id);
  }

  @Override
  public Mono<AccountDTO> deposit(String accountId, TransactionRequest transactionRequest) {
    return accountRepository.findById(accountId)
        .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
        .flatMap( acc->{
          if ("FIXED_TERM".equalsIgnoreCase(acc.getAccountType())&& acc.getFixedTermDay()!=null){
            int today= OffsetDateTime.now().getDayOfMonth();
            if(today!=acc.getFixedTermDay()){
              return Mono.error(
                  new BusinessException("Deposit / withdrawal only allowed on day"+acc.getFixedTermDay()));
            }
          }
          acc.setBalance(acc.getBalance().add(transactionRequest.getAmount()));
          Transaction tx=Transaction.builder()
              .accountId(accountId)
              .type(transactionRequest.getType())
              .amount(transactionRequest.getAmount())
              .timestamp(OffsetDateTime.now())
              .description(transactionRequest.getDescription())
              .build();
          return  accountRepository.save(acc)
              .flatMap(saved->
                transactionRepository.save(tx).thenReturn(saved))
              .map(this::toDto);
        });
  }

  @Override
  public Mono<AccountDTO> withdraw(String accountId, TransactionRequest transactionRequest) {
    return accountRepository.findById(accountId)
        .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
        .flatMap(account -> {
          if(account.getBalance().compareTo(transactionRequest.getAmount())<0){
            return Mono.error(new BusinessException("Insufficient funds"));
          }
          if("SAVINGS".equalsIgnoreCase(
              account.getAccountType())&&account.getMonthlyMovementLimit()!=null){
            OffsetDateTime start=OffsetDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
            return transactionRepository.findByAccountIdAndTimestampBetween(
                accountId,start,OffsetDateTime.now())
                .count()
                .flatMap(count-> {
                  if(count>=account.getMonthlyMovementLimit()){
                    return Mono.error(new BusinessException("Monthly movement limit exceeded"));
                  }
                  account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
                  Transaction tx=Transaction.builder()
                      .accountId(accountId)
                      .type(transactionRequest.getType())
                      .amount(transactionRequest.getAmount())
                      .timestamp(OffsetDateTime.now())
                      .description(transactionRequest.getDescription())
                      .build();
                  return accountRepository.save(account)
                      .flatMap(saved-> transactionRepository.save(tx).thenReturn(saved));
                });
            }else{
            account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
            Transaction tx=Transaction.builder()
                .accountId(accountId)
                .type(transactionRequest.getType())
                .amount(transactionRequest.getAmount())
                .timestamp(OffsetDateTime.now())
                .description(transactionRequest.getDescription())
                .build();
            return accountRepository.save(account)
                .flatMap(saved->transactionRepository.save(tx).thenReturn(saved));
          }
        })
        .map(this::toDto);
  }

  @Override
  public Flux<TransactionRequest> getTransactions(String accountId) {
    return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId)
        .map(tx->{
          TransactionRequest txReq=new TransactionRequest();
          txReq.setType(tx.getType());
          txReq.setAmount(tx.getAmount());
          txReq.setDescription(tx.getDescription());
          return txReq;
        });
  }

  @Override
  public Mono<BigDecimal> getBalance(String accountId) {
    return accountRepository.findById(accountId)
        .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
        .map(Account::getBalance);
  }

  private Mono<AccountDTO> validateAndCreateAccount(AccountRequest accountRequest){
    Account account=Account.builder()
        .accountNumber(generateAccountNumber())
        .accountType(accountRequest.getAccountType())
        .ownerCustomerId(accountRequest.getOwnerCustomerId())
        .coOwners(accountRequest.getCoOwners())
        .balance(BigDecimal.ZERO)
        .monthlyMovementLimit(accountRequest.getMonthlyMovementLimit())
        .fixedTermDay(accountRequest.getFixedTermDay())
        .build();
    return accountRepository.findByAccountTypeAndOwnerCustomerId(
        accountRequest.getAccountType(),accountRequest.getOwnerCustomerId())
        .hasElements()
        .flatMap(exits->{
          if(exits&&accountRequest.getAccountType().equalsIgnoreCase("SAVINGS")){
            return Mono.error(new BusinessException("Personal already has a savings account"));
          }
          return accountRepository.save(account)
              .map(this::toDto);
        });
  }

  private Mono<AccountDTO> customerFallbackCreate(AccountRequest accountRequest, Throwable ex){
    return Mono.error(new BusinessException("Customer service not available: "+ex.getMessage()));
  }
  private String generateAccountNumber(){
    return "ACCT-"+ UUID.randomUUID().toString().substring(0,8).toUpperCase();
  }
  private AccountDTO toDto(Account account){
    return AccountDTO.builder()
        .accountNumber(account.getAccountNumber())
        .accountType(account.getAccountType())
        .ownerCustomerId(account.getOwnerCustomerId())
        .coOwners(account.getCoOwners())
        .balance(account.getBalance())
        .build();
  }
}
