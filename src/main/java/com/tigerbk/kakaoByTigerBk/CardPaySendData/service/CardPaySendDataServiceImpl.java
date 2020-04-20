package com.tigerbk.kakaoByTigerBk.CardPaySendData.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tigerbk.kakaoByTigerBk.CardPaySendData.repostitory.CardPaySendDataRepository;
import com.tigerbk.kakaoByTigerBk.CardPaySendData.vo.CardPaySendDataVO;
import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;
import com.tigerbk.kakaoByTigerBk.common.ErrorCodeEnum;
import com.tigerbk.kakaoByTigerBk.common.BizException;
import com.tigerbk.kakaoByTigerBk.common.UtilsLib;
import com.tigerbk.kakaoByTigerBk.models.CardPayDealInfoEntity;
import com.tigerbk.kakaoByTigerBk.models.CardPaySendDataEntity;

@Service
public class CardPaySendDataServiceImpl implements CardPaySendDataService {

	@Autowired
	CardPaySendDataRepository cardPaySendDataRepository;

	// 전문길이 셋팅
	final private int headerSize = 34;
	final private int bodySize = 416;
	final private int sendSize = 450;

	@Override
	public CardPaySendDataVO sendData(CardPayDealInfoEntity inVO) {
		
		CardPaySendDataVO outVO = new CardPaySendDataVO();
		StringBuffer bodyData = new StringBuffer(bodySize);
		
		// 카드 정보 암호화 카드번호|유효기간|cvc
		String encryptData = UtilsLib.makeEnCardinfo(inVO.getCardNumber(), inVO.getCardExpiredDate(), inVO.getCardCvc());
		String pdealKey = inVO.getBizType() == BizTypeEnum.PAYMENT ? " ":  String.valueOf(inVO.getPdealKey());
		
		// 1. header 조립
		StringBuffer headerData = new StringBuffer(headerSize);
		headerData.append(UtilsLib.strAdd(String.valueOf(sendSize), " ", "R", 4)); // 데이터 길이 숫자 4 "데이터 길이"를 제외한 총 길이
		headerData.append(UtilsLib.strAdd(String.valueOf(inVO.getBizType()), " ", "L", 10)); // 좌측 정렬, 빈공간 채우기
		headerData.append(UtilsLib.strAdd(String.valueOf(inVO.getDealKey()), " ", "R", 20));
		// 2. body 조립
		bodyData.append(UtilsLib.strAdd(inVO.getCardNumber(), " ", "R", 20)); // 카드번호 숫자(L)
		bodyData.append(UtilsLib.strAdd(inVO.getCardPeriod(), "0", "L", 2)); // 할부개월수 숫자 L
		bodyData.append(UtilsLib.strAdd(inVO.getCardExpiredDate(), "0", "L", 4)); // 유효기간숫자 L
		bodyData.append(UtilsLib.strAdd(inVO.getCardCvc(), "0", "L", 3)); // cvc숫자 L
		bodyData.append(UtilsLib.strAdd(String.valueOf(inVO.getCardAmount()), " ", "R", 10)); // 거래금액
		bodyData.append(UtilsLib.strAdd(String.valueOf(inVO.getCardVat()), "0", "L", 10)); // 부가세		
		bodyData.append(UtilsLib.strAdd(pdealKey, " ", "L", 20)); // 취소시에만 결제 관리번호 저장 결제시에는 공백		
		bodyData.append(UtilsLib.strAdd(encryptData, " ", "L", 300)); // 암/복호화 방식 자유롭게 선택
		bodyData.append(UtilsLib.strAdd(" ", " ", "L", 47)); // 향후 생길 데이터를 위해 남겨두는 공간
		
		if (bodyData.length() != bodySize) {
			System.out.println( "body "+bodyData.length() +" 데이타 길이가 " + bodySize + " 바이트가 아닙니다.!!! ");
			throw new BizException(ErrorCodeEnum.ERROR_SENDDATA_LENGTH);
		}
		StringBuffer sendData = new StringBuffer(sendSize);
		sendData.append(headerData);
		sendData.append(bodyData);
		
		CardPaySendDataEntity cardPaySendDataEntity = new CardPaySendDataEntity();
		final String sendDataKey = UtilsLib.getUniqueID();
		cardPaySendDataEntity = CardPaySendDataEntity.builder()
				.sendDataKey(sendDataKey)
				.bizType(inVO.getBizType())
				.dealKey(inVO.getDealKey())
				.sendData(sendData.toString())
				.build();
		
		cardPaySendDataEntity = this.saveData(cardPaySendDataEntity);		
		outVO.setSendDataKey(cardPaySendDataEntity.getSendDataKey());
		return outVO;
	}

	/*
	 * 실제 통신 및 String data 저장한다.
	 */
	public CardPaySendDataEntity saveData(CardPaySendDataEntity cardPaySendDataEntity) {

		CardPaySendDataEntity rdata = new CardPaySendDataEntity();
		try {		
			// 전문 전송 성공 : 00 강제 셋팅
			cardPaySendDataEntity.setSendResult("00");		
			rdata = cardPaySendDataRepository.save(cardPaySendDataEntity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(ErrorCodeEnum.ERROR_SENDDATA_SAVE);
		}
		return rdata;
	}

	@Override
	@Transactional(readOnly = true)
	public CardPaySendDataVO searchSendData(String sendDataKey) {

		CardPaySendDataEntity rData = new CardPaySendDataEntity();
		CardPaySendDataVO outVo = new CardPaySendDataVO();
		try {
			rData = cardPaySendDataRepository.findById(sendDataKey)
					.orElseThrow(() -> new BizException(ErrorCodeEnum.ERROR_SENDDATA_NOTFOUND));
			if (rData != null) {				
				// 전체 전문 데이타
				String sendData = rData.getSendData();
				// bodyData 추출
				String bodyData = UtilsLib.getString(sendData, headerSize, sendSize - headerSize);
				String bodyData1 = UtilsLib.getString(sendData, headerSize, 20); // 카드번호 숫자(L)
				String bodyData2 = UtilsLib.getString(sendData, headerSize + 20, 2); // 할부개월수 숫자 L
				String bodyData3 = UtilsLib.getString(sendData, headerSize + 22, 4); // 유효기간숫자 L
				String bodyData4 = UtilsLib.getString(sendData, headerSize + 26, 3); // cvc숫자 L
				String bodyData5 = UtilsLib.getString(sendData, headerSize + 29, 10); // 거래금액
				String bodyData6 = UtilsLib.getString(sendData, headerSize + 39, 10); // 부가세
				String bodyData7 = UtilsLib.getString(sendData, headerSize + 49, 20); // 취소시에만 결제 관리번호 저장 결제시에는 공백
				String bodyData8 = UtilsLib.getString(sendData, headerSize + 69, 300); // 암/복호화 방식 자유롭게 선택
				String bodyData9 = UtilsLib.getString(sendData, headerSize + 369, 47); // 향후 생길 데이터를 위해 남겨두는 공간				
				
				outVo.setSendDataKey(sendDataKey);
				outVo.setDealKey(rData.getDealKey());
				outVo.setBizType(rData.getBizType());
				outVo.setPdealKey(rData.getPApprKey());
				outVo.setSendResult(rData.getSendResult());
				outVo.setCreatedTimeAt(rData.getCreatedTimeAt());
				outVo.setCardPeriod(bodyData2);
				outVo.setCardAmount(Long.parseLong(bodyData5));
				outVo.setCardVat(Long.parseLong(bodyData6));
				
				// 암호화된 데이타 에서 추출 할경우
				String tmp = UtilsLib.makeDeCardinfo(bodyData8);
				String[] deCardInfo = tmp.split("\\|");  // | 구분자로 split 처리한다. 
				outVo.setCardNumber(deCardInfo[0]);
				outVo.setCardCvc(deCardInfo[1]);
				outVo.setCardExpiredDate(deCardInfo[2]);
				
				
			} else {
				throw new BizException(ErrorCodeEnum.ERROR_SENDDATA_NOTFOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(ErrorCodeEnum.ERROR_SENDDATA_FAIL);
		}
		return outVo;
	}
}
