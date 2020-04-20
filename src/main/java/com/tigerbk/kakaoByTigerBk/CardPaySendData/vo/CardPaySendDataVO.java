package com.tigerbk.kakaoByTigerBk.CardPaySendData.vo;

import java.time.LocalDateTime;

import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;

import lombok.Data;

@Data
public class CardPaySendDataVO {

	private String sendDataKey;
	private String dealKey;
	private String pdealKey;
	private BizTypeEnum bizType;
	private String cardNumber;
	private String cardExpiredDate;
	private String cardCvc;
	private String cardPeriod;
	private Long cardAmount;
	private Long cardVat;
	private String SendResult;
	private LocalDateTime createdTimeAt;
}
