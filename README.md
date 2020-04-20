# KAKAOPAY 사전과제

**개발환경**
  - Spring Tool Suite 4 ver4.6.0.RELEASE
  - JAVA8
  - Spring Boot 2.2.6.RELEAS
  - JPA
  - gradle
  - H2 Database

## 1. Rest API 기반 결제시스템

### 1.1 기능 구현
	 1) 결제 API
	 2) 결제취소 API
	 	- 부분 취소 API 구현
	 3) 데이터 조회 API
	 	- 전문 조회 API
	 	- 승인/취소내역 조회 API 
	 	
### 1.2 제약사항
		3.1 20자리 UNIQUE KEY 구현			
			- String 타입으로 구현
				(H2 DB특성상 Long  타입은 bigint 형으로 변환되며 max   -9223372036854775808 to 9223372036854775807   까지만 사용가능 19자리)
			- UUID 함수 사용 
				UNIQUE KEY = UUID.randomUUID(9자리)의 hascode + UUID.randomUUID(9자리) 의 hascode  값(최대18자리)
				20자리가 안될 경우  Random 함수를 이용 rand.nextint 로 임의수를 생성하여 20자리 조립
			
		3.1 String 데이터 저장 ( string 450 byte)
			- 전문내역 Entity 구현
			- 중요 정보(카드정보)는 암호화후 저장  및 조회시 복호화 처리			 
		3.2 부가가치세		
			- 부가가치 자동 계산 모듈 구현
			- 취소시 부가가치세 제어 기능 
		3.3. 카드정보 암/복호화
			 - 암호화 모듈 : 공개된 AES256 함수사용
			 - 카드번호/유효기간/cvc 정보
		3.4 트랜잭션 데이터 관리	 
			 - multiTreadTest01.java : 1000원 카드 승인 후  600원을 5번씩 카드 취소 한 경우 1건만 정상 처리
			 - multiTreadTest01.java : 1000원 카드 승인 후  300원을 5번씩 카드 취소 한 경우 3건만 정상 처리
		3.5 charset UTF-8
		3.6 에러응답, 에러코드	
		
### 1.3 Multi Thread 환경 대비
		1. 카드 취소시 적용
		  - synchronized 함수 적용
		  - 카드승인키 에 대한 취소 처리 로직에 synchronized block 설정 처리
		    . 전체 프로세스에 적용으로 성능저하 문제
		    . 추후 카드승인키별 처리가능하도록 구현이 필요
		  
### 1.4 API 기능 구현
 ## 1. 카드 결제 승인 요청 	
 		- POST  http://localhost:8080/apprve
 		- input 데이타 
 		- output 데이타
 ## 2. 카드 결제 취소 요청
 		- POST http://localhost:8080/cancel
 		- input 데이타 
 		- output 데이타
## 3. 카드 거래내역 조회  요청
   		- GET http://localhost:8080/search/{key}
 		- input 데이타 
 		- output 데이타
## 4. 카드 전문 거래내역 조회  요청
   		- GET http://localhost:8080/senddata/{sendDatakey}
 		- input 데이타 
 		- output 데이타
   
### entity 구성
	1. CardPayDealInfoEntity
		- 카드 승인/취소 거래내역 테이블
  	2. CardPaySendDataEntity
  		- Ban 사와 통신 전문 내역 테이블
  		- 대외계(EAI) 전문 내용 이력성 데이타  저장
  		- SEND_DATA 필드 확인 450byte
  		  SELECT length(SEND_DATA)   FROM CARD_PAY_SEND_DATA_ENTITY  -> 450 
  		  
### Testcase 
	1. Testcase01  - 사전과제 1번		
	2. Testcase02  - 사전과제 2번
	3. Testcase03  - 사전과제 3번
	4. multiThreadTest01  - 사전과제 멀티쓰레드 테스트
	5. multiThreadTest02  - 사전과제 멀티쓰레드 테스트






