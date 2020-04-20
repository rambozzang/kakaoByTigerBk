package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo;

/*
 *  총 카드취소 금액 구하기 sum(Amount) , sum(VAT)
 * */

public interface  CardDealTotalCancelAmtDTO {

	 Long getCancelTotalAmt();
	 Long getCancelTotalVat();
}
