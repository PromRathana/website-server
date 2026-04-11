package com.cambofreelance.websiteservice.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface StatementDetailProjection {

    String getPlayId();

    String getPostIds();

    LocalDateTime getPlayDate();

    BigDecimal getPrice();

    String getResultValue();

    String getResultStatus();

    String getNumberInput();
}