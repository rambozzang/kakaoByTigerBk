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

import org.hibernate.annotations.CreationTimestamp;

import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "CardPaySendDataEntity")
public class CardPaySendDataEntity {
	// 20자리
	@Id
	@NonNull
	@Column(name = "sendDataKey", length=20, updatable = false)
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String sendDataKey;
	// 거래 시각
	@CreationTimestamp
	@Column(name = "createdTimeAt",  nullable = false, updatable = false)
	private LocalDateTime createdTimeAt;

	// PAYMENT : 카드 승인 요청
	// CALCEL : 카드 취소 요청
	@Enumerated(EnumType.STRING)
	@Column(name = "bizType", nullable = false, updatable = false)
	private BizTypeEnum bizType;
	
	// 카드승인Key or 카드취소Key
	// 0210 : 카드 취소 요청
	@Column(name = "dealKey",  length=20 ,nullable = false, updatable = false)
	private String dealKey;
	
	//카드취소시 원거래 카드승인Key
	@Column(name = "pApprKey", nullable = true, updatable = false)
	private String pApprKey;
	
	
	// 총 450 byte
	@Column(name = "sendData", length = 450, nullable = false, updatable = false)
	private String sendData;

	// 전송 성공 여부
	@Column(name = "SendResult", length = 2, nullable = false, updatable = false)
	private String SendResult;

	
}
