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
public class AccountCreatedEvent {
  private String accountId;
  private String customerId;
  private String accountType;      // SAVINGS, CHECKING, FIXED_TERM
  private BigDecimal initialBalance;
  private String currency;
}
