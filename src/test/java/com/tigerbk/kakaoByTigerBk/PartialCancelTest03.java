package com.tigerbk.kakaoByTigerBk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.repository.CardPayDealInfoRepository;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelOutVO;
import com.tigerbk.kakaoByTigerBk.common.vo.ErrorVO;
import com.tigerbk.kakaoByTigerBk.common.vo.ResultVO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PartialCancelTest03 {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	CardPayDealInfoRepository db;

	@Autowired
	ObjectMapper mapper = new ObjectMapper();

	final private String bizTypePayMent = "PAYMENT";
	final private String bizTypeCancel = "CANCEL";
	final private String cardNumber = "123456789012";
	final private String cardExpiredDate = "0401";
	final private String cardCvc = "999";
	final private String cardPeriod = "00";
	final private String apprUrl = "/apprv";
	final private String cancelUrl = "/cancel";
	// 카드승인 키
	private static String dealKey = "";

	/**
	 * 결제 11,000(1,000)원 결제 성공
	 * 
	 * @throws Exception
	 */
	@Order(1)
	@Test
	@DisplayName("Case03 1번째 => 20,000원 (null원) 결제 성공 테스트.")
	public void testCase01() throws Exception {
		// input 값
		Long cardAmount = 20000L;
		Long cardVat = null;

		ApprOutVO apprOutVO = new ApprOutVO();
		
		// 호출
		ApprInVO apprInVO = ApprInVO.builder().bizType(bizTypePayMent).cardNumber(cardNumber)
				.cardExpiredDate(cardExpiredDate).cardCvc(cardCvc).cardPeriod(cardPeriod).cardAmount(cardAmount)
				.cardVat(cardVat).build();
		ResponseEntity<ResultVO> outVO01 = restTemplate.postForEntity(apprUrl, apprInVO, ResultVO.class);
		String state = outVO01.getBody().getState();
		if ("0000".equals(state)) {
			apprOutVO = mapper.convertValue(outVO01.getBody().getData(), ApprOutVO.class);
			dealKey = apprOutVO.getDealKey();
		}
		// then [결제 성공 state : 0000 , key 생성 ]
		assertThat(outVO01.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(state).isEqualTo("0000");
		assertThat(dealKey).isNotNull();
	}

	@Order(2)
	@Test
	@DisplayName("Case03 2번째 => 10,000원 (1000원) 취소 성공 테스트.")
	public void testCase02() throws Exception {
		// input 값
		Long cardAmount = 10000L;
		Long cardVat = 1000L;

		CancelOutVO cancelOutVO = new CancelOutVO();
		CancelInVO cancelInVO = new CancelInVO();
		
		// 호출
		cancelInVO = CancelInVO.builder().dealKey(dealKey).bizType(bizTypeCancel).cardVat(cardVat)
				.cardAmount(cardAmount).build();		
		ResponseEntity<ResultVO> outVO02 = restTemplate.postForEntity(cancelUrl, cancelInVO, ResultVO.class);
		String state = outVO02.getBody().getState();
		if ("0000".equals(outVO02.getBody().getState())) {
			cancelOutVO = mapper.convertValue(outVO02.getBody().getData(), CancelOutVO.class);
		}
		// then [결제 취소 성공 state : 0000 , key 생성 ]
		assertThat(outVO02.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(state).isEqualTo("0000");
		assertThat(cancelOutVO.getDealKey()).isNotNull();
	}

	@Order(3)
	@Test
	@DisplayName("Case02 3번째 => 10,000원 (909원) 취소 실패 테스트.")
	public void testCase03() throws Exception {
		// input 값
		Long cardAmount = 10000L;
		Long cardVat = 909L;
		
		CancelOutVO cancelOutVO = new CancelOutVO();
		CancelInVO cancelInVO = new CancelInVO();
		ErrorVO errorVO = new ErrorVO();
		// 호출
		cancelInVO = CancelInVO.builder().dealKey(dealKey).bizType(bizTypeCancel).cardVat(cardVat)
				.cardAmount(cardAmount).build();		
		ResponseEntity<ResultVO> outVO03 = restTemplate.postForEntity(cancelUrl, cancelInVO, ResultVO.class);
		String state = outVO03.getBody().getState();
		if ("0000".equals(outVO03.getBody().getState())) {
			cancelOutVO = mapper.convertValue(outVO03.getBody().getData(), CancelOutVO.class);
		} else {
			errorVO = mapper.convertValue(outVO03.getBody().getData(), ErrorVO.class);
			System.out.println("testCase03 : " + errorVO);
		}
	
		// then [결제취소 실패 state : 9999 , key : null ]
		assertThat(outVO03.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(state).isEqualTo("9999");
		assertThat(errorVO.getStatus()).isEqualTo("9006");		 // 오류 상세코드 : 마지막 취소처리할 VAT금액 상이하여 취소 할수 없습니다.
		assertThat(cancelOutVO.getDealKey()).isNull();

	}

	@Order(4)
	@Test
	@DisplayName("Case02 4번째 => 10,000원 (null원) 취소 성공  테스트.")
	public void testCase04() throws Exception {
		// input 값
		Long cardAmount = 10000L;
		Long cardVat = null;

		CancelOutVO cancelOutVO = new CancelOutVO();
		CancelInVO cancelInVO = new CancelInVO();
		
		// 호출
		cancelInVO = CancelInVO.builder().dealKey(dealKey).bizType(bizTypeCancel).cardVat(cardVat)
				.cardAmount(cardAmount).build();		
		ResponseEntity<ResultVO> outVO04 = restTemplate.postForEntity(cancelUrl, cancelInVO, ResultVO.class);
		String state = outVO04.getBody().getState();
		if ("0000".equals(outVO04.getBody().getState())) {
			cancelOutVO = mapper.convertValue(outVO04.getBody().getData(), CancelOutVO.class);
		}
		// then [결제취소 성공 state : 0000 , key : 생성 ]
		assertThat(outVO04.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(state).isEqualTo("0000");
		assertThat(cancelOutVO.getDealKey()).isNotNull();
	}

}
