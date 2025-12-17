package com.bootcamp67.ms_account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionProcessedEvent {

  private String transactionId;
  private String accountId;
  private String customerId;
  private String transactionType;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String description;
  private String sourceAccountId;
  private String cardId;
}
