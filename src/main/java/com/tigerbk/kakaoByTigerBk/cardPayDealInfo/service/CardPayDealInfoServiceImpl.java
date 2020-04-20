package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.service;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tigerbk.kakaoByTigerBk.CardPaySendData.service.CardPaySendDataService;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.repository.CardPayDealInfoRepository;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CardDealTotalCancelAmtDTO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.SearchCardInfoVO;
import com.tigerbk.kakaoByTigerBk.common.AES256Util;
import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;
import com.tigerbk.kakaoByTigerBk.common.ErrorCodeEnum;
import com.tigerbk.kakaoByTigerBk.common.BizException;
import com.tigerbk.kakaoByTigerBk.common.UtilsLib;
import com.tigerbk.kakaoByTigerBk.models.CardPayDealInfoEntity;

@Service
public class CardPayDealInfoServiceImpl implements CardPayDealInfoService {

	@Autowired
	CardPayDealInfoRepository cardRpy;

	@Autowired
	CardPaySendDataService sendDataService;

	// 1. 카드 승인요청 처리 서비스
	@Override
	@Transactional()
	public ApprOutVO procCardApprove(ApprInVO inVO) {
		// 1. uid 유니크키 생성
		final String dealKey = UtilsLib.getUniqueID();
		Long cardAmount = 0L;
		Long vatAmount = 0L;
		ApprOutVO apprOutVO = new ApprOutVO();
		CardPayDealInfoEntity dealInfoVO = new CardPayDealInfoEntity();
		try {			
				AES256Util en = new AES256Util();
				String enCardNumber = en.aesEncode(inVO.getCardNumber());
				String enCardExpiredDate = en.aesEncode(inVO.getCardExpiredDate());
				String enCardCvc = en.aesEncode(inVO.getCardCvc());

				dealInfoVO.setDealKey(dealKey);
				dealInfoVO.setCardNumber(enCardNumber);
				dealInfoVO.setCardExpiredDate(enCardExpiredDate);
				dealInfoVO.setCardCvc(enCardCvc);
				dealInfoVO.setCardAmount(inVO.getCardAmount());
				dealInfoVO.setCardPeriod(inVO.getCardPeriod());
				dealInfoVO.setBizType(BizTypeEnum.PAYMENT);
				// 1. 결제금액 체크 100원 ~ 1000000000원
				cardAmount = inVO.getCardAmount();
				if (100 > cardAmount || cardAmount > 1000000000) {
					throw new BizException(ErrorCodeEnum.ERROR_INVALID_CODE);
				}
				// 2. 부가세가 없을 경우 계산
				vatAmount = inVO.getCardVat() == null ? UtilsLib.CalcVat(inVO.getCardAmount()) : inVO.getCardVat();
				dealInfoVO.setCardVat(vatAmount);

				// 3. 내부 카드 승인내역 테이블 적재
				CardPayDealInfoEntity rData = cardRpy.save(dealInfoVO);

				if (rData != null) {
					// return 값 셋팅
					apprOutVO.setCardAmount(inVO.getCardAmount());
					apprOutVO.setCardVat(inVO.getCardVat());
					apprOutVO.setDealKey(dealKey);
					apprOutVO.setRemaindAmt(inVO.getCardAmount());
					apprOutVO.setRemaindVat(inVO.getCardVat());
					apprOutVO.setResult("정상");
					apprOutVO.setDesc(inVO.getCardAmount() + "원 (Vat : " + vatAmount + "원) 결제 성공");

					// 4. 전문 조립후 외부통신내역 테이블 적재
					dealInfoVO.setCardNumber(inVO.getCardNumber());
					dealInfoVO.setCardExpiredDate(inVO.getCardExpiredDate());
					dealInfoVO.setCardCvc(inVO.getCardCvc());
					sendDataService.sendData(dealInfoVO);

				} else {
					throw new BizException(ErrorCodeEnum.ERROR_APPRV_SAVE);
				}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException(ErrorCodeEnum.ERROR_APPRV_SAVE, e.getMessage());
		}
		System.out.println(apprOutVO);
		return apprOutVO;
	}

	// 2. 카드 취소요청 처리 서비스
	@Override
	@Transactional()
	public CancelOutVO procCardCancel(CancelInVO inVO) {
		// unique key 생성
		final String cencelDealKey = UtilsLib.getUniqueID();
		// 승인 key
		final String apprDealKey = inVO.getDealKey();
		// 카드 승인된 금액,vat
		Long cardAmount = 0L;
		Long vatAmount = 0L;
		// 카드 취소 할 금액
		final Long cardCancelAmount = inVO.getCardAmount();
		Long cardCancelVat = inVO.getCardVat();
		// 카드 누적 된 취소 금액
		Long cardTotalCancelVat = 0L;
		Long cardTotalCancelAmount = 0L;
		Long totalCardCancelVat = 0L;

		CancelOutVO outVO = new CancelOutVO();
		// 카드 승인내역 원본
		CardPayDealInfoEntity apprInfoVO = new CardPayDealInfoEntity();

		// 1.1. vat 는 결제금액보다 클수 없습니다.
		if (cardCancelVat != null && cardCancelAmount.compareTo(cardCancelVat) < 0) {
			throw new BizException(ErrorCodeEnum.ERROR_LESS_VAT);
		}

		synchronized (this) {
			// 1.2 기존 승인내역이 존재하는지 체크
			apprInfoVO = cardRpy.findById(apprDealKey)
					.orElseThrow(() -> new BizException(ErrorCodeEnum.ERROR_APPRV_NOTFOUND));

			// 1.3.존재여부 체크
			if (apprInfoVO == null) {
				throw new BizException(ErrorCodeEnum.ERROR_APPRV_NOTFOUND);
			} else {
				cardAmount = apprInfoVO.getCardAmount();
				vatAmount = apprInfoVO.getCardVat();
			}
			// 1.4 누적 취소 금액/vat
			CardDealTotalCancelAmtDTO totalcalcelAmtVat = cardRpy.findTotalCancelAmtVat(apprDealKey, "CANCEL");
			if (totalcalcelAmtVat != null) {
				cardTotalCancelAmount = totalcalcelAmtVat.getCancelTotalAmt();
				cardTotalCancelVat = totalcalcelAmtVat.getCancelTotalVat();
			}
			// 1.5 취소 가능한 금액보다 취소금액 체크
			Long ableCancelAmount = cardAmount - cardTotalCancelAmount;
			if (ableCancelAmount.compareTo(cardCancelAmount) < 0) {
				throw new BizException(ErrorCodeEnum.ERROR_CANCEL_VAT);
			}

			// 2 부분 취소인 경우만 처리
			if (cardCancelAmount.compareTo(cardAmount) != 0) {
				// 2.1 취소 할수 있는 vat 값하고 취소할 vat 하고 체크
				Long ableCancelVat = vatAmount - cardTotalCancelVat;
				// 2.1.1 부분 취소 처리
				if (cardCancelVat == null) {
					// 최종 취소할 VAT 값 셋팅
					cardCancelVat = UtilsLib.CalcVat(cardCancelAmount);
					cardCancelVat = cardCancelVat.compareTo(ableCancelVat) > 0 ? ableCancelVat : cardCancelVat;
				}
				Long totalCardCancelAmt = cardTotalCancelAmount + cardCancelAmount;
				// 2.1.2 취소 금액이 마지막처리건인 경우 			
				if (totalCardCancelAmt.compareTo(cardAmount) == 0) { // 동일하면
					if (ableCancelVat.compareTo(cardCancelVat) != 0) {
						throw new BizException(ErrorCodeEnum.ERROR_UNABLE_VAT,
								"마지막 취소처리할 VAT금액 상이하여 취소 할수 없습니다. (취소할 VAT : " + cardCancelVat + "원, 가능한 VAT: "
										+ ableCancelVat + " 원)");
					}
				}
				// 2.1.3 취소처리할 VAT금액이 부족 체크
				if (cardCancelVat.compareTo(ableCancelVat) > 0) {
					throw new BizException(ErrorCodeEnum.ERROR_NOTENONGH_VAT,
							"취소처리할 VAT금액이 부족하여 취소 할수 없습니다. (취소할 VAT : " + cardCancelVat + "원, 가능한 VAT: " + ableCancelVat
									+ " 원)");
				}
				
				totalCardCancelVat = vatAmount + cardCancelVat;
			} else {
				// 2.2 전체금액 취소인데 vat가 null로 넘어오 경우 .
				cardCancelVat = cardCancelVat == null ? vatAmount : cardCancelVat;
			}

			// 취소할 vo
			CardPayDealInfoEntity cancelInfoVO = new CardPayDealInfoEntity();
			cancelInfoVO.setBizType(BizTypeEnum.CANCEL);
			cancelInfoVO.setCardAmount(cardCancelAmount);
			cancelInfoVO.setCardCvc(apprInfoVO.getCardCvc());
			cancelInfoVO.setCardExpiredDate(apprInfoVO.getCardExpiredDate());
			cancelInfoVO.setCardNumber(apprInfoVO.getCardNumber());
			cancelInfoVO.setCardPeriod(apprInfoVO.getCardPeriod());
			cancelInfoVO.setCardVat(cardCancelVat);
			cancelInfoVO.setDealKey(cencelDealKey);
			cancelInfoVO.setPdealKey(apprDealKey);

			CardPayDealInfoEntity rData = cardRpy.save(cancelInfoVO);
			if (rData != null) {
				outVO.setDealKey(rData.getDealKey());
				outVO.setCardAmount(inVO.getCardAmount());
				outVO.setCardVat(inVO.getCardVat());
				outVO.setRemaindAmt(cardCancelAmount);
				outVO.setRemaindVat(totalCardCancelVat);
				outVO.setResult("정상");
				outVO.setDesc(cardCancelAmount + " 원(vat: " + cardCancelVat + "원) 결제 취소 성공");

				// 전문 조립후 외부통신내역 테이블 적재
				try {
					AES256Util en = new AES256Util();
					cancelInfoVO.setCardNumber(en.aesDecode(apprInfoVO.getCardNumber()));
					cancelInfoVO.setCardExpiredDate(en.aesDecode(apprInfoVO.getCardExpiredDate()));
					cancelInfoVO.setCardCvc(en.aesDecode(apprInfoVO.getCardCvc()));
				} catch (Exception e) {
					throw new BizException(ErrorCodeEnum.ERROR_ENCRYPT_FAIL);
				}
				sendDataService.sendData(cancelInfoVO);
			} else {
				throw new BizException(ErrorCodeEnum.ERROR_CACEL_SAVE);
			}

		} // synchronized end

		return outVO;
	}

	// 3. dealKey 로 거래내역 단건 조회
	@Override
	public SearchCardInfoVO searchByDealKey(String DealKey) {
		SearchCardInfoVO outVO = new SearchCardInfoVO();
		try {
			AES256Util en = new AES256Util();

			CardPayDealInfoEntity returnVo = cardRpy.findById(DealKey)
					.orElseThrow(() -> new BizException(ErrorCodeEnum.ERROR_NOT_FOUND));

			if (returnVo != null) {
				outVO.setBizType(returnVo.getBizType().toString());
				outVO.setCardAmount(returnVo.getCardAmount());
				outVO.setCardVat(returnVo.getCardVat());
				outVO.setCardNumber(en.aesDecode(returnVo.getCardNumber()));
				outVO.setCardExpiredDate(en.aesDecode(returnVo.getCardExpiredDate()));
				outVO.setCardCvc(en.aesDecode(returnVo.getCardCvc()));
				outVO.setCardPeriod(returnVo.getCardPeriod());
				outVO.setDealKey(returnVo.getDealKey());
				outVO.setPdealKey(returnVo.getPdealKey());
				outVO.setCreatedTimeAt(returnVo.getCreatedTimeAt());
			}

		} catch (Exception e) {
			throw new BizException(ErrorCodeEnum.ERROR_NOT_FOUND);
		}

		return outVO;
	}

}
