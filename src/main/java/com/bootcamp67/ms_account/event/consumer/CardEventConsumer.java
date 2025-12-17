package com.bootcamp67.ms_account.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardEventConsumer {

  @KafkaListener(
      topics = "payment-events",
      groupId = "account-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handlePaymentEvent(String message) {
    log.info("Received payment event: {}", message);

    try {
      // TODO: Parse event and debit from account
      // Example:
      // PaymentProcessedEvent event = parse(message);
      // if (event.wasMainAccount) {
      //     accountService.debit(event.accountId, event.amount);
      // }

      log.info("Processing payment event: {}", message);

    } catch (Exception e) {
      log.error("Error processing payment event: {}", e.getMessage(), e);
      // In production: send to DLQ (Dead Letter Queue)
    }
  }

  @KafkaListener(
      topics = "card-events",
      groupId = "account-service-group"
  )
  public void handleCardEvent(String message) {
    log.info("Received card event: {}", message);

    try {
      // TODO: Implement logic
      // If card created → Validate account exists and belongs to customer
      // If card associated with account → Verify account is active

    } catch (Exception e) {
      log.error("Error processing card event: {}", e.getMessage(), e);
    }
  }
}
