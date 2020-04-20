package com.tigerbk.kakaoByTigerBk.cardPayDealInfo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.service.CardPayDealInfoService;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.SearchCardInfoVO;
import com.tigerbk.kakaoByTigerBk.common.BizTypeEnum;
import com.tigerbk.kakaoByTigerBk.common.ErrorCodeEnum;
import com.tigerbk.kakaoByTigerBk.common.BizException;
import com.tigerbk.kakaoByTigerBk.common.vo.ErrorVO;
import com.tigerbk.kakaoByTigerBk.common.vo.ResultVO;

@RestController

public class CardPaycontroller {

	@Autowired
	CardPayDealInfoService apprSvc;

	final private String succ = "0000";
	final private String err = "9999";

	/*
	 * 카드 승인 요청 URI IN : CardPayApprovedVO OUT : 처리결과 메세지 , 승인키
	 */
	@PostMapping("/apprv")
	@ExceptionHandler 
	public ResultVO CardApprovedController(@RequestBody @Valid ApprInVO inVO) {
		// 1. 초기 변수 셋팅
		ApprOutVO outVO = new ApprOutVO();
		ResultVO resultVO = new ResultVO();

		System.out.println("입력받은 데이타 : " + inVO.toString());
		// 2. 카드 승인 요청
		try {
			// 2.1 카드 승인 저장
			if (!BizTypeEnum.PAYMENT.toString().equals(inVO.getBizType())) {
				throw new BizException(ErrorCodeEnum.ERROR_INVALID_CODE, inVO.getBizType());
			}
			outVO = apprSvc.procCardApprove(inVO);
			
		} catch (BizException e) {				
			ErrorVO errorVO = ErrorVO.builder()
					.status(e.getErrorCodeEnum().getCode())
					.msg(e.getErrorCodeEnum().getMessage())
					.build();
			resultVO.setState(err);
			resultVO.setData(errorVO);
			return resultVO;		
		}
		
		// 3. 최종 결과 전송
		resultVO.setState(succ);
		resultVO.setData(outVO);
		return resultVO;
	}

	@PostMapping("/cancel")
	@ExceptionHandler
	public  ResultVO procCardCancel(@RequestBody @Valid CancelInVO inVO) {
		// 1. 초기 변수 셋팅
		System.out.println("입력받은 데이타 : " + inVO.toString());
		ResultVO resultVO = new ResultVO();
		CancelOutVO outVO = new CancelOutVO();
		// 2.취소 처리 프로세스
		try {
			if (!BizTypeEnum.CANCEL.toString().equals(inVO.getBizType())) {
				throw new BizException(ErrorCodeEnum.ERROR_INVALID_CODE, inVO.getBizType());
			}
			outVO = apprSvc.procCardCancel(inVO);	
		} catch (BizException  e) {		
			ErrorVO errorVO = ErrorVO.builder()
					.status(e.getErrorCodeEnum().getCode())
					.msg(e.getErrorCodeEnum().getMessage())
					.build();
			resultVO.setState(err);
			resultVO.setData(errorVO);
			return resultVO;
		}
		// 3. 최종 결과 전송
		resultVO.setState(succ);
		resultVO.setData(outVO);
		return resultVO;
	}

	@GetMapping("/search/{dealKey}")
	public SearchCardInfoVO searchCardCancel(@PathVariable("dealKey") String dealKey) {
		SearchCardInfoVO outVO = new SearchCardInfoVO();
		try {
			outVO.setDealKey(dealKey);
			outVO = apprSvc.searchByDealKey(dealKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outVO;
	}

}
