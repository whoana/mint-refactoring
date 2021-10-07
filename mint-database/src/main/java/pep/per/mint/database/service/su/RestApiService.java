package pep.per.mint.database.service.su;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pep.per.mint.database.mapper.su.RestApiMapper;

@Service
public class RestApiService {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	RestApiMapper restApiMapper;

	/**
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public List<pep.per.mint.common.data.basic.Service> getRestServiceList(Map params) throws Exception{
		return restApiMapper.getRestServiceList(params);
	}

	public pep.per.mint.common.data.basic.Service getRestServiceDetail(String serviceId) throws Exception{
		return restApiMapper.getRestServiceDetail(serviceId);
	}

	@Transactional
	public int createRestService(pep.per.mint.common.data.basic.Service service) throws Exception {
		return restApiMapper.insertRestService(service);
	}

	public int updateRestService(pep.per.mint.common.data.basic.Service service) throws Exception {
		return restApiMapper.updateRestService(service);
	}

	public int deleteRestService(String serviceId, String modId, String modDate) throws Exception{
		return restApiMapper.deleteRestService(serviceId, modId, modDate);
	}

}
