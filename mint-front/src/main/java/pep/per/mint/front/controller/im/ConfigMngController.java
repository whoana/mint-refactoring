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
import org.springframework.web.bind.annotation.ResponseBody;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.ConfigInfo;
import pep.per.mint.common.data.basic.System;
import pep.per.mint.common.data.basic.test.InterfaceCallDetail;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.im.ConfigMngService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.FieldCheckUtil;
import pep.per.mint.front.util.MessageSourceUtil;

/**
 * <pre>
 * pep.per.mint.front.controller.im
 * ConfigMngController.java
 * </pre>
 * @author whoana
 * @date Nov 1, 2018
 */
@Controller
@RequestMapping("/im")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfigMngController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigMngController.class);

	/**
	 * The Message source.
	 */
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;


	@Autowired
	ConfigMngService configMngService;



	@Autowired
	CommonService commonService;


	@RequestMapping(
			value="/nh/config",
			params="method=POST",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<ConfigInfo, ?> createConfig(
			HttpSession  httpSession,
			@RequestBody ComMessage<ConfigInfo, ?> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		ConfigInfo info = comMessage.getRequestObject();
		if(info == null) {
			throw new Exception(Util.join("???????????????Config???????????? ????????? ????????? RequestObject<ConfigInfo> ?????? ???????????????."));
		}
		//List<System> sysList = commonService.getSystemOfServer(info.getServerId());
		//if(sysList.size()<=0){
		//	throw new Exception(Util.join("???????????????Config??????. System to Server mapping invaild."));
		//}else{
		//	System system = sysList.get(0);
		//	info.setSystemId(system.getSystemId());
			int res = configMngService.createConfig(info);
		//}

		String errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
		String errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
		comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
		return comMessage;
	}

	@RequestMapping(
			value="/nh/config",
			params="method=PUT",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<ConfigInfo, ?> updateConfig(
			HttpSession  httpSession,
			@RequestBody ComMessage<ConfigInfo, ?> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		ConfigInfo info = comMessage.getRequestObject();
		if(info == null) {
			throw new Exception(Util.join("???????????????Config???????????? ????????? ????????? RequestObject<ConfigInfo> ?????? ???????????????."));
		}
		int res = configMngService.updateConfig(info);
		String errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
		String errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
		comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
		return comMessage;
	}

	@RequestMapping(
			value="/nh/config",
			params="method=DELETE",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<ConfigInfo, ?> deleteConfig(
			HttpSession  httpSession,
			@RequestBody ComMessage<ConfigInfo, ?> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		ConfigInfo info = comMessage.getRequestObject();
		if(info == null) {
			throw new Exception(Util.join("???????????????Config???????????? ????????? ????????? RequestObject<ConfigInfo> ?????? ???????????????."));
		}
		int res = configMngService.deleteConfig(info);
		String errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
		String errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
		comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
		return comMessage;
	}


	@RequestMapping(
			value="/nh/config",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<Map, List<ConfigInfo>> getConfigList(
			HttpSession  httpSession,
			@RequestBody ComMessage<Map, List<ConfigInfo>> comMessage,
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

			}

			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			list = configMngService.getConfigList(params);

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
			value="/nh/config/compare",
			params="method=GET",
			method=RequestMethod.POST,
			headers="content-type=application/json")
	public @ResponseBody ComMessage<ConfigInfo, Map> getCompareList(
			HttpSession  httpSession,
			@RequestBody ComMessage<ConfigInfo, Map> comMessage,
			Locale locale, HttpServletRequest request
	) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		//?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";
			ConfigInfo params = null;
			Map res = null;
			//-------------------------------------------------
			//???????????? ??????
			//-------------------------------------------------
			{
				params = comMessage.getRequestObject();
				if(params == null) throw new Exception("????????? ConfigInfo??? ???????????? ?????????.");

			}

			//-----------------------------------------------
			//??????
			//-----------------------------------------------
			res = configMngService.getCompareList(params);

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
				if (res == null || res.size() == 0) {// ????????? ?????? ?????? ???????????? ?????? ??????

					errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
				} else {// ?????? ????????????
					errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
					errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
					comMessage.setResponseObject(res);
				}
				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
			}

			return comMessage;
		}
	}



}
