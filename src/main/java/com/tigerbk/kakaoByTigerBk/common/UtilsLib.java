package com.tigerbk.kakaoByTigerBk.common;

import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

public class UtilsLib {

	/**
	 * 유니크 Key 구하기 20자리
	 */
	public static String getUniqueID() {

		UUID idOne = UUID.randomUUID();
		UUID idOne2 = UUID.randomUUID();
		String str = "" + idOne;
		String str2 = "" + idOne2;	
		String uid = str.hashCode() + "" + str2.hashCode();
		String filterStr = "" + uid;
		str = filterStr.replaceAll("-", "");	
		if(str.length() >  20) {
			str = str.substring(0, 20  );
		} else if(str.length() <   20) {
			Random rand = new Random(System.currentTimeMillis());		
			double doubleVar = Math.pow(10,(20- str.length()));
			doubleVar = Math.round(doubleVar);
			str = str +""+rand.nextInt((int)doubleVar );
		}
		return str;
	}

	
	/**
	 * VAT 계산 결제금액/ 11, 소수점 이하 반올림 계산 1000원일 경우 91원 부가가치세는 결제금액보다 클수 없습니다. 결제금액이
	 * 1000원일때 부가가치세는 0원 일수 있음.
	 */
	public static Long CalcVat(Long amount) {
		Long retVat = 0L;
		if (amount == 0) {
			return retVat;
		}
		retVat = (long) Math.round((amount / 11));
		return retVat;
	}

	/**
	 * 문자열 채우기 @param value값 , 채우기문자, 채우기위치, 사이즈 @return @throws
	 */
	public static String strAdd(String str, String strAdd, String align, int size) {

		StringBuffer strBuf = new StringBuffer();
		// 길이 체크
		if (str.length() > size) {
			return str.substring(0, size);
		}
		if ("L".equals(align)) {
			strBuf.append(str);
		}
		for (int i = 0; i < size - str.getBytes().length; i++) {

			if ("B".equals(strAdd)) {
				strBuf.append(" ");
			} else {
				strBuf.append(strAdd);
			}
		}
		if ("R".equals(align)) {
			strBuf.append(str);
		}
		return strBuf.toString();
	}
	/**
	* 스트링 자르기 (한글포함)
	 */
	public static String getString(String str, int sPoint, int length) throws Exception {
		String EncodingLang = "euc-kr";
		byte[] bytes = str.getBytes("euc-kr");
		byte[] value = new byte[length];
		if (bytes.length < sPoint + length) {
			throw new Exception(
					"문자 길이가 시작점보다 적습니다. =>  length : " + bytes.length + " sPoint : " + sPoint + " length : " + length);
		}
		for (int i = 0; i < length; i++) {
			value[i] = bytes[sPoint + i];
		}
		return new String(value, EncodingLang).trim().replace("_", "");
	}
	/**
	 * 암호화 카드번호|유효기간|cvc
	 */
	public static String makeEnCardinfo(String cardNumber, String cardExpiredDate, String cardCvc) {
		String encryptData = "";
		try {
			StringBuilder enBder = new StringBuilder();
			enBder.append(cardNumber);
			enBder.append("|");
			enBder.append(cardExpiredDate);
			enBder.append("|");
			enBder.append(cardCvc);
			String tmp = enBder.toString();
			AES256Util en = new AES256Util();
			encryptData = en.aesEncode(tmp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(ErrorCodeEnum.ERROR_ENCRYPT_FAIL);
		}
		return encryptData;
	}
	/**
	 * 복호화 작업
	 */
	public static String makeDeCardinfo(String deCardinfo) {
		String decryptData = "";
		try {
			AES256Util en = new AES256Util();
			decryptData = en.aesDecode(deCardinfo);
		} catch (Exception e) {
			e.printStackTrace();
			// throw new ErrorMessage(100, "데이타 전문 암호화시 오류가 발생했습니다.");
			throw new BizException(ErrorCodeEnum.ERROR_ENCRYPT_FAIL);
		}
		return decryptData;
	}

}
