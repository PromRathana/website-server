package com.cambofreelance.websiteservice.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface StatementItemProjection {

    String getInvoiceNo();

    LocalDateTime getPlayDate();

    BigDecimal getAmount();

    BigDecimal getCommission();

    BigDecimal getPayout();

    BigDecimal getWinLost();
}