package pep.per.mint.openapi.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pep.per.mint.database.service.co.CommonService;

@Service
public class CommonOpenService {

	Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CommonService commonService;

	public List<Map> getInterfaceCds() throws Exception {
    	return commonService.getInterfaceCdListV2();
    }

}
