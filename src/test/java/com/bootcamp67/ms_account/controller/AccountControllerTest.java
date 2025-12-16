package com.bootcamp67.ms_account.controller;

import com.bootcamp67.ms_account.dto.AccountDTO;
import com.bootcamp67.ms_account.dto.AccountRequest;
import com.bootcamp67.ms_account.dto.TransactionDTO;
import com.bootcamp67.ms_account.dto.TransactionRequest;
import com.bootcamp67.ms_account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AccountController.class)
class AccountControllerTest {

  @Autowired
  private WebTestClient webClient;

  @MockBean
  private AccountService service;

  @Test
  void getBalance_shouldReturnNumber() {
    // Mock del servicio
    when(service.getBalance("acc1"))
        .thenReturn(Mono.just(new BigDecimal("100.50")));

    webClient.get()
        .uri("/api/v1/accounts/acc1/balance")
        .exchange()
        .expectStatus().isOk()
        .expectBody(BigDecimal.class)
        .isEqualTo(new BigDecimal("100.50"));
  }

  @Test
  void createTransaction_shouldReturn201() {
    TransactionRequest req = new TransactionRequest();
    req.setType("DEPOSIT");
    req.setAmount(new BigDecimal("50"));

    AccountDTO dto = AccountDTO.builder()
        .id("tx1")
        .balance(req.getAmount())
        .accountType(req.getType())
        .build();

    when(service.create(any(AccountRequest.class)))
        .thenReturn(Mono.just(dto));

    webClient.post()
        .uri("/api/v1/accounts/acc1/transactions")
        .bodyValue(req)
        .exchange()
        .expectStatus().isCreated()
        .expectBody()
        .jsonPath("$.id").isEqualTo("tx1");
  }
}
