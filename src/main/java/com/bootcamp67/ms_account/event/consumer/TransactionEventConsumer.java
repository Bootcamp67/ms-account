package com.bootcamp67.ms_account.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

  @KafkaListener(
      topics = "transaction-events",
      groupId = "account-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleTransactionEvent(String message) {
    log.info("Received transaction event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - TRANSACTION_CREATED → Verify balance
      // - TRANSACTION_REVERSED → Reverse balance
      // - TRANSACTION_CONFIRMED → Finalize balance

      log.info("Processing transaction event: {}", message);

    } catch (Exception e) {
      log.error("Error processing transaction event: {}", e.getMessage(), e);
    }
  }

}
