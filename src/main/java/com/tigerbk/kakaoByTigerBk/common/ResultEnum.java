package com.tigerbk.kakaoByTigerBk.common;

/*
 *  Rest API 기본 에러 코드 상세코드는 별도 작성 
 */
public enum ResultEnum {
	
	SUCCESS("0000"),
	ERROR("9999");
	
	private String result;

	ResultEnum(String result) {
		this.result = result;
	}
	
	public String get() {
		return this.result;
	}
	
	
}
