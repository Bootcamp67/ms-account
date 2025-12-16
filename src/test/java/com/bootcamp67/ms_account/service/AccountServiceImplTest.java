package com.bootcamp67.ms_account.service;

import com.bootcamp67.ms_account.dto.AccountRequest;
import com.bootcamp67.ms_account.entity.Account;
import com.bootcamp67.ms_account.respository.AccountRepository;
import com.bootcamp67.ms_account.respository.TransactionRepository;
import com.bootcamp67.ms_account.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class AccountServiceImplTest {

  @Mock
  AccountRepository accountRepository;

  @Mock
  TransactionRepository transactionRepository;

  @Mock
  WebClient msCustomerWebClient;

  @Mock
  WebClient.RequestHeadersUriSpec<?> uriSpec;

  @Mock
  WebClient.RequestHeadersSpec<?> headersSpec;

  @Mock
  WebClient.ResponseSpec responseSpec;

  @InjectMocks
  AccountServiceImpl service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    when(msCustomerWebClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(anyString(), any(Map.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void create_whenCustomerIsPersonal_shouldSaveAccount() {
    when(responseSpec.bodyToMono(CustomerRemote.class))
        .thenReturn(Mono.just((CustomerRemote)()->"PERSONAL"));

    when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
      Account acc = inv.getArgument(0);
      acc.setId("acc1");
      return Mono.just(acc);
    });

    AccountRequest req = new AccountRequest();
    req.setAccountType("SAVINGS");
    req.setOwnerCustomerId("c1");

    StepVerifier.create(service.create(req))
        .assertNext(dto -> {
          assert dto.getId().equals("acc1");
          assert dto.getOwnerCustomerId().equals("c1");
        })
        .verifyComplete();
  }

  @Test
  void create_whenCustomerIsBusinessAndChecking_shouldSaveAccount() {
    when(responseSpec.bodyToMono(CustomerRemote.class))
        .thenReturn(Mono.just((CustomerRemote)()->"BUSINESS"));

    when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
      Account acc = inv.getArgument(0);
      acc.setId("acc-123");
      return Mono.just(acc);
    });

    AccountRequest req = new AccountRequest();
    req.setAccountType("CHECKING");
    req.setOwnerCustomerId("c2");

    StepVerifier.create(service.create(req))
        .assertNext(dto -> {
          assert dto.getId().equals("acc-123");
          assert dto.getOwnerCustomerId().equals("c2");
        })
        .verifyComplete();
  }
}
