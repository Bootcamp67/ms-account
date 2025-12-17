package com.bootcamp67.ms_account.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {
  private String eventId;
  private String eventType;
  private String accountId;
  private String customerId;
  private LocalDateTime timestamp;
  private Object payload;

  public static class EventType {
    public static final String ACCOUNT_CREATED = "ACCOUNT_CREATED";
    public static final String ACCOUNT_DELETED = "ACCOUNT_DELETED";
    public static final String ACCOUNT_BLOCKED = "ACCOUNT_BLOCKED";
    public static final String ACCOUNT_ACTIVATED = "ACCOUNT_ACTIVATED";
    public static final String BALANCE_UPDATED = "BALANCE_UPDATED";
    public static final String TRANSACTION_PROCESSED = "TRANSACTION_PROCESSED";
    public static final String BALANCE_LOW = "BALANCE_LOW";
  }
}
