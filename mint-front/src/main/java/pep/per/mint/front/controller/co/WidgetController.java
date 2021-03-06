package pep.per.mint.front.controller.co;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pep.per.mint.common.data.basic.*;
import pep.per.mint.common.data.basic.System;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.co.ApprovalService;
import pep.per.mint.database.service.co.WidgetService;
import pep.per.mint.database.service.op.DashboardPushService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.FieldCheckUtil;
import pep.per.mint.front.util.MessageSourceUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


/**
 *
 * @author ta
 *
 */
@Controller
@RequestMapping("/co")
public class WidgetController {

	private static final Logger logger = LoggerFactory.getLogger(WidgetController.class);

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	@Autowired
	DashboardPushService dashboardPushService;

	@Autowired
	WidgetService widgetService;

	@Autowired
	private ServletContext servletContext;

	/**
	 * <pre>
	 * Widget Config Info
	 * API ID : REST-R99-CO-99-00
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/config/list", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> getWidgetConfig(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.getWidgetConfig param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = widgetService.getWidgetConfig(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.getWidgetConfig",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}



	/**
	 * <pre>
	 * ???????????????????????? ?????????????????????
	 * API ID : REST-R99-CO-99-01
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/WS0040/01", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> WS004001(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.WS004001 param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = widgetService.getWS0040Target(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.WS004001",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}


	/**
	 * <pre>
	 * WMQ??????????????? ?????????????????????
	 * API ID : REST-R99-CO-99-02
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/WS0042/01", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> WS004201(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.WS004201 param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = widgetService.getWS0042Target(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.WS004201",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}


	/**
	 * <pre>
	 * WMQ???????????????-???????????????????????????
	 * API ID : REST-R99-CO-99-03
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/WS0042/51", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> WS004251(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.WS004251 param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = dashboardPushService.getQmgrAlertListBySystem(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.WS004251",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}

	/**
	 * <pre>
	 * WMQ???????????????-??????????????????
	 * API ID : REST-R99-CO-99-04
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/WS0042/52", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> WS004252(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.WS004252 param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = dashboardPushService.getQueueAlertListBySystem(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.WS004252",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}

	/**
	 * <pre>
	 * WMQ???????????????-?????????????????????
	 * API ID : REST-R99-CO-99-05
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/WS0042/53", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> WS004253(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.WS004253 param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map> list = null;
				{
					list = dashboardPushService.getChannelAlertListBySystem(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(list);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.WS004253",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}


	/**
	 * <pre>
	 * WMQ Explorer Tree - SystemList
	 * API ID : REST-R99-CO-99-06
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(
			value="/widget/systems/treemodel",
			params="method=POST",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, Map<String,Object>> getSystemTreeWithModel(
			HttpSession  httpSession,
			@RequestBody ComMessage<Map, Map<String,Object>> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map<String,Object> treeModel = null;
			//-----------------------------------------------
			//?????? ??????
			//-----------------------------------------------
			Map params = null;
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
			}


			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			treeModel = widgetService.getSystemTreeWithModel(params);

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
				if (treeModel == null) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(treeModel);
				}
				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
			}

			return comMessage;
		}
	}

	/**
	 * <pre>
	 * WMQ Explorer Tree - SystemList
	 * API ID : REST-R99-CO-99-07
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(
			value="/widget/servers/treemodel",
			params="method=POST",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, Map<String,Object>> getServerTreeWithModel(
			HttpSession  httpSession,
			@RequestBody ComMessage<Map, Map<String,Object>> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			Map<String,Object> treeModel = null;
			//-----------------------------------------------
			//?????? ??????
			//-----------------------------------------------
			Map params = null;
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
			}


			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			treeModel = widgetService.getServerTreeWithModel(params);

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
				if (treeModel == null) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(treeModel);
				}
				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
			}

			return comMessage;
		}
	}


	/**
	 * <pre>
	 * Widget Personalization - insert
	 * API ID : REST-R99-CO-99-08
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/insert", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> insertPersonalizationByUser(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.insertPersonalizationByUser param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try {
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				int res = 0;
				{
					res = widgetService.insertPersonalizationByUser(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			} catch(Throwable e) {

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.create.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.insertPersonalizationByUser",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.create.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			} finally {

			}
		}
	}

	/**
	 * <pre>
	 * Widget Personalization - update
	 * API ID : REST-R99-CO-99-09
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/update", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> updatePersonalizationByUser(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.updatePersonalizationByUser param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try {
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				int res = 0;
				{
					res = widgetService.updatePersonalizationByUser(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			} catch(Throwable e) {

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.update.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.updatePersonalizationByUser",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.update.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			} finally {

			}
		}
	}

	/**
	 * <pre>
	 * Widget Personalization - delete
	 * API ID : REST-R99-CO-99-10
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/delete", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map>> deletePersonalizationByUser(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.deletePersonalizationByUser param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try {
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				int res = 0;
				{
					res = widgetService.deletePersonalizationByUser(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			} catch(Throwable e) {

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.delete.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.deletePersonalizationByUser",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.delete.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			} finally {

			}
		}
	}

	/**
	 * <pre>
	 * Widget Personalization - myList
	 * API ID : REST-R99-CO-99-11
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/list/owner", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map<String,Object>>> getPersonalizationByUserList(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map<String,Object>>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.getPersonalizationByUserList param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map<String,Object>> list = null;
				{
					list = widgetService.getPersonalizationByUserList(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setResponseObject(list);
					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			} catch(Throwable e) {

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.getPersonalizationByUserList",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			} finally {

			}
		}
	}

	/**
	 * <pre>
	 * Widget Personalization - shared
	 * API ID : REST-R99-CO-99-12
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/list/shared", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, List<Map<String,Object>>> getPersonalizationBySharedList(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, List<Map<String,Object>>> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.getPersonalizationBySharedList param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				List<Map<String,Object>> list = null;
				{
					list = widgetService.getPersonalizationBySharedList(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);

					comMessage.setResponseObject(list);
					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			} catch(Throwable e) {

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.getPersonalizationBySharedList",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			} finally {

			}
		}
	}

	/**
	 * <pre>
	 * Widget Config Info
	 * API ID : REST-R99-CO-99-13
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws ControllerException
	 */
	@RequestMapping(value = "/widget/personalization/load", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>, Map> getPersonalization(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,Object>, Map> comMessage, Locale locale, HttpServletRequest request)
			throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{

			String errorCd = "";
			String errorMsg = "";

			Map params = comMessage.getRequestObject();
			if(params == null) params = new HashMap();

			//----------------------------------------------------------------------------
			// ????????? JSON ??????
			//----------------------------------------------------------------------------
			{
				logger.debug(Util.join("WidgetController.getPersonalization param dump:\n", FieldCheckUtil.jsonDump(params)));
			}

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				//????????? ?????????/???????????? ????????? ??????
				String regId = comMessage.getUserId();
				String regDate = Util.getFormatedDate();
				params.put("regId", regId);
				params.put("regDate", regDate);
			}

			try{
				//----------------------------------------------------------------------------
				//????????????
				//----------------------------------------------------------------------------
				Map result = null;
				{
					result = widgetService.getPersonalization(params);
				}
				//--------------------------------------------------
				// ????????? ?????? ??????????????? ?????? CM??? ????????????.
				//--------------------------------------------------
				{
					comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
				}
				//--------------------------------------------------
				// ?????????????????? ???????????? ??????/???????????? ????????????.
				//--------------------------------------------------
				{

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(result);
					comMessage.setErrorCd(errorCd);
					comMessage.setErrorMsg(errorMsg);
					return comMessage;
				}
			}catch(Throwable e){

				errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.fail", locale);
				String errorDetail = e.getMessage();
				String[] errorMsgParams = {"WidgetController.getPersonalizationBySharedList",errorDetail};
				errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.fail", errorMsgParams, locale);
				throw new ControllerException(errorCd, errorMsg, e);

			}finally{

			}
		}
	}


}
