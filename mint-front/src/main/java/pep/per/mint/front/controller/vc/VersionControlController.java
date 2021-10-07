/**
 * Copyright 2020 Mocomsys Inc.  All Rights Reserved.
 */
package pep.per.mint.front.controller.vc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.runtime.AppModelAttributeId;
import pep.per.mint.common.util.RequiredFields;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.vc.VersionControlService;
import pep.per.mint.front.exception.ControllerException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <pre>
 *     VersionControl 위한 API 서비스 제공
 * </pre>
 */
@Controller
@RequestMapping("/vc")
public class VersionControlController {
    private static final Logger logger = LoggerFactory.getLogger(VersionControlController.class);

    @Autowired
    ReloadableResourceBundleMessageSource messageSource;

    private ServletContext servletContext;

    @Autowired
    VersionControlService versionControlService;

    @RequestMapping(value = "/versions/data-download", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String,Object>, Map<String,Object>> download(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String,Object>, Map<String,Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {

        Map<String, Object> params = comMessage.getRequestObject();
        String dataType = (String)params.get("dataType");
        String dataId = (String)params.get("dataId");
        int versionNumber = (Integer)params.get("versionNumber");
        String path = (String)params.get("path");

        //RequiredFields.checkMapParams(params, "dataType", "dataId", "versionNumber", "path");
        versionControlService.download(dataType, dataId, versionNumber, path);


        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

    @RequestMapping(value = "/versions/data-download-head", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody
    ComMessage<Map<String,Object>, Map<String,Object>> downloadHead(
            HttpSession httpSession,
            @RequestBody ComMessage<Map<String,Object>, Map<String,Object>> comMessage,
            Locale locale, HttpServletRequest request) throws Exception, ControllerException {

        Map<String, Object> params = comMessage.getRequestObject();
        String dataType = (String)params.get("dataType");
        String dataId = (String)params.get("dataId");
        String path = (String)params.get("path");

        //RequiredFields.checkMapParams(params, "dataType", "dataId", "versionNumber", "path");
        versionControlService.downloadHead(dataType, dataId, path);


        comMessage.setEndTime(Util.getFormatedDate("yyyyMMddHHmmssSSS"));
        comMessage.setErrorCd(messageSource.getMessage("success.cd.ok", null, locale));
        comMessage.setErrorMsg(messageSource.getMessage("success.msg.ok", null, locale));
        return comMessage;
    }

}
