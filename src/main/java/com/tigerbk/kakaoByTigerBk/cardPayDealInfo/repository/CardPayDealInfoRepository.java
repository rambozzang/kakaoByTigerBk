package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CardDealTotalCancelAmtDTO;
import com.tigerbk.kakaoByTigerBk.models.CardPayDealInfoEntity;

public interface CardPayDealInfoRepository extends JpaRepository<CardPayDealInfoEntity, String> {

	
	@Query(value = "SELECT Sum(tb.card_amount) as cancelTotalAmt , Sum(tb.card_vat) as cancelTotalVat "
			+ " FROM card_pay_deal_info_entity tb where  tb.pdeal_key = :pdealKey and tb.biz_type=:bizType"
			+ " group by tb.pdeal_key", nativeQuery = true)
	CardDealTotalCancelAmtDTO findTotalCancelAmtVat(
			@Param("pdealKey") String pdealKey,
			@Param("bizType") String bizType);

}
