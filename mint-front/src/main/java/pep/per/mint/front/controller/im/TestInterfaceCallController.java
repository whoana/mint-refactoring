/**
 * Copyright 2018 Mocomsys Inc.  All Rights Reserved.
 */
package pep.per.mint.front.controller.im;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.test.InterfaceCallDetail;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.im.TestInterfaceCallService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.FieldCheckUtil;
import pep.per.mint.front.util.MessageSourceUtil;

/**
 * <pre>
 * pep.per.mint.front.controller.im
 * TICController.java
 * </pre>
 * @author whoana
 * @date 2018. 8. 7.
 */
@Controller
@RequestMapping("/im")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestInterfaceCallController {

	private static final Logger logger = LoggerFactory.getLogger(TestInterfaceCallController.class);

	/**
	 * The Message source.
	 */
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

    //@Autowired
    //private ServletContext servletContext;

    @Autowired
    TestInterfaceCallService testInterfaceCallService;

    @RequestMapping(
			value="/interface-tests/{testDate}",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, List<InterfaceCallDetail>> getTestResultBySystem(
			HttpSession  httpSession,
			@RequestBody ComMessage<Map, List<InterfaceCallDetail>> comMessage,
			@PathVariable("testDate") String testDate,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map params = null;
			List list = null;
			//-------------------------------------------------
			//???????????? ??????
			//-------------------------------------------------
			{
				params = comMessage.getRequestObject();
				if(params == null) params = new HashMap();
				if (params != null){
					String paramString = FieldCheckUtil.paramString(params);
					logger.debug(Util.join("\nparamString:[", paramString, "]"));
				}
				params.put("testDate", testDate);
			}

			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			list = testInterfaceCallService.getTestResultListBySystem(params);

						//-----------------------------------------------
			//????????? ?????? ??????????????? ?????? CM??? ????????????.
			//-----------------------------------------------
			{
				comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
			}

			//-----------------------------------------------
			// ?????????????????? ???????????? ??????/???????????? ????????????.
			//-----------------------------------------------
			{
				if (list == null || list.size() == 0) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
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

    @RequestMapping(
			value="/interface-tests/{testDate}/{testId}/{interfaceId}",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<?, List<InterfaceCallDetail>> getTestResultByInterface(
			HttpSession  httpSession,
			@RequestBody ComMessage<?, List<InterfaceCallDetail>> comMessage,
			@PathVariable("testDate") String testDate,
			@PathVariable("testId") String testId,
			@PathVariable("interfaceId") String interfaceId,
			Locale locale,
			HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map params = new HashMap();
			List list = null;
			//-------------------------------------------------
			//????????????
			//-------------------------------------------------
			{
				params.put("testDate", testDate);
				params.put("testId", testId);
				params.put("interfaceId", interfaceId);
			}

			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			list = testInterfaceCallService.getTestResultListByInterface(params);

						//-----------------------------------------------
			//????????? ?????? ??????????????? ?????? CM??? ????????????.
			//-----------------------------------------------
			{
				comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
			}

			//-----------------------------------------------
			// ?????????????????? ???????????? ??????/???????????? ????????????.
			//-----------------------------------------------
			{
				if (list == null || list.size() == 0) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
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


    @RequestMapping(
			value="/interface-system-tests/{testDate}/{testId}/{systemId}",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<?, List<InterfaceCallDetail>> getTestResultByInterfaceSystem(
			HttpSession  httpSession,
			@RequestBody ComMessage<?, List<InterfaceCallDetail>> comMessage,
			@PathVariable("testDate") String testDate,
			@PathVariable("testId") String testId,
			@PathVariable("systemId") String systemId,
			Locale locale,
			HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map params = new HashMap();
			List list = null;
			//-------------------------------------------------
			//????????????
			//-------------------------------------------------
			{
				if (params != null){
					String paramString = FieldCheckUtil.paramString(params);
					logger.debug(Util.join("\nparamString:[", paramString, "]"));
				}
				params.put("testDate", testDate);
				params.put("testId", testId);
				params.put("systemId", systemId);
			}

			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			list = testInterfaceCallService.getTestResultListByInterfaceSystem(params);

						//-----------------------------------------------
			//????????? ?????? ??????????????? ?????? CM??? ????????????.
			//-----------------------------------------------
			{
				comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
			}

			//-----------------------------------------------
			// ?????????????????? ???????????? ??????/???????????? ????????????.
			//-----------------------------------------------
			{
				if (list == null || list.size() == 0) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
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

}
