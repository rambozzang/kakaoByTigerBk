package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCardInfoVO {

	@NonNull
	private String dealKey;
	private String pdealKey;
	private String bizType;
	private String state;
	private String cardNumber;
	private String cardExpiredDate;
	private String cardCvc;
	private String cardPeriod;
	private Long cardAmount;
	private Long cardVat;
	private LocalDateTime createdTimeAt;

}
