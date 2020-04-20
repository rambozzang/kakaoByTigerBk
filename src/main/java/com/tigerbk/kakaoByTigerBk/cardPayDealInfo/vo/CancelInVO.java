package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelInVO {

	@NotEmpty
	@NotBlank
	private String bizType;	
	// 카드 승인 요청키
	private String dealKey;
	// 10억원이하 숫자
	private Long cardAmount;
	// 부가가치세
	private Long cardVat;

}
