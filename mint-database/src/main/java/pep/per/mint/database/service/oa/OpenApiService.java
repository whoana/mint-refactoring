package pep.per.mint.database.service.oa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pep.per.mint.database.mapper.oa.OpenApiMapper;

import java.util.List;
import java.util.Map;

@Service
public class OpenApiService {

    @Autowired
    OpenApiMapper openApiMapper;

    public List<String> findInterfaceId(Map<String, String> params) throws Exception {
        return openApiMapper.findInterfaceId(params);
    }


    public List<String> findDataSetId(String cd) throws Exception {
        return openApiMapper.findDataSetId(cd);
    }

    public List<Map> getStatisticsInterface(Map<String, String> params) throws Exception {
    	return openApiMapper.getStatisticsInterface(params);
    }
}
