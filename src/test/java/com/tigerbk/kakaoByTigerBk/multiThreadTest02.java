package com.tigerbk.kakaoByTigerBk;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.service.CardPayDealInfoService;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.ApprOutVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelInVO;
import com.tigerbk.kakaoByTigerBk.cardPayDealInfo.vo.CancelOutVO;
import com.tigerbk.kakaoByTigerBk.common.vo.ResultVO;
import com.tigerbk.kakaoByTigerBk.models.CardPayDealInfoEntity;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @Execution(ExecutionMode.CONCURRENT)
public class multiThreadTest02 {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	CardPayDealInfoRepository db;

	@Autowired
	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	CardPayDealInfoService cardPayDealInfoService;

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
	@BeforeAll
	public static void init() {
		System.out.println("init");
	}

	/**
	 * 결제 10,000원 (100원) 원 결제 성공
	 * 
	 * @throws Exception
	 */
	@Order(1)
	@Test
	@DisplayName("Case01 1번째 => 1000원 (100원) 결제 성공 테스트.")
	public void testCase01() throws Exception {
		// input 값
		Long cardAmount = 1000L;
		Long cardVat = 100L;

		ApprOutVO apprOutVO = new ApprOutVO();
		ApprInVO apprInVO = ApprInVO.builder().bizType(bizTypePayMent).cardNumber(cardNumber)
				.cardExpiredDate(cardExpiredDate).cardCvc(cardCvc).cardPeriod(cardPeriod).cardAmount(cardAmount)
				.cardVat(cardVat).build();
		// when
		ResponseEntity<ResultVO> outVO01 = restTemplate.postForEntity("/apprv", apprInVO, ResultVO.class);
		String state = outVO01.getBody().getState();
		if ("0000".equals(state)) {
			apprOutVO = mapper.convertValue(outVO01.getBody().getData(), ApprOutVO.class);
			dealKey = apprOutVO.getDealKey();
		}
		// then [결제 성공 state : 0000 , key : 생성 ]
		assertThat(outVO01.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(state).isEqualTo("0000");
		assertThat(dealKey).isNotNull();
	}
	@Order(2)
	@Test
	@DisplayName("Case01 2번째 => 300원 (10원) 취소 실패 5건 동시 호출시 3건만 성공 테스트.")
	public void testCase02() throws Exception {
		System.out.println("testCase02() 시작 " + LocalTime.now());
		// input 값
		Long cardAmount = 300L;
		Long cardVat = 10L;
		CancelInVO cancelInVO = CancelInVO.builder().dealKey(dealKey).bizType(bizTypeCancel).cardVat(cardVat)
				.cardAmount(cardAmount).build();
		// when
		// 쓰레드 5개 300(10) 원 출금 서비스 동시 호출
		for (int i = 0; i < 5; i++) {
			new Thread(() -> cardPayDealInfoService.procCardCancel(cancelInVO)).start();
		}
		System.out.println("testCase02() 끝 " + LocalTime.now());
		List<CardPayDealInfoEntity> all = db.findAll();
		System.out.println(">>>>>>>>>>>>>> " + all.size());
		// db 승인 1건 , 취소 3건 =  총 4건이면 성공
		assertThat(4).isEqualTo(all.size());
	}	
}
