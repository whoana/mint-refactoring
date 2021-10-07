package pep.per.mint.openapi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pep.per.mint.database.service.oa.OpenApiService;


@Service
public class StatisticsOpenService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	OpenApiService openApiService;

	/**
	 * <pre>
	 * 데이터트래킹 집계 openapi 서비스
	 * </pre>
	 * @since 202106
	 */
	public List<Map> getStatisticsInterface(Map params) throws Exception {
		String fromDate = (String) params.get("fromDate");
		String toDate = (String) params.get("toDate");

		if(fromDate.length() < 14 || toDate.length() < 14) {
			throw new Exception("Invaild parameter.");
		} else {
			params.put("fromDate", fromDate.substring(0, 14) + "000000");
			params.put("toDate", toDate.substring(0, 14) + "999999");
			return openApiService.getStatisticsInterface(params);
		}
	}

}
