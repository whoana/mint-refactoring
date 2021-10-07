<%@page import="pep.per.mint.front.env.FrontEnvironments" %>
<%@page import="pep.per.mint.common.util.Util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
/**
 * 서비스 정보 모듈
 */
var mint_rest = function() {
};

mint_rest.services = <%=Util.toJSONString(FrontEnvironments.routingOptionMap)%>;

mint_rest.prototype.getServiceUrl = function(restId) {
	try {

		var url = "/routers/" + restId + "?isTest=" + mint.isTestMode;

		for(var key in mint_rest.services[restId]) {
		    url = url + "&" + key + "=" + mint_rest.services[restId][key];
		}

		return mint.baseServiceUrl + url;

	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.rest.getServiceUrl"});
	}
};

/**
 * mint 객체에 추가한다
 */
mint.addConstructor(function() {
	try {
	    if (typeof mint.rest === "undefined") {
	        mint.rest = new mint_rest();
	    }
    } catch( e ) {

    }
});