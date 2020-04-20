package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.service;

import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.SearchCardInfoVO;

public interface CardPayDealInfoService {

	// 1. 카드 승인요청 처리 서비스
	public ApprOutVO procCardApprove(ApprInVO inVO);

	// 2. 카드 취소요청 처리 서비스
	public CancelOutVO procCardCancel(CancelInVO inVO);

	// 3. 카드 승인 내역 조회 서비스
	public SearchCardInfoVO searchByDealKey(String DealKey);

}
