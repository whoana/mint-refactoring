package pep.per.mint.front.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pep.per.mint.common.util.LogUtil;
import pep.per.mint.common.util.Util;

/**
 * request 를 다시 읽기 위한 목적으로 사용되는 클래스<p>
 */
public class DecryptHttpServletRequest extends HttpServletRequestWrapper {

	Logger logger = LoggerFactory.getLogger(DecryptHttpServletRequest.class);

	// 읽어들인 문자열..
	private String readString = null;

	public DecryptHttpServletRequest(HttpServletRequest request) {
		super(request);

		try {
			readString = IOUtils.toString(request.getInputStream(), "utf-8");
		} catch( IOException e) {
			logger.error("#input read failed.", e);
		}

		try {

			//----------------------------------------------------------------------------
			// 기존 암호화 로직 주석처리(2021.04.07)
			// TODO : 특이점 없으면 주석부분 삭제할것
			//----------------------------------------------------------------------------
			/*
			StringBuffer sb = new StringBuffer();
			LogUtil.bar2(sb,LogUtil.prefix("DecryptHTTPServletRequest"));
			LogUtil.prefix(sb, "encryptData : " + readString);


			//----------------------------------------------------------------------------
			// CASE#1. ComMessage 의 개별적인 항목에 대한 복호화시 아래 로직 패턴으로 구현...
			//----------------------------------------------------------------------------
			// convert JSON string to Map
			Map<String,Object> map = Util.jsonToMap(readString);

			Iterator<String> keys = map.keySet().iterator();
			while( keys.hasNext() ) {
				String key = keys.next();

				// 복호화를 한다.
				if( key.equals("requestObject") ) {
					String str = RSAKeyManager.decrypt((String)map.get(key));
					map.put(key, Util.jsonToObject(str));
				}

			}
			// convert map to JSON string
			readString = Util.toJSONString(map);


			//----------------------------------------------------------------------------
			// ComMessage 의 전체 복호화시 아래 로직 패턴으로 구현...
			//  - 성능이슈도 있고 해서, 로그인시에만 암호화 하는것으로 한다.
			//----------------------------------------------------------------------------
			//readString = RSAKeyManager.decrypt(readString);

			LogUtil.prefix(sb, "decryptData : " + readString);
			logger.debug(sb.toString());
			*/



			StringBuffer sb = new StringBuffer();
			LogUtil.bar2(sb,LogUtil.prefix("DecryptHTTPServletRequest"));
			LogUtil.prefix(sb, "encryptData : " + readString);

			//----------------------------------------------------------------------------
			// RequestObject 는 AES 로 암복호화, AES iv, key 는 RSA 로 암복호화
			//----------------------------------------------------------------------------
			// convert JSON string to Map
			//----------------------------------------------------------------------------
			Map<String,Object> map = Util.jsonToMap(readString);

			String key = "requestObject";
			Object value = map.get(key);

			Map<String, Object> requestObject = Util.jsonToMap(Util.toJSONString(value));

			String i = (String)requestObject.get("i");
			String k = (String)requestObject.get("k");
			String p = (String)requestObject.get("p");

			String payload = RSAKeyManager.aesDecrypt(i, k, p);
			map.put("requestObject", Util.jsonToObject( payload ));

			//----------------------------------------------------------------------------
			// convert map to JSON string
			//----------------------------------------------------------------------------
			readString = Util.toJSONString(map);

			LogUtil.prefix(sb, "decryptData : " + readString);
			logger.debug(sb.toString());


		} catch(Exception e){
			logger.error("#input decrypt failed.", e);
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		InputStream is = new ByteArrayInputStream(readString.getBytes("utf-8"));
		return new BufferedReader(new InputStreamReader(is));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readString.getBytes("utf-8"));
	    ServletInputStream servletInputStream = new ServletInputStream(){
	        public int read() throws IOException {
	          return byteArrayInputStream.read();
	        }
	    };

		return servletInputStream;
	}
}
