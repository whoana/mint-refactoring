/*
  Copyright 2020 Mocomsys Inc.  All Rights Reserved.
 */
package pep.per.mint.endpoint.service.deploy;

import java.io.BufferedWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.authority.*;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.*;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.data.basic.version.VersionData;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.an.DataSetService;
import pep.per.mint.database.service.au.AuthorityService;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.rt.ModelService;
import pep.per.mint.database.service.vc.InterfaceModelVersionControlService;
import pep.per.mint.database.service.vc.UpdateHandler;
import pep.per.mint.database.service.vc.VersionControlService;
import pep.per.mint.endpoint.Variables;
import pep.per.mint.endpoint.service.deploy.data.description.*;
import pep.per.mint.endpoint.service.deploy.data.mapping.MappingDefinitionBuilder;
import pep.per.mint.endpoint.service.deploy.data.msg.MessageDefinitionBuilder;

/**
 * <pre>
 * 모델 정보를 파일 및 서비스를 통해 내보내기 하기 위한 서비스
 * </pre>
 *
 * @author whoana
 * @since Jul 30, 2020
 */
@Service
public class ModelDeployService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	InterfaceModelVersionControlService interfaceModelVersionControlService;

	@Autowired
	VersionControlService versionControlService;

	@Autowired
	DataSetService dataSetService;

	@Autowired
	ModelService modelService;

	@Autowired
	CommonService commonService;

	@Autowired
    AuthorityService authorityService;

	@Autowired
	Variables variables;


	@Autowired
	RestTemplate restTemplate;
	final static String SUCCESS_CD = "0";
	final static String ERROR_CD = "9";
	final static String NODE_TYPE_SENDER 	= "0";
	final static String NODE_TYPE_HUB 		= "1";
	final static String NODE_TYPE_RECEIVER 	= "2";
	/**
	 *
	 */
	final static String STAGE_DEV  = "0";

	/**
	 *
	 */
	final static String STAGE_TEST = "1";

	/**
	 *
	 */
	final static String STAGE_REAL = "2";

	/**
	 * <pre>
	 * 인터페이스 모델 정보를 xml 하나의 포멧으로 내보내기 한다.
	 * xml 정의서는 model.xsd 를 참고하라.
	 * 메시지 레이아웃 및 매핑 정의서는 cdata 형태로 포함한다.
	 *
	 * [내보내기 위치 및 파일]
	 * 	{배포 HOME:dest}/{STAGE:deve,test,real}/{INTERFACE_ID}/{MODEL_ID}/model.xml
	 * </pre>
	 *
	 * @param dest
	 * @param interfaceMid
	 * @param deployUserId
	 * @throws Exception
	 * @deprecated 현재 사용되는 곳이 없음
	 */
	public void deployModelToXmlFile(String dest, String interfaceMid, String deployUserId) throws Exception{
		ModelDescriptionXmlBuilder mdxb = new ModelDescriptionXmlBuilder();
		InterfaceModel model = modelService.getInterfaceModel(interfaceMid);
		Interface interfaze = commonService.getInterfaceDetail(model.getInterfaceId());

		String contents = mdxb.build(model, dataSetService, interfaze, variables, deployUserId);
		if(contents == null) {
			throw new Exception("This Model(interfaceMid:" + interfaceMid + ") Have no contents of interface model.");
		}


		String interfaceId = model.getIntegrationId();
		String modelId = model.getMid();

		String stageCd = model.getStage();
		String state;
		if(STAGE_DEV.equals(stageCd)){
			state = "deve";
		}else if(STAGE_TEST.equals(stageCd)){
			state = "test";
		}else if(STAGE_REAL.equals(stageCd)){
			state = "real";
		}else {
			state = "real";
		}

		Path destPath = Paths.get(dest, state, interfaceId,  modelId);

		Files.createDirectories(destPath);
		Path modelFile = Paths.get(destPath.toString(),  "model.xml");

		Files.deleteIfExists(modelFile);
		Files.createFile(modelFile);
		BufferedWriter bw = Files.newBufferedWriter(modelFile, Charset.defaultCharset(), null);
		bw.write(contents);
		bw.flush();

	}

	/**
	 * <pre>
	 * 인터페이스 모델 컨텐츠를 조회하여 리턴한다.
	 * </pre>
	 * @param deployUser
	 * @param interfaceMid
	 * @return
	 * @throws Exception
	 */
	public String getDeployModelXml(String interfaceMid, String deployUser) throws Exception{
		ModelDescriptionXmlBuilder mdxb = new ModelDescriptionXmlBuilder();
		InterfaceModel model = modelService.getInterfaceModel(interfaceMid);
		if(model == null) throw new Exception(Util.join("not found interface model [interfaceMid:",interfaceMid,"]"));
		Interface interfaze = commonService.getInterfaceDetail(model.getInterfaceId());
		return  mdxb.build(model, dataSetService, interfaze, variables, deployUser);
	}

	/**
	 * <pre>
	 * 인터페이스 모델 컨텐츠를 배포시스템에 배포한다.
	 * update
	 * 2021.01.12
	 * ModelDeployment - 추가내용
	 * 1) integrationId
	 * 2) interfaceNm
	 * 3) businessCd(업무코드)
	 * 4) 송신시스템코드
	 * 5) 송신시스템명
	 * 6) 수신시스템코드
	 * 7) 수신시스템명
	 * 2021.01.18
	 * ModelDeployment - 추가내용
	 * 8) 채널코드
	 * 9) 채널명
	 * </pre>
	 * @param request
	 * @throws Exception
	 */
	public ComMessage<ModelDeployment, ModelDeployment> deploy(ComMessage<ModelDeployment, ModelDeployment> request) throws Exception{

		ModelDeployment md = request.getRequestObject();
		try {
			md.setDeployDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT));
			//md.setContents(getDeployModelXml(md.getInterfaceMid(), md.getDeployUser()));

			String interfaceMid = md.getInterfaceMid();
			ModelDescriptionXmlBuilder mdxb = new ModelDescriptionXmlBuilder();
			InterfaceModel model = modelService.getInterfaceModel(interfaceMid);
			if(model == null) throw new Exception(Util.join("not found interface model [interfaceMid:",interfaceMid,"]"));
			Interface interfaze = commonService.getInterfaceDetail(model.getInterfaceId());

			Options options = new Options();
			options.setIncludeServerInfo(false);
			String contents = mdxb.build(options, model, dataSetService, interfaze, variables, md.getDeployUser());
			md.setContents(contents);

			DeploymentInfo di = new DeploymentInfo();
			if(!Util.isEmpty(interfaze.getBusinessList())) di.setBusinessCd(interfaze.getBusinessList().get(0).getBusinessCd());
			di.setIntegrationId(interfaze.getIntegrationId());
			di.setInterfaceNm(interfaze.getInterfaceNm());

			String dataPrMethodCd = interfaze.getDataPrMethod();
			String dataPrMethodNm = interfaze.getDataPrMethodNm();
			if(!Util.isEmpty(dataPrMethodCd)){
				di.setDataPrMethodCd(dataPrMethodCd);
				di.setDataPrMethodNm(dataPrMethodNm);
			}
			List<pep.per.mint.common.data.basic.System> systems = interfaze.getSystemList();
			for (pep.per.mint.common.data.basic.System system : systems) {
				String nodeType = system.getNodeType();
				if(NODE_TYPE_SENDER.equals(nodeType)) {
					di.setSenderSystemCd(system.getSystemCd());
					di.setSenderSystemNm(system.getSystemNm());
				}else if(NODE_TYPE_HUB.equals(nodeType)) {
					di.setHubSystemCd(system.getSystemCd());
					di.setHubSystemNm(system.getSystemNm());
				}else if(NODE_TYPE_RECEIVER.equals(nodeType)) {
					di.setReceiverSystemCd(system.getSystemCd());
					di.setReceiverSystemNm(system.getSystemNm());
				}
			}

			if(interfaze.getChannel() != null) {
				di.setChannelCd(interfaze.getChannel().getChannelCd());
				di.setChannelNm(interfaze.getChannel().getChannelNm());
			}
			md.setDeploymentInfo(di);

			logger.debug("\n model xml:" +md.getContents() + "\n");

			String url = commonService.getEnvironmentalValue("system", "model.deploy.service.url", "http://localhost:8080/mint-bridge-apps/deployments");
			ComMessage<ModelDeployment, ModelDeployment> response = restTemplate.postForObject(url, request, request.getClass());

			if(response == null) {
				response = request;
				response.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
				response.setResponseObject(md);
				md.setResultCd(ERROR_CD);
				md.setResultMsg("have no response from the deployment system.");
			}

			return response;

		}catch(Exception e) {
			md.setResultCd(ERROR_CD);
			md.setResultMsg(e.getMessage());
			throw e;
		}finally {
			//modelService.createModelDeployment(md);
		}
	}


	/**
	 * <pre>
	 *     로컬 레파지토리 commit & bridge 배포
	 * </pre>
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public ComMessage<ModelDeployment, ModelDeployment> deploy2(ComMessage<ModelDeployment, ModelDeployment> request) throws Exception{



		ModelDeployment md = request.getRequestObject();
		InterfaceModel model = null;
		try {
			String date = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT);
			md.setDeployDate(date);
			//md.setContents(getDeployModelXml(md.getInterfaceMid(), md.getDeployUser()));
			String interfaceMid = md.getInterfaceMid();
			ModelDescriptionXmlBuilder mdxb = new ModelDescriptionXmlBuilder();
			model = modelService.getInterfaceModel(interfaceMid);
			model.setLastDeployDate(date);
			model.setLastDeployUser(md.getDeployUser());


			if(commonService.getEnvironmentalBooleanValue("system", "version.control.use",false))
			//커밋 및 새로운 버전 정보 세팅, xml 빌드전에 세팅해야함.
			{
				Version version = interfaceModelVersionControlService.commit(model, md.getDeployUser(), "commit"); // 로컬 레파지토리 커밋
				md.setVersion(version.getVersion());
				model.setVersion(version.getVersionNumber() + "");
			}
			if(model.getVersion() == null) model.setVersion("0");

			if(model == null) throw new Exception(Util.join("not found interface model [interfaceMid:",interfaceMid,"]"));
			Interface interfaze = commonService.getInterfaceDetail(model.getInterfaceId());

			Options options = new Options();
			options.setIncludeServerInfo(false);
			String contents = mdxb.build(options, model, dataSetService, interfaze, variables, md.getDeployUser());
			md.setContents(contents);

			DeploymentInfo di = new DeploymentInfo();
			if(!Util.isEmpty(interfaze.getBusinessList())) di.setBusinessCd(interfaze.getBusinessList().get(0).getBusinessCd());
			di.setIntegrationId(interfaze.getIntegrationId());
			di.setInterfaceNm(interfaze.getInterfaceNm());

			String dataPrMethodCd = interfaze.getDataPrMethod();
			String dataPrMethodNm = interfaze.getDataPrMethodNm();
			if(!Util.isEmpty(dataPrMethodCd)){
				di.setDataPrMethodCd(dataPrMethodCd);
				di.setDataPrMethodNm(dataPrMethodNm);
			}
			List<pep.per.mint.common.data.basic.System> systems = interfaze.getSystemList();
			for (pep.per.mint.common.data.basic.System system : systems) {
				String nodeType = system.getNodeType();
				if(NODE_TYPE_SENDER.equals(nodeType)) {
					di.setSenderSystemCd(system.getSystemCd());
					di.setSenderSystemNm(system.getSystemNm());
				}else if(NODE_TYPE_HUB.equals(nodeType)) {
					di.setHubSystemCd(system.getSystemCd());
					di.setHubSystemNm(system.getSystemNm());
				}else if(NODE_TYPE_RECEIVER.equals(nodeType)) {
					di.setReceiverSystemCd(system.getSystemCd());
					di.setReceiverSystemNm(system.getSystemNm());
				}
			}

			if(interfaze.getChannel() != null) {
				di.setChannelCd(interfaze.getChannel().getChannelCd());
				di.setChannelNm(interfaze.getChannel().getChannelNm());
			}
			md.setDeploymentInfo(di);

			logger.debug("\n model xml:" +md.getContents() + "\n");


			ComMessage<ModelDeployment, ModelDeployment> response = null;


			if(commonService.getEnvironmentalBooleanValue("system", "model.deploy.service.test",false)){
				response = request;
				response.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
				response.setResponseObject(md);
				String errorMsg = "success";
				md.setResultCd(SUCCESS_CD);
				md.setResultMsg(errorMsg);
			}else{
				String url = commonService.getEnvironmentalValue("system", "model.deploy.service.url", null);
				if (url == null) {
					throw new Exception("have no url info, check for environmental value: model.deploy.state.service.url.");
				}

				response = restTemplate.postForObject(url, request, request.getClass());

				if (response == null) {
					response = request;
					response.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
					response.setResponseObject(md);
					String errorMsg = "have no response from the deployment system.";
					md.setResultCd(ERROR_CD);
					md.setResultMsg(errorMsg);
				} else {
					md.setResultCd(response.getErrorCd());
					md.setResultMsg(response.getErrorMsg());
				}
			}

			return response;

		}catch(Exception e) {
			md.setResultCd(ERROR_CD);
			md.setResultMsg(e.getMessage());
			throw e;
		}finally {

			modelService.addModelDeploymentHistory(md); // 모델 배포 히스토리 남기기,
			// @Transaction 으로 몪여 있으므로 예외 발생시에는 롤백처리 되므로 히스토리를 쌓지 않는다.
			// 프론트 화면까지 예외 내용이 전달 되므로 어플리케이션 예외에 대한 히스토리까지 남길 필요는 없을 듯 하다.
			if (SUCCESS_CD.equals(md.getResultCd()) && model != null) {
				modelService.updateModelDeployState(model, ModelDeployState.CHECKIN, md.getDeployDate(), md.getDeployUser()); //모델 배포상태 변경
			}

		}
	}


	/**
	 * <pre>
	 * 내보내기 위치
	 * 	{배포 HOME:dest}/{STAGE:deve,test,real}/{INTERFACE_ID}/{MODEL_ID}
	 * 내보내기 파일 리스트
	 * 	model.json
	 * 	layout.xsd(여러개 가능)
	 * 	mapping.xml(여러개 가능)
	 * </pre>
	 * @param dest
	 * @param interfaceMid
	 * @throws Exception
	 * @deprecated 현재 사용되는 곳이 없음
	 */
	public void deployModelToJsonFile(String dest, String interfaceMid) throws Exception{


		ModelDescription md = buildModelDescription(interfaceMid);

		String interfaceId = md.getInterfaceId();
		String modelId = md.getId();
		String stageCd = md.getStage();
		String state = "real";

		if(STAGE_DEV.equals(stageCd)){
			state = "deve";
		} else if(STAGE_TEST.equals(stageCd)){
			state = "test";
		} else if(STAGE_REAL.equals(stageCd)){
			state = "real";
		}

		Path destPath = Paths.get(dest, state, interfaceId,  modelId);

		Files.createDirectories(destPath);
		Path modelFile = Paths.get(destPath.toString(),  "model.json");

		Files.deleteIfExists(modelFile);
		Files.createFile(modelFile);

		BufferedWriter bw = Files.newBufferedWriter(modelFile, null, null);
		bw.write(Util.toJSONPrettyString(md));
		bw.flush();


		if(md.getLayouts().size() > 0) {
			Collection<Layout> layouts = md.getLayouts().values();
			for (Layout layout : layouts) {
				String id = layout.getId();
				String contents = buildMessageLayout(id);

				Path layoutFile = Paths.get(destPath.toString(), layout.getDefinition());

				Files.deleteIfExists(layoutFile);
				Files.createFile(layoutFile);

				BufferedWriter w = Files.newBufferedWriter(layoutFile, null, null);
				w.write(contents);
				w.flush();

			}
		}



		if(md.getMappings().size() > 0) {
			Collection<Mapping> mappings = md.getMappings().values();
			for (Mapping mapping : mappings) {
				String id = mapping.getId();
				String contents = buildMapping(id);

				Path mappingFile = Paths.get(destPath.toString(), mapping.getDefinition());

				Files.deleteIfExists(mappingFile);
				Files.createFile(mappingFile);
				BufferedWriter w = Files.newBufferedWriter(mappingFile, null, null);
				w.write(contents);
				w.flush();
			}
		}

		URL mappingXsd = getClass().getResource("/config/map.xsd");
		Files.deleteIfExists(Paths.get(destPath.toString(), "map.xsd"));
		Files.copy(Paths.get(mappingXsd.getFile()), Paths.get(destPath.toString(), "map.xsd"));

		URL readme = getClass().getResource("/config/readme.txt");
		Files.deleteIfExists(Paths.get(destPath.toString(), "readme.txt"));
		Files.copy(Paths.get(readme.getFile()), Paths.get(destPath.toString(), "readme.txt"));


	}


	/**
	 * <pre>
	 * mapping 정의서 생성
	 * </pre>
	 * @param dataMapId
	 * @return
	 * @throws Exception
	 */
	public String buildMapping(String dataMapId) throws Exception {
		Map<String, Object> res  = dataSetService.getSimpleDataMap(dataMapId , "N");

		DataMap map = (DataMap) res.get("mapData");
		DataSet tar = (DataSet) res.get("tagDataSet");
		DataSet src = (DataSet) res.get("srcDataSet");
		MappingDefinitionBuilder mdb = new MappingDefinitionBuilder();
		return mdb.build(Arrays.asList(src, tar), map);
	}

	/**
	 *
	 * @param dataSetId
	 * @return
	 * @throws Exception
	 */
	public String buildMessageLayout(String dataSetId) throws Exception {
		MessageDefinitionBuilder xsdBuilder = new MessageDefinitionBuilder();
		DataSet dataSet = dataSetService.getSimpleDataSet(dataSetId, "N");
		return xsdBuilder.build(dataSet, variables);
	}

	/**
	 * <pre>
	 *
	 * </pre>
	 * @param interfaceMid
	 * @return
	 * @throws Exception
	 */
	public ModelDescription buildModelDescription(String interfaceMid)throws Exception {
		InterfaceModel model = modelService.getInterfaceModel(interfaceMid);
		System.out.println("mode:" + Util.toJSONPrettyString(model));
		ModelDescriptionBuilder mdBuilder = new ModelDescriptionBuilder();
		return mdBuilder.build(model);
	}

	/**
	 * 신한생명 형상관리 체크아웃 상태 체크 호출
	 * 관련 포탈환경설정값
	 * 	"system.model.deploy.state.service.test" : 형상관리 배포 상태확인 서비스에 대한 테스트 옵션,
	 * 											   true 로 등록될 경우 수정 가능 여부는 항상 true 로 리턴되도록 고정됨.
	 * 	"system.model.deploy.state.service.url"  : 형상관리 배포 상태확인 서비스에 대한 URL
	 * 	신한생명 스펙
	 * 		{ statusCode:"상태코드", status:"상태명", systemCode:"시스템 코드", packageNo:"패키지번호" }
	 * 		status/Code 예시 - 체크아웃/5, 신규등록/3, 운영중/0, 개발배포완료/B
	 * 		체크아웃/5, 신규등록/3 은 수정 가능으로 처리한다.
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	static String ST_RUNNING = "0";
	static String ST_NEW = "3";
	static String ST_CHECKOUT = "5";
	static String ST_DEPLOY = "B";
	public ComMessage getCheckoutState(ComMessage request) throws Exception {
		ComMessage response = request ;
		if(commonService.getEnvironmentalBooleanValue("system", "model.deploy.state.service.test",false)){
			response.setResponseObject(true);
		}else {
			String url = commonService.getEnvironmentalValue("system", "model.deploy.state.service.url", null);
			if (url == null) {
				throw new Exception("have no url info, check for environmental value: model.deploy.state.service.url.");
			}
			ComMessage<Map<String, String>, Map<String, String>> bridgeResponse = restTemplate.postForObject(url, request, request.getClass());
			if (bridgeResponse == null) throw new Exception("have no response from the deployment system.");
			Map<String, String> responseObject = bridgeResponse.getResponseObject();
			String statusCode = responseObject.get("statusCode");
			response.setResponseObject(ST_NEW.equals(statusCode) || ST_CHECKOUT.equals(statusCode) ? true : false);
		}
		response.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
		return response;
	}

	/**
	 * 신한생명 형상관리 체크인을 위한 패키지 리스트 조회
	 * 관련 포탈환경설정값
	 * 	"system.model.deploy.state.service.test" : 패키지 리스트 조회 서비스에 대한 테스트 옵션
	 * 	"system.model.deploy.state.service.url"  : 패키지 리스트 조회 서비스에 대한 URL
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public ComMessage<Map<String, String>, List<Map>> getPackages(ComMessage request) throws Exception {
		ComMessage response = request ;
		if(commonService.getEnvironmentalBooleanValue("system", "model.deploy.package.service.test",false)){
			Map prg = new HashMap();
			prg.put("prgPath", "/usr/pgm/a");
			prg.put("prgNm","betterCallSaul");

			Map map = new HashMap();
			map.put("prgList", Arrays.asList(prg));
			map.put("pkgNm","테스트패키지01");
			map.put("pkgId","TESTPKG01");
			response.setResponseObject(Arrays.asList(map));
		}else {
			String url = commonService.getEnvironmentalValue("system", "model.deploy.package.service.url", null);
			if (url == null) {
				throw new Exception("have no url info, check for environmental value: model.deploy.package.service.url.");
			}
			ComMessage<Map<String, String>, List<Map>> bridgeResponse = restTemplate.postForObject(url, request, request.getClass());
			response.setResponseObject(bridgeResponse.getResponseObject());
		}
		response.setEndTime(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
		return response;
	}

//	public void checkout(Map<String, String> params) throws Exception{
//		String userId = params.get("userId");
//		int versionNumber = Integer.parseInt(params.get("version"));
//		String integrationId = params.get("integrationId");
//
//
//		//권한검색
//		boolean hasModelAuth =
//		boolean hasDataSetAuth = true;
//
//
//		Map interfaceParam = new HashMap();
//		interfaceParam.put("integrationId", integrationId);
//		InterfaceModel interfaceModel = modelService.getInterfaceModel(interfaceParam);
//		String dataId = interfaceModel.getMid();
//		if(hasModelAuth) {
//			if(hasDataSetAuth){
//				interfaceModelVersionControlService.revert(versionNumber, dataId, userId, "checkout");
//			}else{
//				interfaceModelVersionControlService.revertInterfaceModel(versionNumber, dataId, userId, "checkout");
//			}
//		}
//	}


	/**
	 * <pre>
	 *     신한생명 형상관리 checkout 처리 프로세스
	 * </pre>
	 * @param interfaceModelId
	 * @param versionNumber
	 * @throws Exception
	 */
	@Transactional
	public void checkout(String integrationId, int versionNumber, String userId) throws Exception {

		final String modDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
		Map interfaceParam = new HashMap();
		interfaceParam.put("integrationId", integrationId);
		InterfaceModel interfaceModel = modelService.getInterfaceModel(interfaceParam);
		String interfaceModelId = interfaceModel.getMid();


		VersionData versionData = interfaceModelVersionControlService.retrieveIncludeDeleted(interfaceModelId, versionNumber);
//		VersionData versionData = interfaceModelVersionControlService.retrieve(interfaceModelId, versionNumber);
		if(versionData == null) throw new Exception(
				"Not found version data for checkout.(interfaceModelId:" + interfaceModelId +
						", integrationId:" + integrationId +
						", versionNumber:" + versionNumber + ")");


		ModelDeploymentVersions mdv = versionData.<ModelDeploymentVersions>getDataObject(ModelDeploymentVersions.class);
		//----------------------------------------------------------
		//revert dataSets
		//----------------------------------------------------------
		if(mdv.getDataSetVersions() != null)
		{
			List<Version> versions = mdv.getDataSetVersions();
			for (Version version : versions) {
				String dataSetId = version.getDataId();

				VersionData data =  versionControlService.retrieve(version);
				//DataSet dataSet = data.getDataObject(DataSet.class);

				//브릿지(형상관리)시스템에서 체크아웃 서비스 호출시 요청자 ID 를 넘겨주지 못하고 있으므로
				//인터페이스 모델 등록자의 ID 를 owner 로 보고 데이터셋 수정 권한을 체크한다.
				String ownerId = interfaceModel.getRegId();


				boolean authorized = Environments.enableCheckDataAuthority ?
						authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, ownerId, DataType.DataSet, dataSetId) : true;
				if(authorized) {


					versionControlService.<DataSet>revert(
							version.getVersion(),
							version.getDataType(),
							version.getDataId(),
							userId,
							"checkout",
							new UpdateHandler<DataSet>() {
								@Override
								public void update(DataSet dataSet, String userId, String date) throws Exception {
									dataSet.setModDate(modDate);
									dataSet.setModId(userId);
									dataSetService.modifySimpleDataSet(dataSet);
								}
							},
							DataSet.class
					);

				}
			}
		}
		//----------------------------------------------------------
		//revert maps
		//----------------------------------------------------------
		if(mdv.getDataMapVersions() != null){
			List<Version> versions = mdv.getDataMapVersions();
			for (Version version : versions) {
				String dataMapId = version.getDataId();
				String ownerId = interfaceModel.getRegId();

				boolean authorized = Environments.enableCheckDataAuthority ?
						authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, ownerId, DataType.DataMap, dataMapId) : true;
				if(authorized) {
					versionControlService.<DataMap>revert(
							version.getVersion(),
							version.getDataType(),
							version.getDataId(),
							userId,
							"checkout",
							new UpdateHandler<DataMap>() {
								@Override
								public void update(DataMap dataMap, String userId, String date) throws Exception {
									dataMap.setModDate(modDate);
									dataMap.setModId(userId);
									dataSetService.modifySimpleDataMap(dataMap);
								}
							},
							DataMap.class
					);
					//}
				}
			}
		}
		//----------------------------------------------------------
		//revert interfaceModel
		//----------------------------------------------------------
		//인터페이스 모델도 권한 관리를 한 것인가?
		//하지 않을 것 같아서 권한체킄는 주석처리? 한다.
		//String [] keys = {DataType.InterfaceModel.getCd(), interfaceModelId};
		//if(!authControlService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, userId, keys)){
			//revert model
			interfaceModelVersionControlService.revertInterfaceModel(versionNumber, interfaceModelId, userId, "checkout" );

			//add deploy history
			ModelDeployment md = new ModelDeployment();
			md.setDeployDate(Util.getFormatedDate());
			md.setInterfaceMid(interfaceModel.getMid());
			md.setVersion(versionData.getVersion());
			md.setMethod("9");//checkout method 9
			md.setResultCd("0");
			md.setResultMsg("checkout successfully");
			md.setDeployUser(userId);
			modelService.addModelDeploymentHistory(md); // 모델 배포 히스토리 남기기,
			// @Transaction 으로 몪여 있으므로 예외 발생시에는 롤백처리 되므로 히스토리를 쌓지 않는다.
			// 프론트 화면까지 예외 내용이 전달 되므로 어플리케이션 예외에 대한 히스토리까지 남길 필요는 없을 듯 하다.

			//update model deploy status
			if(SUCCESS_CD.equals(md.getResultCd()) && interfaceModel != null){
				modelService.updateModelDeployState(interfaceModel, ModelDeployState.CHECKOUT, md.getDeployDate(), md.getDeployUser()); //모델 배포상태 변경
			}

		//}
	}

}
