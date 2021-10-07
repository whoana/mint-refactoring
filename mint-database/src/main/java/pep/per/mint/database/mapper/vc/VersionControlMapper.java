package pep.per.mint.database.mapper.vc;

import org.apache.ibatis.annotations.Param;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.data.basic.version.VersionData;

import java.util.List;
import java.util.Map;

public interface VersionControlMapper {

    public void insertVersionData(VersionData versionData) throws Exception;

    public void updateHeadVersionData(@Param("dataType") String dataType, @Param("dataId") String dataId, @Param("headVersionNumber") int headVersionNumber, @Param("msg") String msg,  @Param("modUserId") String modUserId, @Param("modDate") String modDate) throws Exception;

    public VersionData getHeadVersionData(@Param("dataType") String dataType, @Param("dataId") String dataId) throws Exception;

    public VersionData getVersionData(@Param("dataType") String dataType, @Param("dataId") String dataId, @Param("version") String version, @Param("includeDelYnNo") boolean includeDelYnNo) throws Exception;

    public VersionData getVersionDataByVersionNumber(@Param("dataType") String dataType, @Param("dataId") String dataId, @Param("versionNumber") int versionNumber, @Param("includeDelYnNo") boolean includeDelYnNo) throws Exception;

    //public List<VersionData> getVersionDataList(@Param("dataType") String dataType, @Param("dataId") String dataId, @Param("includeDelYnNo") boolean includeDelYnNo) throws Exception;

    public List<VersionData> getVersionDataList(Map params) throws Exception;

    public void deleteVersionData(@Param("dataType") String dataType, @Param("dataId") String dataId, @Param("msg") String msg,  @Param("modUserId") String modUserId, @Param("modDate") String modDate) throws Exception;
}
