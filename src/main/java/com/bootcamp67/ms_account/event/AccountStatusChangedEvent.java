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
public class AccountStatusChangedEvent {

  private String accountId;
  private String customerId;
  private String previousStatus;
  private String newStatus;
  private String reason;
  private String requestedBy;
  private LocalDateTime changedAt;
}
