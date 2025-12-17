package com.bootcamp67.ms_account.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceLowEvent {
  private String accountId;
  private java.math.BigDecimal currentBalance;
  private java.math.BigDecimal threshold;
}
