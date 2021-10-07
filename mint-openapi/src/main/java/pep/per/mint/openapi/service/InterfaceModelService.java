package pep.per.mint.openapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pep.per.mint.common.data.basic.*;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.*;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.DataSetService;
import pep.per.mint.database.service.an.RequirementService;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.oa.OpenApiService;
import pep.per.mint.database.service.rt.ModelService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 인터페이스 모델, 데이터셋 생성을 위한 openapi 서비스
 * </pre>
 * @since 202104
 */
@Service
public class InterfaceModelService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    OpenApiService openApiService;

    @Autowired
    RequirementService requirementService;

    @Autowired
    ModelService modelService;

    @Autowired
    DataSetService dataSetService;

    public List<User> getUserList() throws Exception {
        return commonService.getUserList(new HashMap());
    }

    public List<DataAccessRole> getDataAccessRoleList() throws Exception {
        return commonService.getDataAccessRoleList();
    }

    public List<Business> getBusinessList() throws Exception {
        return commonService.getBusinessCdList();
    }

    public List<CommonCode> getCommonCodeList(String level1, String level2) throws Exception {
        return commonService.getCommonCodeList(level1, level2);
    }

    public List<Channel> getChannelList() throws Exception {
        return commonService.getChannelList();
    }

    public List<Server> getServerList() throws Exception {
        return commonService.getServerCdList();
    }

    public List<pep.per.mint.common.data.basic.System> getSystemList() throws Exception {
        return commonService.getSystemCdListAll();
    }

    public Business findBusinessByCd(String cd) throws Exception {
        return commonService.getBusinessByCd(cd);
    }

    public DataAccessRole findDataAccessRole(String cd) throws Exception {
        return commonService.getDataAccessRoleByCd(cd);
    }

    public pep.per.mint.common.data.basic.System findSystemByCd(String cd) throws Exception {
        return commonService.getSystemByCd(cd);
    }

    public Server findServerByCd(String cd) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serverCd", cd);
        List<Server> servers = commonService.getServerList(params);
        return Util.isEmpty(servers) ? null : servers.get(0);
    }

    public Map<String, AppModelAttributeId> findAppModelAttributeIds(String appType) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("appType", appType);
        List<AppModelAttributeId> list = modelService.getAppModelAttributeIdList(params);
        Map<String, AppModelAttributeId> map = new HashMap<String, AppModelAttributeId>();
        for (AppModelAttributeId aid : list) {
            map.put(aid.getCd(), aid);
        }
        return map;
    }

    public Channel findChannelByCd(String cd) throws Exception {
        return commonService.getChannelByCd(cd);
    }

    public User findUser(String userId) throws Exception {
        return commonService.getUser(userId);
    }

    public List<String> findInterfaceIdList(Map params) throws Exception {
        return openApiService.findInterfaceId(params);
    }



    public String findInterfaceId(Map params) throws Exception {
        String interfaceId = null;
        List<String> interfaceIds = openApiService.findInterfaceId(params);
        if(Util.isEmpty(interfaceIds)){
        }else if(interfaceIds.size() > 1){
            throw new Exception("The size of interface list is more than 2.");
        }else {
            interfaceId = interfaceIds.get(0);
        }
        return interfaceId;
    }

    public Interface findInterface(Map params) throws Exception {
        String interfaceId = findInterfaceId(params);
        return Util.isEmpty(interfaceId) ? null : commonService.getInterfaceDetail(interfaceId);
    }

    public RequirementModel findRequirementModel(Map params) throws Exception {
        String interfaceId = findInterfaceId(params);
        if(Util.isEmpty(interfaceId)) return null;

        String requirementId = requirementService.getRequirementIdByInterfaceId(findInterfaceId(params));
        if(Util.isEmpty(requirementId)) return null;

        Requirement requirement = requirementService.getRequirementDetail(requirementId);
        if(requirement == null) return null;

        RequirementModel requirementModel = new RequirementModel();
        requirementModel.setRequirement(requirement);

        InterfaceModel interfaceModel = modelService.getInterfaceModelByInterfaceId(interfaceId);
        if(interfaceModel == null ) return null;

        requirementModel.setInterfaceModel(interfaceModel);
        List<AppModel> appModels = interfaceModel.getAppModels();
        if(!Util.isEmpty(appModels) && appModels.size() > 0 ) {
            Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();
            for (AppModel appModel : appModels) {
                List<MessageModel> msgs = appModel.getMsgs();
                for (MessageModel msg : msgs) {
                    String dataSetId = msg.getDataSetId();
                    DataSet dataSet = dataSetService.getSimpleDataSet(dataSetId);
                    dataSetMap.put(dataSetId, dataSet);
                }
            }
            requirementModel.setDataSetMap(dataSetMap);
        }
        return requirementModel;
    }

    @Transactional
    public void createRequirementModel(RequirementModel requirementModel) throws Exception {
        int res = requirementService.createRequirementModel(requirementModel);
    }

    @Transactional
    public void modifyRequirementModel(RequirementModel requirementModel) throws Exception {
        int res = requirementService.updateRequirementModel(requirementModel);
    }

    @Transactional
    public void createDataSet(DataSet dataSet) throws Exception {
        dataSetService.createSimpleDataSet(dataSet);
    }

    @Transactional
    public void modifyDataSet(DataSet dataSet) throws Exception {
        dataSetService.modifySimpleDataSet(dataSet);
    }

    public DataSet findDataSetByCd(String cd) throws Exception {
        List<String> dataSetIds = openApiService.findDataSetId(cd);
        if(Util.isEmpty(dataSetIds)){
            return null;
        }else if(dataSetIds.size() > 1){
            throw new Exception("The size of datasets as cd (" + cd + ") is more than 2.");
        }else {
            return dataSetService.getSimpleDataSet(dataSetIds.get(0));
        }
    }

}
