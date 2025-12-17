package com.bootcamp67.ms_account.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventConsumer {

  @KafkaListener(
      topics = "customer-events",
      groupId = "account-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleCustomerEvent(String message) {
    log.info("Received customer event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - CUSTOMER_BLOCKED → Block all customer accounts
      // - CUSTOMER_DELETED → Delete or archive all accounts
      // - CUSTOMER_UPGRADED → Update account tier

      log.info("Processing customer event: {}", message);

    } catch (Exception e) {
      log.error("Error processing customer event: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(
      topics = "customer-status-events",
      groupId = "account-service-group"
  )
  public void handleCustomerStatusChange(String message) {
    log.info("Received customer status change: {}", message);

    try {
      // TODO: Implement logic
      // If customer is blocked → Block all their accounts
      // If customer is VIP → Upgrade account benefits

    } catch (Exception e) {
      log.error("Error processing customer status change: {}", e.getMessage(), e);
    }
  }
}
