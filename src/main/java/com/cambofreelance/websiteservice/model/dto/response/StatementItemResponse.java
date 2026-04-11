package com.cambofreelance.websiteservice.model.dto.response;

import com.cambofreelance.websiteservice.model.dto.common.LocalizedTextDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record StatementItemResponse(
    String playId,
    String invoiceNo,
    LocalDateTime playDate,
    BigDecimal amount,
    BigDecimal commission,
    BigDecimal payout,
    BigDecimal winLost,
    String lotteryId,
    String lotteryName,
    LocalizedTextDto lotteryType,
    LocalTime time,
    List<StatementItemDetailsResponse> details
) {

}