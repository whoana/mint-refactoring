package pep.per.mint.front.controller.co;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.co.ServerFilteringService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.MessageSourceUtil;



@Controller
@RequestMapping("/co")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServerFilteringController {

	private static final Logger logger = LoggerFactory.getLogger(ServerFilteringController.class);

	/**
	 * The ServerFiltering service.
     */
	@Autowired
	ServerFilteringService serverFilteringService;


	@Autowired
	CommonService commonService;


	/**
	 * The Message source.
	 */
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    private ServletContext servletContext;


	/**
	 * DataSet 검색조건 조회 [REST-R01-CO-80-01]
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/filter/datasets/cds/nms", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, List<Map>> getDataSetNameList(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
			Locale locale, HttpServletRequest request) throws Exception, ControllerException {

		String errorCd = "";
		String errorMsg = "";


		//--------------------------------------------------
		// 조회 실행
		//--------------------------------------------------
		List<Map> list = null;
		{
			Map params = comMessage.getRequestObject();
			if(params == null) {
				params = new HashMap();
			}

			//--------------------------------------------------
			// perCount 설정
			//--------------------------------------------------
			{
				String pc = commonService.getEnvironmentalValue("system", "server.filter.percount", "50");
				int perCount = ( pc == null ? 50 : Integer.parseInt(pc) );

				params.put("perCount", perCount);
			}

			list = serverFilteringService.getDataSetNameList(params);
		}
		//--------------------------------------------------
		// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
		//--------------------------------------------------
		{
			comMessage.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
		}
		//--------------------------------------------------
		// 통신메시지에 처리결과 코드/메시지를 등록한다.
		//--------------------------------------------------
		{
			if (list == null || list.size() == 0) {
				//--------------------------------------------------
				// 결과가 없을 경우 비지니스 예외 처리
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
			} else {
				//--------------------------------------------------
				// 성공 처리결과
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
				Object [] errorMsgParams = {list.size()};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.retrieve.list.ok", errorMsgParams, locale);
				comMessage.setResponseObject(list);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}




	/**
	 * DataMap 검색조건 조회 [REST-R01-CO-80-02]
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/filter/datamaps/cds/nms", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, List<Map>> getDataMapName(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
			Locale locale, HttpServletRequest request) throws Exception, ControllerException {

		String errorCd = "";
		String errorMsg = "";


		//--------------------------------------------------
		// 조회 실행
		//--------------------------------------------------
		List<Map> list = null;
		{
			Map params = comMessage.getRequestObject();
			if(params == null) {
				params = new HashMap();
			}

			//--------------------------------------------------
			// perCount 설정
			//--------------------------------------------------
			{
				String pc = commonService.getEnvironmentalValue("system", "server.filter.percount", "50");
				int perCount = ( pc == null ? 50 : Integer.parseInt(pc) );

				params.put("perCount", perCount);
			}

			list = serverFilteringService.getDataMapNameList(params);
		}
		//--------------------------------------------------
		// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
		//--------------------------------------------------
		{
			comMessage.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
		}
		//--------------------------------------------------
		// 통신메시지에 처리결과 코드/메시지를 등록한다.
		//--------------------------------------------------
		{
			if (list == null || list.size() == 0) {
				//--------------------------------------------------
				// 결과가 없을 경우 비지니스 예외 처리
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
			} else {
				//--------------------------------------------------
				// 성공 처리결과
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
				Object [] errorMsgParams = {list.size()};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.retrieve.list.ok", errorMsgParams, locale);
				comMessage.setResponseObject(list);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}


	/**
	 * DataMap - Source/Target DataSet 검색조건 조회 [REST-R01-CO-80-03]
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/filter/datamaps/datasets/cds/nms", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, List<Map>> getDataMapDataSetNm1(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
			Locale locale, HttpServletRequest request) throws Exception, ControllerException {

		String errorCd = "";
		String errorMsg = "";


		//--------------------------------------------------
		// 조회 실행
		//--------------------------------------------------
		List<Map> list = null;
		{
			Map params = comMessage.getRequestObject();
			if(params == null) {
				params = new HashMap();
			}

			//--------------------------------------------------
			// perCount 설정
			//--------------------------------------------------
			{
				String pc = commonService.getEnvironmentalValue("system", "server.filter.percount", "50");
				int perCount = ( pc == null ? 50 : Integer.parseInt(pc) );

				params.put("perCount", perCount);
			}

			// 0 : source, 1 : target
			int type = params.get("type") == null ? 0 : Integer.parseInt(params.get("type").toString());

			if( type == 0 ) {
				list = serverFilteringService.getDataMapSrcDataSetNm1(params);
			} else {
				list = serverFilteringService.getDataMapTagDataSetNm1(params);
			}


		}
		//--------------------------------------------------
		// 서비스 처리 종료시간을 얻어 CM에 세팅한다.
		//--------------------------------------------------
		{
			comMessage.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
		}
		//--------------------------------------------------
		// 통신메시지에 처리결과 코드/메시지를 등록한다.
		//--------------------------------------------------
		{
			if (list == null || list.size() == 0) {
				//--------------------------------------------------
				// 결과가 없을 경우 비지니스 예외 처리
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
			} else {
				//--------------------------------------------------
				// 성공 처리결과
				//--------------------------------------------------
				errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
				Object [] errorMsgParams = {list.size()};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.retrieve.list.ok", errorMsgParams, locale);
				comMessage.setResponseObject(list);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}

}
