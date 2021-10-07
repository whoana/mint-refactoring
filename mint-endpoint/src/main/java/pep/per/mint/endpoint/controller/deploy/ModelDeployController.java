/**
 * Copyright 2020 Mocomsys Inc.  All Rights Reserved.
 */
package pep.per.mint.endpoint.controller.deploy;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.dataset.DataSet;

import pep.per.mint.common.data.basic.runtime.InterfaceModel;
import pep.per.mint.common.data.basic.runtime.ModelDeployment;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.rt.ModelService;
import pep.per.mint.database.service.su.BridgeProviderService;
import pep.per.mint.endpoint.service.deploy.ModelDeployService;

/**
 * @author whoana
 * @since Sep 14, 2020
 */
@RequestMapping("/rt")
@Controller
public class ModelDeployController {

	private static final Logger logger = LoggerFactory.getLogger(ModelDeployController.class);

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	private ServletContext servletContext;

	@Autowired
	ModelDeployService modelDeployService;

	@Autowired
	ModelService modelService;

	/**
	 * @deprecated
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/xmls", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,Object>,String> getDeployXml(
		HttpSession httpSession,
		@RequestBody ComMessage<Map<String,Object>, String> comMessage,
		Locale locale, HttpServletRequest request) throws Exception {

		String errorCd = "";
		String errorMsg = "";
		String xml = null;
		Map params = comMessage.getRequestObject();
		if(params == null || params.get("interfaceMid") == null) {
			String [] errorMsgParams = {this.getClass().toString(), "interfaceMid"};
			errorMsg = messageSource.getMessage("error.msg.request.required.params", errorMsgParams, locale);
			throw new Exception(errorMsg);
		}
		//--------------------------------------------------
		// 조회 실행
		//--------------------------------------------------
		{
			String interfaceMid = (String)params.get("interfaceMid");
			xml = modelDeployService.getDeployModelXml(interfaceMid, comMessage.getUserId());
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
			if (xml == null ) {// 결과가 없을 경우 비지니스 예외 처리
				errorCd = messageSource.getMessage("error.cd.retrieve.none", null, locale);
				errorMsg = messageSource.getMessage("error.msg.retrieve.none", null, locale);
			} else {// 성공 처리결과
				errorCd = messageSource.getMessage("success.cd.ok", null, locale);
				errorMsg = messageSource.getMessage("success.msg.retrieve.list.ok", null, locale);
				comMessage.setResponseObject(xml);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}


	/**
	 * <pre>
	 *  API ID : REST-R01-RT-03-03
	 *  인터페이스모델 배포
	 * 	mint-bridge-apps 애플리케이션을 통해 모델을 배포한다.
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/actions", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<ModelDeployment, ModelDeployment> deploy(
		HttpSession httpSession,
		@RequestBody ComMessage<ModelDeployment, ModelDeployment> comMessage,
		Locale locale, HttpServletRequest request) throws Exception {

		String errorCd = "";
		String errorMsg = "";

		ModelDeployment md = comMessage.getRequestObject();
		if(md == null) {
			String [] errorMsgParams = {this.getClass().toString(), "ModelDeployment"};
			errorMsg = messageSource.getMessage("error.msg.request.required.parame", errorMsgParams, locale);
			throw new Exception(errorMsg);
		}


		try {
			ComMessage response = modelDeployService.deploy2(comMessage); //형상관리 버전 개발중 , 2021.05
			//ComMessage response = modelDeployService.deploy(comMessage);
			response.setRequestObject(null);
			return response;
		}catch(org.springframework.web.client.ResourceAccessException  e) {

			errorCd = messageSource.getMessage("error.cd.system.bridge.connect.fail", null, locale);
			errorMsg = messageSource.getMessage("error.msg.system.bridge.connect.fail", null, locale);
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
			logger.error("fail to connect to mint-bridge-apps",e);
			return comMessage;

		}catch(Exception e) {
			throw e;
		}
	}


	/**
	 * <pre>
	 *  API ID : REST-R01-RT-03-01
	 *  신규배포여부체크
	 * 	인터페아스모델의 배포 여부를 체크하여 true/false 값으로 리턴한다.
	 * 	모델이 존재하지 않거나 배포상태가 등록("0") 이면 false, 수정됨("1"), 체크인("2"), 체크아웃("3") 이면 true
	 * 	URL PATH 포함 : {interfaceMid} 인터페이스모델ID
	 * </pre>
	 * @param httpSession
	 * @param interfaceMid
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/exists/{interfaceMid}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, Boolean> existsDeployedModel(
			HttpSession httpSession,
			@PathVariable("interfaceMid") String interfaceMid,
			@RequestBody ComMessage<Map<String,String>, Boolean> comMessage,
			Locale locale, HttpServletRequest request) throws Exception {
		InterfaceModel interfaceModel = modelService.getInterfaceModel(interfaceMid);
	 	if(interfaceModel == null) throw new Exception("NotFoundInterfaceModel(model id:" + interfaceMid + ")");
		comMessage.setResponseObject(Util.isEmpty(interfaceModel.getDeployStatus()) || InterfaceModel.DEPLOY_STATE_INIT.equals(interfaceModel.getDeployStatus()) ? false : true);
		comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
		comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
		comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
		return comMessage;
	}


	@Autowired
	BridgeProviderService bridgeProviderService;
	/**
	 * <pre>
	 * mint-bridge-apps 애플리케이션이 요청하는 service-cds를 조회한다.
	 * 사용처 : 신한생명
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/bridge/interfaces", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, List<Map<String,String>>> getInterfaces(
		HttpSession httpSession,
		@RequestBody ComMessage<Map<String,String>, List<Map<String,String>>> comMessage,
		Locale locale, HttpServletRequest request) throws Exception {

		String errorCd = "";
		String errorMsg = "";
		List<Map<String,String>> list = null;
		Map params = comMessage.getRequestObject();
		{
			if(params == null || params.get("businessCd") == null) throw new Exception("have no required filed value:businessCd");
		}
		//--------------------------------------------------
		// 모델 contents 조회 및 배포 실행
		//--------------------------------------------------
		{
			list = bridgeProviderService.getInterfaces(params);

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
			if (list == null ) {// 결과가 없을 경우 비지니스 예외 처리
				errorCd = messageSource.getMessage("error.cd.retrieve.none", null, locale);
				errorMsg = messageSource.getMessage("error.msg.retrieve.none", null, locale);
			} else {// 성공 처리결과

				//logger.debug("bridgeProviderService.getInterfaces:\n" + Util.toJSONPrettyString(list));

				errorCd = messageSource.getMessage("success.cd.ok", null, locale);
				errorMsg = messageSource.getMessage("success.msg.retrieve.list.ok", null, locale);
				comMessage.setResponseObject(list);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}


	/**
	 * <pre>
	 *  API ID : REST-R01-RT-03-02
	 *  인터페이스 모델 수정가능여부 판단 (형상관리 체크아웃 상태 체크)
	 *  신한생명 형상관리 (배포된)인터페이스 모델의 체크아웃 상태를 조회하여 true | false 로 리턴한다.
	 *  파라메터: "integrationId"
	 *
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 */;
	@RequestMapping(value = "/models/deploys/checkout-states", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String, String>, Boolean> getCheckoutState(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, Boolean> comMessage,
			Locale locale, HttpServletRequest request) throws Exception {
		try {

			Map params = comMessage.getRequestObject();
			{
				if(params == null || params.get("integrationId") == null) throw new Exception("have no required filed value:integrationId");
			}

			ComMessage response = modelDeployService.getCheckoutState(comMessage);
			response.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
			response.setErrorMsg( messageSource.getMessage("success.msg.ok", null, locale));
			return response;
		}catch(org.springframework.web.client.ResourceAccessException  e) {
			comMessage.setErrorCd(messageSource.getMessage("error.cd.system.bridge.connect.fail", null, locale));
			comMessage.setErrorMsg(messageSource.getMessage("error.msg.system.bridge.connect.fail", null, locale));
			logger.error("fail to connect to mint-bridge-apps",e);
			return comMessage;
		}
	}

	/**
	 * <pre>
	 *  API ID : REST-R01-RT-03-03
	 *  신규배포여부체크
	 *  신한생명 형상관리 패키지정보 리스트 조회
	 *  파라메터 : "userId"
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/packages", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String, String>, List<Map>> getPackages(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
			Locale locale, HttpServletRequest request) throws Exception {
		try {

			Map params = comMessage.getRequestObject();
			{
				if(params == null || params.get("userId") == null) throw new Exception("have no required filed value:userId");
			}

			ComMessage<Map<String, String>, List<Map>> response = modelDeployService.getPackages(comMessage);
			Object [] args = {(Util.isEmpty(response.getResponseObject()) ? 0 : response.getResponseObject().size()) + ""};
			response.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
			response.setErrorMsg( messageSource.getMessage("success.msg.retrieve.list.ok", args, locale));
			return response;
		}catch(org.springframework.web.client.ResourceAccessException  e) {
			comMessage.setErrorCd(messageSource.getMessage("error.cd.system.bridge.connect.fail", null, locale));
			comMessage.setErrorMsg(messageSource.getMessage("error.msg.system.bridge.connect.fail", null, locale));
			logger.error("fail to connect to mint-bridge-apps",e);
			return comMessage;
		}
	}

	@RequestMapping(value = "/models/deploys/bridge/datasets", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, Map<String,List<DataSet>>> getDatasets(
		HttpSession httpSession,
		@RequestBody ComMessage<Map<String,String>, Map<String,List<DataSet>>> comMessage,
		Locale locale, HttpServletRequest request) throws Exception {

		String errorCd = "";
		String errorMsg = "";
		Map<String,List<DataSet>> res = null;
		Map<String,String> params = comMessage.getRequestObject();
		{
			if(params == null || params.get("integrationId") == null) throw new Exception("have no required filed value:integrationId");
			//2021.06.10 요구사항 변경으로 주석 처리
			//if(params.get("systemCd") == null) throw new Exception("have no required filed value:systemCd");
		}
		//--------------------------------------------------
		// 모델 contents 조회 및 배포 실행
		//--------------------------------------------------
		{
			//--------------------------------------------------------------------------------
			// 2021.06.10 요구사항 변경으로 내용 보완
			// - systemCd 삭제
			//--------------------------------------------------------------------------------
			res = bridgeProviderService.getDataSets(params.get("integrationId"));
			//res = bridgeProviderService.getDataSets(params.get("integrationId"), params.get("systemCd"));
			//logger.debug("DataSets:\n" + Util.toJSONPrettyString(res));
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
			if (res == null ) {// 결과가 없을 경우 비지니스 예외 처리
				errorCd = messageSource.getMessage("error.cd.retrieve.none", null, locale);
				errorMsg = messageSource.getMessage("error.msg.retrieve.none", null, locale);
			} else {// 성공 처리결과
				errorCd = messageSource.getMessage("success.cd.ok", null, locale);
				errorMsg = messageSource.getMessage("success.msg.retrieve.list.ok", null, locale);
				comMessage.setResponseObject(res);
			}
			comMessage.setErrorCd(errorCd);
			comMessage.setErrorMsg(errorMsg);
		}
		return comMessage;
	}


	/**
	 * <pre>
	 *  API ID : REST-R01-RT-04-01
	 *  브릿지요청(checkout)
	 *  신한생명 형상관리 체크아웃 처리
	 * </pre>
	 * @param httpSession
	 * @param comMessage
	 * @param locale
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/models/deploys/bridge/checkouts", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, Map<String,String>> checkout(
			HttpSession httpSession,
			@RequestBody ComMessage<Map<String,String>, Map<String,String>> comMessage,
			Locale locale, HttpServletRequest request) throws Exception {

		String errorCd = "";
		String errorMsg = "";
		Map<String,String> params = comMessage.getRequestObject();
		String integrationId = params.get("integrationId");
		String userId = params.get("userId");
		String versionNumber = params.get("version");

		if(params == null || Util.isEmpty(userId)) throw new Exception("have no required filed value:userId");
		if(Util.isEmpty(versionNumber)) throw new Exception("have no required filed value:version");
		if(Util.isEmpty(integrationId)) throw new Exception("have no required filed value:version");

		modelDeployService.checkout(integrationId, Integer.parseInt(versionNumber), userId);

		comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
		comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
		comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));

		return comMessage;
	}


}


