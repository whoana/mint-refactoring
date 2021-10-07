package pep.per.mint.front.filter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import pep.per.mint.common.util.LogUtil;
import pep.per.mint.front.security.AccessControl;

/**
 * HTTP Request 에 대한 Filtering Handle Class
 * HTTP Request -> Filter -> Interceptor -> DispatcherServlet -> Controller
 * TODO : 서비스 호출전 사전 체크사항 도출후 지속적 보완
 * @author IIP-DEV
 *
 */
public class CommonInterceptor implements HandlerInterceptor {

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		boolean flag = true;
		//-------------------------------------------------------------------------------------------
		//Step 1) AccessControl Call
		//-------------------------------------------------------------------------------------------
		flag = accessControl(request, response, handler);
		return flag;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

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
	private boolean accessControl(HttpServletRequest request, HttpServletResponse response, Object handler) {
		boolean flag = true;
		try {

			if( handler instanceof HandlerMethod ) {
				// API 호출시
				flag = AccessControl.apiAuth(request, response, handler);
			} else {
				// Page 호출시
				flag = AccessControl.uiAuth(request, response, handler);
			}

		} catch(Exception e) {
			flag = false;
			logger.error("#CommonInterceptor.accessControl().Exception", e);
		} finally {
			try {
				if(! flag) {
					//----------------------------------------------------------------------------
					// Error Logging
					//----------------------------------------------------------------------------
					{
						StringBuffer sb = new StringBuffer();
						LogUtil.bar2(sb, LogUtil.prefix("[ GAuthority -> Not Authorized ]"));
						LogUtil.prefix(sb, "redirect Page :: " + request.getContextPath() + "/view/main/404.jsp");
						logger.error(sb.toString());
					}

					response.sendRedirect( request.getContextPath() + "/view/main/404.jsp");
				}
			} catch(Exception e) {}
		}

		return flag;
	}

}
