package pep.per.mint.database.service.vc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.Requirement;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.InterfaceModel;
import pep.per.mint.common.data.basic.runtime.ModelDeploymentVersions;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.data.basic.version.VersionData;
import pep.per.mint.common.data.basic.version.VersionDataType;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.mapper.vc.VersionControlMapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VersionControlService {

    @Autowired
    VersionControlMapper versionControlMapper;


    VersionGenerator versionGenerator = new DefaultVersionGenerator();

    /**
     * <pre>
     *  VersionData 를 커밋하고 HEAD 버전 정보를 업데이트한다.
     * </pre>
     * @param versionData
     * @see VersionData
     * @throws Exception
     */
    @Transactional
    public void commit(VersionData versionData) throws Exception {
        versionControlMapper.insertVersionData(versionData);
        //--------------------------------------------------
        // Marking Head Version :
        //--------------------------------------------------
        {
            String dataType = versionData.getDataType();
            String dataId = versionData.getDataId();
            int revertVersionNumber = versionData.getVersionNumber();
            String msg = "move HEAD(" + versionData.getVersionNumber() + ")";
            msg += System.lineSeparator();
            String modUserId = versionData.getCommitUserId();
            String modDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
            versionControlMapper.updateHeadVersionData(dataType, dataId, revertVersionNumber, msg, modUserId, modDate);
        }
    }

    @Transactional
    public <T> void commit(String dataType, String dataId, String msg, String tag, String userId, T contents) throws Exception {
        VersionData versionData = new VersionData();
        versionData.setVersion(versionGenerator.generate());
        versionData.setDataType(dataType);
        versionData.setDataId(dataId);
        versionData.setCommitDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
        versionData.setMsg(msg);
        versionData.setTag(tag);
        versionData.setCommitUserId(userId);
        versionData.setDataObject(contents);
        commit(versionData);
    }

    @Transactional
    public void delete(String dataType, String dataId, String msg, String userId) throws Exception {
        versionControlMapper.deleteVersionData(dataType, dataId, msg, userId, Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
    }

    /**
     *
     * @param revertVersion
     * @param dataType
     * @param dataId
     * @param modUserId
     * @param msg
     * @param updateHandler
     * @param <T>
     * @throws Exception
     */
    @Transactional
    public <T> void revert(String revertVersion, String dataType, String dataId, String modUserId, String msg, UpdateHandler<T> updateHandler, Class<T> type) throws Exception {
        VersionData vd = retrieve(dataType, dataId, revertVersion, false);
        if(vd == null) throw new Exception(Util.join("There is no the revert version (", revertVersion ,") data."));
        revert(vd, modUserId, msg, updateHandler, type);
    }

    /**
     *
     * @param revertVersionNumber
     * @param dataType
     * @param dataId
     * @param modUserId
     * @param msg
     * @param updateHandler
     * @param <T>
     * @throws Exception
     */
    @Transactional
    public <T> void revert(int revertVersionNumber, String dataType, String dataId, String modUserId, String msg, UpdateHandler<T> updateHandler, Class<T> type) throws Exception {
        VersionData vd = retrieve(dataType, dataId, revertVersionNumber, false);
        if(vd == null) throw new Exception(Util.join("There is no the revert version (", revertVersionNumber ,") data."));
        revert(vd, modUserId, msg, updateHandler, type);
    }

    /**
     *
     * @param revertVersionData
     * @param modUserId
     * @param msg
     * @param updateHandler
     * @param <T>
     * @throws Exception
     */
    @Transactional
    public <T> void revert(VersionData revertVersionData, String modUserId, String msg, UpdateHandler<T> updateHandler, Class<T> type) throws Exception {
        String modDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
        T data  = revertVersionData.getDataObject(type);
        updateHandler.update(data, modUserId, modDate);
        int revertVersionNumber = revertVersionData.getVersionNumber();
        msg += System.lineSeparator();
        String dataType = revertVersionData.getDataType();
        String dataId = revertVersionData.getDataId();
        versionControlMapper.updateHeadVersionData(dataType, dataId, revertVersionNumber, msg, modUserId, modDate);
    }

    @Transactional
    public void cancel(String originVersion, String dataType, String dataId, String modUserId, String msg) throws Exception {
        String modDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
        VersionData versionData = versionControlMapper.getVersionData(dataType, dataId, originVersion, true);
        int originVersionNumber = versionData.getVersionNumber();
        versionControlMapper.updateHeadVersionData(dataType, dataId, originVersionNumber, msg, modUserId, modDate);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @return
     * @throws Exception
     */
    public String getHeadVersion(String dataType, String dataId) throws Exception{
        VersionData headVersionData = versionControlMapper.getHeadVersionData(dataType, dataId);
        return headVersionData == null ? null : headVersionData.getVersion();
    }

    public int getHeadVersionNumber(String dataType, String dataId) throws Exception{
        VersionData headVersionData = versionControlMapper.getHeadVersionData(dataType, dataId);
        return headVersionData == null ? -1 : headVersionData.getVersionNumber();
    }


    /**
     *
     * @param dataType
     * @param dataId
     * @return
     * @throws Exception
     */
    public VersionData retrieveHead(String dataType, String dataId) throws Exception{
        return versionControlMapper.getHeadVersionData(dataType, dataId);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param version
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public VersionData retrieve(String dataType, String dataId, String version, boolean includeDelYnNo) throws Exception {
        return versionControlMapper.getVersionData(dataType, dataId, version, includeDelYnNo);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param versionNumber
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public VersionData retrieve(String dataType, String dataId, int versionNumber, boolean includeDelYnNo) throws Exception {
        return versionControlMapper.getVersionDataByVersionNumber(dataType, dataId, versionNumber, includeDelYnNo);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param version
     * @return
     * @throws Exception
     */
    public VersionData retrieve(String dataType, String dataId, String version) throws Exception {
        return retrieve(dataType, dataId, version, true);
    }

    /**
     *
     * @param version
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public VersionData retrieve(Version version, boolean includeDelYnNo) throws Exception {
        return retrieve(version.getDataType(), version.getDataId(), version.getVersion(), includeDelYnNo);
    }

    /**
     *
     * @param version
     * @return
     * @throws Exception
     */
    public VersionData retrieve(Version version) throws Exception {
        return retrieve(version, true);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param versionNumber
     * @return
     * @throws Exception
     */
    public VersionData retrieve(String dataType, String dataId, int versionNumber) throws Exception {
        return retrieve(dataType, dataId, versionNumber, true);
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveList(String dataType, String dataId, boolean includeDelYnNo) throws Exception {
        Map params = new HashMap();
        params.put("dataType", dataType);
        params.put("dataId", dataId);
        params.put("includeDelYnNo", includeDelYnNo);
        List<VersionData> versionDataList = versionControlMapper.getVersionDataList(params);
        return versionDataList;
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @param commitDate
     * @param commitUserId
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveCommitList(String dataType, String dataId, String commitDate, String commitUserId, boolean includeDelYnNo) throws Exception {
        Map params = new HashMap();
        params.put("dataType", dataType);
        params.put("dataId", dataId);
        params.put("commitDate", commitDate);
        if(!Util.isEmpty(commitUserId)) params.put("commitUserId", commitUserId);
        params.put("includeDelYnNo", includeDelYnNo);
        List<VersionData> versionDataList = versionControlMapper.getVersionDataList(params);
        return versionDataList;
    }


    /**
     *
     * @param dataType
     * @param dataId
     * @param commitDate
     * @param commitUserId
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveModList(String dataType, String dataId, String modDate, String modUserId, boolean includeDelYnNo) throws Exception {
        Map params = new HashMap();
        params.put("dataType", dataType);
        params.put("dataId", dataId);
        params.put("modDate", modDate);
        if(!Util.isEmpty(modUserId)) params.put("modUserId", modUserId);
        params.put("includeDelYnNo", includeDelYnNo);
        List<VersionData> versionDataList = versionControlMapper.getVersionDataList(params);
        return versionDataList;
    }

    /**
     *
     * @param tag
     * @param includeDelYnNo
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveList(String tag, boolean includeDelYnNo) throws Exception {
        Map params = new HashMap();
        params.put("tag", tag);
        params.put("includeDelYnNo", includeDelYnNo);
        List<VersionData> versionDataList = versionControlMapper.getVersionDataList(params);
        return versionDataList;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveDeleteList() throws Exception {
        Map params = new HashMap();
        params.put("delYn", "Y");
        params.put("includeDelYnNo", false);
        List<VersionData> versionDataList = versionControlMapper.getVersionDataList(params);
        return versionDataList;
    }

    /**
     *
     * @param dataType
     * @param dataId
     * @return
     * @throws Exception
     */
    public List<VersionData> retrieveList(String dataType, String dataId) throws Exception {
        return retrieveList(dataType, dataId, true);
    }


    public VersionGenerator getVersionGenerator() {
        return versionGenerator;
    }

    public void setVersionGenerator(VersionGenerator versionGenerator) {
        this.versionGenerator = versionGenerator;
    }

    /**
     * <pre>
     *     로컬레파지토리에 저장된 해당버전의 데이터를 json 파일로 저장한다.
     * </pre>
     * @param dataType 데이터유형
     * @param dataId 데이터 ID
     * @param versionNumber 데이터 버전
     * @param path 저장위치
     * @throws Exception
     */
    public void download(String dataType, String dataId, int versionNumber, String path) throws Exception {
        VersionData versionData = retrieve(dataType, dataId, versionNumber);
        if (versionData == null) throw new Exception("not found response for your request.");
        File dest = new File(path);
        if(!dest.exists() || !dest.isDirectory()) throw new Exception("not found the folder for downloading resources or it may not be a directory.");
        String fileName = Util.join(dataType, "-", dataId, "-", versionNumber, ".json");
        File file = new File(dest, fileName);
        file.deleteOnExit();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte [] data = null;
            Object obj = null;
            if(VersionDataType.REQUIREMENT.getCd().equals(dataType)){
                obj = versionData.getDataObject(Requirement.class);
            }else if(VersionDataType.INTERFACE.getCd().equals(dataType)){
                obj = versionData.getDataObject(Interface.class);
            }else if(VersionDataType.DATASET.getCd().equals(dataType)){
                obj = versionData.getDataObject(DataSet.class);
            }else if(VersionDataType.DATAMAP.getCd().equals(dataType)){
                obj = versionData.getDataObject(DataMap.class);
            }else if(VersionDataType.INTERFACE_MODEL.getCd().equals(dataType)){
                obj = versionData.getDataObject(InterfaceModel.class);
            }else if(VersionDataType.INTERFACE_MODEL_VERSION.getCd().equals(dataType)){
                obj = versionData.getDataObject(ModelDeploymentVersions.class);
            }else{
                throw new Exception("Unsupported data type!");
            }
            data = Util.toJSONPrettyString(obj).getBytes(StandardCharsets.UTF_8);
            fos.write(data);
            fos.flush();
        }finally{
            if(fos != null) fos.close();
        }
    }

    public void downloadHead(String dataType, String dataId, String path) throws Exception {
        VersionData versionData = retrieveHead(dataType, dataId);
        if (versionData == null) throw new Exception("not found response for your request.");
        File dest = new File(path);
        if(!dest.exists() || !dest.isDirectory()) throw new Exception("not found the folder for downloading resources or it may not be a directory.");
        String fileName = Util.join(dataType, "-", dataId, "-head.json");
        File file = new File(dest, fileName);
        file.deleteOnExit();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte [] data = null;
            Object obj = null;
            if(VersionDataType.REQUIREMENT.getCd().equals(dataType)){
                obj = versionData.getDataObject(Requirement.class);
            }else if(VersionDataType.INTERFACE.getCd().equals(dataType)){
                obj = versionData.getDataObject(Interface.class);
            }else if(VersionDataType.DATASET.getCd().equals(dataType)){
                obj = versionData.getDataObject(DataSet.class);
            }else if(VersionDataType.DATAMAP.getCd().equals(dataType)){
                obj = versionData.getDataObject(DataMap.class);
            }else if(VersionDataType.INTERFACE_MODEL.getCd().equals(dataType)){
                obj = versionData.getDataObject(InterfaceModel.class);
            }else if(VersionDataType.INTERFACE_MODEL_VERSION.getCd().equals(dataType)){
                obj = versionData.getDataObject(ModelDeploymentVersions.class);
            }else{
                throw new Exception("Unsupported data type!");
            }
            data = Util.toJSONPrettyString(obj).getBytes(StandardCharsets.UTF_8);
            fos.write(data);
            fos.flush();
        }finally{
            if(fos != null) fos.close();
        }
    }
}
