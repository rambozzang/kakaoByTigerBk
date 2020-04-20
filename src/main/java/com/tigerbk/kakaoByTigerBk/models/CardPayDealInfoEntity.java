package com.tigerbk.kakaoByTigerBk.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "CardPayDealInfoEntity")
public class CardPayDealInfoEntity {

	@Id
	@NonNull
	@Column(name = "dealKey",  length = 20, updatable = false)
//	@GeneratedValue(strategy = GenerationType.AUTO)
	private String dealKey; // 20자리
	
	//카드취소시 원거래 카드승인Key
	@Column(name = "pdealKey",  length = 20,nullable = true, updatable = false)
	private String pdealKey;
	
	// PAYMENT : 카드 승인 요청
	// CALCEL : 카드 취소 요청
	@Enumerated(EnumType.STRING)
	@Column(name = "bizType", nullable = false, updatable = false)
	private BizTypeEnum bizType;
	
	// 카드번호(10~16번자)
	@Column(name = "cardNumber", nullable = false, updatable = false)	
	private String cardNumber;

	// 유효기간(4자리)
	@Column(name = "cardExpiredDate",  nullable = false, updatable = false)
	private String cardExpiredDate;

	// cvc(3자리숫자)
	@Column(name = "cardCvc", nullable = false, updatable = false)
	private String cardCvc;

	// 할부개월수 1~12 , 일시불 : 00 
	@ColumnDefault("00") //default 0
	@Column(name = "cardPeriod", length = 2, nullable = false, updatable = false)
	private String cardPeriod;

	// 10억원이하 숫자
	@Column(name = "cardAmount", nullable = false, updatable = false)
	private Long cardAmount;

	// 부가가치세
	@ColumnDefault("0") //default 0
	@Column(name = "cardVat", nullable = false,  updatable = false)
	private Long cardVat;
	
	// 거래 시각 
	@CreationTimestamp
	@Column(name = "createdTimeAt", nullable = false , updatable = false)
	private LocalDateTime createdTimeAt;

	// 수정 시각 
	@UpdateTimestamp
	@Column(name = "updateTimeAt",  nullable = false , updatable = false)
	private LocalDateTime updateTimeAt;

	

}
