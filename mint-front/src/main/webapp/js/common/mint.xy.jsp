<%--
/*****************************************************************************
 * Program Name : mint.xy.jsp
 * Description
 *   - mint.xy project
 ****************************************************************************/
 --%>

<%@page import="org.apache.xmlbeans.impl.util.Base64"%>
<%@page import="pep.per.mint.front.security.RSAKeyManager"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	response.setContentType("text/javascript; charset=utf-8");
	boolean flag = RSAKeyManager.isEncryptEnable();
%>

/**
 * mint.xy module
 */
var mint_xy = function() {

};


/**
 * xy
 */
mint_xy.prototype.getXYK = function() {
	return "<%= RSAKeyManager.getPublicKey() %>";
}

/**
 * xy
 */
mint_xy.prototype.getXYM = function() {
	return "<%= RSAKeyManager.getPublicModule() %>";
}

/**
 * xyEnableCheck
 */
mint_xy.prototype.isEnable = function() {
	return <%= RSAKeyManager.isEncryptEnable() %>;
}

/**
 * ae
 */
mint_xy.prototype.ae = function(data) {
	try {
		var i = forge.random.getBytesSync(16);
		var k = forge.random.getBytesSync(16);

		var cipher = forge.cipher.createCipher('AES-CBC', k);
		cipher.start({iv: i});
		cipher.update(forge.util.createBuffer(data, 'utf8'));
		cipher.finish();

		var p = cipher.output.data;

		if( mint.common.isEmpty(p) ) {
			mint.common.alert('AES Encrypt Fail');
			return '';
		} else {
			return {i: i, k: k, p: hex2b64(forge.util.bytesToHex(p))};
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.xy.ae"});
	}
}

/**
 * re
 */
mint_xy.prototype.re = function(data) {
	try {
		var m = b64tohex(this.getXYM());
		var k = b64tohex(this.getXYK());

		var rsa = forge.pki.setRsaPublicKey(new forge.jsbn.BigInteger(m,16), new forge.jsbn.BigInteger(k,16));
		var p = rsa.encrypt(data, 'RSA-OAEP', { md: forge.md.md5.create(), mgf1: { md: forge.md.sha1.create() } } );

		if( mint.common.isEmpty(p) ) {
			mint.common.alert('RSA Encrypt Fail');
			return '';
		} else {
			return hex2b64(forge.util.bytesToHex(p));
		}
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.xy.re"});
	}
}

/**
 * encrypt
 */
mint_xy.prototype.encrypt = function(data) {
	try {
		var aedata = mint.xy.ae(data);
		return {
			i: mint.xy.re(forge.util.bytesToHex(aedata.i)),
			k: mint.xy.re(forge.util.bytesToHex(aedata.k)),
			p: aedata.p
		};
	} catch( e ) {
		mint.common.errorLog(e, {"fnName" : "mint.xy.encrypt"});
	}
}

/**
 * mint 객체에 추가한다
 */
mint.addConstructor(function() {
	try {
	    if (typeof mint.xy === "undefined") {
	        mint.xy = new mint_xy();
	    }
    } catch( e ) {

    }
});