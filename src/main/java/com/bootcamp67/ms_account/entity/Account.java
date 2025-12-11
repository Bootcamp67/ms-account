package com.bootcamp67.ms_account.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {
  @Id
  private String id;
  private String accountNumber;
  private String accountType;
  private String ownerCustomerId;
  private List<String> coOwners;
  private List<String> authorizedSignatories;
  private BigDecimal balance;
  private Integer monthlyMovementLimit;
  private Integer fixedTermDay;
}
