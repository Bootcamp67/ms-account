package com.bootcamp67.ms_account.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDTO {
  private String id;
  private String accountId;
  private String type;
  private java.math.BigDecimal amount;
  private java.time.OffsetDateTime timestamp;
  private String description;
}
