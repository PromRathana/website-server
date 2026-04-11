package com.cambofreelance.websiteservice.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface StatementHeaderProjection {

    String getPlayId();

    String getInvoiceNo();

    LocalDateTime getPlayDate();

    BigDecimal getAmount();

    BigDecimal getCommission();

    BigDecimal getPayout();

    BigDecimal getWinLost();

    String getLotteryId();

    String getLotteryName();

    LocalTime getTime();

    String getLotteryTypeId();

    String getLotteryTypeEn();

    String getLotteryTypeKm();

    String getLotteryTypeTh();

    String getLotteryTypeVn();
}