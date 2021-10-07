package pep.per.mint.database.service.co;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import pep.per.mint.database.mapper.co.ServerFilteringMapper;

/**
 * Server Filtering Service
 *
 * @since 202102
 * @version 1.0
 * @author iip
 */
@Service
public class ServerFilteringService {

	@Autowired
	ServerFilteringMapper serverFilteringMapper;

	/**
	 *
	 */
	private static final Logger logger = LoggerFactory.getLogger(ServerFilteringService.class);

	/**
	 * DataSet명 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getDataSetNameList(Map<String, String> params) throws Exception {
		return serverFilteringMapper.getDataSetNameList(params);
	}

	/**
	 * 맵핑명 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getDataMapNameList(Map<String, String> params) throws Exception {
		return serverFilteringMapper.getDataMapNameList(params);
	}

	/**
	 * 맵핑 Source DataSet Nm1 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getDataMapSrcDataSetNm1(Map<String, String> params) throws Exception {
		return serverFilteringMapper.getDataMapSrcDataSetNm1(params);
	}

	/**
	 * 맵핑 Target DataSet Nm1 조회
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<Map> getDataMapTagDataSetNm1(Map<String, String> params) throws Exception {
		return serverFilteringMapper.getDataMapTagDataSetNm1(params);
	}

}
