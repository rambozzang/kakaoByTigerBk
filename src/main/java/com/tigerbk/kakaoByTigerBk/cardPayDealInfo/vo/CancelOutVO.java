package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelOutVO {
	// 카드 승인 요청키	
	private String dealKey;
	// 결제금액
	private Long cardAmount ;
	// 결제Vat
	private Long cardVat ;
	// 결과
	private String result;
	// 결제상태인 vat
	private Long remaindAmt; 
	private Long remaindVat; 
	// 설명  ㅡ
	private String  desc; 
}
