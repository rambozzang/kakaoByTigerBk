package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprInVO {
	// PAYMENT : 카드 승인 요청
	// CALCEL : 카드 취소 요청
	@NotEmpty
	@NotBlank
	private String bizType;	
	// 카드 승인 상태(2자리)
	//	@Enumerated(EnumType.STRING)
	private String state;
	// 카드번호(10~16번자)
	@NotEmpty
	@NotBlank
	@Size(min = 12, max = 13)
	private String cardNumber;
	// 유효기간(4자리)
	@NotEmpty
	@NotBlank
	@Size(min = 4, max = 4)
	private String cardExpiredDate;
	// cvc(3자리숫자)
	@NotEmpty
	@NotBlank
	@Size(min = 3, max = 3)
	private String cardCvc;
	// 할부개월수 1~12 , 일시불 : 00 
	@Size(min = 1, max = 2)
	private String cardPeriod;
	// 10억원이하 숫자	
	private Long cardAmount;
	// 부가가치세	
	private Long cardVat;
}
