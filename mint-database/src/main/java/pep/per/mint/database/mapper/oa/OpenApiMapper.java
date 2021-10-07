package pep.per.mint.database.mapper.oa;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OpenApiMapper {

    public List<String> findInterfaceId(Map<String, String> params) throws Exception;

    List<String> findDataSetId(@Param("dataSetCd") String dataSetCd) throws Exception;

    List<Map> getStatisticsInterface(Map<String, String> params) throws Exception;
}
