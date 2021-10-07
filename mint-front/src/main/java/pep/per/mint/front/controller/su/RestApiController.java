package pep.per.mint.front.controller.su;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.Server;
import pep.per.mint.common.data.basic.Service;
import pep.per.mint.common.data.basic.System;
import pep.per.mint.common.data.basic.TreeModel;
import pep.per.mint.common.data.basic.User;
import pep.per.mint.common.data.basic.UserRole;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.im.InfraService;
import pep.per.mint.database.service.su.RestApiService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.FieldCheckUtil;
import pep.per.mint.front.util.MessageSourceUtil;

@Controller
@RequestMapping("/su")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RestApiController {

	private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	/**
	 * The Infra service.
	 */
	@Autowired
	RestApiService restApiService;

	/**
	 * The Message source.
	 */
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private ServletContext servletContext;



	/**
	 * <pre>
	 * 서비스 목록 조회
	 * API ID : REST-R01-SU-10-02
	 * </pre>
	 * @param httpSession the http session
	 * @param comMessage 요청응답통신객체
	 * @param locale 다국어지원을 위한 로케일
	 * @param request the request
	 * @return ComMessage 통신객체
	 * @throws Exception the exception
	 * @throws Exception the exception
	 * @author Solution TF
	 * @since version 1.0(2016.03)
	 */
	@RequestMapping(
			value="/restservices",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, List<Service>> getRestServiceList(
			HttpSession  httpSession,
			@RequestBody ComMessage<Map, List<Service>> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//여긴 비지니스 코드만 작성해라.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map params = null;
			List list = null;
			//-------------------------------------------------
			//파라메터 체크
			//-------------------------------------------------
			{
				params = (Map) comMessage.getRequestObject();
				if(params == null) params = new HashMap();
				if (params != null){
					String paramString = FieldCheckUtil.paramString(params);
					logger.debug(Util.join("\nparamString:[", paramString, "]"));
				}
			}

			//-----------------------------------------------
			//조회
			//-----------------------------------------------
			list = restApiService.getRestServiceList(params);

						//-----------------------------------------------
			//서비스 처리 종료시간을 얻어 CM에 세팅한다.
			//-----------------------------------------------
			{
				comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
			}

			//-----------------------------------------------
			// 통신메시지에 처리결과 코드/메시지를 등록한다.
			//-----------------------------------------------
			{
				if (list == null || list.size() == 0) {// 결과가 없을 경우 비지니스 예외 처리
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// 성공 처리결과
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);
				}
				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
			}
			return comMessage;
		}
	}

	/**
	 * <pre>
	 * 서버 상세 조회
	 * API ID : REST-R02-IM-01-02
	 * </pre>
	 * @param httpSession the http session
	 * @param systemId the system id
	 * @param comMessage 요청응답통신객체
	 * @param locale 다국어지원을 위한 로케일
	 * @param request the request
	 * @return ComMessage 통신객체
	 * @throws Exception the exception
	 * @throws Exception the exception
     * @author Solution TF
     * @since version 1.0(2015.07)
     */
	@RequestMapping(
			value="/restservices/{serviceId}",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, Service> getRestServiceDetail(
			HttpSession  httpSession,
			@PathVariable("serviceId") String serviceId,
			@RequestBody ComMessage<Map, Service> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//여긴 비지니스 코드만 작성해라.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Service service = null;
			//-----------------------------------------------
			//필드 체크
			//-----------------------------------------------
			{
				FieldCheckUtil.checkRequired("restApiService.getRestServiceDetail", "serviceId", serviceId, messageSource, locale);
			}

			//-----------------------------------------------
			//조회
			//-----------------------------------------------
			service = restApiService.getRestServiceDetail(serviceId);

			//-----------------------------------------------
			//서비스 처리 종료시간을 얻어 CM에 세팅한다.
			//-----------------------------------------------
			{
				comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
			}

			//-----------------------------------------------
			// 통신메시지에 처리결과 코드/메시지를 등록한다.
			//-----------------------------------------------
			{
				if (service == null) {// 결과가 없을 경우 비지니스 예외 처리
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// 성공 처리결과
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(service);
				}
				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
			}

			return comMessage;
		}
	}


	/**
	 * <pre>
	 * 서비스 등록
	 * API ID : REST-C01-SU-10-02
	 * </pre>
	 * @param httpSession the http session
	 * @param comMessage the com message
	 * @param locale the locale
	 * @param request the request
	 * @return com message
	 * @throws Exception the exception
     * @throws Exception the exception
     */
	@RequestMapping(value = "/restservices", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Service, Service> createRestService(
			HttpSession httpSession,
			@RequestBody ComMessage<Service, Service> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// 여긴 비지니스 코드만 작성해라.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";

			Service service = comMessage.getRequestObject();
			//----------------------------------------------------------------------------
			// 필수필드 체크하기
			//----------------------------------------------------------------------------
			{
				//FieldCheckUtil.check("restApiService.createRestService", server, messageSource, locale);
			}

			//----------------------------------------------------------------------------
			// 입력값 JSON 덤프
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("createRestService Service param dump:\n", FieldCheckUtil.jsonDump(service)));
			}

			//----------------------------------------------------------------------------
			//등록ID, 등록시간 설정하기
			//----------------------------------------------------------------------------
			{
				//시스템 등록자/등록일자 체크및 세팅
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				service.setRegId(regId);
				service.setRegDate(regDate);
			}

			try{
				//----------------------------------------------------------------------------
				//등록실행
				//----------------------------------------------------------------------------
				int res = 0;
				{
					res = restApiService.createRestService(service);
				}
				//--------------------------------------------------
				// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// 통신메시지에 처리결과 코드/메시지를 등록한다.
				//--------------------------------------------------
				{
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(service);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.create.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"RestApiController.createRestService",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.create.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}

		}

	}


	/**
	 * <pre>
	 * REST API  변경
	 * API ID : REST-U01-IM-01-05
	 * </pre>
	 * @param httpSession the http session
	 * @param comMessage the com message
	 * @param locale the locale
	 * @param request the request
	 * @return com message
	 * @throws Exception the exception
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/restservices", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Service, Service> updateRestService(
			HttpSession httpSession,
			@RequestBody ComMessage<Service, Service> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// 여긴 비지니스 코드만 작성해라.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";

			Service service = comMessage.getRequestObject();
			//----------------------------------------------------------------------------
			// 필수필드 체크하기
			//----------------------------------------------------------------------------
			{
				//FieldCheckUtil.check("RestApiController.createSystem", system, messageSource, locale);
			}

			//----------------------------------------------------------------------------
			// 입력값 JSON 덤프
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("updateRestService Service param dump:\n", FieldCheckUtil.jsonDump(service)));
			}

			//----------------------------------------------------------------------------
			//수정ID, 수정시간 설정하기
			//----------------------------------------------------------------------------
			{
				//시스템 등록자/등록일자 체크및 세팅
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				service.setModId(regId);
				service.setModDate(regDate);
			}

			try{
				//----------------------------------------------------------------------------
				//등록실행
				//----------------------------------------------------------------------------
				int res = 0;
				{
					res = restApiService.updateRestService(service);
				}
				//--------------------------------------------------
				// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// 통신메시지에 처리결과 코드/메시지를 등록한다.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(service);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.create.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"RestApiController.updateRestService",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.create.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}

		}

	}



	/**
	 * <pre>
	 * REST 서비스  삭제
	 * API ID : REST-D01-IM-01-05
	 * </pre>
	 * @param httpSession the http session
	 * @param comMessage the com message
	 * @param locale the locale
	 * @param request the request
	 * @return com message
	 * @throws Exception the exception
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/restservices/{serviceId}", params = "method=DELETE", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<?, ?> deleteRestService(
			HttpSession httpSession,
			@PathVariable("serviceId") String serviceId,
			@RequestBody ComMessage<?, ?> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// 여긴 비지니스 코드만 작성해라.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";

			try{
				//----------------------------------------------------------------------------
				//삭제실행
				//----------------------------------------------------------------------------
				int res = 0;
				{
					//시스템 등록자/등록일자 체크및 세팅
					String modId = comMessage.getUserId();
					String modDate = Util.getFormatedDate();
					res = restApiService.deleteRestService(serviceId,modId, modDate);
				}
				//--------------------------------------------------
				// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// 통신메시지에 처리결과 코드/메시지를 등록한다.
				//--------------------------------------------------
				{
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setResponseObject(null);
					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.create.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"RestApiController.deleteRestService",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.delete.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}
}
