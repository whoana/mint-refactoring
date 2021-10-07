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
import pep.per.mint.common.util.RequiredFields;
import pep.per.mint.openapi.service.StatisticsOpenService;

/**
 * <pre>
 *     OpenApi 서비스
 *     데이터트래킹 통계 현황
 * </pre>
 */
@Controller(value = "pep.per.mint.openapi.controller.StatisticsController")
@RequestMapping("/openapi")
public class StatisticsController {

	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	@Autowired
	StatisticsOpenService statisticsOpenService;

    /**
     * <pre>
     *    인터페이스별 트래킹 집계(인터페이스ID, 성공/처리중/실패 건수, 송/수신 시스템)
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "/statistics/interface", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
	public @ResponseBody ComMessage<Map<String,String>, List<Map>> getStatisticsInterface(
			@RequestBody ComMessage<Map<String,String>, List<Map>> comMessage,
			HttpSession httpSession,
			HttpServletRequest request,
			Locale locale
	) throws Exception {
		Map<String,String> params = comMessage.getRequestObject();
		RequiredFields.checkMapParams(params, "fromDate","toDate");

		List<Map> statisticsInfo = statisticsOpenService.getStatisticsInterface(params);
		comMessage.setResponseObject(statisticsInfo);
		comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
		comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
		return comMessage;


	}

}