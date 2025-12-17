package com.bootcamp67.ms_account.event.producer;

import com.bootcamp67.ms_account.event.AccountCreatedEvent;
import com.bootcamp67.ms_account.event.AccountEvent;
import com.bootcamp67.ms_account.event.AccountStatusChangedEvent;
import com.bootcamp67.ms_account.event.BalanceLowEvent;
import com.bootcamp67.ms_account.event.BalanceUpdatedEvent;
import com.bootcamp67.ms_account.event.TransactionProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  // Kafka Topics
  private static final String ACCOUNT_EVENTS_TOPIC = "account-events";
  private static final String BALANCE_EVENTS_TOPIC = "balance-events";
  private static final String ACCOUNT_STATUS_TOPIC = "account-status-events";
  private static final String TRANSACTION_EVENTS_TOPIC = "transaction-events";

  /**
   * Publish Account Created Event
   */
  public Mono<Void> publishAccountCreated(AccountCreatedEvent event) {
    log.info("Publishing account created event for account: {}", event.getAccountId());

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.ACCOUNT_CREATED)
        .accountId(event.getAccountId())
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(ACCOUNT_EVENTS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Balance Updated Event
   */
  public Mono<Void> publishBalanceUpdated(BalanceUpdatedEvent event) {
    log.info("Publishing balance updated event for account: {} new balance: {}",
        event.getAccountId(), event.getNewBalance());

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.BALANCE_UPDATED)
        .accountId(event.getAccountId())
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(BALANCE_EVENTS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Transaction Processed Event
   */
  public Mono<Void> publishTransactionProcessed(TransactionProcessedEvent event) {
    log.info("Publishing transaction processed event: {} for account: {}",
        event.getTransactionId(), event.getAccountId());

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.TRANSACTION_PROCESSED)
        .accountId(event.getAccountId())
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(TRANSACTION_EVENTS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Account Blocked Event
   */
  public Mono<Void> publishAccountBlocked(AccountStatusChangedEvent event) {
    log.info("Publishing account blocked event for account: {}", event.getAccountId());

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.ACCOUNT_BLOCKED)
        .accountId(event.getAccountId())
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(ACCOUNT_STATUS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Account Activated Event
   */
  public Mono<Void> publishAccountActivated(AccountStatusChangedEvent event) {
    log.info("Publishing account activated event for account: {}", event.getAccountId());

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.ACCOUNT_ACTIVATED)
        .accountId(event.getAccountId())
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(ACCOUNT_STATUS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Account Deleted Event
   */
  public Mono<Void> publishAccountDeleted(String accountId, String customerId, String reason) {
    log.info("Publishing account deleted event for account: {}", accountId);

    AccountStatusChangedEvent statusEvent = AccountStatusChangedEvent.builder()
        .accountId(accountId)
        .customerId(customerId)
        .previousStatus("ACTIVE")
        .newStatus("DELETED")
        .reason(reason)
        .changedAt(LocalDateTime.now())
        .build();

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.ACCOUNT_DELETED)
        .accountId(accountId)
        .customerId(customerId)
        .timestamp(LocalDateTime.now())
        .payload(statusEvent)
        .build();

    return sendEvent(ACCOUNT_EVENTS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Publish Balance Low Event
   * When account balance falls below threshold
   */
  public Mono<Void> publishBalanceLow(String accountId, String customerId,
                                      java.math.BigDecimal currentBalance,
                                      java.math.BigDecimal threshold) {
    log.warn("Publishing balance low event for account: {} balance: {} threshold: {}",
        accountId, currentBalance, threshold);

    BalanceLowEvent balanceLow = new BalanceLowEvent(accountId, currentBalance, threshold);

    AccountEvent accountEvent = AccountEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(AccountEvent.EventType.BALANCE_LOW)
        .accountId(accountId)
        .customerId(customerId)
        .timestamp(LocalDateTime.now())
        .payload(balanceLow)
        .build();

    return sendEvent(BALANCE_EVENTS_TOPIC, accountEvent.getAccountId(), accountEvent);
  }

  /**
   * Send event to Kafka topic
   * Uses ListenableFuture for Spring Boot 2.x compatibility
   */
  private Mono<Void> sendEvent(String topic, String key, Object event) {
    return Mono.create(sink -> {
      try {
        ListenableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(topic, key, event);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
          @Override
          public void onSuccess(SendResult<String, Object> result) {
            log.info("Event sent successfully to topic: {} partition: {} offset: {}",
                topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
            sink.success();
          }

          @Override
          public void onFailure(Throwable ex) {
            log.error("Error sending event to topic: {} error: {}", topic, ex.getMessage(), ex);
            sink.error(ex);
          }
        });
      } catch (Exception e) {
        log.error("Exception sending event to topic: {}", topic, e);
        sink.error(e);
      }
    });
  }

}
