1 jeus version8 websocket 기능 오류 해결을 위한 패치

    * 해당 패치 적용은 tomcat 배포 시에 영향을 주지 않으며 
    * jeus8 서버 배포 시에는 반드시 적용해 주어야 합니다.

    1.1 패치 파일 내역

        1) jeus websocket 지원 라이브러리들 (소스 작성 시에만 참조, 배포 대상은 아님.)
            /mint-websocket/lib
            /mint-websocket/lib/jeus-websocket.jar
            /mint-websocket/lib/jeus-ws.jar
            /mint-websocket/lib/spring-support-4.0.0.RELEASE.jar
            /mint-websocket/lib/spring-support-4.2.0.RELEASE.jar

        2) /mint-websocket/src/main/pep/per/mint/websocket/WebsocketConfig.java
            jeus 웹소켓 프로토콜 업그레이드를 위한 클래스 JeusRequestUpgradeStrategy 적용을 위한 소스 변경

        3) /mint-websocket/pom.xml
            dependency 추가 (jeus system lib)

        4) /mint-front/src/main/webapp/WEB-INF/jeus-web-dd.xml
            jeus deploy description 파일

	1.2 아래 절차에 따라 패치를 진행합니다.

		1) jeus 설치 위치에서 스프링 웹소켓 지원 라이브러리가 존재하는지 확인한다. (spring-support-4.2.0.RELEASE.jar)
		   jeus 설치 위치에 라이브러리가 존재하지 않는 다면 WAS 엔지니어에게 문의해 본다.
			#jeus home 확인
			[root@a26ac933c298 ~]# echo ${JEUS_HOME}
			/root/jeus8

			#제우스 웹소켓 업그레이드 라이브러리 확인
			[root@a26ac933c298 ~]# ls -al ${JEUS_HOME}/lib/shared/spring-support/4.2.0.RELEASE
			total 16
			drwxr-xr-x 2 root root 4096 Feb 28  2019 .
			drwxr-xr-x 4 root root 4096 Feb 28  2019 ..
			-rwxr-xr-x 1 root root 6542 Feb 28  2019 spring-support-4.2.0.RELEASE.jar

		2) /mint 프로젝트 모듈 중 /mint-websocket 모듈을 SVN으로 부터 업데이트 받는다.

		3) /mint-front 모듈 메이븐 빌드를 수행한다.

		4) jeus8 서버에 배포하고 정상 기동 여부를 확인한다.(jeus 웹애플리케이션 배포 절차는 사이트 담당자에게 요청한다.)

	*참고
		본 패치는 Docker 환경 jeus8 설치 후 패치내용을 적용하여 /mint 애플리케이션 정상 기동 여부를 확인하였습니다.
		jeus8 websocket 지원에 관한 보다 상세한 내용 확인은 jeus 엔지니어에게 문의하거나
		아래 상위 문서에서 확인바랍니다.

		/mint/doc/howto/howto-apply-jeus8-websocket.txt
		/mint/doc/howto/jeus8-spring-websocket-support.pdf


