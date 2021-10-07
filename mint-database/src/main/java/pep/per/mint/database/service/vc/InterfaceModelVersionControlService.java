package pep.per.mint.database.service.vc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pep.per.mint.common.data.basic.Requirement;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;

import pep.per.mint.common.data.basic.runtime.*;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.data.basic.version.VersionData;
import pep.per.mint.common.data.basic.version.VersionDataType;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.DataSetService;
import pep.per.mint.database.service.an.RequirementService;
import pep.per.mint.database.service.rt.ModelService;

import java.util.*;

@Service
public class InterfaceModelVersionControlService {

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    ModelService modelService;

    @Autowired
    RequirementService requirementService;

    @Autowired
    DataSetService dataSetService;

    /**
     * <pre>
     * <code>
     * 인터페이스모델 정보를 로컬 레파지토리에 commit 한다.
     * 같이 커밋되는 내역 :
     *  InterfaceModel
     *  Requirement
     *  List<DataSet>
     *  List<DataMap>
     *  ModelDeploymentVersions
     *
     * example)
     *  InterfaceModelVersionControlService vcs;
     *  InterfaceModel interfaceModel;
     *  vcs.commit(interfaceModel, "shl", "commit 8");
     * </code>
     * </pre>
     * @param interfaceModel 커밋할 인터페이스모델
     * @param commitUserId 커밋터 ID
     * @param msg 커밋 메시지
     * @return
     * @throws Exception
     */
    public Version commit(InterfaceModel interfaceModel , String commitUserId, String msg) throws Exception {

        if(interfaceModel == null) throw new Exception("Have no the InterfaceModel contents to commit");


        ModelDeploymentVersions mdv = new ModelDeploymentVersions();

        String commitDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

        List<AppModel> apps = interfaceModel.getAppModels();
        Set<String> dataSets = new HashSet<String>();
        Set<String> dataMaps = new HashSet<String>();
        if(apps != null && apps.size() > 0) {
            for (AppModel app : apps) {
                List<MessageModel> layouts = app.getMsgs();
                if(!Util.isEmpty(layouts)) {
                    for (MessageModel layout : layouts) {
                        if(!dataSets.contains(layout.getDataSetId())){
                            dataSets.add(layout.getDataSetId());
                        }
                    }
                }

                List<MappingModel> mappings = app.getMappings();
                if(!Util.isEmpty(mappings)) {
                    for (MappingModel mapping : mappings) {
                        if(!dataMaps.contains(mapping.getMapId())){
                            dataMaps.add(mapping.getMapId());
                        }
                    }
                }
            }
        }


        //--------------------------------------------------------------------------------------------
        // 3. DataSet Version List
        //--------------------------------------------------------------------------------------------
        if(!dataSets.isEmpty()){
            List<Version> dataSetVersions = new ArrayList<Version>();

            for (String dataSetId : dataSets) {

                VersionData versionData = versionControlService.retrieveHead(VersionDataType.DATASET.getCd(), dataSetId);

                if(versionData == null) {
                    DataSet dataSet = dataSetService.getSimpleDataSet(dataSetId);
                    versionControlService.commit(VersionDataType.DATASET.getCd(), dataSetId, "commit(1)", dataSet.getName1(), commitUserId, dataSet);
                    versionData = versionControlService.retrieveHead(VersionDataType.DATASET.getCd(), dataSetId);
                    //throw new Exception("Not found DataSet Head Version(dataSetId:"+ dataSetId +")");
                }

                Version version = new Version();
                version.setVersion(versionData.getVersion());
                version.setVersionNumber(versionData.getVersionNumber())
                ;
                version.setDataType(VersionDataType.DATASET.getCd());
                version.setDataId(dataSetId);

                dataSetVersions.add(version);
            }
            mdv.setDataSetVersions(dataSetVersions);
        }

        //--------------------------------------------------------------------------------------------
        // 4. DataMap Version List
        //--------------------------------------------------------------------------------------------
        if(!dataMaps.isEmpty()){
            List<Version> dataMapVersions = new ArrayList<Version>();
            for (String dataMapId : dataMaps) {

                VersionData versionData = versionControlService.retrieveHead(VersionDataType.DATAMAP.getCd(), dataMapId);
                if(versionData == null) {

                    Map<String, Object> res = dataSetService.getSimpleDataMap(dataMapId, "N");
                    if (res != null && res.size() > 0) {
                        DataMap map = (DataMap) res.get("mapData");
                        versionControlService.commit(VersionDataType.DATAMAP.getCd(), dataMapId, "commit(1)", map.getName(), commitUserId, map);
                        versionData = versionControlService.retrieveHead(VersionDataType.DATAMAP.getCd(), dataMapId);
                    }
                    //throw new Exception("Not found DataMap Head Version(dataMapId:"+ dataMapId +")");
                }

                Version version = new Version();
                version.setVersion(versionData.getVersion());
                version.setVersionNumber(versionData.getVersionNumber());
                version.setDataType(VersionDataType.DATAMAP.getCd());
                version.setDataId(dataMapId);

                dataMapVersions.add(version);
            }
            mdv.setDataMapVersions(dataMapVersions);
        }



        //--------------------------------------------------------------------------------------------
        // 2. Requirement commit
        //--------------------------------------------------------------------------------------------
        {
            String requirementId = requirementService.getRequirementIdByInterfaceId(interfaceModel.getInterfaceId());
            Requirement requirement = requirementService.getRequirementDetail(requirementId);
            if(requirement == null) throw new Exception("Have no the Requirement contents to commit");

            VersionData versionData = new VersionData();
            versionData.setDataType(VersionDataType.REQUIREMENT.getCd());
            versionData.setDataId(requirement.getRequirementId());
            versionData.setVersion(UUID.randomUUID().toString());
            versionData.setCommitUserId(commitUserId);
            versionData.setCommitDate(commitDate);
            versionData.setDataObject(requirement);
            versionData.setHead(true);
            versionData.setMsg(msg);
            versionData.setTag("#" + requirement.getRequirementNm());
            versionControlService.commit(versionData);

            Version version = new Version();
            version.setVersion(versionData.getVersion());
            version.setVersionNumber(versionData.getVersionNumber());
            version.setDataType(VersionDataType.REQUIREMENT.getCd());
            version.setDataId(requirement.getRequirementId());

            mdv.setRequirementVersion(version);
        }


        //--------------------------------------------------------------------------------------------
        // 1. InterfaceModel commit
        //--------------------------------------------------------------------------------------------
        {

            VersionData versionData = new VersionData();
            versionData.setDataType(VersionDataType.INTERFACE_MODEL.getCd());
            versionData.setDataId(interfaceModel.getMid());
            versionData.setVersion(UUID.randomUUID().toString());
            versionData.setCommitUserId(commitUserId);
            versionData.setCommitDate(commitDate);
            versionData.setDataObject(interfaceModel);
            versionData.setHead(true);
            versionData.setMsg(msg);
            versionData.setTag("#" + interfaceModel.getName());
            versionControlService.commit(versionData);

            interfaceModel.setVersion(Integer.toString(versionData.getVersionNumber())); // 커밋 버전넘버 세팅

            Version version = new Version();
            version.setVersion(versionData.getVersion());
            version.setVersionNumber(versionData.getVersionNumber());
            version.setDataType(VersionDataType.INTERFACE_MODEL.getCd());
            version.setDataId(interfaceModel.getMid());


            mdv.setName(interfaceModel.getName());
            mdv.setInterfaceModelVersion(version);
        }


        //--------------------------------------------------------------------------------------------
        // 5. ModelDeploymentVersions commit
        //--------------------------------------------------------------------------------------------
        {
            VersionData versionData = new VersionData();
            versionData.setDataType(VersionDataType.INTERFACE_MODEL_VERSION.getCd());
            versionData.setDataId(interfaceModel.getMid());
            versionData.setVersion(UUID.randomUUID().toString());
            versionData.setCommitUserId(commitUserId);
            versionData.setCommitDate(commitDate);
            versionData.setDataObject(mdv);
            versionData.setHead(true);
            versionData.setMsg(msg);
            versionData.setTag("#" + mdv.getName());
            versionControlService.commit(versionData);
        }
        return mdv.getInterfaceModelVersion();
    }

    /**
     * <pre>
     * <code>
     * 인터페이스모델 정보를 로컬 레파지토리에 commit 한다.
     * 같이 커밋되는 내역 :
     *  InterfaceModel
     *  Requirement
     *  List<DataSet>
     *  List<DataMap>
     *  ModelDeploymentVersions
     *
     * example)
     *  InterfaceModelVersionControlService vcs;
     *  vcs.commit("156", "shl", "commit 8");
     * </code>
     * </pre>
     * @param interfaceModelId 커밋할 인터페이스모델 ID
     * @param commitUserId 커밋터 ID
     * @param msg 커밋 메시지
     * @throws Exception
     */
    @Transactional
    public Version commit(String interfaceModelId, String commitUserId, String msg) throws Exception {
        return commit(modelService.getInterfaceModel(interfaceModelId), commitUserId, msg);
    }

    /**
     *
     * @param dataId
     * @param versionNumber
     * @return
     * @throws Exception
     */
    public VersionData retrieve(String dataId, int versionNumber) throws Exception {
        return versionControlService.retrieve(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId, versionNumber);
    }

    public VersionData retrieve(String dataId, String version) throws Exception {
        return versionControlService.retrieve(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId, version);
    }

    public VersionData retrieveIncludeDeleted(String dataId, int versionNumber) throws Exception {
        return versionControlService.retrieve(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId, versionNumber, false);
    }

    public VersionData retrieveIncludeDeleted(String dataId, String version) throws Exception {
        return versionControlService.retrieve(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId, version,false);
    }

    public VersionData retrieveHead(String dataId) throws Exception {
        return versionControlService.retrieveHead(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId);
    }

    public List<VersionData> retrieveList(String dataId) throws Exception {
        return retrieveList(dataId, true);
    }

    public List<VersionData> retrieveList(String dataId, boolean includeDelYnNo) throws Exception {
        return versionControlService.retrieveList(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId, false);
    }

    @Transactional
    public void updateModelContents(ModelDeploymentVersions modelDeploymentVersions, String msg, String userId, String date) throws Exception{
        updateContents(true, true, false, false, modelDeploymentVersions, msg, userId, date);
    }

    @Transactional
    public void updateAllContents(ModelDeploymentVersions modelDeploymentVersions, String msg, String userId, String date) throws Exception{
        updateContents(true, true, true, true, modelDeploymentVersions, msg, userId, date);
    }

    @Transactional
    public void updateContents(
            boolean updateInterfaceModel,
            boolean updateRequirement,
            boolean updateDataSet,
            boolean updateDataMap,
            ModelDeploymentVersions modelDeploymentVersions,
            String msg,
            String userId,
            String date) throws Exception{
        final String modDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
        //------------------------------------------------------------------------------------
        // revert local repository and update origin table data InterfaceMode
        //------------------------------------------------------------------------------------
        if(updateInterfaceModel){
            Version version = modelDeploymentVersions.getInterfaceModelVersion();
            versionControlService.<InterfaceModel>revert(
                    version.getVersion(),
                    version.getDataType(),
                    version.getDataId(),
                    userId,
                    msg,
                    new UpdateHandler<InterfaceModel>() {
                        @Override
                        public void update(InterfaceModel interfaceModel, String userId, String date) throws Exception {
                            modelService.updateInterfaceModel(interfaceModel, modDate, userId, true);
                        }
                    },
                    InterfaceModel.class
            );
        }

        //------------------------------------------------------------------------------------
        // revert local repository and update origin table data Requirement
        //------------------------------------------------------------------------------------
        if(updateRequirement){
            Version version = modelDeploymentVersions.getRequirementVersion();
            versionControlService.<Requirement>revert(
                    version.getVersion(),
                    version.getDataType(),
                    version.getDataId(),
                    userId,
                    msg,
                    new UpdateHandler<Requirement>() {
                        @Override
                        public void update(Requirement requirement, String userId, String date) throws Exception {
                            requirement.setModId(userId);
                            requirement.setModDate(modDate);
                            requirementService.updateRequirement(requirement);
                        }
                    },
                    Requirement.class
            );
        }

        //------------------------------------------------------------------------------------
        // revert local repository and update origin table data DataSet
        //------------------------------------------------------------------------------------
        if(updateDataSet) {
            List<Version> dataSetVersions = modelDeploymentVersions.getDataSetVersions();
            for (Version version : dataSetVersions) {
                versionControlService.<DataSet>revert(
                        version.getVersion(),
                        version.getDataType(),
                        version.getDataId(),
                        userId,
                        msg,
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
        //------------------------------------------------------------------------------------
        // revert local repository and update origin table data DataMap
        //------------------------------------------------------------------------------------
        if(updateDataMap) {
            List<Version> dataMapVersions = modelDeploymentVersions.getDataMapVersions();
            for (Version version : dataMapVersions) {
                versionControlService.<DataMap>revert(
                        version.getVersion(),
                        version.getDataType(),
                        version.getDataId(),
                        userId,
                        msg,
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
            }
        }
    }


    /**
     * <pre>
     *     모델정보에 속한 모든 Entity 를 revert 한다.
     *     모델정보에 entity 들
     *      ModelDeploymentVersions, InterfaceMdoel, Requirement, DataSet, DataMap
     * </pre>
     * @param revertVersion 모델배포버전
     * @param dataId ModelDeploymentVersions 또는 InterfaceMdoel 의 interfaceModelId 값
     * @param userId revert 요청자 ID
     * @param msg revert 메시지
     * @throws Exception
     */
    @Transactional
    public void revert(String revertVersion, String dataId, String userId, final String msg) throws Exception {
        String currentVersion = versionControlService.getHeadVersion(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId);
        if(revertVersion.equals(currentVersion)) throw new Exception(Util.join("The current version(", currentVersion, ") is equal to the revert version(", revertVersion, ")"));

        versionControlService.<ModelDeploymentVersions>revert(
                revertVersion,
                VersionDataType.INTERFACE_MODEL_VERSION.getCd(),
                dataId,
                userId,
                msg,
                new UpdateHandler<ModelDeploymentVersions>() {
                    @Override
                    public void update(ModelDeploymentVersions modelDeploymentVersions, String userId, String date) throws Exception {
                        updateAllContents(modelDeploymentVersions, msg, userId, date);
                    }
                },
                ModelDeploymentVersions.class);
    }

    /**
     * <pre>
     *     모델정보에 속한 모든 Entity 를 revert 한다.
     *     모델정보에 entity 들
     *      ModelDeploymentVersions, InterfaceMdoel, Requirement, DataSet, DataMap
     * </pre>
     * @param revertVersionNumber 모델배포버전넘버
     * @param dataId ModelDeploymentVersions 또는 InterfaceMdoel 의 interfaceModelId 값
     * @param userId revert 요청자 ID
     * @param msg revert 메시지
     * @throws Exception
     */
    @Transactional
    public void revert(int revertVersionNumber, String dataId, String userId, final String msg) throws Exception {
        int currentVersionNumber = versionControlService.getHeadVersionNumber(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId);
        if(revertVersionNumber == currentVersionNumber) throw new Exception(Util.join("The current version(", currentVersionNumber, ") is equal to the revert version(", revertVersionNumber, ")"));
        versionControlService.<ModelDeploymentVersions>revert(
                revertVersionNumber,
                VersionDataType.INTERFACE_MODEL_VERSION.getCd(),
                dataId,
                userId,
                msg,
                new UpdateHandler<ModelDeploymentVersions>() {
                    @Override
                    public void update(ModelDeploymentVersions modelDeploymentVersions, String userId, String date) throws Exception {
                        updateAllContents(modelDeploymentVersions, msg,  userId, date);
                    }
                },
                ModelDeploymentVersions.class);
    }

    /**
     * <pre>
     *     모델정보 중에 ModelDeploymentVersions, InterfaceMdoel, Requirement 만  revert 한다.
     *     DataSet, DataMap 는 revert 하지 않는다.
     * </pre>
     * @param revertVersion 모델배포버전
     * @param dataId ModelDeploymentVersions 또는 InterfaceMdoel 의 interfaceModelId 값
     * @param userId revert 요청자 ID
     * @param msg revert 메시지
     * @throws Exception
     */
    @Transactional
    public void revertInterfaceModel(String revertVersion, String dataId, String userId, final String msg) throws Exception {
        String currentVersion = versionControlService.getHeadVersion(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId);
        if(revertVersion.equals(currentVersion)) throw new Exception(Util.join("The current version(", currentVersion, ") is equal to the revert version(", revertVersion, ")"));
        versionControlService.<ModelDeploymentVersions>revert(
                revertVersion,
                VersionDataType.INTERFACE_MODEL_VERSION.getCd(),
                dataId,
                userId,
                msg,
                new UpdateHandler<ModelDeploymentVersions>() {
                    @Override
                    public void update(ModelDeploymentVersions modelDeploymentVersions, String userId, String date) throws Exception {
                        updateModelContents(modelDeploymentVersions, msg, userId, date);
                    }
                },
                ModelDeploymentVersions.class);
    }

    /**
     * <pre>
     *     모델정보 중에 ModelDeploymentVersions, InterfaceMdoel, Requirement 만  revert 한다.
     *     DataSet, DataMap 는 revert 하지 않는다.
     * </pre>
     * @param revertVersionNumber 모델배포버전넘버
     * @param dataId ModelDeploymentVersions 또는 InterfaceMdoel 의 interfaceModelId 값
     * @param userId revert 요청자 ID
     * @param msg revert 메시지
     * @throws Exception
     */
    @Transactional
    public void revertInterfaceModel(int revertVersionNumber, String dataId, String userId, final String msg) throws Exception {
        int currentVersionNumber = versionControlService.getHeadVersionNumber(VersionDataType.INTERFACE_MODEL_VERSION.getCd(), dataId);
        if(revertVersionNumber == currentVersionNumber) throw new Exception(Util.join("The current version(", currentVersionNumber, ") is equal to the revert version(", revertVersionNumber, ")"));
        versionControlService.<ModelDeploymentVersions>revert(
                revertVersionNumber,
                VersionDataType.INTERFACE_MODEL_VERSION.getCd(),
                dataId,
                userId,
                msg,
                new UpdateHandler<ModelDeploymentVersions>() {
                    @Override
                    public void update(ModelDeploymentVersions modelDeploymentVersions, String userId, String date) throws Exception {
                        updateModelContents(modelDeploymentVersions, msg,  userId, date);
                    }
                },
                ModelDeploymentVersions.class);
    }



}
