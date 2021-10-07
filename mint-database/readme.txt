===================================================
- mint-database 
--------------------------------------------------------------------------------------------
1.프로젝트 모듈 개요
	1) 프로젝트명   : mint
	2) 모  듈  명  : mint-database
	3) 용     도  : 데이터베이스 엔터티 정보에 대한 CRUD기능 제공을 위한 모듈
	
2.모듈 개발 환경 구성을 위해 미리 준비해 두어야 할 것들
	1) 자바설치 : 1.6 이상 설치
	2) maven 3.X 설치 
	3) 개발툴 설치 : 
		스프링 프레임워크3.2 및 빌드툴 maven 플러그인을 지원하는 eclipse
		예) STS 3.X(Spring Tool Suite)

3.모듈 구성
	[개발Directory]
		/mint/mint-database					--> 모듈 최상위 위치
		/mint-database/src/main/java		--> 자바소스 위치
		/mint-database/src/test/java		--> Unit 테스트 자바소스 위치
		/mint-database/src/main/resources	--> 자바외의 리소스 위치 
		/mint-database/src/test/resources	--> 자바외의 개발 리소스 위치
		/mint-database/database				--> 데이터베이스 작업용 sql 파일
		/mint-database/src					--> 소스 최상위 위치
		/mint-database/target				--> 모듈 jar 파일 빌드 위치
		/mint-database/target-test			--> 테스트 모듈 jar 파일 빌드 위치 
		/mint-database/pom.xml				--> maven 빌드 설정파일
		/mint-database/reademe.txt			--> 프로젝트 모듈 설명서

4.진행 사항
	1) 2015.06.30 
		IIP(Interface Integration Portal) TF 개발에 mint 소스 재사용 결정(2015.06.30)
	   	에 따라 기존 개발소스의 리팩토링은 추후 진행하기로하고 신규 소스는 하위 패키지를 두어 좀더 세분화하여 진행하기로 함.
	    이에 따라 pep.per.mint.database.mybatis.handler 패키지는 재사용 되며,  
	    패키지 pep.per.mint.database.mybatis.persistance 및 pep.per.mint.database.service 는 하위에 
	    좀더 세분화된 패키지를 두어 신규 소스와 기존 소스 분리 진행후 룰과 관련된 기존 소스는 향후 milestone이 진행되는
	    상황에 따라 리팩토링 예상해봄.
	    
	 2) 2015.07.13

5.추가 변경 History
	1) 2020.11.04, 미국 대선일 
		변경 내용
			------------------------------------------------------------------------------------
			[시스템 환경변수(TSU0302) 추가]
			------------------------------------------------------------------------------------
				환경변수 :
					system.endpoint.implementaion
				용도 : 
					엔드포인트 적용 구분을 위한 판단 값으로 사용된다.
				값 : 
					true|false
				적용 CASE1 :
					엔드포인트 적용 구분에 따라 인터페이스 조회 조건의 비교 대상 테이블을 변경하도록 동적 SQL을 반영 
					변경 소스 
						RequirementController.java, RequirementMapper.xml 
					옵션이 true 일 경우 검색 파라메터 service 및 serviceDesc 값을 TRT0202.TAG 값과 비교하고
					옵션이 false 일 경우 TAN0213.SERVICE 및 TAN0213.SERVICE_DESC 값과 비교한다.
				적용 사이트 :
					신한생명(SHL)
			------------------------------------------------------------------------------------
	2) 2021.03.17 신한생명 현장 테스트 SQL 전달.

		     SELECT DISTINCT
		    	 c.BUSINESS_CD	   	as "DOMAIN"
		    	,f.INTEGRATION_ID	as "MODEL"
		    	,h.VAL				as "DESCRIPTION"
		    	,im04.NM            as "DATASOURCE"
		      FROM TRT0101 a
		INNER JOIN TAN0101 b
		        ON a.INTERFACE_ID 	= b.INTERFACE_ID
		       AND a.DEL_YN			= 'N'
		       AND b.DEL_YN 		= 'N'
		INNER JOIN TIM0301 c
		        ON c.BUSINESS_ID 	= b.BUSINESS_ID
		       AND c.BUSINESS_CD 	= 'NCS' -- 값 변경
		INNER JOIN TAN0213 d
				ON d.INTERFACE_ID 	= b.INTERFACE_ID
			   AND d.NODE_TYPE 		= '0'
			   AND d.SEQ  			= (SELECT min(SEQ) FROM TAN0213 WHERE INTERFACE_ID = d.INTERFACE_ID AND NODE_TYPE = '0')
			   AND d.DEL_YN 		= 'N'
		INNER JOIN TIM0101 e
		  		ON e.SYSTEM_ID  	= d.SYSTEM_ID
			   AND e.SYSTEM_CD 		= 'CUI' -- 값 변경
		INNER JOIN TAN0201 f
				ON f.INTERFACE_ID = a.INTERFACE_ID
		INNER JOIN TRT0201 g
		        ON g.INTERFACE_MID  = a.INTERFACE_MID
		       AND g.SYSTEM_TYPE 	= '2'
		       AND g.SEQ 			= (SELECT min(SEQ) FROM TRT0201 WHERE INTERFACE_MID = a.INTERFACE_MID AND SYSTEM_TYPE = '2')
		INNER JOIN TRT0202 h
                ON h.INTERFACE_MID 	= a.INTERFACE_MID
               AND h.APP_MID 		= g.APP_MID
 		INNER JOIN TRT0203 i
        	    ON i.AID      		= h.AID
        	   AND i.APP_TYPE 		= h.APP_TYPE
        	   AND i.CD       		= 'serviceId'
        	   AND i.DEL_YN   		= 'N'
	    INNER JOIN TRT0207 j
                ON j.APP_TYPE 		= i.APP_TYPE
        INNER JOIN TSU0301 im04
        		ON im04.CD 			= j.RSS_CD
               AND im04.LEVEL1 		= 'IM'
		       AND im04.LEVEL2 		= '04'
		       ;

	3) 2021.03.31 앱속성 SQL 값 BASE64 변환 및 컬럼 타입변경( VARCHAR -> CLOB )
	   변경 컬럼  : TRT0202.VAL, TRT0203.DEFAULT_VALUE
	   작업 내용 :

        3.1) 앱속성값 테이블 TESTAREA 유형의 데이터 조회
        SELECT
            tan0201.integration_id   AS integration_id,
            trt0201.system_type      AS system_type,
            trt0203.input_type       AS input_type,
            trt0202.val              AS val
        FROM
            tan0201   tan0201
            INNER JOIN trt0101   trt0101 ON tan0201.interface_id = trt0101.interface_id
            INNER JOIN trt0201   trt0201 ON trt0101.interface_mid = trt0201.interface_mid
            INNER JOIN trt0202   trt0202 ON trt0201.interface_mid = trt0202.interface_mid
            AND trt0201.app_mid = trt0202.app_mid
            INNER JOIN trt0203   trt0203 ON trt0202.aid = trt0203.aid
            AND trt0202.app_type = trt0203.app_type
            AND trt0203.INPUT_TYPE = '6';

        3.2) 앱속성 테이블 기본값 데이터 조회
        SELECT AID, APP_TYPE, DEFAULT_VALUE FROM TRT0203 WHERE INPUT_TYPE = '6';

        3.3) 앱속성값 테이블 컬럼 타입 변경
           SELECT * FROM trt0202;
           -- 새 CLOB 컬럼을 추가합니다.
           ALTER TABLE TRT0202 ADD (TMP_CONTENTS CLOB);
           -- 데이터를 복사합니다.
           UPDATE TRT0202 SET TMP_CONTENTS = VAL ;
           COMMIT;
           -- 기존 컬럼을 삭제합니다.
           ALTER TABLE TRT0202 DROP COLUMN VAL;
           -- 새로 추가한 임시 컬럼의 이름을 변경합니다.
           ALTER TABLE TRT0202 RENAME COLUMN TMP_CONTENTS TO VAL;

           SELECT * FROM trt0203;
           -- 새 CLOB 컬럼을 추가합니다.
           ALTER TABLE TRT0203 ADD (TMP_CONTENTS CLOB);
           -- 데이터를 복사합니다.
           UPDATE TRT0203 SET TMP_CONTENTS = DEFAULT_VALUE ;
           COMMIT;
           -- 기존 컬럼을 삭제합니다.
           ALTER TABLE TRT0203 DROP COLUMN DEFAULT_VALUE;
           -- 새로 추가한 임시 컬럼의 이름을 변경합니다.
           ALTER TABLE TRT0203 RENAME COLUMN TMP_CONTENTS TO DEFAULT_VALUE;



				  	    


		
		
		
		
		