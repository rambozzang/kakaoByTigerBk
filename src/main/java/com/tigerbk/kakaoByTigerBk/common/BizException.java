package com.tigerbk.kakaoByTigerBk.common;


/*
 *  비즈니스 Exception 처리 .
 * */
public class BizException extends RuntimeException  {

	private static final long serialVersionUID = 1133942101198931627L;

	private Exception e;
	private ErrorCodeEnum errorCodeEnum;
	private Object data = "";

	public BizException(ErrorCodeEnum errorCodeEnum) {
		this.errorCodeEnum = errorCodeEnum;
	}

	public BizException(ErrorCodeEnum errorCodeEnum, Object data) {
		this.errorCodeEnum = errorCodeEnum;
		this.data = data;
	}

	public BizException(Exception e, ErrorCodeEnum errorCodeEnum) {
		this.e = e;
		this.errorCodeEnum = errorCodeEnum;
	}

	public BizException(Exception e, ErrorCodeEnum errorCodeEnum, Object data) {
		this.e = e;
		this.errorCodeEnum = errorCodeEnum;
		this.data = data;
	}

	public Exception getE() {
		return e;
	}

	public ErrorCodeEnum getErrorCodeEnum() {
		return errorCodeEnum;
	}

	public Object getData() {
		return data;
	}
}
