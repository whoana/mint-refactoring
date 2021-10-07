package pep.per.mint.openapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pep.per.mint.common.data.basic.*;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.RequirementModel;
import pep.per.mint.common.util.RequiredFields;
import pep.per.mint.common.util.Util;
import pep.per.mint.openapi.service.InterfaceModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <pre>
 *     OpenApi 서비스
 *     인터페이스모델 등록 수정
 * </pre>
 */
@Controller
@RequestMapping("/openapi")
public class InterfaceModelController {

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    @Autowired
    InterfaceModelService interfaceModelService;

    /**
     * <pre>
     *     전체 DataAccessRoles 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/data-access-roles", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<DataAccessRole>> getDataAccessRoleList(
            @RequestBody ComMessage<Map<String,String>, List<DataAccessRole>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<DataAccessRole> dataAccessRoles = interfaceModelService.getDataAccessRoleList();
        comMessage.setResponseObject(dataAccessRoles);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }


    /**
     * <pre>
     *     사용자 찾기
     *     파라메터맵에서 "userId" 값을 참고하여 사용자를 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/users/{userId}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, User> findUser(
            @PathVariable("userId") String userId,
            @RequestBody ComMessage<Map<String,String>, User> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
//        Map<String,String> params = comMessage.getRequestObject();
//        String userId = params.get("userId");
//        if(Util.isEmpty(userId)) throw new Exception("have no userId value, userId parameter required.");
        User user = interfaceModelService.findUser(userId);
        comMessage.setResponseObject(user);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     사용자 전체 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/users", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<User>> getUserList(
            @RequestBody ComMessage<Map<String,String>, List<User>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<User> users = interfaceModelService.getUserList();
        comMessage.setResponseObject(users);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }


    /**
     * <pre>
     *     Channel 찾기
     *     파라메터맵에서 "channelCd" 값을 참고하여 Channel을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/channels/{channelCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, Channel> findChannelByCd(
            @PathVariable("channelCd") String channelCd,
            @RequestBody ComMessage<Map<String,String>, Channel> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Channel channel = interfaceModelService.findChannelByCd(channelCd);
        comMessage.setResponseObject(channel);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     전체 Channel 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/channels", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<Channel>> getChannelList(
            @RequestBody ComMessage<Map<String,String>, List<Channel>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<Channel> channels = interfaceModelService.getChannelList();
        comMessage.setResponseObject(channels);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     Server 찾기
     *     파라메터맵에서 "serverCd" 값을 참고하여 Server을 조회한다.
     * </pre>
     * @param serverCd
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/servers/{serverCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, Server> findServerByCd(
            @PathVariable("serverCd") String serverCd,
            @RequestBody ComMessage<Map<String,String>, Server> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Server server = interfaceModelService.findServerByCd(serverCd);
        comMessage.setResponseObject(server);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     Server 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/servers", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<Server>> getServerList(
            @RequestBody ComMessage<Map<String,String>, List<Server>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<Server> servers = interfaceModelService.getServerList();
        comMessage.setResponseObject(servers);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     Business 찾기
     *     파라메터맵에서 "businessCd" 값을 참고하여 Business을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/businesses/{businessCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, Business> findBusinessByCd(
            @PathVariable("businessCd") String businessCd,
            @RequestBody ComMessage<Map<String,String>, Business> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Business business = interfaceModelService.findBusinessByCd(businessCd);
        comMessage.setResponseObject(business);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * 전체 Business 리스트 조회
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/businesses", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<Business>> getBusinessList(
            @RequestBody ComMessage<Map<String,String>, List<Business>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<Business> businesses = interfaceModelService.getBusinessList();
        comMessage.setResponseObject(businesses);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     pep.per.mint.common.data.basic.System 찾기
     *     파라메터맵에서 "systemCd" 값을 참고하여 System을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/systems/{systemCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, pep.per.mint.common.data.basic.System> findSystemByCd(
            @PathVariable("systemCd") String systemCd,
            @RequestBody ComMessage<Map<String,String>, pep.per.mint.common.data.basic.System> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        pep.per.mint.common.data.basic.System system = interfaceModelService.findSystemByCd(systemCd);
        comMessage.setResponseObject(system);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     pep.per.mint.common.data.basic.System 전체 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/systems", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<pep.per.mint.common.data.basic.System>> getSystemList(
            @RequestBody ComMessage<Map<String,String>, List<pep.per.mint.common.data.basic.System>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        List<pep.per.mint.common.data.basic.System> systems = interfaceModelService.getSystemList();
        comMessage.setResponseObject(systems);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }


    /**
     * <pre>
     *     Interface 찾기
     *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 Interface을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/interfaces", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, Interface> findInterface(
            @RequestBody ComMessage<Map<String,String>, Interface> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Map<String,String> params = comMessage.getRequestObject();
        RequiredFields.checkMapParams(params, "serviceId", "channelCd", "businessCd");

        Interface interfaze = interfaceModelService.findInterface(params);
        comMessage.setResponseObject(interfaze);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     interfaceId 찾기
     *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 Interface을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/interfaces/id", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, String> findInterfaceId(
            @RequestBody ComMessage<Map<String,String>, String> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Map<String,String> params = comMessage.getRequestObject();
        RequiredFields.checkMapParams(params, "serviceId", "channelCd", "businessCd");
        String interfaceId= interfaceModelService.findInterfaceId(params);
        comMessage.setResponseObject(interfaceId);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }


    /**
     * <pre>
     *     DataSet 찾기
     *     파라메터맵에서 "dataSetCd" 값을 참고하여 DataSet을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/datasets/{dataSetCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, DataSet> findDataSetByCd(
            @PathVariable("dataSetCd") String dataSetCd,
            @RequestBody ComMessage<Map<String,String>, DataSet> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        comMessage.setResponseObject(interfaceModelService.findDataSetByCd(dataSetCd));
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     DataSet 등록
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/datasets", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<DataSet, DataSet> createDataSet(
            @RequestBody ComMessage<DataSet, DataSet> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    )throws Exception {
        DataSet dataSet = comMessage.getRequestObject();
        interfaceModelService.createDataSet(dataSet);
        comMessage.setResponseObject(dataSet);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     DataSet 수정
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/datasets", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<DataSet, DataSet> modifyDataSet(
            @RequestBody ComMessage<DataSet, DataSet> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    )throws Exception {
        DataSet dataSet = comMessage.getRequestObject();
        interfaceModelService.modifyDataSet(dataSet);
        comMessage.setResponseObject(dataSet);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     RequirementModel 찾기
     *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 RequirementModel을 조회한다.
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/requirement-models", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, RequirementModel> findRequirementModel(
            @RequestBody ComMessage<Map<String,String>, RequirementModel> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {
        Map<String,String> params = comMessage.getRequestObject();
        RequiredFields.checkMapParams(params, "serviceId", "channelCd", "businessCd");
        comMessage.setResponseObject(interfaceModelService.findRequirementModel(params));
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     RequirementModel 신규 등록
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/requirement-models", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<RequirementModel, RequirementModel> createRequirementModel(
            @RequestBody ComMessage<RequirementModel, RequirementModel> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    )throws Exception {
        RequirementModel requirementModel = comMessage.getRequestObject();
        interfaceModelService.createRequirementModel(requirementModel);
        comMessage.setResponseObject(requirementModel);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    /**
     * <pre>
     *     RequirementModel 수정
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/requirement-models", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<RequirementModel, RequirementModel> modifyRequirementModel(
            @RequestBody ComMessage<RequirementModel, RequirementModel> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    )throws Exception {
        RequirementModel requirementModel = comMessage.getRequestObject();
        interfaceModelService.modifyRequirementModel(requirementModel);
        comMessage.setResponseObject(requirementModel);
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }




}
