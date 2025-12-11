package com.bootcamp67.ms_account.dto;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AccountRequest {
  @NotBlank
  private String accountType;
  @NotBlank
  private String ownerCustomerId;
  private List<String> coOwners;
  private Integer fixedTermDay;
  private Integer monthlyMovementLimit;
}
