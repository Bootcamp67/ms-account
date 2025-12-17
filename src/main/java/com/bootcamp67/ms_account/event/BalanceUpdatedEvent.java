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
public class BalanceUpdatedEvent {
  private String accountId;
  private String customerId;
  private BigDecimal previousBalance;
  private BigDecimal newBalance;
  private BigDecimal changeAmount;
  private String operationType;    // DEBIT, CREDIT
  private String transactionId;
  private String description;
}
