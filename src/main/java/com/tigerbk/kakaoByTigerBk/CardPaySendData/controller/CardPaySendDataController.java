package com.tigerbk.kakaoByTigerBk.CardPaySendData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tigerbk.kakaoByTigerBk.CardPaySendData.service.CardPaySendDataService;
import com.tigerbk.kakaoByTigerBk.CardPaySendData.vo.CardPaySendDataVO;

@RestController
public class CardPaySendDataController {

	@Autowired
	CardPaySendDataService svc; 
	
	@GetMapping("/senddata/{dealKey}")
	private CardPaySendDataVO searchSendData(@PathVariable("dealKey") String dealKey ) throws Exception {		
		CardPaySendDataVO outVo = new CardPaySendDataVO();				
		outVo = svc.searchSendData(dealKey);		
		return outVo;
	}
}
