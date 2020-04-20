package com.tigerbk.kakaoByTigerBk.CardPaySendData.service;

import com.tigerbk.kakaoByTigerBk.CardPaySendData.vo.CardPaySendDataVO;
import com.tigerbk.kakaoByTigerBk.models.CardPayDealInfoEntity;
import com.tigerbk.kakaoByTigerBk.models.CardPaySendDataEntity;

public interface CardPaySendDataService {
	// 카드 승인/취소 거래시 전문 저장 
	CardPaySendDataVO sendData(CardPayDealInfoEntity inVO);
	// sendData Key 전문 내용 조회 서비스
	CardPaySendDataVO searchSendData(String dealKey);
}
