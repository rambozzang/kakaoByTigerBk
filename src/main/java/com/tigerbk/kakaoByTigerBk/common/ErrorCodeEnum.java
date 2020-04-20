package com.tigerbk.kakaoByTigerBk.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 *  에러 코드 enum 
 */

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCodeEnum   {
	
	ERROR_INVALID_CODE("9001",  "업무구분 코드가  잘못 들어왔습니다."),
	ERROR_AMOUNT_RANGE("9002",  "카드결제금액은 100원 ~ 10억까지만 가능합니다. "),
	ERROR_APPRV_SAVE("9003", "카드승인 처리 데이타 저장시 오류가 발생했습니다."),
	ERROR_APPRV_NOTFOUND("9004",  "카드 승인 내역 조회 데이타 존재하지 않습니다."),
	ERROR_LESS_VAT("9005",  "부가가치세는 결제금액보다 클수 없습니다."),	
	ERROR_CANCEL_VAT("9005",  "취소 가능한 금액보다 취소금액이 더 큽니다."),
	ERROR_UNABLE_VAT("9006", " 마지막 취소처리할 VAT금액 상이하여 취소 할수 없습니다."),
	ERROR_NOTENONGH_VAT("9007", "취소처리할 VAT금액이 부족하여 취소 할수 없습니다. "),

	ERROR_CACEL_SAVE("9008",  "결제취소 저장시 오류가 발생했습니다."),
	ERROR_NOT_FOUND("9009",  "조회 데이타가 존재하지 않습니다."),
	ERROR_NOT_FOUND2("9010", "조회 중 오류가 발생되었습니다."),		
	ERROR_SENDDATA_LENGTH("9011", "전문 길이가 일치 하지 않아 오류가 발생했습니다."),
	ERROR_SENDDATA_SAVE("9011", "전문 데이타 저장시 오류가 발생했습니다"),
	ERROR_SENDDATA_NOTFOUND("9011", "전문 조회 된 데이타 없습니다"),
	ERROR_SENDDATA_FAIL("9011", "전문 데이타 조회 중 오류가 발생했습니다"),	
	ERROR_ENCRYPT_FAIL("9011", "데이타  암복호화시 오류가 발생했습니다."),
	ERROR_UNKNOWN_ERROR("9011",  "알수없는 오류 발생");
	
	private final String code;
	private final String message;
		 

}
