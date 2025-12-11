package com.bootcamp67.ms_account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
  private String id;
  private String accountNumber;
  private String accountType;
  private String ownerCustomerId;
  private List<String> coOwners;
  private BigDecimal balance;
}
