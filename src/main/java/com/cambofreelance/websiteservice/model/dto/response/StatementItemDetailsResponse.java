package com.cambofreelance.websiteservice.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StatementItemDetailsResponse(
    List<String> postIds,
    LocalDateTime playDate,
    BigDecimal price,
    String resultValue,
    String resultStatus,
    String numberInput
) {

}