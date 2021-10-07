package pep.per.mint.inhouse.sso.shl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.inhouse.sso.ILoginService;


public class SHLLoginService implements ILoginService {

	static Logger logger = LoggerFactory.getLogger(SHLLoginService.class);

	@Autowired
	@Qualifier(value="eucKrRestTemplate")
	RestTemplate restTemplate;

	@Autowired
	CommonService commonService;

	public final static String OK = "00000000";

	String systemId = null;

	String url = null;

	public void init() throws Exception{

		List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<HttpMessageConverter<?>>();
		HttpMessageConverter<?> stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("EUC-KR"));
        httpMessageConverters.add(stringHttpMessageConverter);

        restTemplate.setMessageConverters(httpMessageConverters);

        boolean ssoLogin = false;
        {
			Map<String, List<String>> environmentalValues = commonService.getEnvironmentalValues();
			List<String> values = environmentalValues.get("system.sso.login");
			if (values != null && values.size() > 0) {
				ssoLogin = Boolean.parseBoolean(values.get(0));
			}
        }

		if(ssoLogin) {
			{
				Map<String, List<String>> environmentalValues = commonService.getEnvironmentalValues();
				List<String> values = environmentalValues.get("system.sso.login.url");
				if (values != null && values.size() > 0) {
					url = values.get(0);
					logger.debug(Util.join("system.sso.login.url:",url));
				}else{
					//change-when : 170927
					//change-what :
					//system.sso.login = true 일 경우 환경변수가 존재하지 않으면 아예 어플리케이션으르 올리지 않도록 하였으나.
					//사이트별로 환경변수가 틀릴수 있으므로 에러로그만 남기는 것으로 대체한다.
					//throw new Exception("can't login to sso system because there is no environment values of system.sso.login.url");
					logger.error("can't login to sso system because there is no environment values of system.sso.login.url");
				}
			}

			{
				Map<String, List<String>> environmentalValues = commonService.getEnvironmentalValues();
				List<String> values = environmentalValues.get("system.sso.login.system.id");
				if (values != null && values.size() > 0) {
					systemId = values.get(0);
					logger.debug(Util.join("system.sso.login.system.id:",systemId));
				}else{
					//change-when : 170927
					//change-what :
					//system.sso.login = true 일 경우 환경변수가 존재하지 않으면 아예 어플리케이션으르 올리지 않도록 하였으나.
					//사이트별로 환경변수가 틀릴수 있으므로 에러로그만 남기는 것으로 대체한다.
					//throw new Exception("can't login to sso system because there is no environment values of system.sso.login.system.id");
					logger.error("can't login to sso system because there is no environment values of system.sso.login.system.id");
				}
			}
		}
	}


	@Override
	public Map<?, ?> login(Map<?, ?> params) throws Exception {

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;

		String userId = ((String)params.get("userId"));
		String password = ((String)params.get("password"));

		String eid = new String(DatatypeConverter.printBase64Binary(userId.getBytes()));
		String epassword = new String(DatatypeConverter.printBase64Binary(password.getBytes()));

		Map<String, String> res = new HashMap<String, String>();
		try {
			URL urlx = new URL(url+"?userid="+eid+"&password="+epassword);
			logger.debug("URL :" + url);

			//HTTP Connection 구하기
			conn = (HttpURLConnection) urlx.openConnection();
			conn.setRequestMethod("GET");

			//연결 타임아웃 설정
			conn.setConnectTimeout(30000);  //30초
			//읽기 타임아웃 설정
			conn.setReadTimeout(30000);

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();

			//int httpResCode = conn.getResponseCode();
			//String httpResMsg = conn.getResponseMessage();
			//String httpBody = (String)conn.getContent();

			is = conn.getInputStream();
			byte[] buffer = new byte[1024];
			baos = new ByteArrayOutputStream();

			while(true) {
				int len = is.read(buffer);
				if(len < 0) {
					break;
				}
				baos.write(buffer, 0, len);
			}

			String readData = new String(baos.toByteArray(), "MS949");
			//String responseCode = conn.getHeaderField("response-code");
			//String responseMessage = conn.getHeaderField("response-message");

			logger.debug("userId : "+userId+" 로그인 "+readData);

			//코드 확인용
			res.put("ssoReturn", readData);

			if(readData.equals("true")) {
				res.put("errorCd", COMMON_SUCCESS_CD);
			}else if(readData.equals("100")) {
				//화면에서 먼저 거르는 에러
				res.put("errorCd", COMMON_FAIL_CD);
				res.put("errorMsg", "인자값이 없거나 비정상 처리입니다(100)");
			}else if(readData.equals("101")||readData.equals("102")) {
				//아이디 없음/패스워드 없음
				res.put("errorCd", COMMON_FAIL_CD);
				res.put("errorMsg", "ID/PASSWORD 확인 필요");
			}else if(readData.equals("103")||readData.equals("106")||readData.equals("107")) {
				//비밀번호 기간 만료/비밀번호 5회 실패/초기비밀번호
				//SSO로 이동  >>> SSO 페이지(통합 로그인 시스템의 페이지) 전환 필요시 errorCd = COMMON_WARNING_CD 설정
				res.put("errorCd", COMMON_WARNING_CD);
				if(readData.equals("103")) {
					res.put("errorMsg", "비밀번호 기간 만료(103)");
				}else if(readData.equals("106")) {
					res.put("errorMsg", "로그인을 5회 이상 실패했습니다(106)");
				}else if(readData.equals("107")) {
					res.put("errorMsg", "초기 비밀번호이므로 비밀번호 변경이 필요합니다(107)");
				}
			}else if(readData.equals("104")) {
				//내부오류
				res.put("errorCd", COMMON_FAIL_CD);
				res.put("errorMsg", "내부 오류. 관리자에게 문의하세요(104)");
			}else if(readData.equals("105")) {
				//비밀번호 기간 만료 경고
				//에러만 띄워주고 로그인 진행
				res.put("errorCd", COMMON_SUCCESS_CD);
				res.put("alertMsg", "비밀번호 기간이 곧 만료됩니다(105)");
			}

		}catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}finally {
			if(baos != null) {
				try { baos.close(); } catch (Exception ee) {}
			}
			if(is != null) {
				try { is.close(); } catch (Exception ee) {}
			}
			if(os != null) {
				try { os.close(); }	catch (Exception ee) {}
			}
			if(conn != null) {
				try { conn.disconnect(); } catch(Exception ee) {}
			}
		}
		return res;
	}
}
