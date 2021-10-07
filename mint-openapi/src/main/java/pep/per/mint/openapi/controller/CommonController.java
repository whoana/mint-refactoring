package pep.per.mint.openapi.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.openapi.service.CommonOpenService;

/**
 * <pre>
 *     OpenApi 서비스
 *     공통코드 정보조회
 * </pre>
 */
@Controller(value = "pep.per.mint.openapi.controller.CommonController")
@RequestMapping("/openapi")
public class CommonController {

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	@Autowired
	CommonOpenService commonOpenService;

	/**
     * <pre>
     *     InterfaceCd List
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/interfaceCds", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<Map>> getInterfaceCds(
    		@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
    		HttpSession httpSession,
    		HttpServletRequest request,
    		Locale locale
    ) throws Exception {
    	List<Map> interfaceCds = commonOpenService.getInterfaceCds();
    	comMessage.setResponseObject(interfaceCds);
    	comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
    	comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
    	return comMessage;
    }

}