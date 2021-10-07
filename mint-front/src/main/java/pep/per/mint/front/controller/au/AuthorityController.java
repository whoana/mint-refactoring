package pep.per.mint.front.controller.au;

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
import pep.per.mint.common.data.basic.authority.AuthorityRequest;
import pep.per.mint.common.data.basic.authority.AuthorityUserGroup;
import pep.per.mint.common.data.basic.authority.Category;
import pep.per.mint.common.data.basic.authority.DataType;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.exception.authority.NotFoundAuthorityException;
import pep.per.mint.common.exception.authority.NotFoundPolicyException;

import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.au.AuthorityRegisterBatchService;
import pep.per.mint.database.service.au.AuthorityService;
import pep.per.mint.front.exception.ControllerException;
import pep.per.mint.front.util.MessageSourceUtil;

//import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 통합권한 AOP
 * @author whoana
 * @since 202108
 */
@Controller
@RequestMapping("/au")
public class AuthorityController {



    private static final Logger logger = LoggerFactory.getLogger(AuthorityController.class);

    /**
     * The Message source.
     * 비지니스처리중 프론트까지 전달할 메시지들을 참조할 수 있는 다국어지원용 번들 객체
     */
    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    /**
     * 서블리컨텍스트 관련정보 참조를 위한 객체
     */
//    @Autowired
//    private ServletContext servletContext;

    @Autowired
    AuthorityService authorityService;


    /**
     * <pre>
     *     SERVICE ID : REST-R01-AU-01-01
     *     권한 소유여부 조회
     *      [ComMessage.requestObject의  AuthorityRequest 유형 설명]
     *      {
     *          "objectType" : "AuthorityRequest",
     *          "categoryCd" : "DATA", //카테고리 코드 : DATA, SERVICE, PROGRAM, MENU
     *          "itemType"   : "2",    //권한 아이템 유형 :  0:CREATE, 1:READ, 2:UPDATE, 3:DELETE, 4:EXECUTE, 5:ACCESS, 6:DEPLOY
     *          "ownerId"    : "shl",  //userId
     *          "dataType"   : "1",    //데이터 유형 : 0 : Requirement, 1: Interface, 2: DataSet, 3: DataMap, 4 : InterfaceModel
     *          "dataId":"F@00000440", //데이터 ID    : 데이터 유형에 따른 테이블 PK 값
     *          "authorized" : false   //소유여부
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<AuthorityRequest, AuthorityRequest> haveAuthority(
            HttpSession httpSession,
            @RequestBody ComMessage<AuthorityRequest, AuthorityRequest> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {


        String errorCd = "";
        String errorMsg = "";

        AuthorityRequest authorityRequest = comMessage.getRequestObject();

        logger.debug("authorityRequest:" + Util.toJSONPrettyString(authorityRequest));

        try {
            boolean authorized = authorityService.haveAuthority(
	                    authorityRequest.getCategory(),
	                    authorityRequest.getItem(),
	                    comMessage.getUserId(),
	                    authorityService.getDataType(authorityRequest.getDataType()),
	                    authorityRequest.getDataId()
		        );

            authorityRequest.setAuthorized(authorized);

        }catch(NotFoundAuthorityException e){ // 권한이 등록되어있지 않을 경우
            authorityRequest.setAuthorized(false);
            logger.debug("의도된 예외처리", e);
        }catch (NotFoundPolicyException e){// 정책이 등록되어 있지 않을 경우
            throw e;
        }catch (AuthorityException e){ //기타 권한관련 예외 발생
            throw e;
        }

        comMessage.setResponseObject(authorityRequest);
        comMessage.setRequestObject(null);
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
        Object [] errorMsgParams = {};
        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);

        return comMessage;

    }


    /**
     * <pre>
     *     SERVICE ID : REST-U01-AU-01-02
     *                  통합권한옵션 값 설정 변경(웹서버 기동시에만 유효, 재기동시 초기설정으로 되될아감.)
     *  [ComMessage.requestObject 설명]
     *      {
     *          "enableRegisterAuthority" : true, //권한자동등록사용구분 옵션 설정값
     *          "enableCheckDataAuthority": true  //데이터권한체크사용구분 옵션 설정값
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/options", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, Map<String, Object>> changeAuthorityOptions(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, Map<String, Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {

        String errorCd = "";
        String errorMsg = "";

        if(!authorityService.isSuperUser(comMessage.getUserId())) {
        	errorCd = "9";
        	errorMsg = "Can't execute the batch for registering authority because you are not a super user.";
        }else {
	        Map<String, Object> params = comMessage.getRequestObject();
	        if(params != null){
	            if(params.get("enableRegisterAuthority") != null) {
	                boolean enableRegisterAuthority = (Boolean)params.get("enableRegisterAuthority");
	                Environments.enableRegisterAuthority = enableRegisterAuthority;
	                logger.info("changed enableRegisterAuthority option value : " + enableRegisterAuthority);
	            }
	            if(params.get("enableCheckDataAuthority") != null) {
	                boolean enableCheckDataAuthority = (Boolean) params.get("enableCheckDataAuthority");
	                Environments.enableCheckDataAuthority = enableCheckDataAuthority;
	                logger.info("changed enableCheckDataAuthority option value : " + enableCheckDataAuthority);
	            }
	        }else{
	            logger.info("have no authority options value to change");
	        }
	        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
	        errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
	        Object [] errorMsgParams = {};
	        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);

        }

        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);

        return comMessage;

    }


    //@Autowired
    AuthorityRegisterBatchService authorityRegisterBatchService;

    /**
     * <pre>
     *  SERVICE ID : REST-C01-AU-02-01
     *  통합권한등록일괄배치실행 호출, super user 인 경우에만 실행 허용
     *  [ComMessage.requestObject 설명]
     *      {
     *          "commitCount" : 500,  //배치 트랜잭션 커밋 건수 (default : 500)
     *          "async"       : false //동기 비동기 실행 구분 : 동기 : true, 비동기 : false (default : false)
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/batchjobs/registers", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, Map<String, Object>> registerAuthorityBatch(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, Map<String, Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {


        String errorCd = "";
        String errorMsg = "";

        if(!authorityService.isSuperUser(comMessage.getUserId())) {
        	errorCd = "9";
        	errorMsg = "Can't execute the batch for registering authority because you are not a super user.";
        }else {

        	int DEFAULT_COMMIT_COUNT = 500;
	        Map<String, Object> params = comMessage.getRequestObject();
	        boolean async =  params != null && params.get("async") != null ? (Boolean)params.get("async") : false;
	        int commitCount = params != null && params.get("commitCount") != null ? (Integer)params.get("commitCount") : DEFAULT_COMMIT_COUNT;
	        if(!async) {
	            authorityRegisterBatchService.registerAuthorityBatch(authorityService.getCategory(Category.DATA.getCd()), commitCount);
	        }else {
	            authorityRegisterBatchService.registerAuthorityBatchAsync(authorityService.getCategory(Category.DATA.getCd()), commitCount);
	        }

	        errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
	        Object [] errorMsgParams = {};
	        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);
        }

        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);

        return comMessage;

    }

    /**
     * <pre>
     *     SERVICE ID : REST-D01-AU-03-01
     *     캐시된 권한 삭제 , super user 인 경우에만 실행 허용
     *      [ComMessage.requestObject의  Map parmas key, value]
     *      {
     *      	"option"     : "one",      //캐시 삭제 대상 옵션 : all : 모든 권한 cache 값 삭제 , one(all이 아닌 값) : 이하 파라메터로 구성한 키값에 해당하는 cache 값 삭제
     *      							   //*키구성 방법 : String key = Util.join(categoryCd, "@", itemType, "@", comMessage.getUserId(), "@", dataType, "@", dataId);
     *          "categoryCd" : "DATA",     //카테고리 코드 : DATA, SERVICE, PROGRAM, MENU
     *          "itemType"   : "2",        //권한 아이템 유형 :  0:CREATE, 1:READ, 2:UPDATE, 3:DELETE, 4:EXECUTE, 5:ACCESS, 6:DEPLOY
     *          "dataType"   : "1",        //데이터 유형 : 0 : Requirement, 1: Interface, 2: DataSet, 3: DataMap, 4 : InterfaceModel
     *          "dataId"     :"F@00000440" //데이터 ID    : 데이터 유형에 따른 테이블 PK 값
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/caches", params = "method=DELETE", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, Map<String, Object>> evictAuthorityCaches(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, Map<String, Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {


        String errorCd = "";
        String errorMsg = "";

        if(!authorityService.isSuperUser(comMessage.getUserId())) {
        	errorCd = "9";
        	errorMsg = "Can't evict cache because you are not a super user.";
        }else {


	        Map<String, Object> params = comMessage.getRequestObject();

	        if(params != null){
	        	String option = params.get("option") != null ? (String)params.get("option") : "none";
	        	if("all".equalsIgnoreCase(option)) {
	        		authorityService.clearAllCachedAuthorities();
	    			errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
			        Object [] errorMsgParams = {};
			        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);

	        	}else {
	        		String categoryCd = params.get("categoryCd") != null ? (String)params.get("categoryCd") : "none";
	        		String itemType   = params.get("itemType") != null ? (String)params.get("itemType") : "none";
	        		String ownerId    = params.get("ownerId") != null ? (String)params.get("ownerId") : "none";
	        		String dataType   = params.get("dataType") != null ? (String)params.get("dataType") : "none";
	        		String dataId     = params.get("dataId") != null ? (String)params.get("dataId") : "none";
	        		String key = Util.join(categoryCd, "@", itemType, "@", ownerId, "@", dataType, "@", dataId);
	        		Boolean value = (Boolean)authorityService.getAuthorityValueInCache(key);
	        		if(value != null) {
	        			authorityService.clearCachedAuthority(key);
	        			errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
	    		        Object [] errorMsgParams = {};
	    		        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);

	        		}else {
	        			errorCd = "9";
	        			errorMsg = Util.join("not found authority value to delete for key:", key);
	        		}
	        	}
	        }else{
	        	errorCd = "9";
	        	errorMsg = "have no any parameters for evicting authority cache.";
	        }
        }

        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);
        return comMessage;

    }


    /**
     * <pre>
     *     SERVICE ID : REST-R01-AU-03-02
     *     캐싱된 권한 소유여부 조회(DB 가 아닌 Cache 로 부터 존재 유무 조회), super user 인 경우에만 실행 허용
     *      [ComMessage.requestObject의  Map parmas key, value]
     *      {
     *          "categoryCd" : "DATA",     //카테고리 코드    : DATA, SERVICE, PROGRAM, MENU
     *          "itemType"   : "2",        //권한 아이템 유형 :  0:CREATE, 1:READ, 2:UPDATE, 3:DELETE, 4:EXECUTE, 5:ACCESS, 6:DEPLOY
     *          "dataType"   : "1",        //데이터 유형     : 0 : Requirement, 1: Interface, 2: DataSet, 3: DataMap, 4 : InterfaceModel
     *          "dataId"     :"F@00000440" //데이터 ID       : 데이터 유형에 따른 테이블 PK 값
     *      }
     *
     *      [ComMessage.responseObject의  Map parmas key, value] cache내에 값이 존재할때만 응답 값이 존재한다.
     *      {
     *          "cached"     : true,     //cache에 해당 키값 존재 유무 cached=false 이면 아래 authorized 항목은 responseObject에서 제외됨.
     *          "authorized" : true      //권한 유무
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/caches", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, Map<String, Object>> getAuthorityInCache(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, Map<String, Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {


        String errorCd = "";
        String errorMsg = "";

        if(!authorityService.isSuperUser(comMessage.getUserId())) {
        	errorCd = "9";
        	errorMsg = "Can't access cache because you are not a super user.";
        }else {


	        Map<String, Object> params = comMessage.getRequestObject();

	        if(params != null){

        		String categoryCd = params.get("categoryCd") != null ? (String)params.get("categoryCd") : "none";
        		String itemType   = params.get("itemType") != null ? (String)params.get("itemType") : "none";
        		String ownerId    = params.get("ownerId") != null ? (String)params.get("ownerId") : "none";
        		String dataType   = params.get("dataType") != null ? (String)params.get("dataType") : "none";
        		String dataId     = params.get("dataId") != null ? (String)params.get("dataId") : "none";
        		String key = Util.join(categoryCd, "@", itemType, "@", ownerId, "@", dataType, "@", dataId);
        		Boolean value = (Boolean)authorityService.getAuthorityValueInCache(key);
        		if(value != null) {
        			params.put("cached", true);
        			params.put("authorized", value);
        			errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
    		        Object [] errorMsgParams = {};
    		        errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", errorMsgParams, locale);

        		}else {
        			params.put("cached", false);
        			errorCd = "9";
        			errorMsg = Util.join("not found authority value for key:", key);
        		}
        		comMessage.setResponseObject(params);
	        }else{
	        	errorCd = "9";
	        	errorMsg = "have no any parameters for get authority in the cache.";
	        }
        }
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);
        return comMessage;

    }



    /**
     * <pre>
     * 	권한 그룹 조회
	 *	SERVICE ID :  REST-R01-AU-01-02
	 *  comMessage.userId 값을 기준으로 권한 그룹을 조회한다. 권한 그룹값은 2개 이상일 수 있다.
	 *  조회된 권한그룹 리스트 중 선택된 groupId(AuthorityUserGroup.groupId) 값은 Interface, DataSet, DataMap 등록 서비스 호출 시 ComMessage.groupId 파라메터를 통해 전달한다.
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/groups", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, List<AuthorityUserGroup>> getAuthorityGroups(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, List<AuthorityUserGroup>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {

        String errorCd = "";
        String errorMsg = "";
        List<AuthorityUserGroup> groups = authorityService.getUserGroups(comMessage.getUserId());
        if(!Util.isEmpty(groups)) {
        	errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
			errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
			comMessage.setResponseObject(groups);
        }else {
        	errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
			errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
        }
        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);
        return comMessage;

    }



    /**
     * <pre>
     * 	권한오브젝트 소속 그룹 조회
	 *	SERVICE ID :  REST-R01-AU-01-03
 	 *  [ComMessage.requestObject의  Map parmas key, value]
     *      {
     *          "categoryCd" : "DATA",     //카테고리 코드    : DATA, SERVICE, PROGRAM, MENU
     *          "dataType"   : "1",        //데이터 유형     : 0 : Requirement, 1: Interface, 2: DataSet, 3: DataMap, 4 : InterfaceModel
     *          * 인터페이스등록수정화면은 dataType "1" 로 던지세요.
     *          "dataId"     :"F@00000440" //데이터 ID       : 데이터 유형에 따른 테이블 PK 값
     *      }
     * </pre>
     * @param httpSession
     * @param comMessage
     * @param locale
     * @param request
     * @return
     * @throws Exception
     * @throws ControllerException
     */
    @RequestMapping(value = "/authorities/objects/groups", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String, Object>, AuthorityUserGroup> getAuthorityObjectGroup(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String, Object>, AuthorityUserGroup> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {

        String errorCd = "";
        String errorMsg = "";


        Map<String, Object> params = comMessage.getRequestObject();
        String dataId	  = null;
        String categoryCd = null;
        String dataTypeCd = null;



        if(params != null){
        	dataId = params.get("dataId") != null ? (String)params.get("dataId") : null;
        	categoryCd = params.get("categoryCd") != null ? (String)params.get("categoryCd") : null;
        	dataTypeCd = params.get("dataType") != null ? (String)params.get("dataType") : null;

        	if(Util.isEmpty(dataId) || Util.isEmpty(categoryCd) || Util.isEmpty(dataTypeCd)) {
        		errorCd = "9";
            	errorMsg = "have no some parameters among dataId, categoryCd, dataTypeCd ";
        	}else{

	        	Category category = authorityService.getCategory(categoryCd);
	        	DataType dataType = authorityService.getDataType(dataTypeCd);
	        	AuthorityUserGroup group = authorityService.getAuthorityObjectGroup(category, dataType, dataId);

	        	if(group != null) {
	        		errorCd = MessageSourceUtil.getErrorCd(messageSource, "success.cd.ok", locale);
	        		errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "success.msg.ok", null, locale);
	        		comMessage.setResponseObject(group);
	        	}else {
	        		errorCd = MessageSourceUtil.getErrorCd(messageSource, "error.cd.retrieve.none", locale);
	        		errorMsg = MessageSourceUtil.getErrorMsg(messageSource, "error.msg.retrieve.none", null, locale);
	        	}
        	}

        }else {
        	errorCd = "9";
        	errorMsg = "have no any parameters";
        }

        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(errorCd);
        comMessage.setErrorMsg(errorMsg);

        return comMessage;

    }






}
