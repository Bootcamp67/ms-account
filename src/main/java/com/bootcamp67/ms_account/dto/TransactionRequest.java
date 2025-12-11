package com.bootcamp67.ms_account.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
  @NotBlank
  private String Type;
  @NotNull
  @DecimalMin("0.01")
  private BigDecimal amount;
  private String description;
}
