package pep.per.mint.endpoint.tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.runtime.InterfaceModel;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.DataSetService;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.rt.ModelService;
import pep.per.mint.database.service.su.BridgeProviderService;
import pep.per.mint.endpoint.Variables;
import pep.per.mint.endpoint.service.deploy.ModelDeployService;
import pep.per.mint.endpoint.service.deploy.data.description.ModelDescriptionXmlBuilder;
import pep.per.mint.endpoint.service.deploy.data.description.Options;

@RequestMapping("/rt")
@Controller
public class ModelDeployDataApiProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelDeployDataApiProvider.class);
	
	@Autowired
	ModelDeployService modelDeployService;
	
	@Autowired
	BridgeProviderService bridgeProviderService;
	
	@Autowired
	DataSetService dataSetService;

	@Autowired
	ModelService modelService;

	@Autowired
	CommonService commonService;
	
	@Autowired
	Variables variables;
	
	@RequestMapping(value="/model/interface/xml/{integrationId}", method = RequestMethod.GET, produces="application/xml;charset=utf8")
	public @ResponseBody String getDeployXmlData(HttpServletRequest request, @PathVariable String integrationId) throws Exception {
		
		String interfaceId = bridgeProviderService.getInterfaceId(integrationId);
		
		ModelDescriptionXmlBuilder mdxb = new ModelDescriptionXmlBuilder();
		InterfaceModel model = modelService.getInterfaceModelByInterfaceId(interfaceId);
		if(model == null) throw new Exception(Util.join("not found interface model [integrationId:",integrationId,"]"));
		Interface interfaze = commonService.getInterfaceDetail(model.getInterfaceId());
		
		Options options = new Options();
		options.setIncludeServerInfo(false);
		return  mdxb.build(options, model, dataSetService, interfaze, variables, "9999999");
		
		}

}
