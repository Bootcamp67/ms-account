package com.bootcamp67.ms_account.controller;

import com.bootcamp67.ms_account.dto.AccountDTO;
import com.bootcamp67.ms_account.dto.AccountRequest;
import com.bootcamp67.ms_account.dto.TransactionRequest;
import com.bootcamp67.ms_account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
  private final AccountService accountService;
  
  @GetMapping
  public Flux<AccountDTO> listAll(){
    return accountService.findAll();
  }
  
  @GetMapping("/{id}")
  public Mono<AccountDTO> getById(@PathVariable String id){
    return accountService.findById(id);
  }
  
  @PostMapping
  public Mono<ResponseEntity<AccountDTO>> create(
      @Valid
      @RequestBody AccountRequest accountRequest){
    return accountService.create(accountRequest)
        .map(acc->
          ResponseEntity.status(201).body(acc)
        );
  }
  
  @PutMapping("/{id}")
  public Mono<AccountDTO> update(@PathVariable String id,
                                 @Valid @RequestBody AccountRequest accountRequest){
    return accountService.update(id,accountRequest);
  }
  
  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> delete(@PathVariable String id){
    return accountService.delete(id)
        .thenReturn(ResponseEntity.noContent().build());
  }
  
  @PostMapping("/{id}/transactions/deposit")
  public Mono<AccountDTO> deposit (@PathVariable String id,
                                   @Valid @RequestBody TransactionRequest transactionRequest){
    return accountService.deposit(id,transactionRequest);
  }

  @PostMapping("/{id}/transactions/withdraw")
  public Mono<AccountDTO> withdraw(
      @PathVariable String id,
      @Valid @RequestBody TransactionRequest transactionRequest){
    return accountService.withdraw(id,transactionRequest);
  }

  @GetMapping("/{id}/transactions")
  public Flux<TransactionRequest> getTransactions(@PathVariable String id){
    return accountService.getTransactions(id);
  }

  @GetMapping("/{id}/balance")
  public Mono<BigDecimal> getBalance(@PathVariable String id){
    return accountService.getBalance(id);
  }
}
