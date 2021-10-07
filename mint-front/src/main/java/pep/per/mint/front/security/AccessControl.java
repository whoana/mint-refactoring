package pep.per.mint.front.security;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pep.per.mint.common.data.basic.User;
import pep.per.mint.common.util.LogUtil;
import pep.per.mint.common.util.Util;
import pep.per.mint.front.env.FrontEnvironments;
import pep.per.mint.front.exception.ControllerException;

/**
 * AcceccControl 임시버전
 * @author iip
 *
 */
public class AccessControl {

	final static String SRC_EXPR = "^*\\{[a-zA-Z0-9]*\\}*"; // {abcd} 패턴 문자열 찾음
	final static String TAG_EXPR = "\\^*[a-zA-Z0-9]*"; // URI 매칭여부 체크용도

	static Logger logger = LoggerFactory.getLogger(AccessControl.class);

	/**
	 * GAuthority Check( UI, API, Command )
	 * @param request
	 * @param session
	 * @param locale
	 * @param messageSource
	 * @throws ControllerException
	 */
//	public static void authCheck(HttpServletRequest request, HttpSession session, Locale locale, ReloadableResourceBundleMessageSource messageSource ) throws ControllerException {
//
//		//(1)UI
//
//		//(2)API
//		apiAuth(request, session, locale, messageSource);
//
//		//(3)Command
//
//	}


	/**
	 * API(URL) GAuthority Check
	 * @param request
	 * @param session
	 * @param locale
	 * @param messageSource
	 * @throws ControllerException
	 */
//	public static void apiAuth(HttpServletRequest request, HttpSession session, Locale locale, ReloadableResourceBundleMessageSource messageSource) throws ControllerException {
//		//----------------------------------------------------------------------------
//		//[임시버전-20210330]
//		//- 형사통합 보안 취약점 대응 수준에서 보완, 향후 Access Control 표준화 정리되면 해당 내용으로 대체
//		//----------------------------------------------------------------------------
//		boolean isAuth = true;
//
//		User user = (User) session.getAttribute("user");
//		//----------------------------------------------------------------------------
//		// AOP 앞단에서 session Check 수행하므로, 유효 session 으로 가정하고 체크
//		//----------------------------------------------------------------------------
//		if( user != null &&  !user.getRole().getIsInterfaceAdmin().equals("Y") ) {
//
//			String uri = request.getRequestURI();
//			String method = request.getParameter("method");
//			String requestURL = Util.join(uri, "?", "method=", method);
//			String contextPath = request.getContextPath();
//
//			String[] urls = getAdminUrl();
//			for(String url : urls) {
//
//				url = Util.join(contextPath, url.replaceAll(SRC_EXPR, TAG_EXPR).replaceAll("\\?", "\\\\?") );
//				boolean isMatches = Pattern.matches(url, requestURL);
//
//				if(isMatches) {
//
//					StringBuffer sb = new StringBuffer();
//					LogUtil.bar2(sb, LogUtil.prefix("<< API GAuthority Check :: Not Authorized >>"));
//					LogUtil.prefix(sb, "requestURL " + requestURL);
//					LogUtil.prefix(sb, "matchURL " + url);
//					logger.debug(sb.toString());
//
//					isAuth = false;
//					break;
//				}
//			}
//		}
//
//		if( !isAuth ) {
//			String[] errorMsgParams = {"API GAuthority","Not Authorized"};
//
//			String errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.system.unexpected", locale);
//			String errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.system.unexpected", errorMsgParams, locale);
//			throw new ControllerException(errorCd, errorMsg);
//		}
//	}

	/**
	 * 로그인 사용자별 App 접근권한 체크 수행
	 * [임시버전-20200520]
	 * - NH농협 보안취약점 대응수준으로 작성된 부분으로, 향후 AccessControl 표준 정리후 보완 필요
	 * - TODO : AccessControl 내용 보완
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	public static boolean uiAuth(HttpServletRequest request, HttpServletResponse response, Object handler) {

		boolean isAuth = true;

		if( FrontEnvironments.acPass.equals("N") ) {

			String requestURI = request.getRequestURI();
			//-------------------------------------------------------------------------------------------
			// Login 시점에, RestirctAccess Application List Setting
			//-------------------------------------------------------------------------------------------
			HttpSession httpSession = request.getSession();
			Object acObj = httpSession.getAttribute("restrictAccessAppList");
			Map<String,String> restrictAccessAppList = null;
			if( acObj != null ) {
				restrictAccessAppList = (Map) acObj;
			}

			if( restrictAccessAppList != null ) {
				//-------------------------------------------------------------------------------------------
				// IIP 페이지 기준으로 체크, Access 금지 페이지 접근시도시 차단
				// - 403 이나 Exception throw 하지 않고, 404로 처리
				// - TODO : 하드코딩 부분 보완
				//-------------------------------------------------------------------------------------------
				if( requestURI.startsWith( request.getContextPath() +   "/view/sub-") ) {
					if(requestURI.endsWith(".html") || requestURI.endsWith(".jsp")) {

						requestURI = Util.replaceString(requestURI, ".html", "");
						requestURI = Util.replaceString(requestURI, ".jsp", "");

						if( restrictAccessAppList.containsKey(requestURI) ) {

							//----------------------------------------------------------------------------
							// Error Logging
							//----------------------------------------------------------------------------
							{
								StringBuffer sb = new StringBuffer();
								LogUtil.bar2(sb, LogUtil.prefix("[ UI(APP) GAuthority Check :: Not Authorized ]"));
								LogUtil.prefix(sb, "requestURL " + requestURI);
								logger.error(sb.toString());
							}

							isAuth = false;
						}
					}
				}
			} else {
				//-------------------------------------------------------------------------------------------
				// Null 이면 Login 수행되지 않은 시점으로, 세션 체크부분에서 오류 핸들링
				//-------------------------------------------------------------------------------------------
			}

		}

		return isAuth;

	}


	/**
	 * API(URL) GAuthority Check
	 * [임시버전-20210409]
	 * - 형사통합 보안 취약점 대응 수준에서 보완, 향후 Access Control 표준화 정리되면 해당 내용으로 대체
	 * @param request
	 * @param session
	 * @param locale
	 * @param messageSource
	 * @throws ControllerException
	 */
	public static boolean apiAuth(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		boolean isAuth = true;

		//----------------------------------------------------------------------------
		// 혹시 기존 서비스에 문제가 생기면 security.ac.pass = 'Y' 로 생성(설정) 한다 :: 긴급 대응용
		//----------------------------------------------------------------------------
		if( FrontEnvironments.acPass.equals("N") ) {

			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			//----------------------------------------------------------------------------
			// 유효 session 으로 가정하고 체크
			//----------------------------------------------------------------------------
			if( user != null &&  !user.getRole().getIsInterfaceAdmin().equals("Y") ) {

				String uri = request.getRequestURI();
				String method = request.getParameter("method");
				String requestURL = Util.join(uri, "?", "method=", method);
				String contextPath = request.getContextPath();

				String[] urls = getAdminUrl();
				for(String url : urls) {

					url = Util.join(contextPath, url.replaceAll(SRC_EXPR, TAG_EXPR).replaceAll("\\?", "\\\\?") );
					boolean isMatches = Pattern.matches(url, requestURL);

					if(isMatches) {
						//----------------------------------------------------------------------------
						// Error Logging
						//----------------------------------------------------------------------------
						{
							StringBuffer sb = new StringBuffer();
							LogUtil.bar2(sb, LogUtil.prefix("[ API GAuthority Check :: Not Authorized ]"));
							LogUtil.prefix(sb, "requestURL " + requestURL);
							LogUtil.prefix(sb, "matchURL " + url);
							logger.error(sb.toString());
						}

						isAuth = false;
						break;
					}
				}
			}

		}

		return isAuth;
	}


	/**
	 * Admin URL
	 * [임시버전-20210409]
	 * - IIP개발자페이지(CRUD), 그외 Admin 페이지(CUD) 적용
	 * @return
	 */
	private static String[] getAdminUrl() {
		String[] adminUrl = {
			"/im/agents/solutions?method=POST",	//EAI Agent  등록
			"/im/agents/solutions?method=PUT",	//EAI Agent  수정
			"/im/agents/solutions/{agentId}?method=DELETE",	//EAI Agent  삭제
			"/im/agents/solutions/{agentId}/brokers?method=POST",	//EAI Agent-Runner  등록
			"/im/agents/solutions/{agentId}/brokers?method=PUT",	//EAI Agent-Runner  수정
			"/im/agents/solutions/{agentId}/brokers?method=DELETE",	//EAI Agent-Runner  수정
			"/im/agents?method=POST",	//IIPAgent  등록
			"/im/agents/{agentId}/monitors?method=POST",	//IIPAgent-모니터항목  등록
			"/im/agents/{agentId}?method=DELETE",	//IIPAgent  삭제
			"/im/agents/{agentId}/monitors?method=DELETE",	//IIPAgent-모니터항목  삭제
			"/im/agents?method=PUT",	//IIPAgent  수정
			"/op/agents/services/cmds?method=POST",	//에이전트배포명령일괄발행
			"/im/agents/{agentId}/monitors?method=PUT",	//IIPAgent-모니터항목  수정


			"/im/organizations?method=POST",	//기관(그룹) 등록
			"/im/organizations/path?method=POST",	//기관그룹 <-> 기관 맵핑 등록
			"/im/organizations/{organizationId}?method=DELETE",	//기관(그룹) 삭제
			"/im/organizations?method=PUT",	//기관(그룹) 수정
			"/im/systems?method=POST",	//시스템(그룹) 등록
			"/im/systems/path?method=POST",	//시스템그룹 <-> 시스템 맵핑 등록
			"/im/systems?method=PUT",	//시스템(그룹) 수정
			"/im/systems/{systemId}?method=DELETE",	//시스템(그룹) 삭제
			"/im/servers?method=POST",	//서버 등록
			"/im/servers?method=PUT",	//서버 수정
			"/im/servers/{serverId}?method=DELETE",	//서버 삭제
			"/im/business?method=POST",	//업무(그룹) 등록
			"/im/business/path?method=POST",	//업무그룹 <-> 업무 맵핑 등록
			"/im/business?method=PUT",	//업무(그룹) 수정
			"/im/business/{businessId}?method=DELETE",	//업무(그룹) 삭제
			"/im/users?method=POST",	//사용자 등록
			"/im/users?method=PUT",	//사용자 수정
			"/im/users/{userId}/?method=DELETE",	//사용자 삭제
			"/im/users?method=PUT",	//사용자 수정
			"/an/interface-users/{userId}/interfaces/move?method=POST",	//인터페이스 담당자 업무이관


			"/su/data-access/roles?method=POST",	//데이타접근권한 등록
			"/su/data-access/roles/path?method=POST",	//데이타접근권한그룹 <-> 데이타접근권한 맵핑 등록
			"/su/data-access/roles?method=PUT",	//데이타접근권한(그룹) 수정
			"/su/data-access/roles/{roleId}?method=DELETE",	//데이타접근권한 삭제
			"/su/roles?method=POST",	//기능권한 등록
			"/su/roles/{roleId}?method=PUT",	//기능권한 수정
			"/su/roles/{roleId}?method=DELETE",	//기능권한 삭제
			"/su/roles/{roleId}/menus?method=PUT",	//기능권한 - 매뉴 수정
			"/su/roles/{roleId}/apps?method=PUT",	//사용자롤별 프로그램권한 수정


			"/co/cds/common-codes?method=POST",	//공통코드 등록
			"/co/cds/common-codes?method=PUT",	//공통코드 변경
			"/co/cds/common-codes?method=DELETE",	//공통코드 삭제


			"/en/frontlogs?method=GET",	//프론트로그조회
			"/su/menus?method=GET",	//매뉴  조회
			"/su/menus?method=POST",	//매뉴  등록
			"/su/menus?method=PUT",	//매뉴  수정
			"/su/menus/{menuId}?method=DELETE",	//매뉴  삭제
			"/su/menus/notuse-treemodel?method=GET",	//매뉴 미적용 메뉴
			"/su/menus/treemodel?method=GET",	//매뉴 트리모델
			"/su/menus/{menuId}?method=GET",	//매뉴 상세 조회
			"/su/menus/path?method=POST",	//매뉴그룹 <-> 매뉴 맵핑 등록
			"/su/menus/{menuId}/treemodel?method=DELETE",	//매뉴그룹 <-> 매뉴 맵핑 삭제
			"/su/menus/apps?method=PUT",	//매뉴-프로그램  수정
			"/su/applications?method=GET",	//프로그램  조회
			"/su/applications?method=POST",	//프로그램  등록
			"/su/applications?method=PUT",	//프로그램  수정
			"/su/applications/{appId}?method=DELETE",	//프로그램  삭제
			"/su/labels?method=GET",	//화면라벨 조회
			"/su/labels?method=GET",	//화면라벨 조회
			"/su/labels/{langId}/{labelId}?method=GET",	//화면라벨 상세조회
			"/su/labels?method=POST",	//화면라벨 등록
			"/su/labels?method=PUT",	//화면라벨 수정
			"/su/environments?method=GET",	//포털환경설정  조회
			"/su/environments?method=POST",	//포털환경설정  등록
			"/su/environments?method=PUT",	//포털환경설정  수정
			"/su/environments?method=DELETE",	//포털환경설정  삭제
			"/co/cds/apps?method=GET",	//App 리스트 검색
			"/su/help?method=GET",	//도움말 리스트 조회
			"/su/help/{appId}/{helpId}/{langId}?method=GET",	//도움말조회
			"/su/help?method=POST",	//도움말저장
			"/su/help?method=PUT",	//도움말수정
			"/su/help/{appId}/{helpId}/{langId}?method=DELETE",	//도움말삭제
			"/su/channel/attributes?method=GET",	//채널속성 조회
			"/su/channel/attributes?method=POST",	//채널속성 등록
			"/su/channel/attributes?method=PUT",	//채널속성 수정
			"/su/channel/attributes/{channelId}/{attrId}?method=DELETE",	//채널속성 삭제
			"/su/interface/attributes?method=GET",	//인터페이스속성 조회
			"/su/interface/attributes?method=POST",	//인터페이스속성 등록
			"/su/interface/attributes?method=PUT",	//인터페이스속성 수정
			"/su/interface/attributes/{attrId}?method=DELETE",	//인터페이스속성 삭제
			"/rt/models/apps/appType?method=POST",	//APP모델유형 등록
			"/rt/models/apps/appType?method=PUT",	//APP모델유형 수정
			"/rt/models/apps/appType?method=DELETE",	//APP모델유형 삭제
			"/su/channels?method=GET",	//연계채널  조회
			"/su/channels?method=POST",	//연계채널  등록
			"/su/channels?method=PUT",	//연계채널  수정
			"/su/channels/{channelId}?method=DELETE",	//연계채널  삭제
			"/su/restservices?method=GET",	//서비스 조회
			"/su/restservices?method=GET",	//서비스 조회
			"/su/restservices/{serviceId}?method=GET",	//서비스 상세조회
			"/su/restservices?method=POST",	//서비스 등록
			"/su/restservices?method=PUT",	//서비스 수정
			"/su/restservices/{serviceId}?method=DELETE",	//서비스 삭제
		};
		return adminUrl;
	}

}
